package com.jianzixing.webapp.valcode.multipoint;

import com.jianzixing.webapp.valcode.PixelUtils;
import org.mimosaframework.core.utils.RandomUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangankang
 */
public class ImageMultiPointCode implements MultiPointCode {

    private static Font defaultFont = null;
    private BufferedImage backgroundImage;
    private int fontSize = 30;
    private int fontYGap = 3;
    private boolean isRenderingHint = false;

    static {
        InputStream fontFile = ImageMultiPointCode.class.getResourceAsStream("/fonts/yh.ttf");
        try {
            defaultFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fontFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setBackgroundImage(BufferedImage bi) {
        this.backgroundImage = bi;
        this.fontSize = (int) (bi.getWidth() * 0.125);
        this.fontYGap = (int) (this.fontSize * (6d / 30));
    }

    @Override
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void isRenderingHint(boolean is) {
        this.isRenderingHint = is;
    }

    @Override
    public Result builder(List<String> words, boolean isIncludeWord) {
        return getResult(words, isIncludeWord);
    }

    @Override
    public boolean check(List<Point> oldPoints, List<Point> userPoints) {

        if (oldPoints == null || userPoints == null || oldPoints.size() != userPoints.size()) {
            return false;
        }

        for (Point p : oldPoints) {
            boolean match = false;
            for (Point point : userPoints) {
                if (p.getX() - fontSize <= point.getX() &&
                        p.getX() + fontSize >= point.getX()
                        && p.getY() - fontSize <= point.getY()
                        && p.getY() + fontSize >= point.getY()
                        && p != point) {
                    match = true;
                }
            }

            if (!match) {
                return false;
            }
        }
        return true;
    }

    private Result getResult(List<String> words, boolean isIncludeWord) {
        List<Point> points = this.getPoints(words);
        BufferedImage textImage = this.createTextImage(points);
        for (int i = 0; i < backgroundImage.getWidth(); i++) {
            for (int j = 0; j < backgroundImage.getHeight(); j++) {
                int c = textImage.getRGB(i, j);
                if (c != 0) {
                    int rgb = PixelUtils.mix(backgroundImage.getRGB(i, j), c, 0.9);
                    backgroundImage.setRGB(i, j, rgb);
                }
            }
        }


        OilPaintFilter filter = new OilPaintFilter();
        filter.setRadius(4);
        backgroundImage = filter.filter(backgroundImage, null);

        if (isIncludeWord) {
            backgroundImage = this.includeWordImage(points, backgroundImage);
        }

        Result result = new Result();
        result.setImage(backgroundImage);
        result.setPoints(points);
        return result;
    }

    private BufferedImage includeWordImage(List<Point> points, BufferedImage backgroundImage) {
        int height = 40, fz = (int) (height * 0.5), spacing = 8;
        BufferedImage wordImage = new BufferedImage(backgroundImage.getWidth(), height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) wordImage.getGraphics();
        //抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0xFFFFFFFF));
        g.fillRect(0, 0, wordImage.getWidth(), wordImage.getHeight());
        int c = 0;
        Font font = defaultFont.deriveFont(Font.ITALIC, fz);
        g.setFont(font);
        for (Point point : points) {
            Point p = new Point();
            p.setX(10 + c * fz + spacing * c);
            p.setY((int) (height - height * 0.3));
            p.setWord(point.getWord());
            if (RandomUtils.randomNumber(0, 2) == 0) {
                p.setRotate(RandomUtils.randomNumber(0, 20));
            } else {
                p.setRotate(RandomUtils.randomNumber(340, 360));
            }
            p.setColor(0xFF333333);
            this.drawString(g, p);
            c++;
        }
        g.dispose();

        BufferedImage image = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight() + height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (j < backgroundImage.getHeight()) {
                    image.setRGB(i, j, backgroundImage.getRGB(i, j));
                } else {
                    image.setRGB(i, j, wordImage.getRGB(i, j - backgroundImage.getHeight()));
                }
            }
        }

        return image;
    }

    private List<Point> getPoints(List<String> words) {
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();
//        int fontColor = PixelUtils.getAverageColor(
//                backgroundImage.getRGB(2, 2),
//                backgroundImage.getRGB(2, 3),
//                backgroundImage.getRGB(width - 2, 2),
//                backgroundImage.getRGB(width - 2, 3),
//                backgroundImage.getRGB(2, height - 2),
//                backgroundImage.getRGB(3, height - 2),
//                backgroundImage.getRGB(width - 2, height - 2),
//                backgroundImage.getRGB(width - 3, height - 3),
//                backgroundImage.getRGB(width / 2 - 2, height / 2 - 2),
//                backgroundImage.getRGB(width / 2 - 3, height / 2 - 3)
//        );
        List<Point> points = new ArrayList<>();
        for (String s : words) {
            Point point = new Point();
            point.setWord(s);
            point.setX(RandomUtils.randomNumber(fontSize, width - fontSize));
            point.setY(RandomUtils.randomNumber(fontSize, height - fontSize));
            point.setRotate(RandomUtils.randomNumber(0, 360));
            points.add(point);

            this.check(points, point);
        }
        return points;
    }

    private void check(List<Point> points, Point point) {
        int count = 0;
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();
        for (; ; ) {
            boolean repeat = true;
            for (Point p : points) {
                if (p.getX() - fontSize <= point.getX() &&
                        p.getX() + fontSize * 1.5 >= point.getX()
                        && p.getY() - fontSize <= point.getY()
                        && p.getY() + fontSize * 1.5 >= point.getY()
                        && p != point) {
                    point.setX(RandomUtils.randomNumber(fontSize, width - fontSize));
                    point.setY(RandomUtils.randomNumber(fontSize, height - fontSize));
                    repeat = false;
                }
            }
            if (repeat) {
                break;
            }
            count++;
            if (count > 10) {
                break;
            }
        }
    }

    private BufferedImage createTextImage(List<Point> points) {
        BufferedImage textImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) textImage.getGraphics();
        //抗锯齿
        if (isRenderingHint) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        for (Point point : points) {
            this.drawStringImage(g, point);
        }

        g.dispose();
        return textImage;
    }

    private void drawStringImage(Graphics2D gsrc, Point point) {
        int w = fontSize + this.fontYGap * 2, h = fontSize + this.fontYGap * 2;
        BufferedImage wordImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) wordImage.getGraphics();
        Font font = defaultFont.deriveFont(Font.BOLD, fontSize);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.rotate(point.getRotate(), w / 2, h / 2);
        g.drawString(point.getWord(), this.fontYGap, fontSize);

        gsrc.drawImage(wordImage, point.getX() - w / 2, point.getY() - h / 2, null);
        g.dispose();
    }

    private void drawString(Graphics2D g, Point point) {
        int x = point.getX(), y = point.getY();
        g.translate(x, y);
        g.rotate(point.getRotate() * Math.PI / 180);

        if (point.getColor() != 0) {
            g.setColor(new Color(point.getColor()));
        } else {
            g.setColor(new Color(0xA0FFFFFF));
        }
        g.drawString(point.getWord(), 0, 0);
        g.rotate(-point.getRotate() * Math.PI / 180);
        g.translate(-x, -y);
    }
}
