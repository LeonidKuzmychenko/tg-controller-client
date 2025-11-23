package lk.tech.tgcontrollerclient.services;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ScreenCapture {

    public static List<byte[]> captureAllScreens() throws Exception {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        List<byte[]> screenshots = new ArrayList<>();
        Robot robot = new Robot();

        for (GraphicsDevice screen : screens) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            BufferedImage screenshot = robot.createScreenCapture(bounds);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", baos);

            screenshots.add(baos.toByteArray());
        }

        return screenshots;
    }


}
