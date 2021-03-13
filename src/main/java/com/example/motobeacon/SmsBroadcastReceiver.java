package com.example.motobeacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";

    private HashMap<String, String> members;
    private final String serviceProviderSmsCondition;
    private LogAdapter mAdapter;

    public SmsBroadcastReceiver(LogAdapter adapter, String membersPath, String serviceProviderSmsCondition) {
        this.mAdapter = adapter;
        this.serviceProviderSmsCondition = serviceProviderSmsCondition;
        this.members = new HashMap<>();
        members.put("+4915730106797", "penis");
        members.put("+4917634384826", "jasper");
        // open csv, fill map
        /*try {
            BufferedReader br = new BufferedReader(new FileReader(membersPath));
            String line = "";

            while ((line = br.readLine()) != null) {
                String[] user = line.split(",");
                members.put(user[0], user[1]);
            }

        } catch (FileNotFoundException e) {
            System.out.println("FILE \"members.csv\" NOT FOUND - PATH USED: "+membersPath);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }

            //todo: here: check whether number (smsSender?) is in list of motonumbers, and only forward when that is the case, adding his username to the message

            if (members.containsKey(smsSender))   // && smsBody.startsWith(serviceProviderSmsCondition)
                onTextReceived(smsSender, smsBody);
            else
                log(smsSender, smsBody, "denied - sender not moto");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onTextReceived(String smsSender, String smsBody) {
        //send out sms to ev'rybody
        for (String number : this.members.keySet()) {
            if (!number.equals(smsSender))
                SmsManager.getDefault().sendTextMessage(number, null, this.members.get(smsSender) + ": " + smsBody, null, null);
            else {
                if (smsBody.length()>10)
                    smsBody = smsBody.substring(0, 9)+"...";
                SmsManager.getDefault().sendTextMessage(number, null, "[success sending message] " + smsBody, null, null);
            }
        }
        log(smsSender, smsBody, "forwarded");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void log(String sender, String msg, String action) {
        MainActivity.log(this.mAdapter, sender + ": \"" + msg + "\"", action);
    }

}