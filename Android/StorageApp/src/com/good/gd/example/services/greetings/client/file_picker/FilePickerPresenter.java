package com.good.gd.example.services.greetings.client.file_picker;

import android.util.Log;

import com.example.internal_storage_utils.InternalStorageUtils;
import com.good.gd.icc.GDICCForegroundOptions;
import com.good.gd.icc.GDServiceClient;
import com.good.gd.icc.GDServiceException;

public class FilePickerPresenter implements FilePickerPresenterInterface {
    private static final String TAG = FilePickerPresenter.class.getSimpleName();
    private static final String CHAT_APP_ID = "com.good.mygoodsample";
    private FilePickerViewInterface filePickerViewInterface;
    private FilePickerModelInterface filePickerModelInterface;

    public FilePickerPresenter(FilePickerViewInterface filePickerViewInterface, FilePickerModelInterface filePickerModelInterface) {
        this.filePickerViewInterface = filePickerViewInterface;
        this.filePickerModelInterface = filePickerModelInterface;

        filePickerModelInterface.refreshFilesList();
    }

    @Override
    public void initRecyclerView() {
        filePickerModelInterface.refreshFilesList();
        filePickerViewInterface.initRecyclerView(filePickerModelInterface.getFilesList());
    }

    @Override
    public void handleOnFileItemClick(int position) {
        try {
            String filesToSend[] = new String[1];
            filesToSend[0] = filePickerModelInterface.getFilePathByPosition(position);
            if (InternalStorageUtils.exists(filesToSend[0])){
                String requestID = GDServiceClient.sendTo(CHAT_APP_ID,
                        "sendPhoto", "1.0.0", "handlePhotoReceived", null,
                        filesToSend, GDICCForegroundOptions.NoForegroundPreference);
                Log.d(TAG, "sendPhoto requestID=" + requestID);
            } else {
                Log.e(TAG, "File to sent does not exists");
            }
        } catch (GDServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleOnAddFileButtonClick() {
        filePickerViewInterface.openFileSourceActivity();
    }

    @Override
    public void handleActivityOnResume() {
        filePickerViewInterface.refreshData();
    }
}
