package com.sz.core.util;

import com.sz.core.common.entity.SliderPuzzle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Random;

public class SlidePuzzleUtil {

    // 大图宽度（原图裁剪拼图后的背景图）
    private static final Integer bigWidth = 320;
    // 大图高度
    private static final Integer bigHeight = 160;
    // 小图宽度（滑块拼图）
    private static final Integer smallWidth = 50;
    // 小图高度
    private static final Integer smallHeight = 50;
    // 小圆半径，即拼图上的凹凸轮廓半径
    private static final Integer smallCircle = 10;
    // 小圆距离点
    private static final Integer smallCircleR1 = smallCircle / 2;


    /**
     * 生成滑块拼图验证码
     * @return 返回null, 表示生成滑块拼图验证码异常
     */
    public static SliderPuzzle createImage(InputStream input) {
        SliderPuzzle sliderPuzzle = new SliderPuzzle();
        try {
            // 1.获取原图对象
            BufferedImage originalImage = ImageIO.read(input);
            // 规范原图的大小
            BufferedImage bigImage = resizeImage(originalImage, bigWidth, bigHeight, true);
            // 2.随机生成离左上角的(X,Y)坐标，上限为 [bigWidth-smallWidth, bigHeight-smallHeight]。最好离大图左边远一点，上限不要紧挨着大图边界
            Random random = new Random();
            // X范围：[2*smallWidth, bigWidth - 2*smallWidth - smallCircle)
            int randomX = random.nextInt(bigWidth - 4 * smallWidth - smallCircle) + 2 * smallWidth;
            // Y范围：[smallCircle, bigHeight - smallHeight - smallCircle)
            int randomY = random.nextInt(bigHeight - smallHeight - 2 * smallCircle) + smallCircle;

            // 3.创建小图对象
            BufferedImage smallImage = new BufferedImage(smallWidth, smallHeight, BufferedImage.TYPE_4BYTE_ABGR);
            // 4.随机生成拼图轮廓数据
            int[][] slideTemplateData = getSlideTemplateData(smallCircleR1);
            // 5.从大图中裁剪拼图。抠原图，裁剪拼图
            cutByTemplate(bigImage, smallImage, slideTemplateData, randomX, randomY);
            sliderPuzzle.setLongTime(System.currentTimeMillis());
            sliderPuzzle.setPosX(randomX);
            sliderPuzzle.setPosY(randomY);
            sliderPuzzle.setBigWidth(bigWidth);
            sliderPuzzle.setBigHeight(bigHeight);
            sliderPuzzle.setBigImage(bigImage);
            sliderPuzzle.setBigImageBase64(getImageBASE64(bigImage));
            sliderPuzzle.setSmallWidth(smallWidth);
            sliderPuzzle.setSmallHeight(smallHeight);
            sliderPuzzle.setSmallImage(smallImage);
            sliderPuzzle.setSmallImageBase64(getImageBASE64(smallImage));
            return sliderPuzzle;
        } catch (Exception e) {
            sliderPuzzle = null;
            return sliderPuzzle;
        }
    }

