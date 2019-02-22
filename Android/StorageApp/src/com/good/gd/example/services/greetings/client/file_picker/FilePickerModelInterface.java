package com.good.gd.example.services.greetings.client.file_picker;

import java.util.ArrayList;

public interface FilePickerModelInterface {
    ArrayList<String> getFilesList();
    void refreshFilesList();
    String getFilePathByPosition(int position);
}
