package com.ar.echoafcavlapplication.Camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.app.Fragment;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.ar.echoafcavlapplication.Utils.Utility;

import java.io.File;

public class MyHiddenCameraFragment extends Fragment implements MyCameraCallbacks {
    private MyCameraConfig mCachedCameraConfig;
    private MyCameraPreview mCameraPreview;

    /**
     * Start the hidden camera. Make sure that you check for the runtime permissions before you start
     * the camera.
     *
     * @param cameraConfig camera configuration {@link CameraConfig}
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    protected void startCamera(MyCameraConfig cameraConfig) {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) { //check if the camera permission is available

            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
        } else if (cameraConfig.getFacing() == CameraFacing.FRONT_FACING_CAMERA
                && !HiddenCameraUtils.isFrontCameraAvailable(getActivity())) {   //Check if for the front camera

            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
        } else {
            //Add the camera preview surface to the root of the activity view.
            if (mCameraPreview == null) mCameraPreview = addPreView();
            mCameraPreview.startCameraInternal(cameraConfig);
            mCachedCameraConfig = cameraConfig;
        }
    }

    /**
     * Call this method to capture the image using the camera you initialized. Don't forget to
     * initialize the camera using {@link #startCamera(MyCameraConfig)} before using this function.
     */
    protected void takePicture() {
        if (mCameraPreview != null) {
            if (mCameraPreview.isSafeToTakePictureInternal()) {
                mCameraPreview.takePictureInternal();
            }
        } else {
            throw new RuntimeException("Background camera not initialized. Call startCamera() to initialize the camera.");
        }
    }

    /**
     * Stop and release the camera forcefully.
     */
    protected void stopCamera() {
        mCachedCameraConfig = null;    //Remove config.
        if (mCameraPreview != null) mCameraPreview.stopPreviewAndFreeCamera();
    }

    /**
     * Add camera preview to the root of the activity layout.
     *
     * @return {@link MyCameraPreview} that was added to the view.
     */
    private MyCameraPreview addPreView() {
        //create fake camera view
        MyCameraPreview cameraSourceCameraPreview = new MyCameraPreview(getActivity(), this);
        cameraSourceCameraPreview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View view = ((ViewGroup) getActivity().getWindow().getDecorView().getRootView()).getChildAt(0);

        if (view instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) view;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, 1);
            linearLayout.addView(cameraSourceCameraPreview, params);
        } else if (view instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) view;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            relativeLayout.addView(cameraSourceCameraPreview, params);
        } else if (view instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) view;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(1, 1);
            frameLayout.addView(cameraSourceCameraPreview, params);
        } else {
            throw new RuntimeException("Root view of the activity/fragment cannot be other than Linear/Relative or frame layout");
        }

        return cameraSourceCameraPreview;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCachedCameraConfig != null) {
            //noinspection MissingPermission
            if (ActivityCompat.checkSelfPermission(Utility.getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            startCamera(mCachedCameraConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCameraPreview != null) mCameraPreview.stopPreviewAndFreeCamera();
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {

    }

    @Override
    public void onCameraError(int errorCode) {

    }
}
