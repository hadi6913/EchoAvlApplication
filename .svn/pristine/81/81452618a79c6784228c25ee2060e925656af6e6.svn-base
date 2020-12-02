package com.ar.echoafcavlapplication.Fragments;

import android.content.res.AssetFileDescriptor;
import android.os.AsyncTask;

import com.ar.echoafcavlapplication.Enums.UiBeepType;
import com.ar.echoafcavlapplication.Enums.UiCommandTypeEnum;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChangeFragment extends AsyncTask<Void, Integer, Void> {
    private static Logger log = LoggerFactory.getLogger(ChangeFragment.class);
    private UiCommandTypeEnum operation;
    private UiBeepType beepType;
    private int timeout;
    private MainActivity activity;
    private boolean temporary;
    private UiCommandTypeEnum oldOperation;
    private Map<Integer, String> newMsg;
    private Map<Integer, String> currentMsg;


    public ChangeFragment(UiCommandTypeEnum operation,  UiCommandTypeEnum currentFragmentCommand, Map<Integer, String> newMsg, Map<Integer, String> currentMsg, int timeout, MainActivity mainActivity, UiBeepType beep, boolean temporary) {
        super();
        this.operation = operation;
        this.timeout = timeout;
        this.activity = mainActivity;
        this.beepType = beep;
        this.temporary = temporary;
        this.oldOperation = currentFragmentCommand;
        this.newMsg = newMsg;
        this.currentMsg = currentMsg;
    }

    @Override
    protected void onPreExecute() {
        try {
            activity.setOnThread(true);
            activity.setFrag(operation, newMsg);
        }catch (Exception ex){
            log.error( "ChangeFragment() --> onPreExecute():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    @Override
    protected Void doInBackground(Void... aVoid) {
        try {
            if (timeout == -1 && !beepType.equals(UiBeepType.NO_BEEP)) {
                publishProgress(1);
                return null;
            } else if (timeout == -1)
                return null;
            int count = timeout / 10;
            for (int i = 1; i <= count; i++) {
                if (isCancelled())
                    break;
                publishProgress(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {

                }
            }
            return null;
        }catch (Exception ex){
            log.error( "ChangeFragment() --> doInBackground():" + ex.getMessage());
            log.error(ex.toString());
            return null;
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        try {
            if (values[0] == 1) {
                if (activity.getMediaPlayer().isPlaying()) {
                    activity.getMediaPlayer().stop();
                }
                if(activity.getLoudnessEnhancer().getEnabled())
                    activity.getLoudnessEnhancer().setEnabled(false);
                activity.getLoudnessEnhancer().setEnabled(true);
                // change loudness level here
                activity.getLoudnessEnhancer().setTargetGain(Constants.EXTRA_VOLUME_LEVEL);
                activity.getMediaPlayer().reset();
                AssetFileDescriptor afd = null;
                if (!beepType.equals(UiBeepType.NO_BEEP)) {
                    switch (beepType) {
                        case ERROR:
                            afd = activity.getAssets().openFd("sounds/error.mp3");
                            break;
                        case SUCCESS:
                        case NOTIFY:
                            afd = activity.getAssets().openFd("sounds/success10.mp3");
                            break;
                    }
                    activity.getMediaPlayer().setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    activity.getMediaPlayer().prepare();
                    activity.getMediaPlayer().start();
                }
            }
        } catch (Exception ex) {
            log.error( "ChangeFragment() --> onProgressUpdate():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activity.setOnThread(false);
        try {
            if (temporary) {
                activity.removeAllFrag();
                activity.setFrag(oldOperation, currentMsg);
                activity.setCurrentFragmentTempCommand(null);
            }
            if (operation.equals(UiCommandTypeEnum.DRIVER_STATS))
                ReaderHandler.getInstance().setTransactionCountForUI(0);
        } catch (Exception ex) {
            log.error( "ChangeFragment() --> onPostExecute():" + ex.getMessage());
            log.error(ex.toString());
        }
    }
}
