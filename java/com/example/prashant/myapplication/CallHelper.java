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
                  /*  else
                    {
                        Toast.makeText(ctx, "Record not found!", Toast.LENGTH_LONG).show();
                    }*/

                /*    WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                            RadioGroup.LayoutParams.MATCH_PARENT,
                            RadioGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT |
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSPARENT);

                    params.height = RadioGroup.LayoutParams.MATCH_PARENT;
                    params.width = RadioGroup.LayoutParams.MATCH_PARENT;
                    params.format = PixelFormat.TRANSLUCENT;

                    params.gravity = Gravity.TOP;

                    LinearLayout ly = new LinearLayout(ctx);
                    ly.setBackgroundColor(Color.RED);
                    ly.setOrientation(LinearLayout.VERTICAL);
                    //Inflater inflater;
                    //RelativeLayout overlay = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
/*
                    _av = new ActivatorView(this);
                    _avLayoutParams = new WindowManager.LayoutParams(0, 0, 0, 0,
                            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            PixelFormat.OPAQUE);
                    _avLayoutParams.screenBrightness = _fScreenBrightness = 20f;


                    wm.addView(_av, _avLayoutParams);

                    View v = View.inflate(ctx,R.layout.test, null);
                    wm.addView(v, params);*/
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
                for (int i=0;i<2;i++)
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
