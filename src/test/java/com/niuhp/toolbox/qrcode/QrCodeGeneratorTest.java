/**
 *
 */
package com.niuhp.toolbox.qrcode;

import org.junit.Test;

/**
 * @author niuhaipeng
 * @date 2015年12月24日
 */
public class QrCodeGeneratorTest {

    @Test
    public void testGenerateQrCode() {
        QrCodeGenerator generator = new QrCodeGenerator();
        String text = "http://niuhp.com";
        String targetPath = "d:/temp/niuhp/t1.jpg";
        String logoPath = "d:/temp/niuhp/niuhp.jpg";
        generator.generateQrCode(text, targetPath, logoPath);
    }

}
