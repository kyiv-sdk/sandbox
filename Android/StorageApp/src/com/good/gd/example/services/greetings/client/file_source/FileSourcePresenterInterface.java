package com.good.gd.example.services.greetings.client.file_source;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public interface FileSourcePresenterInterface {
    void handleMakePhotoButtonClick();
    void handleSelectFromGalleryPhotoButtonClick();
    void handlePhotoPicked(Context context, Uri uri);
    void handlePhotoMade(Bitmap bitmap);
}
