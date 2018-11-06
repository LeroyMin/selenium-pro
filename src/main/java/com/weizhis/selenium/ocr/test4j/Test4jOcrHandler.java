package com.weizhis.selenium.ocr.test4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @Auther: minliang
 * @Date: 2018/11/6 16:28
 * @Description:
 */
public class Test4jOcrHandler {

    private static byte[] takeScreenshot(WebDriver driver){
        byte[] screenshot = null;
        screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);//得到截图
        return screenshot;
    }

    private static BufferedImage createElementImage(WebDriver driver, WebElement webElement, int x, int y, int width, int heigth)
            throws IOException {
        Dimension size = webElement.getSize();
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(takeScreenshot(driver)));
        BufferedImage croppedImage = originalImage.getSubimage(x, y,
                size.getWidth() + width, size.getHeight() + heigth);//进行裁剪
        return croppedImage;
    }

    private static String getVerificationCode(String path, WebDriver driver){
        File imageFile = new File(path);
        try {
            imageFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        WebElement element = driver.findElement(By.className("pointer"));
        try {
            BufferedImage image = createElementImage(driver, element, 800, 410,
                    20, 35);//得到裁剪的图片
            ImageIO.write(image, "png", imageFile);//进行保存
        } catch (IOException e) {
            e.printStackTrace();
        }
        ITesseract instance = new Tesseract();//调用Tesseract
        URL url = ClassLoader.getSystemResource("lang");//获得Tesseract的文字库
        String tesspath = url.getPath().substring(1);
        instance.setDatapath(tesspath);//进行读取，默认是英文，如果要使用中文包，加上instance.setLanguage("chi_sim");
        String result = null;
        File imageFile1 = new File(path);
        try {
            result = instance.doOCR(imageFile1);
        } catch (TesseractException e1) {
            e1.printStackTrace();
        }
        result = result.replaceAll("[^a-z^A-Z^0-9]", "");//替换大小写及数字
        return result;
    }
}
