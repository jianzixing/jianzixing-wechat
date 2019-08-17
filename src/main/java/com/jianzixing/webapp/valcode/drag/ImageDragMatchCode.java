package com.jianzixing.webapp.valcode.drag;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author yangankang
 */
public class ImageDragMatchCode implements DragMatchCode {

    private BufferedImage backgroundBi;
    private BufferedImage outsideBi;
    private BufferedImage insideBi;
    private BufferedImage innerBi;
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int bw = 0;
    private int bh = 0;

    @Override
    public void setBackgroundBufferedImage(BufferedImage bi) {
        this.backgroundBi = bi;
        this.bw = bi.getWidth();
        this.bh = bi.getHeight();
    }

    @Override
    public void setOutsideBufferedImage(BufferedImage bi) {
        this.outsideBi = bi;
        if (width == 0) width = bi.getWidth();
        if (height == 0) height = bi.getHeight();
        this.checkWH(width, height);
    }

    @Override
    public void setInsideBufferedImage(BufferedImage bi) {
        this.insideBi = bi;
        if (width == 0) width = bi.getWidth();
        if (height == 0) height = bi.getHeight();
        this.checkWH(width, height);
    }

    @Override
    public void setInnerBufferedImage(BufferedImage bi) {
        this.innerBi = bi;
        if (width == 0) width = bi.getWidth();
        if (height == 0) height = bi.getHeight();
        this.checkWH(width, height);
    }

    private void checkWH(int w, int h) {
        if (this.width != w || this.height != h) {
            throw new IllegalArgumentException("贴图尺寸必须一致");
        }
    }

    @Override
    public void setPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Result build() {
        if (width * 2 > bw || height > bh) {
            throw new IllegalArgumentException("装载的图片尺寸不能小于抠图尺寸");
        }
        BufferedImage image = getImageFragment();
        BufferedImage chartlet = getChartlet();
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.drawImage(chartlet, 0, 0, width, height, null);
        g.dispose();

        Graphics2D gb = (Graphics2D) backgroundBi.getGraphics();
        gb.drawImage(innerBi, x, y, width, height, null);
        gb.dispose();

        Result result = new Result();
        result.setBackgroundImage(backgroundBi);
        result.setMatchImage(image);
        result.setX(x);
        result.setY(y);

        return result;
    }

    private BufferedImage getChartlet() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int color1 = outsideBi.getRGB(i, j);
                if (color1 == -1 || color1 == 0xFFFFFFFF) {
                    outsideBi.setRGB(i, j, 0);
                }
                int color2 = insideBi.getRGB(i, j);
                if (color2 == -1 || color2 == 0xFFFFFFFF) {
                    insideBi.setRGB(i, j, 0);
                }
                int color3 = innerBi.getRGB(i, j);
                if (color3 == -1 || color3 == 0xFFFFFFFF) {
                    innerBi.setRGB(i, j, 0);
                }
            }
        }
        Graphics2D g = (Graphics2D) outsideBi.getGraphics();
        g.drawImage(insideBi, 0, 0, width, height, null);
        g.dispose();
        return outsideBi;
    }

    private BufferedImage getImageFragment() {
        int[][] rect = new int[width][height];
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                int nx = i - x;
                int ny = j - y;
                int c = outsideBi.getRGB(nx, ny);
                if (c == -1 || c == 0xFFFFFFFF) {
                    rect[nx][ny] = backgroundBi.getRGB(i, j);
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, rect[i][j]);
            }
        }
        return image;
    }
}
