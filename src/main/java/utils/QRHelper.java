package utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class QRHelper {

    /**
     * Generates a QR code from a string.
     * This string is then displayed as a QR code.
     * But beware BufferedImage can not be used immediately for Javafx.
     *
     * @param qrcodeText  this string is converted to a QR code
     */
    public static BufferedImage generateQRCodeImage(String qrcodeText) {
        QRCodeWriter barcodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = barcodeWriter.encode(qrcodeText, BarcodeFormat.QR_CODE, 1000, 1000);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    /**
     * Convert a BufferedImage to a usable Javafx Image.
     *
     * @param image  image to convert to a Javafx Image
     */
    public static javafx.scene.image.Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }
}
