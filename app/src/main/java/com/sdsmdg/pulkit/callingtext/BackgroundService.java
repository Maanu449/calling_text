package com.sdsmdg.pulkit.callingtext;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by pulkit on 4/2/17.
 */

public class BackgroundService extends Service {

    DatabaseReference callertree = FirebaseDatabase.getInstance().getReference().child("caller");
    DatabaseReference receivertree = FirebaseDatabase.getInstance().getReference().child("receiver");
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
    String name;
    String number;
    String type;
    String time;
    String msg;
    String receiver;
    public static int count=0;
    DataBaseHandler dbh;

    public void onCreate()
    {
        receiver = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("NUMBER", "7248187747");
        Log.e("Background service","service started"+BaseActivity.receiver);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("receiver").child(BaseActivity.receiver) != null) {
                    count++;
                    Log.e("count",count+"");
                    Log.e("on data change listener", "on data change listener");
                    BackGroundWorker.value = snapshot.child("receiver").child(BaseActivity.receiver).child("caller").getValue().toString();
                    BackGroundWorker.gifId = snapshot.child("receiver").child(BaseActivity.receiver).child("gifId").getValue().toString();
                    BackGroundWorker.msg = snapshot.child("receiver").child(BaseActivity.receiver
                    ).child("message").getValue().toString();
                    Log.e("gif",BackGroundWorker.gifId );
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(BackGroundWorker.value));
                    Cursor phones = getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
                    while (phones.moveToNext()) {
                        name = phones.getString(phones.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    }
                    number = BackGroundWorker.value;
                    type = "incoming";
                    time = DateFormat.getDateTimeInstance().format(new Date());
                    msg=BackGroundWorker.msg;
                    CallerDetails cd =new CallerDetails(name,number,msg,type,time);
                    dbh= DataBaseHandler.getInstance(getBaseContext());
                    dbh.addCaller(cd);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Background service","service started");

        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
