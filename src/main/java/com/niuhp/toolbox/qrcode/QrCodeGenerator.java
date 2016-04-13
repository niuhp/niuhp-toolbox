/**
 *
 */
package com.niuhp.toolbox.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.niuhp.core.logadapter.LogX;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by niuhp on 2016/4/13.
 */
public class QrCodeGenerator {
    private static final LogX logx = LogX.getLogX(QrCodeGenerator.class);

    private static final Color defaultBackgroundColor = new Color(192, 192, 192);
    private static final Color defaultForegroundColor = new Color(0, 128, 0);
    private static final Color defaultLogoBoundColor = new Color(0, 0, 255);
    private int background = defaultBackgroundColor.getRGB();
    private int foreground = defaultForegroundColor.getRGB();
    private String format = "jpg";
    private int width = 300;
    private int height = 300;
    private double logoRatio = 0.2;
    private Color logoBoundColor = defaultLogoBoundColor;

    public void generateQrCode(String text, String qrCodePath) {
        generateQrCode(text, qrCodePath, null);
    }

    public void generateQrCode(String text, String qrCodePath, String qrLogoPath) {
        int index = qrCodePath.lastIndexOf('.');
        if (index != -1) {
            format = qrCodePath.substring(index + 1);
        }
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 二维码边界空白大小 1,2,3,4 (4为默认,最大)
        hints.put(EncodeHintType.MARGIN, 1);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage image = toBufferedImage(bitMatrix);
            if (qrLogoPath != null) {
                File qrLogoFile = new File(qrLogoPath);
                image = addLogoToQrCode(image, qrLogoFile);
            }
            File qrCodeFile = new File(qrCodePath);
            ImageIO.write(image, format, qrCodeFile);
        } catch (Exception e) {
            logx.error(String.format("generateQrCode to %s error", qrCodePath), e);
        }
    }

    private BufferedImage addLogoToQrCode(BufferedImage qrImg, File qrLogoFile) {
        if (qrLogoFile == null || !qrLogoFile.exists() || !qrLogoFile.isFile()) {
            return qrImg;
        }
        try {
            Graphics2D g = qrImg.createGraphics();
            BufferedImage logoImg = ImageIO.read(qrLogoFile);
            int qrWidth = qrImg.getWidth();
            int qrHeight = qrImg.getHeight();
            int logoWidth = (int) (qrWidth * logoRatio);
            int logoHeight = (int) (qrHeight * logoRatio);
            int x = (qrWidth - logoWidth) / 2;
            int y = (qrHeight - logoHeight) / 2;
            g.drawImage(logoImg, x, y, logoWidth, logoHeight, null);
            g.drawRoundRect(x, y, logoWidth, logoHeight, 15, 15);
            // logo边框大小
            g.setStroke(new BasicStroke(1));
            g.setColor(logoBoundColor);
            g.drawRect(x, y, logoWidth, logoHeight);
            g.dispose();
            logoImg.flush();
            qrImg.flush();
        } catch (IOException e) {
            logx.error(
                    String.format("add logo %s to qr image error", qrLogoFile.getAbsolutePath()), e);
        }
        return qrImg;
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? foreground : background);
            }
        }
        return image;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getForeground() {
        return foreground;
    }

    public void setForeground(int foreground) {
        this.foreground = foreground;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getLogoRatio() {
        return logoRatio;
    }

    public void setLogoRatio(double logoRatio) {
        this.logoRatio = logoRatio;
    }

    public Color getLogoBoundColor() {
        return logoBoundColor;
    }

    public void setLogoBoundColor(Color logoBoundColor) {
        this.logoBoundColor = logoBoundColor;
    }
}
