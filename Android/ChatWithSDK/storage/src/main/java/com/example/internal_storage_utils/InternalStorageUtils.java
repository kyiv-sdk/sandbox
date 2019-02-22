package com.example.internal_storage_utils;

import android.content.Context;

import com.good.gd.file.File;
import com.good.gd.file.FileInputStream;
import com.good.gd.file.FileOutputStream;
import com.good.gd.file.GDFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InternalStorageUtils {
    private static final String PATH = "/";
    private static final String EXTENSION = ".txt";

    public static boolean writeToFile(String fileName, byte[] data){
        try {
            FileOutputStream stream = GDFileSystem.openFileOutput(PATH + fileName + EXTENSION, Context.MODE_PRIVATE);
            stream.write(data);
            stream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readFile(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            FileInputStream in = GDFileSystem.openFileInput(PATH + fileName + EXTENSION);

            int i = 0;
            int offset = 0;

            while (true) {
                byte[] buf = new byte[1024];
                i = in.read(buf);

                if (i >= 0) {
                    outputStream.write(buf, offset, i);
                } else {
                    break;
                }
            }

            in.close();
            return outputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists(String filename){
        File file = new File(PATH + filename + EXTENSION);
        return file.exists();
    }

    public static boolean deleteFile(String filename){
        File file = new File(PATH + filename + EXTENSION);
        return file.delete();
    }

    public static boolean copyFile(String fromFileName, String toFileName){
        byte[] fromFileContent = readFile(fromFileName);
        if (fromFileContent != null){
            return writeToFile(toFileName, fromFileContent);
        }

        return false;
    }
}
