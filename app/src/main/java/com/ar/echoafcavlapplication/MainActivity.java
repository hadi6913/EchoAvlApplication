package com.ar.echoafcavlapplication;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ar.echoafcavlapplication.Data.DataFileUtility;
import com.ar.echoafcavlapplication.Data.LastShiftHandler;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.ShiftDataTextFileUtility;
import com.ar.echoafcavlapplication.Data.ShiftStatusFileManager;
import com.ar.echoafcavlapplication.Data.ShiftStatusTemporaryFileManager;
import com.ar.echoafcavlapplication.Data.StatFileManager;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Data.StuffActivityFileManager;
import com.ar.echoafcavlapplication.Data.SummaryReportFileHandler;
import com.ar.echoafcavlapplication.Enums.BrightnessType;
import com.ar.echoafcavlapplication.Enums.CardValidationResults;
import com.ar.echoafcavlapplication.Enums.QomCardType;
import com.ar.echoafcavlapplication.Enums.Role;
import com.ar.echoafcavlapplication.Enums.StuffActivityType;
import com.ar.echoafcavlapplication.Enums.UiBeepType;
import com.ar.echoafcavlapplication.Enums.UiCommandTypeEnum;
import com.ar.echoafcavlapplication.Fragments.ChangeFragment;
import com.ar.echoafcavlapplication.Fragments.FragmentHardwareTest;
import com.ar.echoafcavlapplication.Fragments.FragmentLoginToMaintenance;
import com.ar.echoafcavlapplication.Fragments.FragmentMaintenance;
import com.ar.echoafcavlapplication.Fragments.FragmentSummaryReport;
import com.ar.echoafcavlapplication.Fragments.FragmentTap;
import com.ar.echoafcavlapplication.Fragments.FragmentUtility;
import com.ar.echoafcavlapplication.Models.LastShiftDataHelper;
import com.ar.echoafcavlapplication.Models.ProcessCardResult;
import com.ar.echoafcavlapplication.Services.LocationHandler;
import com.ar.echoafcavlapplication.Services.LogHandler;
import com.ar.echoafcavlapplication.Services.OutOfServiceHandler;
import com.ar.echoafcavlapplication.Services.ParameterHandler;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Services.SleepHandler;
import com.ar.echoafcavlapplication.Services.TransactionsFileHandler;
import com.ar.echoafcavlapplication.System.FatalExceptionHandler;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.ParameterUtils;
import com.ar.echoafcavlapplication.Utils.Utility;
import com.google.android.material.snackbar.Snackbar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saman.zamani.persiandate.PersianDate;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements ReaderHandler.Callbacks, OutOfServiceHandler.Callbacks, LocationHandler.Callbacks, SleepHandler.Callbacks, ParameterHandler.Callbacks, TransactionsFileHandler.Callbacks, LogHandler.Callbacks {
    private static Logger log = LoggerFactory.getLogger(ReaderHandler.class);

    private Intent readerHandlerIntent;
    private ReaderHandler readerHandler;
    private Intent outOfServiceIntent;
    private OutOfServiceHandler outOfServiceHandler;
    private Intent locationHandlerIntent;
    private Intent sleepHandlerIntent;
    private SleepHandler sleepHandler;
    private LocationHandler locationHandler;
    private Intent parameterHandlerIntent;
    private ParameterHandler parameterHandler;
    private Intent transactionFileHandlerIntent;
    private TransactionsFileHandler transactionFileHandler;
    private Intent logHandlerIntent;
    private LogHandler logHandler;
    private List<WeakReference<Fragment>> fragList = new ArrayList<>();
    private boolean onThread = false;
    private ChangeFragment myTask = null;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private LoudnessEnhancer loudnessEnhancer = new LoudnessEnhancer(getMediaPlayer().getAudioSessionId());
    private UiCommandTypeEnum currentFragmentCommand = null;
    private UiCommandTypeEnum currentFragmentTempCommand = null;
    private Map<Integer, String> currentMsg = null;
    private TextView gpsTxtView, appVersion, lineId, busId, paramVersion, transactionCountTxt;
    private ImageView gpsImageView;
    private ImageView shiftStatus, netStatus;
    private boolean paramLoadStatus = false;
    private PersianDate startShiftDateTime = null;

    private int brightness;
    private ContentResolver cResolver;
    private Window window;
    private int MAX_BRIGHT = 255;
    private int HALF_BRIGHT = 100;
    private int MIN_BRIGHT = 50;
    private Snackbar mainSnackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new FatalExceptionHandler(this));
        if (getIntent().getBooleanExtra("crashed", false)) {
            Toast.makeText(this, "راه اندازی مجدد", Toast.LENGTH_SHORT).show();
        }
        setUiComponent();
        new initialTask().execute();
    }

    @Override
    public BrightnessType getBrightness() {
        if (brightness == MAX_BRIGHT)
            return BrightnessType.FULL;
        if (brightness == HALF_BRIGHT)
            return BrightnessType.MID;
        if (brightness == MIN_BRIGHT)
            return BrightnessType.MIN;
        return null;
    }

    @Override
    public void setBrightness(BrightnessType type) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (type) {
                        case FULL:
                            if (ParameterUtils.getInstance().getBrightness().equals(0)) {
                                brightness = MAX_BRIGHT;
                            } else {
                                brightness = (ParameterUtils.getInstance().getBrightness() * 255) / 100;
                            }
                            break;
                        case MID:
                            brightness = HALF_BRIGHT;
                            break;
                        case MIN:
                            brightness = MIN_BRIGHT;
                            break;
                    }
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                    WindowManager.LayoutParams layoutParts = window.getAttributes();
                    layoutParts.screenBrightness = brightness / (float) 255;
                    window.setAttributes(layoutParts);
                }
            });
        } catch (Exception ex) {
            log.error("MainActivity --> setBrightness():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void setBrightnessToManual() {
        window = getWindow();
        try {
            cResolver = getContentResolver();
            Settings.System.putInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception ex) {
            log.error("MainActivity --> setBrightnessToManual():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopServices();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    public void showToast(String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void changeTemporaryMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg) {
        UiBeepType beepType = UiBeepType.NO_BEEP;
        switch (operation) {
            case SUCCESS:
                beepType = UiBeepType.SUCCESS;
                break;
            case FAILED:
                beepType = UiBeepType.ERROR;
                break;
            case OUT_OF_SERVICE:
                beepType = UiBeepType.ERROR;
                break;
        }
        changeMessageUi(operation, newMsg, 1000, beepType, true);
    }

    @Override
    public void changeFixedMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg, UiBeepType beep) {
        UiBeepType beepType = UiBeepType.NO_BEEP;
        switch (operation) {
            case SUCCESS:
                beepType = UiBeepType.SUCCESS;
                break;
            case FAILED:
                beepType = UiBeepType.ERROR;
                break;
            case OUT_OF_SERVICE:
                beepType = UiBeepType.ERROR;
                break;
            case CLOSE_SHIFT:
                beepType = UiBeepType.ERROR;
                break;
            case TAP_CARD:
                beepType = UiBeepType.SUCCESS;
                break;
            case MAINTENANCE:
                beepType = UiBeepType.SUCCESS;
                break;
        }
        changeMessageUi(operation, newMsg, -1, beepType, false);
    }

    @Override
    public void disableLock() {
        disableLockScreen();
    }

    public void disableLockScreen() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                    setUiComponent();
                }
            });
        } catch (Exception ex) {
            log.error("MainActivity --> disableLockScreen():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public boolean isInitialTaskCheckedShiftStatus() {
        return initialTaskCheckedShiftStatus;
    }

    private boolean initialTaskCheckedShiftStatus = false;

    public class initialTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            LastShiftHandler.getInstance();
            setBrightnessToManual();
            setBrightness(BrightnessType.FULL);
            turnGPSOn();
            initialUIComponents();
            Utility.setContext(getApplicationContext());
            Utility.setWindow(getWindow());
            Utility.initialFolders();
            LicenceFileHandler.getInstance();
            ShiftStatusTemporaryFileManager.getInstance();
            StuffActivityFileManager.getInstance();
            SummaryReportFileHandler.getInstance();
            DataFileUtility.getInstance();
            //ShiftDataFileUtility.getInstance();
            ShiftDataTextFileUtility.getInstance();
            startServices();
            setDateAndTime(1000, 1000);
            Utility.setSystemTimeZone(getApplicationContext(), Constants.DEFAULT_TIME_ZONE);
            OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_WAIT_FOR_INITIAL, 1);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                changeAppVersion();
                changeLineAndBusID();
                paramLoadStatus = ParameterUtils.getInstance().loadParameters();
                ParameterUtils.getInstance().loadOperators();
                ParameterUtils.getInstance().getNextParamVersionAndLoadDateFromVersionFile(new File(Constants.paramFolder + "version.sis"));
                ParameterUtils.getInstance().dailyCheckerForLocalParamUpdate();
                Thread.sleep(5000);
                return null;
            } catch (Exception ex) {
                log.error("initialTask --> doInBackground():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getGpsStatus();
            OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_WAIT_FOR_INITIAL);
            if (paramLoadStatus) {
                changeLineId(ParameterUtils.getInstance().getLineName());
                changeParamVersion(ParameterUtils.getInstance().getCurrentParamVersion());
            } else {
                changeParamVersion("نامشخص");
            }
            if (StateHandler.getInstance().getBusId() == 0) {
                OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_INVALID_BUS_ID, 1);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        LastShiftDataHelper lastShiftData = LastShiftHandler.getInstance().getMainShiftData();
                        if (!lastShiftData.getCardSerial().equals(-1L) && !lastShiftData.getCardSerial().equals(0L)) {
                            //we should resume previous shift.
                            ReaderHandler.getInstance().setCurrentCardSerial(lastShiftData.getCardSerial());
                            StateHandler.getInstance().getShiftId();
                            openShift(lastShiftData.getCardSerial(), "ادامه شیفت قبلی\nخوش آمدید", true);
                            log.error("shiftC: " + lastShiftData.getValidationCount());
                            ReaderHandler.getInstance().setTransactionCountForUI(lastShiftData.getValidationCount());
                            startShiftDateTime = new PersianDate(lastShiftData.getStartShiftDate());
                        }
                    } catch (Exception ex) {
                        log.error("MainActivity > initialTask > resumeShiftChecker():" + ex.getMessage());
                        StringWriter errors = new StringWriter();
                        ex.printStackTrace(new PrintWriter(errors));
                        log.error(errors.toString());
                    }
                    initialTaskCheckedShiftStatus = true;
                }
            }, 2000);
        }
    }

    @Override
    public void resumeShiftAfterOutOfService(LastShiftDataHelper lastShiftData) {
        try {
            if (!initialTaskCheckedShiftStatus)
                return;
            //we should resume previous shift.
            ReaderHandler.getInstance().setCurrentCardSerial(lastShiftData.getCardSerial());
            StateHandler.getInstance().getShiftId();
            openShift(lastShiftData.getCardSerial(), "ادامه شیفت قبلی\nخوش آمدید", true);
            ReaderHandler.getInstance().setTransactionCountForUI(lastShiftData.getValidationCount());
            startShiftDateTime = new PersianDate(lastShiftData.getStartShiftDate());
        } catch (Exception ex) {
            log.error("MainActivity > resumeShiftAfterOutOfService():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public UiCommandTypeEnum getCurrentUiPage() {
        return currentFragmentCommand;
    }

    private void initialUIComponents() {
        try {
            gpsImageView = findViewById(R.id.gpsImg);
            gpsTxtView = findViewById(R.id.gpsStatusTxt);
            appVersion = findViewById(R.id.appVersion);
            paramVersion = findViewById(R.id.paramVersion);
            transactionCountTxt = findViewById(R.id.transactionCountTxt);
            lineId = findViewById(R.id.lineIDTxtView);
            busId = findViewById(R.id.busIDTxtView);
            shiftStatus = findViewById(R.id.shiftStatus);
            netStatus = findViewById(R.id.netStatus);
        } catch (Exception ex) {
            log.error("MainActivity > initialUIComponents():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void startServices() {
        try {
            readerHandlerIntent = new Intent(getApplicationContext(), ReaderHandler.class);
            startService(readerHandlerIntent);
            getApplicationContext().bindService(readerHandlerIntent, readerHandlerConnection, Context.BIND_AUTO_CREATE);


            outOfServiceIntent = new Intent(getApplicationContext(), OutOfServiceHandler.class);
            startService(outOfServiceIntent);
            getApplicationContext().bindService(outOfServiceIntent, outOfServiceConnection, Context.BIND_AUTO_CREATE);
            locationHandlerIntent = new Intent(getApplicationContext(), LocationHandler.class);
            startService(locationHandlerIntent);
            getApplicationContext().bindService(locationHandlerIntent, locationHandlerConnection, Context.BIND_AUTO_CREATE);
            sleepHandlerIntent = new Intent(getApplicationContext(), SleepHandler.class);
            startService(sleepHandlerIntent);
            getApplicationContext().bindService(sleepHandlerIntent, sleepHandlerConnection, Context.BIND_AUTO_CREATE);
            parameterHandlerIntent = new Intent(getApplicationContext(), ParameterHandler.class);
            startService(parameterHandlerIntent);
            getApplicationContext().bindService(parameterHandlerIntent, parameterHandlerConnection, Context.BIND_AUTO_CREATE);
            transactionFileHandlerIntent = new Intent(getApplicationContext(), TransactionsFileHandler.class);
            startService(transactionFileHandlerIntent);
            getApplicationContext().bindService(transactionFileHandlerIntent, transactionFileHandlerConnection, Context.BIND_AUTO_CREATE);
            logHandlerIntent = new Intent(getApplicationContext(), LogHandler.class);
            startService(logHandlerIntent);
            getApplicationContext().bindService(logHandlerIntent, logHandlerConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception ex) {
            log.error("MainActivity > startServices():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void stopServicesForLineAdmin() {
        try {
            getApplicationContext().unbindService(outOfServiceConnection);
            stopService(outOfServiceIntent);
            getApplicationContext().unbindService(sleepHandlerConnection);
            stopService(sleepHandlerIntent);
            getApplicationContext().unbindService(parameterHandlerConnection);
            stopService(parameterHandlerIntent);
            getApplicationContext().unbindService(transactionFileHandlerConnection);
            stopService(transactionFileHandlerIntent);
            getApplicationContext().unbindService(logHandlerConnection);
            stopService(logHandlerIntent);
        } catch (Exception ex) {
            log.error("MainActivity > stopServicesForOperator():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void stopServicesForOperator() {
        try {
            getApplicationContext().unbindService(outOfServiceConnection);
            stopService(outOfServiceIntent);
            getApplicationContext().unbindService(locationHandlerConnection);
            stopService(locationHandlerIntent);
            getApplicationContext().unbindService(sleepHandlerConnection);
            stopService(sleepHandlerIntent);
            getApplicationContext().unbindService(parameterHandlerConnection);
            stopService(parameterHandlerIntent);
            getApplicationContext().unbindService(transactionFileHandlerConnection);
            stopService(transactionFileHandlerIntent);
            getApplicationContext().unbindService(logHandlerConnection);
            stopService(logHandlerIntent);
        } catch (Exception ex) {
            log.error("MainActivity > stopServicesForOperator():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void stopServices() {
        try {
            setBrightness(BrightnessType.FULL);
            /*getApplicationContext().unbindService(readerHandlerConnection);
            stopService(readerHandlerIntent);

            getApplicationContext().unbindService(outOfServiceConnection);
            stopService(outOfServiceIntent);

            getApplicationContext().unbindService(locationHandlerConnection);
            stopService(locationHandlerIntent);

            getApplicationContext().unbindService(sleepHandlerConnection);
            stopService(sleepHandlerIntent);

            getApplicationContext().unbindService(parameterHandlerConnection);
            stopService(parameterHandlerIntent);

            getApplicationContext().unbindService(transactionFileHandlerConnection);
            stopService(transactionFileHandlerIntent);

            getApplicationContext().unbindService(logHandlerConnection);
            stopService(logHandlerIntent);*/
            //Try to stop Reader handler
            if (ReaderHandler.getInstance() != null)
                ReaderHandler.getInstance().stopSelf();
            //Try to stop LocationHandler
            if (LocationHandler.getInstance() != null)
                LocationHandler.getInstance().stopSelf();
            //Try to stop ParameterHandler
            if (ParameterHandler.getInstance() != null)
                ParameterHandler.getInstance().stopSelf();
            //Try to stop SleepHandler
            if (SleepHandler.getInstance() != null)
                SleepHandler.getInstance().stopSelf();
            //Try to stop TransactionsFileHandler
            if (TransactionsFileHandler.getInstance() != null)
                TransactionsFileHandler.getInstance().stopSelf();
            //Try to stop OutOfServiceHandler
            if (OutOfServiceHandler.getInstance() != null)
                OutOfServiceHandler.getInstance().stopSelf();
            //Try to stop LogHandler
            if (LogHandler.getInstance() != null)
                LogHandler.getInstance().stopSelf();
        } catch (Exception ex) {
            log.error("MainActivity > stopServices():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void stopServicesForMaintenance() {
        try {
            getApplicationContext().unbindService(readerHandlerConnection);
            stopService(readerHandlerIntent);
            getApplicationContext().unbindService(outOfServiceConnection);
            stopService(outOfServiceIntent);
            getApplicationContext().unbindService(sleepHandlerConnection);
            stopService(sleepHandlerIntent);
        } catch (Exception ex) {
            log.error("MainActivity > stopServices():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void changeNetStatus(boolean isOnline) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isOnline)
                    netStatus.setImageResource(R.drawable.connect);
                else
                    netStatus.setImageResource(R.drawable.disconnect);
            }
        });
    }

    @Override
    public void setTransactionCountOnUI(String count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                transactionCountTxt.setText(count);
            }
        });
    }

    private Long lastCardUsedForOpenShift = 0l;

    @Override
    public void openShift(Long cardSerial, String welcomeMsg, boolean resume) {
        try {
            LastShiftDataHelper lastShiftData = LastShiftHandler.getInstance().getMainShiftData();
            if (!lastShiftData.getCardSerial().equals(-1L) && !lastShiftData.getCardSerial().equals(0L) && !resume) {
                return;
            }
            if (OutOfServiceHandler.isOutOfService) {
                return;
            }
            lastCardUsedForOpenShift = cardSerial;
            startShiftDateTime = new PersianDate();
            if (currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE) || currentFragmentCommand.equals(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "خطا، لطفا ابتدا از صفحه نگهداری خارج شوید", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            StateHandler.getInstance().setShiftOpen(true);
            StatFileManager.getInstance().newFile(false);
            // TODO: 8/19/2020 added recently
            ShiftStatusFileManager.getInstance().newFile();
            if (!resume) {
                SummaryReportFileHandler.getInstance().writeFirstPartIntoReport(StateHandler.getInstance().getShiftId(), new Date());
                ReaderHandler.getInstance().setTransactionCountForUI(0);
                ShiftStatusTemporaryFileManager.getInstance().writeNewFile();
            }
            changeFixedMessageUi(UiCommandTypeEnum.TAP_CARD, null, UiBeepType.SUCCESS);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shiftStatus.setImageResource(R.drawable.unlock44);
                }
            });
            OutOfServiceHandler.startValidation();
            if (currentFragmentCommand.equals(UiCommandTypeEnum.TAP_CARD) && fragmentTap != null) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentTap.showWelcomeToDriver(welcomeMsg);
                    }
                }, 500);
                /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragmentTap.handleDriverImage(true);
                    }
                }, 500);*/
            }
            LastShiftHandler.getInstance().writeShiftData(cardSerial, 0, new Date());
        } catch (Exception ex) {
            log.error("MainActivity > openShift():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public boolean isFromLineAdmin = false;
    public boolean isFromOperator = false;

    public void goBackFromRegisterPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.UPLOAD_DOWNLOAD_PAGE) && !currentFragmentCommand.equals(UiCommandTypeEnum.REGISTER_PAGE))
                return;
            Map<Integer, String> msg = new HashMap<>();
            msg.put(1, operatorCode);
            if (isFromLineAdmin && !isFromOperator)
                changeFixedMessageUi(UiCommandTypeEnum.LINE_ADMIN_PAGE, msg, UiBeepType.NO_BEEP);
            else if (!isFromLineAdmin && isFromOperator)
                changeFixedMessageUi(UiCommandTypeEnum.OPERATOR_PAGE, msg, UiBeepType.NO_BEEP);
        } catch (Exception ex) {
            log.error("MainActivity > goBackFromRegisterPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void turnGPSOn() {
        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (!provider.contains("gps")) { //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception ex) {
            log.error("MainActivity > turnGPSOn():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void turnGPSOff() {
        try {
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider.contains("gps")) { //if gps is enabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                sendBroadcast(poke);
            }
        } catch (Exception ex) {
            log.error("MainActivity > turnGPSOn():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void openMaintenance() {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE)) {
                stopServices();
                setBrightness(BrightnessType.FULL);
                changeFixedMessageUi(UiCommandTypeEnum.MAINTENANCE, null, UiBeepType.SUCCESS);
            }
        } catch (Exception ex) {
            log.error("MainActivity > openMaintenance():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void exitFromLineAdminPage(View view) {
        StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.EXIT_ADMIN_PAGE);
        stopServices();
        restart();
    }

    public void exitFromOperatorPage(View view) {
        StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.EXIT_OPERATOR_PAGE);
        stopServices();
        restart();
    }

    public void exitFromDriverPage(View view) {
        StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.EXIT_DRIVER_PAGE);
        stopServices();
        restart();
    }

    public void openMaintenance(View view) {
        openMaintenance();
        StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_MAINTENANCE);
    }

    public void startShiftFromLineAdminPage(View view) {
        try {
            if (!OutOfServiceHandler.isOutOfService && !StateHandler.getInstance().isShiftOpen() && initialTaskCheckedShiftStatus) {
                String welcomeMsg = "خوش آمدید";
                openShift(ReaderHandler.getInstance().getStaffID(), welcomeMsg, false);
                StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_SHIFT);
            }
        } catch (Exception ex) {
            log.error("MainActivity > startShiftFromLineAdminPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void startShiftFromDriverPage(View view) {
        try {
            if (!OutOfServiceHandler.isOutOfService && !StateHandler.getInstance().isShiftOpen() && initialTaskCheckedShiftStatus) {
                openShift(ReaderHandler.getInstance().getStaffID(), welStr, false);
                StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_SHIFT);
            }
        } catch (Exception ex) {
            log.error("MainActivity > startShiftFromDriverPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void goToSummaryReportPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.LINE_ADMIN_PAGE) && !currentFragmentCommand.equals(UiCommandTypeEnum.DRIVER_PAGE))
                return;
            Map<Integer, String> msg = new HashMap<>();
            OutOfServiceHandler.stopValidation();
            changeFixedMessageUi(UiCommandTypeEnum.SUMMARY_REPORT, msg, UiBeepType.SUCCESS);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_SUMMARY_REPORT);
        } catch (Exception ex) {
            log.error("MainActivity > goToSummaryReportPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void backToLineAdminPage(View view) {
        if (currentFragmentCommand.equals(UiCommandTypeEnum.SUMMARY_REPORT) || currentFragmentCommand.equals(UiCommandTypeEnum.HARDWARE_TEST)) {
            gotoLineAdminPage(null, adminCode);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_ADMIN_LINE_PAGE);
        }
    }

    public void backToDriverPage() {
        if (currentFragmentCommand.equals(UiCommandTypeEnum.SUMMARY_REPORT)) {
            gotoDriverPage(null, welStr.split("\n")[0] + " خوش آمدید");
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), false, StuffActivityType.ENTER_DRIVER_PAGE);
        }
    }

    private Role currentRole = Role.UNKNOWN;

    public void backFromSummaryPage(View view) {
        try {
            if (currentRole.equals(Role.DRIVER)) {
                backToDriverPage();
            } else if (currentRole.equals(Role.LINE_ADMIN)) {
                backToLineAdminPage(view);
            }
        } catch (Exception ex) {
            log.error("MainActivity > backFromSummaryPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void goToHardwareTestPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.LINE_ADMIN_PAGE))
                return;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            changeFixedMessageUi(UiCommandTypeEnum.HARDWARE_TEST, msg, UiBeepType.SUCCESS);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_HARDWARE_TEST);
        } catch (Exception ex) {
            log.error("MainActivity > goToHardwareTestPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void goToUploadDownloadPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.OPERATOR_PAGE))
                return;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            changeFixedMessageUi(UiCommandTypeEnum.UPLOAD_DOWNLOAD_PAGE, msg, UiBeepType.NO_BEEP);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.OPEN_HARDWARE_TEST);
        } catch (Exception ex) {
            log.error("MainActivity > goToUploadDownloadPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void goToRegisterPageFromOperatorPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.OPERATOR_PAGE) && !currentFragmentCommand.equals(UiCommandTypeEnum.LINE_ADMIN_PAGE))
                return;
            isFromLineAdmin = false;
            isFromOperator = true;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            changeFixedMessageUi(UiCommandTypeEnum.REGISTER_PAGE, msg, UiBeepType.NO_BEEP);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.ENTER_REGISTER_PAGE);
        } catch (Exception ex) {
            log.error("MainActivity > goToRegisterPageFromOperatorPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void goToRegisterPageFromLineAdminPage(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.OPERATOR_PAGE) && !currentFragmentCommand.equals(UiCommandTypeEnum.LINE_ADMIN_PAGE))
                return;
            isFromLineAdmin = true;
            isFromOperator = false;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            changeFixedMessageUi(UiCommandTypeEnum.REGISTER_PAGE, msg, UiBeepType.NO_BEEP);
            StuffActivityFileManager.getInstance().writeNewFile(ReaderHandler.getInstance().getCurrentCardSerial(), true, StuffActivityType.ENTER_REGISTER_PAGE);
        } catch (Exception ex) {
            log.error("MainActivity > goToRegisterPageFromLineAdminPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void startForGTest(View view) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.HARDWARE_TEST) && fragmentHardwareTest != null) {
                fragmentHardwareTest.startFourGTest();
            }
        } catch (Exception ex) {
            log.error("MainActivity > startForGTest():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void startGPSTest(View view) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.HARDWARE_TEST) && fragmentHardwareTest != null) {
                fragmentHardwareTest.startGPSListener();
            }
        } catch (Exception ex) {
            log.error("MainActivity > startGPSTest():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private String adminCode = "";

    @Override
    public void gotoLineAdminPage(Long cardSerial, String _adminCode) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.LINE_ADMIN_PAGE))
                return;
            //stopServicesForLineAdmin();
            currentRole = Role.LINE_ADMIN;
            if (cardSerial != null)
                StuffActivityFileManager.getInstance().writeNewFile(cardSerial, true, StuffActivityType.OPEN_ADMIN_LINE_PAGE);
            adminCode = _adminCode;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            msg.put(1, _adminCode);
            changeFixedMessageUi(UiCommandTypeEnum.LINE_ADMIN_PAGE, msg, UiBeepType.SUCCESS);
        } catch (Exception ex) {
            log.error("MainActivity > gotoLineAdminPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private String welStr = "";

    @Override
    public void gotoDriverPage(Long cardSerial, String welcomeMsg) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.DRIVER_PAGE))
                return;
            currentRole = Role.DRIVER;
            if (cardSerial != null)
                StuffActivityFileManager.getInstance().writeNewFile(cardSerial, true, StuffActivityType.ENTER_DRIVER_PAGE);
            welStr = welcomeMsg;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            msg.put(1, welcomeMsg.split("\n")[0] + " خوش آمدید");
            changeFixedMessageUi(UiCommandTypeEnum.DRIVER_PAGE, msg, UiBeepType.SUCCESS);
        } catch (Exception ex) {
            log.error("MainActivity > gotoLineAdminPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private String operatorCode = "";

    @Override
    public void gotoOperatorPage(Long cardSerial, String _operator) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.OPERATOR_PAGE))
                return;
            currentRole = Role.OPERATOR;
            stopServicesForOperator();
            StuffActivityFileManager.getInstance().writeNewFile(cardSerial, true, StuffActivityType.ENTER_OPERATOR_PAGE);
            operatorCode = _operator;
            OutOfServiceHandler.stopValidation();
            Map<Integer, String> msg = new HashMap<>();
            msg.put(1, operatorCode);
            changeFixedMessageUi(UiCommandTypeEnum.OPERATOR_PAGE, msg, UiBeepType.SUCCESS);
        } catch (Exception ex) {
            log.error("MainActivity > gotoOperatorPage():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void closeShift(Long cardSerial, boolean isSystem, boolean isLineAdmin) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE) || currentFragmentCommand.equals(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "خطا، لطفا ابتدا از صفحه نگهداری خارج شوید", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            if (!isSystem) {
                /*if (currentFragmentCommand.equals(UiCommandTypeEnum.TAP_CARD) && fragmentTap != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentTap.handleDriverImage(false);
                        }
                    });
                }*/
                //Thread.sleep(2500);
                LastShiftHandler.getInstance().writeShiftData(-1L, 0, new Date());
                lastCardUsedForOpenShift = 0l;
                currentFragmentTempCommand = null;
                StateHandler.getInstance().setShiftOpen(false);
                OutOfServiceHandler.stopValidation();
                Map<Integer, String> msg = new HashMap<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat();
                PersianDate endDate = new PersianDate();
                SummaryReportFileHandler.getInstance().writeSecondPartIntoReport(StateHandler.getInstance().getShiftId(), new Date(), ReaderHandler.getInstance().getTransactionCountForUI());
                msg.put(1, "شروع شیفت: " + " " + startShiftDateTime.getShDay() + " " + startShiftDateTime.monthName() + " | " + startShiftDateTime.getHour() + ":" + startShiftDateTime.getMinute() + ":" + startShiftDateTime.getSecond());
                msg.put(2, "پایان شیفت: " + " " + endDate.getShDay() + " " + endDate.monthName() + " | " + endDate.getHour() + ":" + endDate.getMinute() + ":" + endDate.getSecond());
                msg.put(3, "تعداد تراکنش: " + ReaderHandler.getInstance().getTransactionCountForUI());
                changeFixedMessageUi(UiCommandTypeEnum.DRIVER_STATS, msg, UiBeepType.NO_BEEP);
                Thread.sleep(5000);
                StatFileManager.getInstance().closeFile(false);
                changeFixedMessageUi(UiCommandTypeEnum.CLOSE_SHIFT, null, UiBeepType.ERROR);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shiftStatus.setImageResource(R.drawable.lock23);
                    }
                });
            }
        } catch (Exception ex) {
            log.error("MainActivity > closeShift():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void closeShift(Long cardSerial, boolean isSystem) {
        try {
            if (currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE) || currentFragmentCommand.equals(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "خطا، لطفا ابتدا از صفحه نگهداری خارج شوید", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            if (!isSystem) {
                //Manual
                if (!cardSerial.equals(lastCardUsedForOpenShift)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "خطا، لطفا از کارت اصلی برای بستن شیفت اقدام کنید", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if (mainSnackbar == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "برای بستن شیفت دوباره کارت خود را نزدیک کنید", Snackbar.LENGTH_LONG)
                                    .setCallback(new Snackbar.Callback() {
                                        @Override
                                        public void onDismissed(Snackbar snackbar, int event) {
                                            mainSnackbar = null;
                                        }

                                        @Override
                                        public void onShown(Snackbar snackbar) {
                                            mainSnackbar = snackbar;
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    /*if (currentFragmentCommand.equals(UiCommandTypeEnum.TAP_CARD) && fragmentTap != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragmentTap.handleDriverImage(false);
                            }
                        });
                    }
                    Thread.sleep(1000);*/
                    LastShiftHandler.getInstance().writeShiftData(-1L, 0, new Date());
                    currentFragmentTempCommand = null;
                    lastCardUsedForOpenShift = 0l;
                    StateHandler.getInstance().setShiftOpen(false);
                    OutOfServiceHandler.stopValidation();
                    Map<Integer, String> msg = new HashMap<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat();
                    PersianDate endDate = new PersianDate();
                    SummaryReportFileHandler.getInstance().writeSecondPartIntoReport(StateHandler.getInstance().getShiftId(), new Date(), ReaderHandler.getInstance().getTransactionCountForUI());
                    msg.put(1, "شروع شیفت: " + " " + startShiftDateTime.getShDay() + " " + startShiftDateTime.monthName() + " | " + startShiftDateTime.getHour() + ":" + startShiftDateTime.getMinute() + ":" + startShiftDateTime.getSecond());
                    msg.put(2, "پایان شیفت: " + " " + endDate.getShDay() + " " + endDate.monthName() + " | " + endDate.getHour() + ":" + endDate.getMinute() + ":" + endDate.getSecond());
                    msg.put(3, "تعداد تراکنش: " + ReaderHandler.getInstance().getTransactionCountForUI());
                    changeFixedMessageUi(UiCommandTypeEnum.DRIVER_STATS, msg, UiBeepType.NO_BEEP);
                    Thread.sleep(10000);
                    StatFileManager.getInstance().closeFile(false);
                    changeFixedMessageUi(UiCommandTypeEnum.CLOSE_SHIFT, null, UiBeepType.ERROR);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shiftStatus.setImageResource(R.drawable.lock23);
                        }
                    });
                }
            } else {
                //Auto
                lastCardUsedForOpenShift = 0l;
                LastShiftHandler.getInstance().writeShiftData(-1L, 0, new Date());
                StateHandler.getInstance().setShiftOpen(false);
                OutOfServiceHandler.stopValidation();
                Map<Integer, String> msg = new HashMap<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat();
                PersianDate endDate = new PersianDate();
                SummaryReportFileHandler.getInstance().writeSecondPartIntoReport(StateHandler.getInstance().getShiftId(), new Date(), ReaderHandler.getInstance().getTransactionCountForUI());
                msg.put(1, "شروع شیفت: " + " " + startShiftDateTime.getShDay() + " " + startShiftDateTime.monthName() + " | " + startShiftDateTime.getHour() + ":" + startShiftDateTime.getMinute() + ":" + startShiftDateTime.getSecond());
                msg.put(2, "پایان شیفت: " + " " + endDate.getShDay() + " " + endDate.monthName() + " | " + endDate.getHour() + ":" + endDate.getMinute() + ":" + endDate.getSecond());
                msg.put(3, "تعداد تراکنش: " + ReaderHandler.getInstance().getTransactionCountForUI());
                changeFixedMessageUi(UiCommandTypeEnum.DRIVER_STATS, msg, UiBeepType.NO_BEEP);
                Thread.sleep(10000);
                StatFileManager.getInstance().closeFile(false);
                changeFixedMessageUi(UiCommandTypeEnum.CLOSE_SHIFT, null, UiBeepType.ERROR);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shiftStatus.setImageResource(R.drawable.lock23);
                    }
                });
                DataFileUtility.getInstance().setValidationCount("0");
            }
        } catch (Exception ex) {
            log.error("MainActivity > closeShift():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void closeShiftFromOutOfServiceHandler() {
        try {
            StateHandler.getInstance().setShiftOpen(false);
            StatFileManager.getInstance().closeFile(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shiftStatus.setImageResource(R.drawable.lock23);
                }
            });
        } catch (Exception ex) {
            log.error("MainActivity > closeShiftFromOutOfServiceHandler():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void changeAppVersion() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appVersion.setText(Utility.getAppVersionName());
            }
        });
    }

    @Override
    public void changeParamVersion(final String version) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                paramVersion.setText(version);
            }
        });
    }

    @Override
    public void changeLineId(final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lineId.setText("خط: " + name);
            }
        });
    }

    @Override
    public void changeBusId(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                busId.setText("اتوبوس: " + id);
            }
        });
    }

    private void changeLineAndBusID() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (StateHandler.getInstance().getLineId() != 0)
                    lineId.setText("خط: " + StateHandler.getInstance().getLineId());
                else
                    lineId.setText("خط: " + "---");
                if (StateHandler.getInstance().getBusId() != 0)
                    busId.setText("اتوبوس: " + StateHandler.getInstance().getBusId());
                else
                    busId.setText("اتوبوس: " + "---");
            }
        });
    }

    @Override
    public void showOnUi(ProcessCardResult processCardResult) {
        try {
            Map<Integer, String> msg = new ArrayMap<>();
            if (processCardResult.getValidationResults().equals(CardValidationResults.SUCCESS)) {
                if (processCardResult.getCard().getQomCardType().equals(QomCardType.NEW)) {
                    msg.put(1, "کرایه: " + processCardResult.getCard().getNewTransactionInfo().getFare() + " ریال");
                    msg.put(2, "باقیمانده: " + processCardResult.getCard().getNewTransactionInfo().getTotalAmount() + " ریال");
                } else {
                    msg.put(1, "کرایه: " + processCardResult.getCard().getNewTransactionInfo().getFare() * 10 + " ریال");
                    msg.put(2, "باقیمانده: " + processCardResult.getCard().getNewTransactionInfo().getTotalAmount() * 10 + " ریال");
                }
                changeMessageUi(UiCommandTypeEnum.SUCCESS, msg, 1000, UiBeepType.SUCCESS, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.FAIL)) {
                msg.put(1, "خطا در تراکنش");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.BYPASS_TIME)) {
                return;
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.INSUFFICIENT_BALANCE)) {
                if (processCardResult.getCard().getCardVersion().equals(0)) {
                    //old card
                    msg.put(1, "موجودی کارت ناکافی است\nموجودی: " + processCardResult.getCard().getLastTransactionInfo().getTotalAmount() * 10 + " ریال");
                } else {
                    //new card
                    msg.put(1, "موجودی کارت ناکافی است\nموجودی: " + processCardResult.getCard().getLastTransactionInfo().getTotalAmount() + " ریال");
                }
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.INSUFFICIENT_BALANCE_CHECK_CARD)) {
                msg.put(1, "لطفا به باجه مراجعه کنید\n  موجودی کارت ناکافی است");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.NOT_A_CREW_CARD)) {
                msg.put(1, "لطفا کارت راننده یا اعضا را ارائه دهید");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.PROBLEM_WHILE_VALIDATING_CREW_CARD)) {
                msg.put(1, "خطا در پذیرش کارت اعضا");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.CLOSE_SHIFT_FIRST)) {
                msg.put(1, "لطفا برای شروع عملیات، ابتدا شیفت را ببندید");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.CALCULATE_FARE_ERROR)) {
                msg.put(1, "خطا در تراکنش (1)");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            } else if (processCardResult.getValidationResults().equals(CardValidationResults.INVALID_DRIVER)) {
                msg.put(1, "شما مجاز به انجام عملیات در این اتوبوس نیستید");
                changeMessageUi(UiCommandTypeEnum.FAILED, msg, 1000, UiBeepType.ERROR, true);
            }
        } catch (Exception ex) {
            log.error("MainActivity > showOnUi():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private ServiceConnection readerHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ReaderHandler.LocalBinder binder = (ReaderHandler.LocalBinder) service;
            readerHandler = binder.getServiceInstance();
            readerHandler.registerClient(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection outOfServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            OutOfServiceHandler.LocalBinder binder = (OutOfServiceHandler.LocalBinder) service;
            outOfServiceHandler = binder.getServiceInstance();
            outOfServiceHandler.registerClient(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection locationHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LocationHandler.LocalBinder binder = (LocationHandler.LocalBinder) service;
            locationHandler = binder.getServiceInstance();
            locationHandler.registerClient(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection sleepHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SleepHandler.LocalBinder binder = (SleepHandler.LocalBinder) service;
            sleepHandler = binder.getServiceInstance(); //Get instance of your service!
            sleepHandler.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection parameterHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ParameterHandler.LocalBinder binder = (ParameterHandler.LocalBinder) service;
            parameterHandler = binder.getServiceInstance(); //Get instance of your service!
            parameterHandler.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection transactionFileHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            TransactionsFileHandler.LocalBinder binder = (TransactionsFileHandler.LocalBinder) service;
            transactionFileHandler = binder.getServiceInstance(); //Get instance of your service!
            transactionFileHandler.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    private ServiceConnection logHandlerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LogHandler.LocalBinder binder = (LogHandler.LocalBinder) service;
            logHandler = binder.getServiceInstance(); //Get instance of your service!
            logHandler.registerClient(MainActivity.this); //Activity register in the service as client for callabcks!
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public void changeMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg, int timeout, UiBeepType beep, boolean temporary) {
        try {
            if (onThread && myTask != null) {
                myTask.cancel(true);
                onThread = false;
                while (myTask.getStatus() != AsyncTask.Status.FINISHED) {
                    AsyncTask.Status s = myTask.getStatus();
                }
            }
            if (!temporary) {
                currentFragmentCommand = operation;
                currentMsg = newMsg;
            } else {
                currentFragmentTempCommand = operation;
            }
            myTask = new ChangeFragment(operation, currentFragmentCommand, newMsg, currentMsg, timeout, this, beep, temporary);
            myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception ex) {
            log.error("changeMessageUi():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
    }

    private int maintenanceClickCounter = 0;
    private Date lastCLick = null;

    public void goToLoginMaintenance(View view) {
        try {
            if (maintenanceClickCounter == 0) {
                maintenanceClickCounter += 1;
                lastCLick = Calendar.getInstance().getTime();
                return;
            }
            maintenanceClickCounter += 1;
            if (maintenanceClickCounter > 10) {
                if (Math.abs(Calendar.getInstance().getTime().getTime() - lastCLick.getTime()) < 5000) {
                    //show login page
                    if (currentFragmentCommand.equals(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE) || currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE)) {
                        maintenanceClickCounter = 0;
                        lastCLick = null;
                        return;
                    }
                    if (StateHandler.getInstance().isShiftOpen())
                        return;
                    stopServices();
                    changeFixedMessageUi(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE, null, UiBeepType.NO_BEEP);
                }
                maintenanceClickCounter = 0;
                lastCLick = null;
            }
        } catch (Exception ex) {
            log.error("MainActivity > goToLoginMaintenance():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void restart(View view) {
        try {
            restart();
        } catch (Exception ex) {
            log.error("MainActivity > restart(View):" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void restartTheAppFromService() {
        try {
            restart();
        } catch (Exception ex) {
            log.error("MainActivity > restartTheAppFromService():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void doLogin(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.LOGIN_FOR_OPEN_MAINTENANCE))
                return;
            stopServices();
            FragmentLoginToMaintenance fragmentLoginToMaintenance = (FragmentLoginToMaintenance) getFragmentManager().findFragmentByTag("login_maintenance_frag");
            if (fragmentLoginToMaintenance != null)
                fragmentLoginToMaintenance.doLogin(this);
        } catch (Exception ex) {
            log.error("MainActivity > doLogin(View):" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void saveChanges(View view) {
        try {
            if (!currentFragmentCommand.equals(UiCommandTypeEnum.MAINTENANCE))
                return;
            FragmentMaintenance fragmentMaintenance = (FragmentMaintenance) getFragmentManager().findFragmentByTag("maintenance_frag");
            if (fragmentMaintenance != null)
                fragmentMaintenance.saveChanges();
        } catch (Exception ex) {
            log.error("MainActivity > saveChanges(View):" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(getApplicationContext(), "بهینه سازی تنظیمات", Toast.LENGTH_SHORT).show();
        /*if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
        }*/
    }

    public void restart() {
        try {
            Intent mStartActivity = new Intent(getApplicationContext(), SplashScreen.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
            this.finishAffinity();
            System.exit(0);
        } catch (Exception ex) {
            log.error("MainActivity > restart():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    ///////////////////////////////////////////////////FRAGMENT THINGS//////////////////////////////////////////////////
    public List<Fragment> getActiveFragments() {
        try {
            ArrayList<Fragment> ret = new ArrayList<Fragment>();
            for (WeakReference<Fragment> ref : fragList) {
                Fragment f = ref.get();
                if (f != null) {
                    if (f.isVisible()) {
                        ret.add(f);
                    }
                }
            }
            return ret;
        } catch (Exception ex) {
            log.error("MainActivity > getActiveFragments():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            ArrayList<Fragment> ret = new ArrayList<Fragment>();
            return ret;
        }
    }

    public synchronized void removeAllFrag() {
        try {
            List<Fragment> fList = getActiveFragments();
            androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            for (Fragment f : fList) {
                ft.remove(f).commitAllowingStateLoss();
            }
        } catch (Exception ex) {
            log.error("MainActivity > removeAllFrag():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            fragList.add(new WeakReference(fragment));
        } catch (Exception ex) {
            log.error("MainActivity > onAttachFragment():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private FragmentTap fragmentTap = null;
    private FragmentSummaryReport fragmentSummaryReport = null;
    private FragmentHardwareTest fragmentHardwareTest = null;

    @SuppressLint("ResourceType")
    public void setFrag(UiCommandTypeEnum operation, Map<Integer, String> newMsg) {
        try {
            removeAllFrag();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();*/
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right);
            switch (operation) {
                case INITIALIZING:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentInitInstance(), "init_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case SUCCESS:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentSuccessInstance(newMsg), "success_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case FAILED:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentFailedInstance(newMsg), "failed_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case DRIVER_STATS:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentStatsInstance(newMsg), "stats_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case TAP_CARD:
                    fragmentTap = FragmentUtility.newFragmentTapInstance();
                    ft.replace(R.id.frameLayout, fragmentTap, "tap_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case OUT_OF_SERVICE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentOutOfServiceInstance(newMsg), "out_of_service_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case CLOSE_SHIFT:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentCloseShiftInstance(), "close_shift_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case LOGIN_FOR_OPEN_MAINTENANCE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentLoginToMaintenanceInstance(), "login_maintenance_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case MAINTENANCE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentMaintenanceInstance(), "maintenance_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case LINE_ADMIN_PAGE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentLineAdminInstance(newMsg), "line_admin_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case OPERATOR_PAGE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentOperatorInstance(newMsg), "operator_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case SUMMARY_REPORT:
                    fragmentSummaryReport = FragmentUtility.newFragmentSummaryReportInstance();
                    ft.replace(R.id.frameLayout, fragmentSummaryReport, "summary_report_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case CLOSE_SHIFT_CONFIRM:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentShiftCloseConfirmInstance(), "close_shift_confirm_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case HARDWARE_TEST:
                    fragmentHardwareTest = FragmentUtility.newFragmentHardwareTestInstance();
                    ft.replace(R.id.frameLayout, fragmentHardwareTest, "hardware_test_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case UPLOAD_DOWNLOAD_PAGE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentDownloadUploadInstance(), "upload_download_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case REGISTER_PAGE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentRegisterInstance(), "register_frag");
                    ft.commitAllowingStateLoss();
                    break;
                case DRIVER_PAGE:
                    ft.replace(R.id.frameLayout, FragmentUtility.newFragmentDriverInstance(newMsg), "driver_frag");
                    ft.commitAllowingStateLoss();
                    break;
            }
        } catch (Exception ex) {
            log.error("MainActivity > setFrag():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void setDateAndTime(final int intervalTime, final int intervalDate) {
        try {
            PersianDate pDate = new PersianDate();
            final SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");
            final Handler handlerTime = new Handler();
            final Handler handlerDate = new Handler();
            final TextView time_txtView = findViewById(R.id.timeTxtView);
            final TextView date_txtView = findViewById(R.id.dateTxtView);
            handlerTime.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    time_txtView.setText(sdf_time.format(calendar.getTime()));
                    handlerTime.postDelayed(this, intervalTime);
                }
            }, 1000);
            handlerDate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PersianDate pDate = new PersianDate();
                    date_txtView.setText(pDate.dayName() + " " + pDate.getShDay() + " " + pDate.monthName() + " " + pDate.getShYear());
                    handlerTime.postDelayed(this, intervalDate);
                }
            }, 1000);
        } catch (Exception ex) {
            log.error("MainActivity > setDateAndTime():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void setUiComponent() {
        try {
            final View decorView = getWindow().getDecorView();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // This work only for android 4.4+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(flags);
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            log.error("MainActivity > setUiComponent():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void getGpsStatus() {
        try {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS)
                makeGPSIconEnable();
            else
                makeGPSIconDisable();
        } catch (Exception ex) {
            log.error("Main --> getGpsStatus():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void makeGPSIconDisable() {
        try {
            gpsTxtView.setText("خاموش");
            gpsImageView.setImageResource(R.drawable.ic_navigation_off);
        } catch (Exception ex) {
            log.error("Main() --> makeDisableIcon():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void makeGPSIconEnable() {
        try {
            gpsTxtView.setText("روشن");
            gpsTxtView.setGravity(Gravity.RIGHT);
            gpsTxtView.setGravity(Gravity.CENTER_VERTICAL);
            gpsImageView.setImageResource(R.drawable.ic_navigation_on);
        } catch (Exception ex) {
            log.error("Main() --> makeEnableIcon():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void makeGPSIconGetSignal() {
        try {
            gpsTxtView.setText("فعال");
            gpsImageView.setImageResource(R.drawable.ic_navigation_signal);
            new Handler().postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              gpsImageView.setImageResource(R.drawable.ic_navigation_on);
                                          }
                                      }
                    , 500);
        } catch (Exception ex) {
            log.error("Main() --> makeSignalIcon():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public boolean isOnThread() {
        return onThread;
    }

    public void setOnThread(boolean onThread) {
        this.onThread = onThread;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public LoudnessEnhancer getLoudnessEnhancer() {
        return loudnessEnhancer;
    }

    public void setLoudnessEnhancer(LoudnessEnhancer loudnessEnhancer) {
        this.loudnessEnhancer = loudnessEnhancer;
    }

    public void setCurrentFragmentTempCommand(UiCommandTypeEnum currentFragmentTempCommand) {
        this.currentFragmentTempCommand = currentFragmentTempCommand;
    }
}
