package com.example.prashant.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.zip.Inflater;

/**
 * Created by Prashant on 05-Apr-2015.
 */
public class CallHelper  {

    private Context ctx;
    private TelephonyManager tm;
    private CallStateListener callStateListener;

    private OutgoingReceiver outgoingReceiver;
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    /**
     * Listener to detect incoming calls.
     */
    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING: // called when someone is ringing to this phone

                   // Toast.makeText(ctx, "Testing!", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                    Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, incomingNumber, null, null );
                    if(cursor.moveToFirst()){
                        incomingNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    }
                    cursor.close();

                    Cursor mCursor = db.query("mytable", new String[] {"name","message"}, "name" + "='" + incomingNumber+"'", null,
                                    null, null, null, null);

                    if (mCursor != null && mCursor.moveToFirst()) {
                        String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                        for (int i=0;i<2;i++)
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                    }

                    break;

            }
        }
    }


    private class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            //Toast.makeText(ctx, number, Toast.LENGTH_LONG).show();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = ctx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, number, null, null );
            if(cursor.moveToFirst()){
                number = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();

            Cursor mCursor = db.query("mytable", new String[] {"name","message"}, "name" + "='" + number+"'", null,
                    null, null, null, null);

            if (mCursor != null && mCursor.moveToFirst()) {
                String msg = mCursor.getString(mCursor.getColumnIndex("message"));
                for (int i=0;i<1;i++)
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
            }
        }

    }

    public CallHelper(Context ctx) {
        this.ctx = ctx;

        callStateListener = new CallStateListener();
        mDbHelper = new FeedReaderDbHelper(ctx);
        db = mDbHelper.getReadableDatabase();
        outgoingReceiver = new OutgoingReceiver();
    }

    /**
     * Start calls detection.
     */
    public void start() {
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

       IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
       ctx.registerReceiver(outgoingReceiver, intentFilter);
    }

    /**
     * Stop calls detection.
     */
    public void stop() {
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        ctx.unregisterReceiver(outgoingReceiver);
    }
}
