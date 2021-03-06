package com.locator_app.locator.view.register;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.locator_app.locator.R;
import com.locator_app.locator.controller.UserController;
import com.locator_app.locator.service.CameraService;
import com.locator_app.locator.util.BitmapHelper;
import com.locator_app.locator.view.LoadingSpinner;
import com.locator_app.locator.view.LocatorHeader;
import com.locator_app.locator.view.UiError;
import com.locator_app.locator.view.home.HomeActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class RegisterProfilePictureActivity extends Activity {

    private static final int SELECT_FILE = 1;

    @Bind(R.id.profilePicture)
    ImageView profilePicture;
    @Bind(R.id.profileNo)
    ImageView profileNo;
    @Bind(R.id.profilePictureText)
    TextView profilePictureText;

    LoadingSpinner loadingSpinner;
    CameraService cameraService;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_picture);
        ButterKnife.bind(this);

        LocatorHeader header = new LocatorHeader(this);
        header.setTitle(R.string.profile_picture);
        header.hideBackIcon();
        header.hideCancelIcon();

        cameraService = new CameraService(this);
        loadingSpinner = new LoadingSpinner(this);

        loadImages();
    }

    @OnClick(R.id.profilePicture)
    public void onProfilePictureClick() {
        selectImage();
    }

    @OnClick(R.id.profileNo)
    public void onProfileNoClick() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void selectImage() {
        String takePhoto = getResources().getString(R.string.take_photo);
        String choosePhoto = getResources().getString(R.string.choose_photo);
        String cancel = getResources().getString(R.string.cancel);
        final CharSequence[] items = { takePhoto, choosePhoto, cancel};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterProfilePictureActivity.this);
        builder.setTitle(getResources().getString(R.string.choose_profile_picture));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(takePhoto)) {
                    cameraService.takePhoto().subscribe(
                            (uri) -> {
                                RegisterProfilePictureActivity.this.uri = uri;
                            },
                            (err) -> {
                            }
                    );
                } else if (items[item].equals(choosePhoto)) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getResources().getString(R.string.select_file)),
                            SELECT_FILE);
                } else if (items[item].equals(cancel)) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CameraService.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                Uri profilePictureUri = this.uri;
                uploadProfilePicture(profilePictureUri);
            } else if (requestCode == SELECT_FILE) {
                Uri profilePictureUri = data.getData();
                uploadProfilePicture(profilePictureUri);
            }
        }
    }

    private void uploadProfilePicture(Uri uri) {
        Bitmap profilePicture = BitmapHelper.get(uri, 500, 500);
        loadingSpinner.showSpinner();
        UserController.getInstance().setProfilePicture(profilePicture)
                .subscribe(
                        (res) -> {
                            Glide.with(this).load(BitmapHelper.getRealPathFromURI(uri))
                                    .asBitmap()
                                    .transform(new CropCircleTransformation(this))
                                    .into(this.profilePicture);
                            profilePictureText.setText(getResources().getString(R.string.your_profile_picture));
                            loadingSpinner.hideSpinner();
                            Glide.with(this).load(R.drawable.continue_white).into(profileNo);
                            reloadUserProfilePictureFromServer();
                        },
                        (error) -> {
                            loadingSpinner.hideSpinner();
                            UiError.showError(this, error, "Da ist was schief gelaufen");
                        }
                );
    }

    private void reloadUserProfilePictureFromServer() {
        // request current user to update the path to the profile image
        if (UserController.getInstance().loggedIn()) {
            UserController.getInstance().checkProtected()
                    .subscribe(
                            (val) -> {},
                            (err) -> {}
                    );
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void loadImages() {
        Glide.with(this).load(R.drawable.profile).into(profilePicture);
        Glide.with(this).load(R.drawable.no).into(profileNo);
    }
}

