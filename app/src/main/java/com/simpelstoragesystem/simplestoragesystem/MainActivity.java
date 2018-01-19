package com.simpelstoragesystem.simplestoragesystem;

import android.graphics.Color;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setIcon(R.drawable.ic_refresh_black_24dp);
        getSupportActionBar().setIcon(R.drawable.ic_settings_black_24dp);

        updateValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                updateValues();
                return true;

            case R.id.action_settings:
                Toaster.s(this,"Settings no yet implemented");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateValues(){
         Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

             TextView status = findViewById(R.id.status);
             ParticleDevice mDevice;

             @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         TextView textView = (TextView) findViewById(R.id.sensor0int);
                         textView.setText("Loading...");
                         textView = (TextView) findViewById(R.id.sensor1int);
                         textView.setText("Loading...");
                         textView = (TextView) findViewById(R.id.sensor2int);
                         textView.setText("Loading...");
                         textView = (TextView) findViewById(R.id.sensor3int);
                         textView.setText("Loading...");

                         status.setText("Connecting to Photon...");
                         status.setTextColor(Color.GRAY);
                     }
                 });

                 mDevice = particleCloud.getDevice(getIntent().getStringExtra(LoginActivity.PHOTONID_TYPE));

                 if(!mDevice.isConnected())
                 {
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                     status.setText("Photon is not connected to the Internet");
                     status.setTextColor(Color.RED);
                         }
                     });
                    return 0;
                 }

                 final List<String> sen0 = new Vector<String>();
                 final List<String> sen1 = new Vector<String>();
                 final List<String> sen2 = new Vector<String>();
                 final List<String> sen3 = new Vector<String>();

                 sen0.add("0");
                 sen1.add("1");
                 sen2.add("2");
                 sen3.add("3");

                 try {
                     final TextView textView = (TextView) findViewById(R.id.sensor0int);
                     final Object obj = mDevice.callFunction("getDistance", sen0);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             status.setText("Photon is connected to the Internet");
                             status.setTextColor(Color.GREEN);

                             textView.setText(obj.toString());
                         }
                     });
                 } catch (ParticleDevice.FunctionDoesNotExistException e) {
                 }

                 try {
                     final TextView textView = (TextView) findViewById(R.id.sensor1int);
                     final Object obj = mDevice.callFunction("getDistance", sen1);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             textView.setText(obj.toString());
                         }
                     });
                 } catch (ParticleDevice.FunctionDoesNotExistException e) {
                 }

                 try {
                     final TextView textView = (TextView) findViewById(R.id.sensor2int);
                     final Object obj = mDevice.callFunction("getDistance", sen2);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             textView.setText(obj.toString());
                         }
                     });
                 } catch (ParticleDevice.FunctionDoesNotExistException e) {
                 }

                 try {
                     final TextView textView = (TextView) findViewById(R.id.sensor3int);
                     final Object obj = mDevice.callFunction("getDistance", sen3);
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             textView.setText(obj.toString());
                         }
                     });
                 } catch (ParticleDevice.FunctionDoesNotExistException e) {
                 }

                 return 0;
             }

            @Override
            public void onSuccess(Object obj) {
             }

            @Override
            public void onFailure(ParticleCloudException exception) {
                status.setText("Photon is not connected to the Internet");
                status.setTextColor(Color.RED);
             }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ParticleCloudSDK.getCloud().logOut();
        Toaster.s(this, "You have been logged out!");
    }

}
