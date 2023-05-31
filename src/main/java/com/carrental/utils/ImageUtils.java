package com.carrental.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageUtils {

    public static byte[] toBytes(MultipartFile file) {
        byte[] bytes = new byte[0];
        try {
            bytes = new byte[file.getBytes().length];

            int i = 0;
            for (byte b : file.getBytes()) {
                bytes[i++] = b;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
