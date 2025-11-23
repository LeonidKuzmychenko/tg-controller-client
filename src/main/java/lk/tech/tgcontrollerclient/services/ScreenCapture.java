package lk.tech.tgcontrollerclient.services;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ScreenCapture {

    private static final int MAX_FRAME_SIZE = 63 * 1024; // 63 KB

    /** Главный метод — снимает все экраны и возвращает сжатые JPEG (<63KB) */
    public static List<byte[]> captureAllScreens() throws Exception {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        List<byte[]> screenshots = new ArrayList<>();
        Robot robot = new Robot();

        for (GraphicsDevice screen : screens) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            BufferedImage screenshot = robot.createScreenCapture(bounds);

            byte[] compressed = compressToMaxSize(screenshot, MAX_FRAME_SIZE);
            screenshots.add(compressed);
        }

        return screenshots;
    }

    /** Сжатие BufferedImage в JPEG до заданного размера */
    private static byte[] compressToMaxSize(BufferedImage image, int maxBytes) throws IOException {
        float quality = 0.2f; // Start high, then reduce

        while (quality >= 0.01f) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // ищем JPEG encoder
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("No JPEG writers available");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            writer.setOutput(ImageIO.createImageOutputStream(baos));
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();

            byte[] output = baos.toByteArray();

            IO.println("try to compress: " + output.length);

            if (output.length <= maxBytes) {
                IO.println("Compressed to " + output.length + " bytes @ quality=" + quality);
                return output;
            }

            quality -= 0.01f; // уменьшение качества
        }

        throw new IOException("Cannot compress image below " + maxBytes + " bytes");
    }

//    public static List<byte[]> captureAllScreens() throws Exception {
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] screens = ge.getScreenDevices();
//
//        List<byte[]> screenshots = new ArrayList<>();
//        Robot robot = new Robot();
//
//        for (GraphicsDevice screen : screens) {
//            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
//            BufferedImage screenshot = robot.createScreenCapture(bounds);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(screenshot, "png", baos);
//
//            screenshots.add(baos.toByteArray());
//        }
//
//        return screenshots;
//    }


}
