package net.callofdroidy.silva;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityMain extends AppCompatActivity{
    public static final String TAG = "ActivityMain";

    TextView tvDisplay;

    Button btnStart;
    Button btnStop;

    BroadcastReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String[] permissionsToCheck = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
        String[] notGrantedPermission = PermissionHandler.checkPermissions(this, permissionsToCheck);
        if(notGrantedPermission != null)
            PermissionHandler.requestPermissions(this, notGrantedPermission, Constants.PERMISSION_REQUEST_CODE_BUNDLE);

        tvDisplay = (TextView) findViewById(R.id.tv_display);
        tvDisplay.setMovementMethod(new ScrollingMovementMethod());

        localReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String rawMessage = intent.getStringExtra("message");
                Toast.makeText(ActivityMain.this, rawMessage, Toast.LENGTH_SHORT).show();
                tvDisplay.append(rawMessage + "\n");
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, new IntentFilter("status"));

        btnStart = (Button) findViewById(R.id.btn_start_service);
        btnStop = (Button) findViewById(R.id.btn_stop_service);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] notGrantedPermission = PermissionHandler.checkPermissions(ActivityMain.this, Constants.PERMISSION_READ_PHONE_STATE);
                if(notGrantedPermission != null)
                    PermissionHandler.requestPermissions(ActivityMain.this, notGrantedPermission, Constants.PERMISSION_REQUEST_CODE_READ_PHONE_STATE);
                else
                    startService(new Intent(ActivityMain.this, ServiceMonitor.class));
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ActivityMain.this, ServiceMonitor.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE_READ_PHONE_STATE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startService(new Intent(ActivityMain.this, ServiceMonitor.class));
                else
                    Toast.makeText(this, "no permission to read phone state", Toast.LENGTH_SHORT).show();
                break;
            case Constants.PERMISSION_REQUEST_CODE_BUNDLE:
                break;
        }
    }

    @Override
    public void onDestroy(){

        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        super.onDestroy();
    }
}
