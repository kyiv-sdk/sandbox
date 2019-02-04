package com.example.internal_storage_utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalStorageUtils {
    public static boolean writeToFile(Context context, String fileName, byte[] data){
        try {
            File path = context.getFilesDir();
            File file = new File(path, fileName);
            FileOutputStream stream = new FileOutputStream(file);
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

    public static byte[] readFile(Context context, String fileName){
        File path = context.getFilesDir();
        File file = new File(path, fileName);
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        FileInputStream in;
        try {
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
            return bytes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExists(Context context, String filename){
        File path = context.getFilesDir();
        File file = new File(path, filename);
        return file.exists();
    }

    public static boolean deleteFile(Context context, String filename){
        File path = context.getFilesDir();
        File file = new File(path, filename);
        return file.delete();
    }
}
