package com.ar.echoafcavlapplication.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.app.Fragment;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraFragment;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.ar.echoafcavlapplication.Camera.MyCameraConfig;
import com.ar.echoafcavlapplication.Camera.MyHiddenCameraFragment;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.DayMoments;
import com.ar.echoafcavlapplication.Enums.LocalCalendarDateType;
import com.ar.echoafcavlapplication.Models.Card;
import com.ar.echoafcavlapplication.Models.Fare;
import com.ar.echoafcavlapplication.Models.FareClass;
import com.ar.echoafcavlapplication.Models.Line;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Utils.ByteUtils;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.ParameterUtils;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentTap extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentTap.class);
    private TextView lineIdView, lineFareView, welcomeView;
    private ImageView tapImage;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tap_frag, parent, false);
        lineIdView = v.findViewById(R.id.lineIdView);
        lineFareView = v.findViewById(R.id.lineFareView);
        tapImage = v.findViewById(R.id.gifImageView);
        welcomeView = v.findViewById(R.id.welcomeView);
        handler = new Handler(Looper.getMainLooper());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try{
            Long fare = calculateFare();
            if (fare != -1L){
                lineFareView.setText("کرایه خط : "+fare+" ریال");
            }
            lineIdView.setText("خط "+ParameterUtils.getInstance().getLineName());
            showFare();
            //initCamera();

        }catch (Exception ex){
            log.error("FragmentTap > onViewCreated():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showFare(){
        try{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Long fare = calculateFare();
                    if (fare != -1L){
                        lineFareView.setText("کرایه خط : "+fare+" ریال");
                    }
                    lineIdView.setText("خط "+ParameterUtils.getInstance().getLineName());
                    handler.postDelayed(this, 10000);
                }
            }, 10000);
        }catch (Exception ex){
            log.error("FragmentTap > showFare():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void showWelcomeToDriver(String msg){
        try{
            while (tapImage == null)
                Thread.sleep(10);
            tapImage.setVisibility(View.GONE);
            welcomeView.setText(msg);
            welcomeView.setVisibility(View.VISIBLE);
            welcomeView.animate().alpha(1.0f).setDuration(500);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    welcomeView.animate().alpha(0f).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            welcomeView.setText("---");
                            welcomeView.setVisibility(View.GONE);
                            tapImage.setVisibility(View.VISIBLE);
                            tapImage.animate().alpha(1.0f).setDuration(500);
                        }
                    });
                }
            }, 5000);
        }catch (Exception ex){
            log.error("FragmentTap > showWelcomeToDriver():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    private Long calculateFare() {
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            if (StateHandler.getInstance().getLineId() == 0)
                return -1L;
            if (ParameterUtils.getInstance().getLineList() == null)
                return -1L;
            Line myLine = null;
            for (Line line : ParameterUtils.getInstance().getLineList()){
                if (line.getLineCode().equals(StateHandler.getInstance().getLineId())){
                    myLine = line;
                }
            }
            if (myLine == null)
                return -1L;
            if (ParameterUtils.getInstance().getLocalCalendarMap().isEmpty())
                return -1L;
            LocalCalendarDateType myLocalCalDateType;
            if (!ParameterUtils.getInstance().getLocalCalendarMap().containsKey(simpleDateFormat.format(Calendar.getInstance().getTime())))
                myLocalCalDateType = LocalCalendarDateType.WORKDAY;
            else
                myLocalCalDateType = ParameterUtils.getInstance().getLocalCalendarMap().get(simpleDateFormat.format(Calendar.getInstance().getTime()));

            if (ParameterUtils.getInstance().getFareClassList() == null)
                return -1L;

            FareClass myFareClass = null;
            for (FareClass fareClass : ParameterUtils.getInstance().getFareClassList()){
                if (fareClass.getCode().equals(myLine.getFareClassCode())){
                    myFareClass = fareClass;
                }
            }

            if (myFareClass == null)
                return -1L;

            Fare myFare = null;
            // TODO: 7/14/2020 review below
            if (!myFareClass.getFareMap().containsKey(232)) {
                if (!myFareClass.getFareMap().containsKey(1)) {
                    return -1L;
                }else{
                    myFare = myFareClass.getFareMap().get(1);
                }
            }else{
                myFare = myFareClass.getFareMap().get(232);
            }
            DayMoments myDayMoment = isNightMoments();
            if (myDayMoment.equals(DayMoments.UNKNOWN))
                return -1L;
            if (myLocalCalDateType.equals(LocalCalendarDateType.WORKDAY)){
                if (myDayMoment.equals(DayMoments.NIGHT))
                    return myFare.getNightBaseFare();
                else
                    return myFare.getBaseFare();
            }else{
                if (myDayMoment.equals(DayMoments.NIGHT))
                    return myFare.getHolidayNightFare();
                else
                    return myFare.getHolidayBaseFare();
            }
        }catch (Exception ex){
            log.error("FragmentTap > calculateFare():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return -1L;
    }


    private DayMoments isNightMoments(){
        try{
            if (ParameterUtils.getInstance().getNightTimeFrom().equals("") || ParameterUtils.getInstance().getNightTimeTo().equals(""))
                return DayMoments.UNKNOWN;
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            to.add(Calendar.DATE, 1);
            Integer fromHour = Integer.parseInt(ParameterUtils.getInstance().getNightTimeFrom().split(":")[0]);
            Integer fromMin = Integer.parseInt(ParameterUtils.getInstance().getNightTimeFrom().split(":")[1]);
            Integer toHour = Integer.parseInt(ParameterUtils.getInstance().getNightTimeTo().split(":")[0]);
            Integer toMin = Integer.parseInt(ParameterUtils.getInstance().getNightTimeTo().split(":")[1]);
            from.set(Calendar.HOUR_OF_DAY, fromHour);
            from.set(Calendar.MINUTE, fromMin);
            to.set(Calendar.HOUR_OF_DAY, toHour);
            to.set(Calendar.MINUTE, toMin);

            if ((to.getTimeInMillis() > now.getTimeInMillis()) && (now.getTimeInMillis() >= from.getTimeInMillis()))
                return DayMoments.NIGHT;
            return DayMoments.DAY;
        }catch (Exception ex){
            log.error("FragmentTap > isNightMoments():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return DayMoments.UNKNOWN;
    }
    /*private static final int REQ_CODE_CAMERA_PERMISSION = 1253;
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(cameraConfig);
            } else {
                Toast.makeText(getActivity(), "R.string.error_camera_permission_denied", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/


    /*public void initCamera(){
        try{
            //Setting camera configuration
            cameraConfig = new MyCameraConfig()
                    .getBuilder(Utility.getContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .setImageRotation(CameraRotation.ROTATION_180)
                    .build();

            //Check for the camera permission for the runtime
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                //Start camera preview
                startCamera(cameraConfig);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                        REQ_CODE_CAMERA_PERMISSION);
            }
        }catch (Exception ex){
            log.error("FragmentTap > initCamera():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }*/


    /*public void handleDriverImage(boolean _isForStartShift){
        try{
            isForStartShift = _isForStartShift;
            takePicture();
        }catch (Exception ex) {
            log.error("FragmentTap > handleDriverImage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }*/

    /*private MyCameraConfig cameraConfig = null;

    private boolean isForStartShift = false;
    @Override
    public void onImageCapture(@NonNull File imageFile) {
        File mediaFile = null;
        try{
            String timeStamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date());
            File startDir = new File(Constants.startShiftImagesFolder);
            File endDir = new File(Constants.endShiftImagesFolder);
            if (!startDir.isDirectory())
                startDir.mkdirs();
            if (!endDir.isDirectory())
                endDir.mkdirs();

            if (isForStartShift) {
                //startShift
                mediaFile = new File(Constants.startShiftImagesFolder + StateHandler.getInstance().getBusId() + "_" + timeStamp + "_START_" + StateHandler.getInstance().getShiftId() + ".jpg");
            } else {
                //closeShift
                mediaFile = new File(Constants.endShiftImagesFolder + StateHandler.getInstance().getBusId() + "_" + timeStamp + "_END_" + StateHandler.getInstance().getShiftId() + ".jpg");
            }
            if (mediaFile.exists())
                mediaFile.delete();
            mediaFile.createNewFile();
            FileUtils.copyFile(imageFile, mediaFile);
            imageFile.delete();
        }catch (Exception ex){
            log.error("ReaderHandler > onImageCapture():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(Utility.getContext(), "R.string.error_cannot_open", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(Utility.getContext(), "R.string.error_cannot_write", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(Utility.getContext(), "R.string.error_cannot_get_permission", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(Utility.getContext());
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(Utility.getContext(), "R.string.error_not_having_camera", Toast.LENGTH_LONG).show();
                break;
        }
    }*/
}

