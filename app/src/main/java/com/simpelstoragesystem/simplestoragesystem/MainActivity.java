package com.simpelstoragesystem.simplestoragesystem;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.w3c.dom.Text;

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

    private SharedPreferences mPreferences;
    public static String mPrefFile = "com.simplestoragesystem.simplestoragesystem.sensorData";
    public static String SENSOR0_KEY = "com.simplestoragesystem.simplestoragesystem.sensor0key";
    public static String SENSOR1_KEY = "com.simplestoragesystem.simplestoragesystem.sensor1key";
    public static String SENSOR2_KEY = "com.simplestoragesystem.simplestoragesystem.sensor2key";
    public static String SENSOR3_KEY = "com.simplestoragesystem.simplestoragesystem.sensor3key";

    public static String ITEM0LENGTH_KEY = "com.simplestoragesystem.simplestoragesystem.item0lengthKey";
    public static String ITEM1LENGTH_KEY = "com.simplestoragesystem.simplestoragesystem.item1lengthKey";
    public static String ITEM2LENGTH_KEY = "com.simplestoragesystem.simplestoragesystem.item2lengthKey";
    public static String ITEM3LENGTH_KEY = "com.simplestoragesystem.simplestoragesystem.item3lengthKey";

    private float mBoxHeigth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(mPrefFile,MODE_PRIVATE);

        TextView button = findViewById(R.id.sensor0);
        button.setText(mPreferences.getString(SENSOR0_KEY,"Sensor0"));
        button = findViewById(R.id.sensor1);
        button.setText(mPreferences.getString(SENSOR1_KEY,"Sensor1"));
        button = findViewById(R.id.sensor2);
        button.setText(mPreferences.getString(SENSOR2_KEY,"Sensor2"));
        button = findViewById(R.id.sensor3);
        button.setText(mPreferences.getString(SENSOR3_KEY,"Sensor3"));

        mBoxHeigth = Float.parseFloat(mPreferences.getString(OptionActivity.BOXHEIGTH_KEY,"25"));

        getSupportActionBar().setIcon(R.drawable.ic_refresh_white_24dp);
        getSupportActionBar().setIcon(R.drawable.ic_settings_white_24dp);

        updateValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView button = findViewById(R.id.sensor0);
        button.setText(mPreferences.getString(SENSOR0_KEY,"Sensor0"));
        button = findViewById(R.id.sensor1);
        button.setText(mPreferences.getString(SENSOR1_KEY,"Sensor1"));
        button = findViewById(R.id.sensor2);
        button.setText(mPreferences.getString(SENSOR2_KEY,"Sensor2"));
        button = findViewById(R.id.sensor3);
        button.setText(mPreferences.getString(SENSOR3_KEY,"Sensor3"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                updateValues();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this, OptionActivity.class);
                startActivity(intent);
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
                         textView.setTextColor(Color.rgb(148,148,148));
                         textView = (TextView) findViewById(R.id.sensor1int);
                         textView.setText("Loading...");
                         textView.setTextColor(Color.rgb(148,148,148));
                         textView = (TextView) findViewById(R.id.sensor2int);
                         textView.setText("Loading...");
                         textView.setTextColor(Color.rgb(148,148,148));
                         textView = (TextView) findViewById(R.id.sensor3int);
                         textView.setText("Loading...");
                         textView.setTextColor(Color.rgb(148,148,148));

                         status.setText("Connecting to Photon...");
                         status.setTextColor(Color.rgb(148,148,148));
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
                             status.setTextColor(Color.rgb(63, 81, 181));

                             final float length = mPreferences.getFloat(ITEM0LENGTH_KEY, 0);
                             if (length == 0) {
                                 textView.setText("Not calibrated");
                                 textView.setTextColor(Color.RED);
                             } else {
                                 float amount = (mBoxHeigth - Float.parseFloat(obj.toString())) / length;
                                 if(amount % 1 > 0.5)
                                     amount += 1;
                                 textView.setText(Integer.toString((int)amount));
                             }
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
                             final float length = mPreferences.getFloat(ITEM1LENGTH_KEY, 0);
                             if (length == 0) {
                                 textView.setText("Not calibrated");
                                 textView.setTextColor(Color.RED);
                             } else {
                                 float amount = (mBoxHeigth - Float.parseFloat(obj.toString())) / length;
                                 if(amount % 1 > 0.5)
                                     amount += 1;
                                 textView.setText(Integer.toString((int)amount));
                             }
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
                             final float length = mPreferences.getFloat(ITEM2LENGTH_KEY, 0);
                             if (length == 0) {
                                 textView.setText("Not calibrated");
                                 textView.setTextColor(Color.RED);
                             } else {
                                 float amount = (mBoxHeigth - Float.parseFloat(obj.toString())) / length;
                                 if(amount % 1 > 0.5)
                                     amount += 1;
                                 textView.setText(Integer.toString((int)amount));
                             }
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
                             final float length = mPreferences.getFloat(ITEM3LENGTH_KEY, 0);
                             if (length == 0) {
                                 textView.setText("Not calibrated");
                                 textView.setTextColor(Color.RED);
                             } else {
                                 float amount = (mBoxHeigth - Float.parseFloat(obj.toString())) / length;
                                 if(amount % 1 > 0.5)
                                     amount += 1;
                                 textView.setText(Integer.toString((int)amount));
                             }
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
