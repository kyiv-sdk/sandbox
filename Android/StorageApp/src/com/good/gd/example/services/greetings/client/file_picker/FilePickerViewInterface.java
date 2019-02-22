package com.good.gd.example.services.greetings.client.file_picker;

import java.util.ArrayList;

public interface FilePickerViewInterface {
    void refreshData();
    void openFileSourceActivity();
    void initRecyclerView(ArrayList<String> files);
}
