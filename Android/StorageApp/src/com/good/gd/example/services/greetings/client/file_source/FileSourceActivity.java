package com.good.gd.example.services.greetings.client.file_source;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.good.gd.GDAndroid;
import com.good.gd.GDStateListener;
import com.good.gd.example.services.greetings.client.R;

import java.util.Map;

public class FileSourceActivity extends AppCompatActivity implements GDStateListener, FileSourseViewInterface {

    private static final String TAG = FileSourceActivity.class.getSimpleName();
    private final int REQUEST_CAMERA_PERMISSION = 1;
    private final int REQUEST_CAMERA = 2;
    private final int REQUEST_GALLERY_PHOTO_PICKER = 3;

    FileSourcePresenterInterface fileSourcePresenterInterface;

    private Button makePhotoBtn, selectFromGalleryPhotoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GDAndroid.getInstance().activityInit(this);

        setContentView(R.layout.activity_file_source);

        fileSourcePresenterInterface = new FileSourcePresenter(this, getPreferences(Context.MODE_PRIVATE));

        makePhotoBtn = findViewById(R.id.make_photo_btn);
        makePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileSourcePresenterInterface.handleMakePhotoButtonClick();
            }
        });

        selectFromGalleryPhotoBtn = findViewById(R.id.select_from_gallery_photo_btn);
        selectFromGalleryPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileSourcePresenterInterface.handleSelectFromGalleryPhotoButtonClick();
            }
        });
    }

    @Override
    public void startCameraActivity() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    public void showGalleryPickerDialog() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_GALLERY_PHOTO_PICKER);
    }

    @Override
    public boolean checkCameraPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch(requestCode) {
                case REQUEST_CAMERA:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    fileSourcePresenterInterface.handlePhotoMade(photo);
                    Toast.makeText(this, "photo success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case REQUEST_GALLERY_PHOTO_PICKER:
                    Uri selectedImage = data.getData();
                    fileSourcePresenterInterface.handlePhotoPicked(getApplicationContext(), selectedImage);
                    Toast.makeText(this, "photo success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "permission " + "REQUEST_CAMERA_PERMISSION" + " granted", Toast.LENGTH_LONG).show();
                        fileSourcePresenterInterface.handleMakePhotoButtonClick();
                    } else {
                        Toast.makeText(this, "permission " + "REQUEST_CAMERA_PERMISSION" + " denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onAuthorized() {
        Log.i(TAG, "onAuthorized()");
    }

    @Override
    public void onLocked() {
        Log.i(TAG, "onLocked()");
    }

    @Override
    public void onWiped() {
        Log.i(TAG, "onWiped()");
    }

    @Override
    public void onUpdateConfig(Map<String, Object> settings) {
        Log.i(TAG, "onUpdateConfig()");
    }

    @Override
    public void onUpdatePolicy(Map<String, Object> policyValues) {
        Log.i(TAG, "onUpdatePolicy()");
    }

    @Override
    public void onUpdateServices() {
        Log.i(TAG, "onUpdateServices()");
    }

    @Override
    public void onUpdateEntitlements() {
        Log.i(TAG, "onUpdateEntitlements()");
    }
}
