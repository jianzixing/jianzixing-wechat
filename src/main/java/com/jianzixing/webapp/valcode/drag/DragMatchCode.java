package com.jianzixing.webapp.valcode.drag;

import java.awt.image.BufferedImage;

/**
 * 实现类会根据BufferedImage的大小计算
 * 背景图尺寸的宽度必须是抠图尺寸的两倍
 *
 * @author yangankang
 */
public interface DragMatchCode {

    void setBackgroundBufferedImage(BufferedImage bi);

    void setOutsideBufferedImage(BufferedImage bi);

    void setInsideBufferedImage(BufferedImage bi);

    void setInnerBufferedImage(BufferedImage bi);

    void setPoint(int x, int y);

    Result build();

    class Result {
        private BufferedImage backgroundImage;
        private BufferedImage matchImage;
        private int x = 0;
        private int y = 0;

        public BufferedImage getBackgroundImage() {
            return backgroundImage;
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        public BufferedImage getMatchImage() {
            return matchImage;
        }

        public void setMatchImage(BufferedImage matchImage) {
            this.matchImage = matchImage;
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
    }
}
