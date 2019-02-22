package com.good.gd.example.services.greetings.client.file_picker;

import com.example.internal_storage_utils.InternalStorageUtils;

import java.util.ArrayList;
import java.util.Collections;

public class FilePickerModel implements FilePickerModelInterface {
    private static final String TAG = FilePickerModel.class.getSimpleName();
    private static final String FILES_PATH = "/files_to_send";

    private ArrayList<String> files;

    public FilePickerModel() {
        this.files = new ArrayList<>();
        String[] filesInDir = InternalStorageUtils.getAllFilesInDir(FILES_PATH);
        if (filesInDir != null) {
            files.clear();
            Collections.addAll(files, filesInDir);
        }
    }

    @Override
    public ArrayList<String> getFilesList() {
        return files;
    }

    @Override
    public void refreshFilesList() {
        String[] filesInDir = InternalStorageUtils.getAllFilesInDir(FILES_PATH);
        if (filesInDir != null) {
            files.clear();
            Collections.addAll(files, filesInDir);
        }
    }

    @Override
    public String getFilePathByPosition(int position) {
        return FILES_PATH + "/" + files.get(position);
    }

}
