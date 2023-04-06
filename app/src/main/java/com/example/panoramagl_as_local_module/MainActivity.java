package com.example.panoramagl_as_local_module;

import android.Manifest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.panoramagl_as_local_module.databinding.ActivityMainBinding;
import com.gitlab.aakumykov.file_dir_helper.PublicFileHelper;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIView;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.PLViewListener;
import com.panoramagl.ios.UITouch;
import com.panoramagl.structs.PLRotation;
import com.panoramagl.utils.PLUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private PLManager mPlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getSupportActionBar().hide();

        MainActivityPermissionsDispatcher.askForReadWithPermissionCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlManager.onResume();
    }

    @Override
    protected void onPause() {
        mPlManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mPlManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPlManager.onTouchEvent(event);
    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void askForRead() {
        displayPanorama();
    }

    private void displayPanorama() {
        mPlManager = new PLManager(this);
        mPlManager.setContentView(mBinding.panoView);

        mPlManager.setListener(new PLViewListener() {
            @Override
            public void onDidMoveTouching(@Nullable PLIView view, @Nullable List<UITouch> touches, @Nullable MotionEvent event) {
                super.onDidMoveTouching(view, touches, event);
                displayYawPitchRoll();
            }

            @Override
            public void onTouchesMoved(@Nullable PLIView view, @Nullable List<UITouch> touches, @Nullable MotionEvent event) {
                super.onTouchesMoved(view, touches, event);
                displayYawPitchRoll();
            }
        });

//        plManager.activateOrientation();
        mPlManager.setAcceleratedTouchScrollingEnabled(false);
        mPlManager.setAccelerometerEnabled(true);
        mPlManager.setResetEnabled(false);

        mPlManager.onCreate();

        PLSphericalPanorama panorama = new PLSphericalPanorama();
        final PLICamera camera = panorama.getCamera();
        camera.lookAt(0.0f, 0.0f);

        PLRotation plRotation = camera.getLookAtRotation();

        camera.setZoomFactor(2f);
        camera.zoomIn(true);


        File file = PublicFileHelper.getFileFromDownloads("pano.jpg");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytesArray = new byte[fileInputStream.available()];
            fileInputStream.read(bytesArray);

            panorama.setImage(new PLImage(PLUtils.getBitmap(bytesArray), false));
            mPlManager.setPanorama(panorama);
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


//        panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.raw.image1024), false));
//        plManager.setPanorama(panorama);
    }

    private void displayYawPitchRoll() {
        PLICamera plCamera = mPlManager.getCamera();
        if (null == plCamera)
            return;

        PLRotation rotation = mPlManager.getCamera().getRotation();

        mBinding.yawView.setText(rotation.yaw+"");
        mBinding.pitchView.setText(rotation.pitch+"");
        mBinding.rollView.setText(rotation.roll+"");
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}