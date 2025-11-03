package com.sosd.insightnews.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtil {

    public static boolean isImage(MultipartFile multipartFile) {
        File file = multipartFileToFile(multipartFile);
        //判断file是否为图片格式
        if (file == null) {
            return false;
        }
        Image img;
        try {
            img = ImageIO.read(file);
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            return false;
        } finally {
            file.delete();
        }
    }


    public static boolean isVideo(MultipartFile multipartFile){

        List<String> formatList = new ArrayList<>();

        formatList.add("avi");
        formatList.add("flv");
        formatList.add("mov");
        formatList.add("mp4");
        formatList.add("mpg");
        formatList.add("mpeg");
        formatList.add("mpv");
        formatList.add("navi");
        formatList.add("qt");
        formatList.add("rm");
        formatList.add("ram");
        formatList.add("ram");
        formatList.add("ram");
        formatList.add("swf");
        formatList.add("wmv");

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        for (String s : formatList) {
            if (extension.equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }

    public static File multipartFileToFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try {
            OutputStream os = Files.newOutputStream(file.toPath());
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
