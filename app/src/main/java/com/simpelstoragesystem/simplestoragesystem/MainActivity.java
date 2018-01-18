package com.simpelstoragesystem.simplestoragesystem;

import android.graphics.Color;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private ParticleDevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud,Object>() {

        TextView status = findViewById(R.id.status);

            @Override
            public Object callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                mDevice = particleCloud.getDevice(getIntent().getStringExtra(LoginActivity.PHOTONID_TYPE));

                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                status.setText("Photon is connected to the Internet");
                status.setTextColor(Color.GREEN);

            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                status.setText("Photon is not connected to the Internet");
                status.setTextColor(Color.RED);


            }
        });

        updateValues();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.refresh:
                updateValues();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateValues(){
         Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

             @Override
            public Object callApi(@NonNull ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {

                Object obj;
                List<String> sen0 = new Vector<String>();
                List<String> sen1 = new Vector<String>();
                List<String> sen2 = new Vector<String>();
                List<String> sen3 = new Vector<String>();

                sen0.add("0");
                sen1.add("1");
                sen2.add("2");
                sen3.add("3");

                obj = -1;

                // FIX THIS
                try {
                    obj = mDevice.callFunction("getDistance",sen0);
                    TextView temp = (TextView) findViewById(R.id.sensor0int);
                    temp.setText(obj.toString());
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    Toaster.s(MainActivity.this,"Data could not be received");
                }
/*

                try {
                    obj = mDevice.callFunction("getDistance",sen1);
                    TextView temp = (TextView) findViewById(R.id.sensor1int);
                    temp.setText(obj.toString());
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    Toaster.s(MainActivity.this,"Data could not be received");
                }

                try {
                    obj = mDevice.callFunction("getDistance",sen2);
                    TextView temp = (TextView) findViewById(R.id.sensor2int);
                    temp.setText(obj.toString());
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    Toaster.s(MainActivity.this,"Data could not be received");
                }

                try {
                    obj = mDevice.callFunction("getDistance",sen3);
                    TextView temp = (TextView) findViewById(R.id.sensor3int);
                    temp.setText(obj.toString());
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    Toaster.s(MainActivity.this,"Data could not be received");
                }
  */
                return obj;
            }

            @Override
            public void onSuccess(Object obj) {
                Toaster.s(MainActivity.this,"Data successfully pulled of the Particle cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Toaster.s(MainActivity.this,"Data unsuccessfully pulled of the Particle cloud");
            }
        });
    }
    @Override
    public void onBackPressed() {
        ParticleCloudSDK.getCloud().logOut();
        Toaster.s(this,"You have been logged out!");
        finish();
    }

}
