package com.jianzixing.webapp.valcode.multipoint;

import org.mimosaframework.core.utils.RandomUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author yangankang
 */
public class ImageMultiPointFactory {

    public static MultiPointCode.Result buildDefault() throws IOException {
        BufferedImage bgImage = ImageIO.read(ImageMultiPointFactory.class.getResourceAsStream("/images/default_multi_bg.jpg"));
        ImageMultiPointCode multiPointCode = new ImageMultiPointCode();
        multiPointCode.setBackgroundImage(bgImage);
        long time = System.currentTimeMillis();
        MultiPointCode.Result result = multiPointCode.builder(Arrays.asList(new String[]{
                RandomUtils.randomChineseCharacters(1),
                RandomUtils.randomChineseCharacters(1),
                RandomUtils.randomChineseCharacters(1),
                RandomUtils.randomChineseCharacters(1)}), true);

//        System.out.println((System.currentTimeMillis() - time) / 1000d);

        return result;
    }

    public static boolean checkDefault(List<MultiPointCode.Point> oldPoints, List<MultiPointCode.Point> userPoints) {
        ImageMultiPointCode multiPointCode = new ImageMultiPointCode();
        return multiPointCode.check(oldPoints, userPoints);
    }
}
