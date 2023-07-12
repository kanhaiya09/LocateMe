package com.example.kanhaiyalalyadav.map_location;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReceiveSms extends Activity {


    private static final String INBOX_URI = "content://sms/inbox";
    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private static ReceiveSms activity;
    private ArrayList<String> smsList = new ArrayList<String>();
    private ListView mListView;
    private ArrayAdapter<String> adapter;

    public static ReceiveSms instance() {
        return activity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receive_sms);

        mListView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(MyItemClickListener);

        if (ActivityCompat.shouldShowRequestPermissionRationale(ReceiveSms.this,
                Manifest.permission.READ_SMS)) {


        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(ReceiveSms.this,
                    new String[]{Manifest.permission.READ_SMS},
                    MY_PERMISSIONS_REQUEST_READ_SMS);

        }
        readSMS();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        activity = ReceiveSms.this;
    }


    public void readSMS() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse(INBOX_URI), null, null, null, null);

        int senderIndex = smsInboxCursor.getColumnIndex("address");
        int messageIndex = smsInboxCursor.getColumnIndex("body");

        if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;

        adapter.clear();

        do {

            String sender = smsInboxCursor.getString(senderIndex);
            String message = smsInboxCursor.getString(messageIndex);

            String formattedText = String.format(getResources().getString(R.string.sms_message), sender, message);

            adapter.add(Html.fromHtml(formattedText).toString());
        } while (smsInboxCursor.moveToNext());
    }

    public void updateList(final String newSms) {
        adapter.insert(newSms, 0);
        adapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener MyItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            try {
                String msg = adapter.getItem(pos);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReceiveSms.this,ShowMap.class);
                intent.putExtra(ShowMap.EXTRA_MESSAGE2,msg);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
}