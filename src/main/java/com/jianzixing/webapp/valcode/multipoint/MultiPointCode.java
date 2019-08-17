package com.jianzixing.webapp.valcode.multipoint;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
public interface MultiPointCode {

    void setBackgroundImage(BufferedImage bi);

    void setFontSize(int fontSize);

    /**
     * 是否开启抗锯齿
     *
     * @param is
     */
    void isRenderingHint(boolean is);

    Result builder(List<String> words, boolean isIncludeWord);

    boolean check(List<Point> oldPoints, List<Point> userPoints);

    class Result {
        BufferedImage image;
        List<Point> points;

        public BufferedImage getImage() {
            return image;
        }

        public void setImage(BufferedImage image) {
            this.image = image;
        }

        public List<Point> getPoints() {
            return points;
        }

        public void setPoints(List<Point> points) {
            this.points = points;
        }

        public void addPoint(int x, int y) {
            Point point = new Point();
            point.setX(x);
            point.setY(y);
            if (points == null) {
                points = new ArrayList<>();
            }
            this.points.add(point);
        }
    }

    class Point {
        private String word;
        private int color;
        private int x;
        private int y;
        private int rotate; //0-360

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getRotate() {
            return rotate;
        }

        public void setRotate(int rotate) {
            this.rotate = rotate;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }
    }
}
