package com.sz.core.util;

import com.sz.core.common.entity.PointVO;
import com.sz.core.common.entity.SliderPuzzle;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;

@Slf4j
public class SlidePuzzleUtil {

    private static final Integer bigWidth = 320;

    private static final Integer bigHeight = 160;

    private static final Integer smallWidth = 50;

    private static final Integer smallHeight = 50;

    private static final Integer smallCircle = 10;

    private static final Integer smallCircleR1 = 2;

    public static SliderPuzzle createImage(InputStream input, HttpServletRequest request) {
        SliderPuzzle sliderPuzzle = new SliderPuzzle();
        try {
            String requestId = Utils.generateSha256Id(Utils.generateCaptchaRequestId(request));

            BufferedImage originalImage = ImageIO.read(input);
            BufferedImage bigImage = resizeImage(originalImage, bigWidth, bigHeight, true);

            String watermark = SysConfigUtils.getConfValue("sys.captcha.waterText"); // 水印文字
            String waterState = SysConfigUtils.getConfValue("sys.captcha.waterEnable"); // 是否启用水印
            String waterFont = SysConfigUtils.getConfValue("sys.captcha.waterFont"); // 是否启用水印

            if ("true".equals(waterState)) {
                // Add watermark
                Graphics2D g2d = bigImage.createGraphics();
                Font font = new Font(waterFont, Font.BOLD, 20); // 要注意服务器是否有此字体，如果商用要考虑字体版权问题。
                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int x = bigImage.getWidth() - fontMetrics.stringWidth(watermark) - 10;
                int y = bigImage.getHeight() - fontMetrics.getHeight() + 20;
                g2d.drawString(watermark, x, y);
                g2d.dispose();
            }

            String secretKey = AESUtil.getRandomString(16);
            PointVO jigsawPoint = generateJigsawPoint(bigWidth, bigHeight, smallWidth, smallHeight, secretKey);
            int randomX = jigsawPoint.getX();
            int randomY = jigsawPoint.getY();

            BufferedImage smallImage = new BufferedImage(smallWidth, smallHeight, BufferedImage.TYPE_4BYTE_ABGR);
            int[][] slideTemplateData = getSlideTemplateData();
            cutByTemplate(bigImage, smallImage, slideTemplateData, randomX, randomY);

            sliderPuzzle.setRequestId(requestId);
            sliderPuzzle.setPosX(randomX);
            sliderPuzzle.setPosY(randomY);
            sliderPuzzle.setBigWidth(bigWidth);
            sliderPuzzle.setBigHeight(bigHeight);
            sliderPuzzle.setBigImageBase64(getImageBASE64(bigImage));
            sliderPuzzle.setSmallWidth(smallWidth);
            sliderPuzzle.setSmallHeight(smallHeight);
            sliderPuzzle.setSmallImageBase64(getImageBASE64(smallImage));
            sliderPuzzle.setSecretKey(jigsawPoint.getSecretKey());
            return sliderPuzzle;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成验证码异常: {}", e.getMessage());
            return null;
        }
    }

    private static PointVO generateJigsawPoint(int originalWidth, int originalHeight, int jigsawWidth, int jigsawHeight, String secretKey) {
        Random random = new Random();
        int widthDifference = originalWidth - jigsawWidth;
        int heightDifference = originalHeight - jigsawHeight;
        int x, y = 0;

        if (widthDifference <= 0) {
            x = 5;
        } else {
            x = random.nextInt(widthDifference - 100) + 100 + random.nextInt(20) - 10; // Adding variability
        }

        if (heightDifference <= 0) {
            y = 5;
        } else {
            y = random.nextInt(heightDifference) + 5 + random.nextInt(20) - 10; // Adding variability
        }

        return new PointVO(x, y, secretKey);
    }

