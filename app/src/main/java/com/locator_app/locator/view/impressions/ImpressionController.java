package com.locator_app.locator.view.impressions;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.locator_app.locator.controller.LocationController;
import com.locator_app.locator.model.impressions.AbstractImpression;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ImpressionController extends Activity {

    public static final int IMAGE = 100;
    public static final int VIDEO = 200;
    private String locationId;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationId = getIntent().getStringExtra("locationId");
        String type = getIntent().getStringExtra("type");
        if (type.equals("image")) {
            createImageImpression();
        } else if (type.equals("video")) {
            createVideoImpression();
        } else {
            finish();
        }
    }

    private void createImageImpression() {
        ContentValues values = new ContentValues();
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE);
    }

    private void createVideoImpression() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
        startActivityForResult(intent, VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (requestCode == IMAGE) {
            doUploadImage();
        } else if (requestCode == VIDEO) {
            doUploadVideo();
        }
        finish();
    }

    private void doUploadImage() {
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            LocationController.getInstance().createImageImpression(locationId, imageBitmap)
                    .subscribe(
                            (val) -> {
                                notify(AbstractImpression.ImpressionType.IMAGE);
                            },
                            (err) -> {
                                notifyError(AbstractImpression.ImpressionType.IMAGE,
                                        new Throwable("Dein Bild konnte leider nicht hochgeladen werden"));
                            }
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doUploadVideo() {

    }


    private static Set<ImpressionObserver> observers = new HashSet<>();
    public static void addImpressionObserver(ImpressionObserver obs) {
        observers.add(obs);
    }

    public static void removeImpressionObserver(ImpressionObserver obs) {
        observers.remove(obs);
    }

    private static void notify(AbstractImpression.ImpressionType type) {
        for (ImpressionObserver obs: observers) {
            obs.onImpressionCreated(type);
        }
    }

    private static void notifyError(AbstractImpression.ImpressionType type, Throwable error) {
        for (ImpressionObserver obs: observers) {
            obs.onImpressionCreationFailed(type, error);
        }
    }
}
