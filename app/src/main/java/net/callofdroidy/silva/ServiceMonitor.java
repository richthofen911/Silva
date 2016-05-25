package net.callofdroidy.silva;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceMonitor extends Service {
    private final static String TAG = "ServiceMonitor";

    private BroadcastReceiver broadcastReceiver;
    private TelephonyManager telephonyManager;
    private NotificationManager notificationManager;
    private LocalBroadcastManager localBroadcastManager;

    private int missedCalls = 0;

    private TextToSpeech textToSpeech;

    private Timer timer;

    private boolean isReadyToSpeak = false;

    public ServiceMonitor() {
    }

    @Override
    public void onCreate(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.CANADA);
                    textToSpeech.setSpeechRate(0.6f);
                    isReadyToSpeak = true;
                    sendLocalBroadcast("TTS --> Ready");
                }else
                    sendLocalBroadcast("TTS --> Start Failed");
            }
        });

        showNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
                    switch (telephonyManager.getCallState()){
                        case TelephonyManager.CALL_STATE_RINGING:
                            String incomingNumber = intent.getStringExtra("incoming_number");
                            Log.e(TAG, "onStartCommand: incoming number: " + incomingNumber);
                            if(incomingNumber.equals(Constants.PHONE_NUMBER_XIAOLU))
                                missedCalls++;
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            if(missedCalls >= 2)
                                if(isReadyToSpeak)
                                    repeatAlarm("Alarm! Alarm! You are in big trouble!");
                            break;
                        default:
                            break;
                    }
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

        return START_STICKY;
    }

    private void showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActivityMain.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_face_24dp)  // the status icon
                .setTicker("D-Silva")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Monitoring incoming calls")  // the label of the entry
                .setContentText("D-Silva")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        notificationManager.notify(Constants.NOTIFICATION_ID_MONITOR, notification);
    }

    private void repeatAlarm(final String alarmContent){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                textToSpeech.speak(alarmContent, TextToSpeech.QUEUE_FLUSH, null, Constants.SPEAK_ID_GENERAL);
            }
        }, 0, 5000);
    }

    private void stopAlarm(){
        timer.cancel();
    }

    final protected void sendLocalBroadcast(String message){
        localBroadcastManager.sendBroadcast(new Intent("status").putExtra("message", message));
    }

    @Override
    public void onDestroy(){
        if(timer != null)
            stopAlarm();
        missedCalls = 0;
        if(textToSpeech != null){
            textToSpeech.shutdown();
            sendLocalBroadcast("TTS --> Shutdown");
        }
        if(broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
        notificationManager.cancel(Constants.NOTIFICATION_ID_MONITOR);

        super.onDestroy();
    }
}
