package com.jianzixing.webapp.valcode.drag;

import org.mimosaframework.core.utils.RandomUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yangankang
 */
public class ImageDragFactory {

    private static final InputStream defaultBackgroundImage = ImageDragFactory.class.getResourceAsStream("/images/default_drag_code.png");
    private static final InputStream defaultOutsideImage = ImageDragFactory.class.getResourceAsStream("/images/default_outside.png");
    private static final InputStream defaultInsideImage = ImageDragFactory.class.getResourceAsStream("/images/default_inside.png");
    private static final InputStream defaultInnerImage = ImageDragFactory.class.getResourceAsStream("/images/default_inner.png");

    public static DragMatchCode.Result buildDefault() throws IOException {
        long time1 = System.currentTimeMillis();

        BufferedImage bgImage = ImageIO.read(defaultBackgroundImage);
        BufferedImage osImage = ImageIO.read(defaultOutsideImage);
        BufferedImage isImage = ImageIO.read(defaultInsideImage);
        BufferedImage inImage = ImageIO.read(defaultInnerImage);

        int width = bgImage.getWidth(), height = bgImage.getHeight();
        int sw = osImage.getWidth(), sh = osImage.getHeight();
        int x = RandomUtils.randomNumber(sw, width - sw),
                y = RandomUtils.randomNumber(0, height - sh);

        long time2 = System.currentTimeMillis();
        DragMatchCode dragMatchCode = new ImageDragMatchCode();
        dragMatchCode.setBackgroundBufferedImage(bgImage);
        dragMatchCode.setOutsideBufferedImage(osImage);
        dragMatchCode.setInsideBufferedImage(isImage);
        dragMatchCode.setInnerBufferedImage(inImage);
        dragMatchCode.setPoint(x, y);

        DragMatchCode.Result result = dragMatchCode.build();
        System.out.println("jt = " + (System.currentTimeMillis() - time2) / 1000d);
        System.out.println("t = " + (System.currentTimeMillis() - time1) / 1000d);

        return result;
    }
}