    /**
     * 获取拼图图轮廓数据
     * @param color 色彩 0和1，其中0表示没有颜色，1有颜色
     */
    private static int[][] getSlideTemplateData(int color) {
        // 拼图轮廓数据
        int[][] data = new int[(int) SlidePuzzleUtil.smallWidth][(int) SlidePuzzleUtil.smallHeight];

        //拼图去掉凹凸的白色距离
        // 不写smallCircleR1时，凹凸为半圆
        int xBlank = SlidePuzzleUtil.smallWidth - SlidePuzzleUtil.smallCircle - smallCircleR1;
        int yBlank = SlidePuzzleUtil.smallHeight - SlidePuzzleUtil.smallCircle - smallCircleR1;

        // 圆的位置
        int rxa = xBlank / 2;
        int ryb = SlidePuzzleUtil.smallHeight - (int) SlidePuzzleUtil.smallCircle;
        double rPow = Math.pow(SlidePuzzleUtil.smallCircle, 2);

        /*
          计算需要的拼图轮廓(方块和凹凸)，用二维数组来表示，二维数组有两张值，0和1，其中0表示没有颜色，1有颜色
          圆的标准方程 (x-a)²+(y-b)²=r²,标识圆心（a,b）,半径为r的圆
         */
        for (int i = 0; i < SlidePuzzleUtil.smallWidth; i++) {
            for (int j = 0; j < SlidePuzzleUtil.smallHeight; j++) {
                // 圆在拼图下方内
                double topR = Math.pow(i - rxa, 2) + Math.pow(j - 2, 2);
                // 圆在拼图下方外
                double downR = Math.pow(i - rxa, 2) + Math.pow(j - ryb, 2);
                // 圆在拼图左侧内 || (i <= xBlank && leftR <= rPow)
                //double leftR = Math.pow(i - 2, 2) + Math.pow(j - rxa, 2);
                // 圆在拼图右侧外
                double rightR = Math.pow(i - ryb, 2) + Math.pow(j - rxa, 2);
                if ((j <= yBlank && topR <= rPow) || (j >= yBlank && downR >= rPow)
                        || (i >= xBlank && rightR >= rPow)) {
                    data[i][j] = 0;
                } else {
                    data[i][j] = 1;
                }
            }
        }
        return data;
    }


    /**
     * 裁剪拼图
     *
     * @param bigImage          - 原图规范大小之后的大图
     * @param smallImage        - 小图
     * @param slideTemplateData - 拼图轮廓数据
     * @param x                 - 坐标x
     * @param y                 - 坐标y
     */
    private static void cutByTemplate(BufferedImage bigImage, BufferedImage smallImage, int[][] slideTemplateData, int x, int y) {
        int[][] martrix = new int[3][3];
        int[] values = new int[9];
        //拼图去掉凹凸的白色距离
        int yBlank = smallHeight - smallCircle - smallCircleR1;
        // 不写smallCircleR1时，凹凸为半圆
        // int xBlank = smallWidth - smallCircle - smallCircleR1;

        // 创建shape区域，即原图抠图区域模糊和抠出小图
        /*
          遍历小图轮廓数据,创建shape区域。即原图抠图处模糊和抠出小图
         */
        for (int i = 0; i < smallImage.getWidth(); i++) {
            for (int j = 0; j < smallImage.getHeight(); j++) {
                // 获取大图中对应位置变色
                int rgb_ori = bigImage.getRGB(x + i, y + j);

                //0和1，其中0表示没有颜色，1有颜色
                int rgb = slideTemplateData[i][j];
                if (rgb == 1) {
                    // 设置小图中对应位置变色
                    smallImage.setRGB(i, j, rgb_ori);

                    // 大图抠图区域高斯模糊
                    readPixel(bigImage, x + i, y + j, values);
                    fillMatrix(martrix, values);
                    bigImage.setRGB(x + i, y + j, avgMatrix(martrix));

                    //边框颜色
                    Color white = new Color(230, 230, 230);
                    Color black = new Color(20, 20, 20);
                    //左侧边界，加重高亮阴暗
                    if (j < yBlank) {
                        bigImage.setRGB(x, y + j, black.getRGB());
                        smallImage.setRGB(0, j, white.getRGB());
                    }
                } else {
                    // 这里把背景设为透明
                    smallImage.setRGB(i, j, rgb_ori & 0x00ffffff);
                }
            }
        }
    }

    /**
     * 图片转BASE64
     */
    public static String getImageBASE64(BufferedImage image) throws IOException {
        byte[] imagedata = null;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bao);
        imagedata = bao.toByteArray();
        return Base64.getEncoder().encodeToString(imagedata);
    }

    /**
     * 改变图片大小
     * @param image  原图
     * @param width  目标宽度
     * @param height 目标高度
     * @return 目标图
     */
    public static BufferedImage resizeImage(final Image image, int width, int height, boolean type) {
        BufferedImage bufferedImage;
        if (type) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        final Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        // 以下三行用于渲染提示 ,处理时间较长
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
        for (int[] x : matrix) {
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