    private static int[][] getSlideTemplateData() {
        int[][] data = new int[smallWidth][smallHeight];
        int xBlank = smallWidth - smallCircle - smallCircleR1;
        int yBlank = smallHeight - smallCircle - smallCircleR1;
        int rxa = xBlank / 2;
        int ryb = smallHeight - smallCircle;
        double rPow = Math.pow(smallCircle, 2);

        for (int i = 0; i < smallWidth; i++) {
            for (int j = 0; j < smallHeight; j++) {
                double topR = Math.pow(i - rxa, 2) + Math.pow(j - 2, 2);
                double downR = Math.pow(i - rxa, 2) + Math.pow(j - ryb, 2);
                double rightR = Math.pow(i - ryb, 2) + Math.pow(j - rxa, 2);

                if ((j <= yBlank && topR <= rPow) || (j >= yBlank && downR >= rPow) || (i >= xBlank && rightR >= rPow)) {
                    data[i][j] = 0;
                } else {
                    data[i][j] = 1;
                }
            }
        }
        return data;
    }

    private static void cutByTemplate(BufferedImage bigImage, BufferedImage smallImage, int[][] slideTemplateData, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        int yBlank = smallHeight - smallCircle - smallCircleR1;

        Graphics2D g2dBig = bigImage.createGraphics();
        Graphics2D g2dSmall = smallImage.createGraphics();
        g2dBig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dSmall.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < smallImage.getWidth(); i++) {
            for (int j = 0; j < smallImage.getHeight(); j++) {
                if (x + i >= bigImage.getWidth() || y + j >= bigImage.getHeight() || x + i < 0 || y + j < 0) {
                    continue; // Skip if out of bounds
                }
                int rgb_ori = bigImage.getRGB(x + i, y + j);
                int rgb = slideTemplateData[i][j];
                if (rgb == 1) {
                    smallImage.setRGB(i, j, rgb_ori);
                    readPixel(bigImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    bigImage.setRGB(x + i, y + j, avgMatrix(martrix));

                    Color white = new Color(255, 255, 255);
                    if (j < yBlank) {
                        bigImage.setRGB(x, y + j, white.getRGB());
                        smallImage.setRGB(0, j, white.getRGB());
                    }
                } else {
                    smallImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }
        }

        // Enhance the contour by making it thicker and white for both big and small
        // images
        for (int i = 0; i < smallImage.getWidth(); i++) {
            for (int j = 0; j < smallImage.getHeight(); j++) {
                if (x + i >= bigImage.getWidth() || y + j >= bigImage.getHeight() || x + i < 0 || y + j < 0) {
                    continue; // Skip if out of bounds
                }
                if (slideTemplateData[i][j] == 0) {
                    if (i > 0 && slideTemplateData[i - 1][j] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (i < smallImage.getWidth() - 1 && slideTemplateData[i + 1][j] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (j > 0 && slideTemplateData[i][j - 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (j < smallImage.getHeight() - 1 && slideTemplateData[i][j + 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    // Additional checks for diagonal neighbors
                    if (i > 0 && j > 0 && slideTemplateData[i - 1][j - 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (i > 0 && j < smallImage.getHeight() - 1 && slideTemplateData[i - 1][j + 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (i < smallImage.getWidth() - 1 && j > 0 && slideTemplateData[i + 1][j - 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                    if (i < smallImage.getWidth() - 1 && j < smallImage.getHeight() - 1 && slideTemplateData[i + 1][j + 1] == 1) {
                        bigImage.setRGB(x + i, y + j, Color.white.getRGB());
                        smallImage.setRGB(i, j, Color.white.getRGB());
                    }
                }
            }
        }

        g2dBig.dispose();
        g2dSmall.dispose();
    }

    public static String getImageBASE64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bao);
        byte[] imagedata = bao.toByteArray();
        return Base64.getEncoder().encodeToString(imagedata);
    }

    public static BufferedImage resizeImage(final Image image, int width, int height, boolean type) {
        BufferedImage bufferedImage;
        if (type) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();
        return bufferedImage;
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++) {
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;
                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);
            }
        }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] x = matrix[i];
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

}