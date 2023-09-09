/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package cnt.pqh.BGServices;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asim.sdk.common.Utils;
import asim.sdk.locker.DeviceInfo;
import asim.sdk.locker.SDKLocker;
import cnt.pqh.BGServices.ServiceInstance.*;

import static cnt.pqh.BGServices.Globals.leapTime;

public class Service extends android.app.Service {
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service-supervisor";
    private static Service mCurrentService;
    private int counterLocker = 0;
    private int counterConfiguration = 0;
    private static Timer lockerOperationTimer;
    private static TimerTask lockerOperationTimerTask;

    private static Timer configOperationTimer;
    private static TimerTask configOperationTimerTask;

    private static Timer rebootTimer;
    private static TimerTask rebootTimerTask;

    @SuppressLint("HardwareIds") String m_androidId;
    ExecutorService es = Executors.newScheduledThreadPool(30);

    public Service() {
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        m_androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mCurrentService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        counterLocker = 0;
        counterConfiguration = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }

        // make sure you call the startForeground on onStartCommand because otherwise
        // when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                Log.i(TAG, "restarting foreground successful");
                startTimer();
            } catch (Exception e) {
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }


    /**
     * this is called when the process is killed by Android
     *
     * @param rootIntent
     */

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // do not call stoptimertask because on some phones it is called asynchronously
        // after you swipe out the app and therefore sometimes
        // it will stop the timer after it was restarted
        // stoptimertask();
    }


    /**
     * static to avoid multiple timers to be created when the service is called several times
     */
    public static Timer timerLocker;
    public static TimerTask timerLockerTask;
    public static Timer timerPrinter;
    public static TimerTask timerPrinterTask;
    public static Timer timerUps;
    public static TimerTask timerUpsTask;
    public static Timer timerUps30S;
    public static TimerTask timerUpsTask30S;
    public static Timer timerSensor;
    public static TimerTask timerSensorTask;

    public static Timer timerSimdispenser1;
    public static TimerTask timerSimdispenser1Task;

    public static Timer timerSimdispenser2;
    public static TimerTask timerSimdispenser2Task;

    public static Timer timerSimdispenser3;
    public static TimerTask timerSimdispenser3Task;

    public static Timer timerSimdispenser4;
    public static TimerTask timerSimdispenser4Task;

    public void startTimer() {
        Log.i(TAG, "Starting timer for collecting data from peripheral");
        Log.i(TAG, "Globals.isInitiate ===" + String.valueOf(Globals.isInitiate));
        //initialize the TimerTask's job
        initializeTimerTask();
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        if (Globals.isInitiate) return;
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        Globals.isInitiate = true;
        stoptimertask();

        Log.i(TAG, "initialising TimerTask");
        try  {
            //Your code goes here
//            boolean getConfig = GetConfig.getConfig(m_androidId, getApplicationContext());
//            if (!getConfig) {
//                while (!getConfig) {
//                    getConfig = GetConfig.getConfig(m_androidId, getApplicationContext());
//                    try {
//                        Thread.sleep(5000);
//                        Log.d(TAG, "---waiting for config is updated---");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            connectWithFirebase();
            timerSimdispenser1Task = SimdispenserService.getTimerTask(m_androidId, getApplicationContext(), Globals.simdispenser1TimeToRecyleCard, Globals.simdispenser1ComName, Globals.simdispenser1Baurate, 1);
            timerSimdispenser2Task = SimdispenserService.getTimerTask(m_androidId, getApplicationContext(), Globals.simdispenser2TimeToRecyleCard, Globals.simdispenser2ComName, Globals.simdispenser2Baurate, 2);
            timerSimdispenser3Task = SimdispenserService.getTimerTask(m_androidId, getApplicationContext(), Globals.simdispenser3TimeToRecyleCard, Globals.simdispenser3ComName, Globals.simdispenser3Baurate, 3);
            timerSimdispenser4Task = SimdispenserService.getTimerTask(m_androidId, getApplicationContext(), Globals.simdispenser4TimeToRecyleCard, Globals.simdispenser4ComName, Globals.simdispenser4Baurate, 4);
            timerUpsTask30S = UPSService.getTimerTask30(m_androidId, getApplicationContext());
            timerUpsTask = UPSService.getTimerTask(m_androidId, getApplicationContext());
            timerLockerTask = LockerService.getTimerTask(getApplicationContext());
            timerSensorTask = SensorService.getTimerTask(m_androidId, getApplicationContext());
            timerPrinterTask = PrinterService.getTimerTask(m_androidId, getApplicationContext());

            Log.i(TAG, "Scheduling...");
            //schedule the timer, to wake up every 1 second
            try {
                timerLocker = new Timer();
                timerLocker.schedule(timerLockerTask, Globals.delayTimeBeforCollecting + leapTime * 1, Globals.lockerTimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                timerPrinter = new Timer();
                timerPrinter.schedule(timerPrinterTask, Globals.delayTimeBeforCollecting + leapTime * 2, Globals.printerTimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                timerUps = new Timer();
                timerUps.schedule(timerUpsTask, Globals.delayTimeBeforCollecting + leapTime * 3, Globals.upsTimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                timerSimdispenser1 = new Timer();
                timerSimdispenser1.schedule(timerSimdispenser1Task, Globals.delayTimeBeforCollecting + leapTime * 4, Globals.simdispenser1TimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                timerSimdispenser2 = new Timer();
                timerSimdispenser2.schedule(timerSimdispenser2Task, Globals.delayTimeBeforCollecting + leapTime * 5, Globals.simdispenser2TimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                timerSimdispenser3 = new Timer();
                timerSimdispenser3.schedule(timerSimdispenser3Task, Globals.delayTimeBeforCollecting + leapTime * 6, Globals.simdispenser3TimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                timerSimdispenser4 = new Timer();
                timerSimdispenser4.schedule(timerSimdispenser4Task, Globals.delayTimeBeforCollecting + leapTime * 7, Globals.simdispenser4TimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                timerSensor = new Timer();
                timerSensor.schedule(timerSensorTask, Globals.delayTimeBeforCollecting + leapTime * 8, Globals.temphuTimeSpanOfCollection); //
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                timerUps30S = new Timer();
                timerUps30S.schedule(timerUpsTask30S, Globals.delayTimeBeforCollecting + leapTime * 9, Globals.upsTimeSpanOfFetch); //
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timerLocker != null) {
            timerLocker.cancel();
            timerLocker = null;
        }
        if (timerPrinter != null) {
            timerPrinter.cancel();
            timerPrinter = null;
        }
        if (timerSensor != null) {
            timerSensor.cancel();
            timerSensor = null;
        }
        if (timerUps != null) {
            timerUps.cancel();
            timerUps = null;
        }
        if (timerSimdispenser1 != null) {
            timerSimdispenser1.cancel();
            timerSimdispenser1 = null;
        }

        if (timerSimdispenser2 != null) {
            timerSimdispenser2.cancel();
            timerSimdispenser2 = null;
        }

        if (timerSimdispenser3 != null) {
            timerSimdispenser3.cancel();
            timerSimdispenser3 = null;
        }

        if (timerSimdispenser4 != null) {
            timerSimdispenser4.cancel();
            timerSimdispenser4 = null;
        }
        if (timerUps30S != null) {
            timerUps30S.cancel();
            timerUps30S = null;
        }
        // stop timer task if any
        if (timerLockerTask != null) {
            timerLockerTask.cancel();
            timerLockerTask = null;
        }
        if (timerPrinterTask != null) {
            timerPrinterTask.cancel();
            timerPrinterTask = null;
        }
        if (timerSensorTask != null) {
            timerSensorTask.cancel();
            timerSensorTask = null;
        }
        if (timerUpsTask != null) {
            timerUpsTask.cancel();
            timerUpsTask = null;
        }
        if (timerSimdispenser1Task != null) {
            timerSimdispenser1Task.cancel();
            timerSimdispenser1Task = null;
        }

        if (timerSimdispenser2Task != null) {
            timerSimdispenser2Task.cancel();
            timerSimdispenser2Task = null;
        }

        if (timerSimdispenser3Task != null) {
            timerSimdispenser3Task.cancel();
            timerSimdispenser3Task = null;
        }

        if (timerSimdispenser4Task != null) {
            timerSimdispenser4Task.cancel();
            timerSimdispenser4Task = null;
        }
        if (timerUpsTask30S != null) {
            timerUpsTask30S.cancel();
            timerUpsTask30S = null;
        }

    }

    public static Service getmCurrentService() {
        return mCurrentService;
    }

    public static void setmCurrentService(Service mCurrentService) {
        Service.mCurrentService = mCurrentService;
    }
    public boolean openLocker()
    {
        SDKLocker locker = new SDKLocker();
        int count = 0;
        List<Integer> excludedDevices = new ArrayList<>();
        excludedDevices.add(Globals.temphuDeviceId);
        Log.d("==Locker Controller==", "Excluded devices = " + Globals.temphuDeviceId );
        Log.d("Starting to open lock: ", "............count = " + count);
        return locker.openLockNewNew(getApplicationContext(), 1, excludedDevices, 9600);
    }
    public boolean closeLocker()
    {
        SDKLocker locker = new SDKLocker();
        boolean clear = false;
        int countClear = 0;
        List<Integer> excludedDevices = new ArrayList<>();
        excludedDevices.add(Globals.temphuDeviceId);
        while(countClear < 5 && !clear) {
            Log.d("ClearRelay", "Trying to clear relay ... countClear = " + countClear);
            clear = locker.closeLockNewNew(getApplicationContext(), excludedDevices, 9600);
            Utils.sleep((long) 1);
            countClear += 1;
        }
        return clear;
    }


}
