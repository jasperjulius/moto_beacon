package com.example.motobeacon;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // todo: broadcast service stops working when app is quit - change such that it always runs
    // todo: coole sonderzeichen, die man einfügen kann

    private RecyclerView mRecyclerView;
    private LogAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final int SMS_PERMISSION_CODE = 0;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<LogItem> exampleList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.logView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new LogAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        context = this;

        killedMode();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void launch(View v) {
        Log.d("launch", "commencing Launch...");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,}, SMS_PERMISSION_CODE);
        // case, where user answers "no" is not handled - should result in moving to killed mode
        String path =  context.getApplicationInfo().dataDir+"";
        String membersPath =  path+"/members.csv";

        smsBroadcastReceiver = new SmsBroadcastReceiver(this.mAdapter, membersPath, "");
        registerReceiver(smsBroadcastReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        launchedMode();
        //boot up listener for text messages
        Intent serviceIntent = new Intent(this, BeaconService.class);
        serviceIntent.putExtra("inputExtra", "message forwarding activated   #motogang");
        ContextCompat.startForegroundService(this, serviceIntent);


    }

    public void kill(View v) {
        unregisterReceiver(smsBroadcastReceiver);

        killedMode();
        Intent serviceIntent = new Intent(this, BeaconService.class);
        stopService(serviceIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void launchedMode() {
        Button btn_launch = findViewById(R.id.button_launch);
        Button btn_kill = findViewById(R.id.button_kill);
        RecyclerView rv = findViewById(R.id.logView);

        rv.setAlpha(1f);

        log(this.mAdapter, "Launched", "success");

        btn_launch.setAlpha(.3f);
        btn_launch.setClickable(false);

        btn_kill.setAlpha(1f);
        btn_kill.setClickable(true);
    }

    private void killedMode() {
        Button btn_launch = findViewById(R.id.button_launch);
        Button btn_kill = findViewById(R.id.button_kill);
        RecyclerView rv = findViewById(R.id.logView);

        rv.setAlpha(0f);
        ArrayList<LogItem> emptyList = new ArrayList<LogItem>();

        mAdapter = new LogAdapter(emptyList);
        mRecyclerView.setAdapter(mAdapter);

        btn_kill.setAlpha(.3f);
        btn_kill.setClickable(false);

        btn_launch.setAlpha(1f);
        btn_launch.setClickable(true);
    }

    private void setBtns(boolean launch, boolean kill) {
        Button btn_launch = findViewById(R.id.button_launch);
        Button btn_kill = findViewById(R.id.button_kill);

        btn_launch.setEnabled(launch);
        btn_kill.setEnabled(kill);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void log(LogAdapter logger, String msg, String action){
        String time = java.time.LocalTime.now().toString();
        String dateTime = java.time.LocalDate.now().toString() + " " + time.substring(0, time.length()-4);
        logger.addToLog(new LogItem(msg, dateTime, action));
    }
}