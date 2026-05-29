package com.sz.core.util;

import com.sz.core.common.entity.PointVO;
import com.sz.core.common.entity.SliderPuzzle;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class SlidePuzzleUtil {

    private SlidePuzzleUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 水印配置参数，由调用方从业务配置中读取后传入，工具类本身不感知配置来源。
     */
    @AllArgsConstructor
    public static class WatermarkConfig {

        /** 是否启用水印 */
        public final boolean enabled;

        /** 水印文字 */
        public final String text;

        /** 水印字体 */
        public final String font;
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    static final int BIG_WIDTH = 320;

    static final int BIG_HEIGHT = 160;

    static final int SMALL_WIDTH = 50;

    static final int SMALL_HEIGHT = 50;

    // 拼图凸起圆半径
    private static final int SMALL_CIRCLE = 10;

    // 凸起边界缓冲
    private static final int SMALL_CIRCLE_R1 = 2;

    public static SliderPuzzle createImage(InputStream input, HttpServletRequest request, WatermarkConfig watermark) {
        SliderPuzzle sliderPuzzle = new SliderPuzzle();
        try {
            String requestId = Utils.generateSha256Id(Utils.generateAgentRequestId(request));

            BufferedImage originalImage = ImageIO.read(input);
            BufferedImage bigImage = resizeImage(originalImage, BIG_WIDTH, BIG_HEIGHT, true);

            if (watermark != null && watermark.enabled) {
                Graphics2D g2d = bigImage.createGraphics();
                Font font = new Font(watermark.font, Font.BOLD, 16);
                g2d.setFont(font);
                g2d.setColor(new Color(255, 255, 255, 160));
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int x = bigImage.getWidth() - fontMetrics.stringWidth(watermark.text) - 8;
                int y = bigImage.getHeight() - fontMetrics.getDescent() - 6;
                g2d.drawString(watermark.text, x, y);
                g2d.dispose();
            }

            String secretKey = AESUtil.getRandomString(16);

            // 生成正确拼图坐标
            PointVO jigsawPoint = generateJigsawPoint(BIG_WIDTH, BIG_HEIGHT, SMALL_WIDTH, SMALL_HEIGHT, secretKey);
            int randomX = jigsawPoint.getX();
            int randomY = jigsawPoint.getY();

            // 生成拼图模板数据（固定形状：右边+上面各一个圆形凸起）
            int[][] slideTemplateData = getSlideTemplateData();

            // 创建透明小图，后端在大图上刻缺口并填充小图像素
            BufferedImage smallImage = new BufferedImage(SMALL_WIDTH, SMALL_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
            cutByTemplate(bigImage, smallImage, slideTemplateData, randomX, randomY);

            sliderPuzzle.setRequestId(requestId);
            sliderPuzzle.setPosX(randomX);
            sliderPuzzle.setPosY(randomY);
            sliderPuzzle.setBigWidth(BIG_WIDTH);
            sliderPuzzle.setBigHeight(BIG_HEIGHT);
            sliderPuzzle.setBigImageBase64(getImageBASE64(bigImage));
            sliderPuzzle.setSmallWidth(SMALL_WIDTH);
            sliderPuzzle.setSmallHeight(SMALL_HEIGHT);
            sliderPuzzle.setSmallImageBase64(getImageBASE64(smallImage));
            sliderPuzzle.setSecretKey(jigsawPoint.getSecretKey());
            return sliderPuzzle;
        } catch (Exception e) {
            log.error("生成验证码异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成拼图模板：右边和上面各有一个圆形凸起，左边平坦 返回二维数组：1 = 拼图区域（显示像素），0 = 镂空区域（透明）
     */
    private static int[][] getSlideTemplateData() {
        int[][] data = new int[SMALL_WIDTH][SMALL_HEIGHT];
        int xBlank = SMALL_WIDTH - SMALL_CIRCLE - SMALL_CIRCLE_R1;
        int yBlank = SMALL_HEIGHT - SMALL_CIRCLE;
        int rxa = xBlank / 2;
        int ryb = SMALL_HEIGHT - SMALL_CIRCLE;
        double rPow = Math.pow(SMALL_CIRCLE, 2);

        for (int i = 0; i < SMALL_WIDTH; i++) {
            for (int j = 0; j < SMALL_HEIGHT; j++) {
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

    /**
     * 根据模板裁切小图，并在大图对应区域绘制缺口（模糊处理 + 白色描边）
     */
    private static void cutByTemplate(BufferedImage bigImage, BufferedImage smallImage, int[][] slideTemplateData, int x, int y) {
        int[][] matrix = new int[3][3];
        int[] values = new int[9];
        int yBlank = SMALL_HEIGHT - SMALL_CIRCLE - SMALL_CIRCLE_R1;

        Graphics2D g2dBig = bigImage.createGraphics();
        Graphics2D g2dSmall = smallImage.createGraphics();
        g2dBig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2dSmall.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < smallImage.getWidth(); i++) {
            for (int j = 0; j < smallImage.getHeight(); j++) {
                if (x + i >= bigImage.getWidth() || y + j >= bigImage.getHeight() || x + i < 0 || y + j < 0) {
                    continue;
                }
                int rgbOri = bigImage.getRGB(x + i, y + j);
                int rgb = slideTemplateData[i][j];
                if (rgb == 1) {
                    smallImage.setRGB(i, j, rgbOri);
                    readPixel(bigImage, x + i, y + j, values);
                    fillMatrix(matrix, values);
                    bigImage.setRGB(x + i, y + j, avgMatrix(matrix));

                    Color white = new Color(255, 255, 255);
                    if (j < yBlank) {
                        bigImage.setRGB(x, y + j, white.getRGB());
                        smallImage.setRGB(0, j, white.getRGB());
                    }
                } else {
                    // 镂空区域：小图透明
                    smallImage.setRGB(i, j, rgbOri & 0x00ffffff);
                }
            }
        }

        // 描边：对镂空区域边缘绘制白色轮廓，增强视觉对比
        for (int i = 0; i < smallImage.getWidth(); i++) {
            for (int j = 0; j < smallImage.getHeight(); j++) {
                if (x + i >= bigImage.getWidth() || y + j >= bigImage.getHeight() || x + i < 0 || y + j < 0) {
                    continue;
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

    private static PointVO generateJigsawPoint(int originalWidth, int originalHeight, int jigsawWidth, int jigsawHeight, String secretKey) {
        int widthDifference = originalWidth - jigsawWidth;
        int heightDifference = originalHeight - jigsawHeight;
        int x, y;

        if (widthDifference <= 0) {
            x = 5;
        } else {
            int rawX = RANDOM.nextInt(widthDifference - 100) + 100 + RANDOM.nextInt(20) - 10;
            x = Math.max(100, Math.min(rawX, widthDifference - 5));
        }

        if (heightDifference <= 0) {
            y = 5;
        } else {
            int rawY = RANDOM.nextInt(heightDifference) + 5 + RANDOM.nextInt(20) - 10;
            y = Math.max(5, Math.min(rawY, heightDifference - 5));
        }

        return new PointVO(x, y, secretKey);
    }

    public static String getImageBASE64(BufferedImage image) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bao);
        return Base64.getEncoder().encodeToString(bao.toByteArray());
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
        for (int[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                row[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(row[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

}
