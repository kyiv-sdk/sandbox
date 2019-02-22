package com.good.gd.example.services.greetings.client.file_source;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.internal_storage_utils.InternalStorageUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileSourcePresenter implements FileSourcePresenterInterface {
    private static final String TAG = FileSourcePresenter.class.getSimpleName();

    private static final String FILES_PATH = "/files_to_send";
    private static final String NEXT_FILE_NAME_KEYWORD = "next_filename";

    private FileSourseViewInterface fileSourseViewInterface;
    private SharedPreferences sharedPreferences;

    public FileSourcePresenter(FileSourseViewInterface fileSourseViewInterface, SharedPreferences sharedPreferences) {
        this.fileSourseViewInterface = fileSourseViewInterface;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void handleMakePhotoButtonClick() {
        if (fileSourseViewInterface.checkCameraPermission()) {
            fileSourseViewInterface.startCameraActivity();
        }
    }

    @Override
    public void handleSelectFromGalleryPhotoButtonClick() {
        fileSourseViewInterface.showGalleryPickerDialog();
    }

    @Override
    public void handlePhotoPicked(Context context, Uri uri) {
        try {
            InputStream iStream = context.getContentResolver().openInputStream(uri);

            byte[] inputData = getBytes(iStream);

            boolean isPathCorrect = true;
            if (!InternalStorageUtils.exists(FILES_PATH)){
                isPathCorrect = InternalStorageUtils.mkdir(FILES_PATH);
            }

            if (isPathCorrect) {
                String nextFilename = getNextFileName(sharedPreferences);
                InternalStorageUtils.writeToFile(FILES_PATH + "/" + nextFilename, inputData);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    @Override
    public void handlePhotoMade(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        boolean isPathCorrect = true;
        if (!InternalStorageUtils.exists(FILES_PATH)){
            isPathCorrect = InternalStorageUtils.mkdir(FILES_PATH);
        }

        if (isPathCorrect) {
            String nextFilename = getNextFileName(sharedPreferences);
            InternalStorageUtils.writeToFile(FILES_PATH + "/" + nextFilename, byteArray);
        }
    }

    private String getNextFileName(SharedPreferences sharedPreferences){
        Integer i = sharedPreferences.getInt(NEXT_FILE_NAME_KEYWORD, 0);
        sharedPreferences.edit().putInt(NEXT_FILE_NAME_KEYWORD, i + 1).apply();
        return String.valueOf(i);
    }
}
