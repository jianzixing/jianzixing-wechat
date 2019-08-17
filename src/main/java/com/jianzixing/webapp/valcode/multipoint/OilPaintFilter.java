package com.jianzixing.webapp.valcode.multipoint;

import java.awt.image.BufferedImage;

public class OilPaintFilter extends AbstractBufferedImageOp {

    private int radius = 5; // default value
    private int intensity = 20; // default value

    public OilPaintFilter(int radius, int graylevel) {
        this.radius = radius;
        this.intensity = graylevel;
    }

    public OilPaintFilter() {
        this(5, 20);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dest == null)
            dest = createCompatibleDestImage(src, null);

        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        getRGB(src, 0, 0, width, height, inPixels);
        int index = 0;
        int subradius = this.radius / 2;
        int[] intensityCount = new int[intensity + 1];
        int[] ravg = new int[intensity + 1];
        int[] gavg = new int[intensity + 1];
        int[] bavg = new int[intensity + 1];
        for (int i = 0; i <= intensity; i++) {
            intensityCount[i] = 0;
            ravg[i] = 0;
            gavg[i] = 0;
            bavg[i] = 0;
        }
        for (int row = 0; row < height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for (int col = 0; col < width; col++) {

                for (int subRow = -subradius; subRow <= subradius; subRow++) {
                    for (int subCol = -subradius; subCol <= subradius; subCol++) {
                        int nrow = row + subRow;
                        int ncol = col + subCol;
                        if (nrow >= height || nrow < 0) {
                            nrow = 0;
                        }
                        if (ncol >= width || ncol < 0) {
                            ncol = 0;
                        }
                        index = nrow * width + ncol;
                        tr = (inPixels[index] >> 16) & 0xff;
                        tg = (inPixels[index] >> 8) & 0xff;
                        tb = inPixels[index] & 0xff;
                        int curIntensity = (int) (((double) ((tr + tg + tb) / 3) * intensity) / 255.0f);
                        intensityCount[curIntensity]++;
                        ravg[curIntensity] += tr;
                        gavg[curIntensity] += tg;
                        bavg[curIntensity] += tb;
                    }
                }

                // find the max number of same gray level pixel
                int maxCount = 0, maxIndex = 0;
                for (int m = 0; m < intensityCount.length; m++) {
                    if (intensityCount[m] > maxCount) {
                        maxCount = intensityCount[m];
                        maxIndex = m;
                    }
                }

                // get average value of the pixel
                int nr = ravg[maxIndex] / maxCount;
                int ng = gavg[maxIndex] / maxCount;
                int nb = bavg[maxIndex] / maxCount;
                index = row * width + col;
                outPixels[index] = (ta << 24) | (nr << 16) | (ng << 8) | nb;

                // post clear values for next pixel
                for (int i = 0; i <= intensity; i++) {
                    intensityCount[i] = 0;
                    ravg[i] = 0;
                    gavg[i] = 0;
                    bavg[i] = 0;
                }

            }
        }
        setRGB(dest, 0, 0, width, height, outPixels);
        return dest;
    }

}