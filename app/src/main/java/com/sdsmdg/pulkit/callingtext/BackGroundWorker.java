package com.sdsmdg.pulkit.callingtext;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Observable;

/**
 * Created by this pc on 08-08-2016.
 */
public class BackGroundWorker extends AsyncTask<String, Void, String> {
    private Context context;
    String caller, receiver;
    int n1;
    CallManager cm;
    public static String value = "";
    public static String gifId;
    public static String msg;
    resultInterface mCallback;
    DatabaseReference callertree = FirebaseDatabase.getInstance().getReference().child("caller");
    DatabaseReference receivertree = FirebaseDatabase.getInstance().getReference().child("receiver");
    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();

    public BackGroundWorker(Context context1, int n) {
        mCallback=(CallManager)new CallManager();
        context = context1;
        n1 = n;

    }
    public interface resultInterface
    {
        public void getContent(String s);
    }


    @Override
    protected String doInBackground(String... params) {
        if (n1 == 1) {
            caller = params[0];
            receiver = params[1];
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.e("in datachange","in datachange");
                    value = snapshot.child("caller").child(caller).child("receiver").getValue().toString();
                    Log.e("Value : ",value);
                    Log.e("Receiver : ",receiver);
                    gifId=snapshot.child("caller").child(caller).child("gifId").getValue().toString();
                    msg=snapshot.child("caller").child(caller).child("message").getValue().toString();
                    if (value.equals(receiver))
                    {
                        Log.e("in value","in value");
                        mCallback.getContent(msg+" "+gifId);
                    }

                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
            
            if(value.equals(caller))
            {
                Log.e("in value","in value");
               mCallback.getContent(msg+" "+gifId);
            }
        } else {
            caller = params[0];
            receiver = params[1];
            msg = params[2];
            gifId = params[3];
            callertree.child(caller).child("receiver").setValue(receiver);
            callertree.child(caller).child("message").setValue(msg);
            callertree.child(caller).child("gifId").setValue(gifId);
            receivertree.child(receiver).child("caller").setValue(caller);
            receivertree.child(receiver).child("message").setValue(msg);
            receivertree.child(receiver).child("gifId").setValue(gifId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

