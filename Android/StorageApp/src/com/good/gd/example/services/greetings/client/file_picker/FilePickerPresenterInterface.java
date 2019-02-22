package com.good.gd.example.services.greetings.client.file_picker;

public interface FilePickerPresenterInterface {
    void initRecyclerView();
    void handleOnFileItemClick(int position);
    void handleOnAddFileButtonClick();
    void handleActivityOnResume();
}
