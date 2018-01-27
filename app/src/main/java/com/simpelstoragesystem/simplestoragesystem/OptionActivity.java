package com.simpelstoragesystem.simplestoragesystem;

import android.app.VoiceInteractor;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.List;
import java.util.Vector;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class OptionActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    private TextView item0;
    private TextView item1;
    private TextView item2;
    private TextView item3;

    private float item3length;
    private float item0length;
    private float item1length;
    private float item2length;

    EditText sensor0amount;
    EditText sensor1amount;
    EditText sensor2amount;
    EditText sensor3amount;
    EditText boxHeigth;

    public static String BOXHEIGTH_KEY = "com.simplestoragesystem.simplestoragesystem.boxHeigthKey";
    private static String SENSOR0AMOUNT_KEY = "com.simplestoragesystem.simplestoragesystem.sensor0amountKey";
    private static String SENSOR1AMOUNT_KEY = "com.simplestoragesystem.simplestoragesystem.sensor1amountKey";
    private static String SENSOR2AMOUNT_KEY = "com.simplestoragesystem.simplestoragesystem.sensor2amountKey";
    private static String SENSOR3AMOUNT_KEY = "com.simplestoragesystem.simplestoragesystem.sensor3amountKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        mPreferences = getSharedPreferences(MainActivity.mPrefFile, MODE_PRIVATE);

        item0 = findViewById(R.id.item0);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        item0.setText(mPreferences.getString(MainActivity.SENSOR0_KEY, "Sensor0"));
        item1.setText(mPreferences.getString(MainActivity.SENSOR1_KEY, "Sensor1"));
        item2.setText(mPreferences.getString(MainActivity.SENSOR2_KEY, "Sensor2"));
        item3.setText(mPreferences.getString(MainActivity.SENSOR3_KEY, "Sensor3"));

        item0length = mPreferences.getFloat(MainActivity.ITEM0LENGTH_KEY, 0);
        item1length = mPreferences.getFloat(MainActivity.ITEM1LENGTH_KEY, 0);
        item2length = mPreferences.getFloat(MainActivity.ITEM2LENGTH_KEY, 0);
        item3length = mPreferences.getFloat(MainActivity.ITEM3LENGTH_KEY, 0);

        sensor0amount = findViewById(R.id.sensor0amount);
        sensor1amount = findViewById(R.id.sensor1amount);
        sensor2amount = findViewById(R.id.sensor2amount);
        sensor3amount = findViewById(R.id.sensor3amount);
        sensor0amount.setText(mPreferences.getString(SENSOR0AMOUNT_KEY, "1"));
        sensor1amount.setText(mPreferences.getString(SENSOR1AMOUNT_KEY, "1"));
        sensor2amount.setText(mPreferences.getString(SENSOR2AMOUNT_KEY, "1"));
        sensor3amount.setText(mPreferences.getString(SENSOR3AMOUNT_KEY, "1"));

        boxHeigth = findViewById(R.id.boxHeightText);
        boxHeigth.setText(mPreferences.getString(BOXHEIGTH_KEY, "25"));

        Button apply = findViewById(R.id.button_apply);
        apply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toaster.s(OptionActivity.this, "Changes applied!");

                SharedPreferences.Editor prefEdit = mPreferences.edit();

                prefEdit.putString(MainActivity.SENSOR0_KEY, item0.getText().toString());
                prefEdit.putString(MainActivity.SENSOR1_KEY, item1.getText().toString());
                prefEdit.putString(MainActivity.SENSOR2_KEY, item2.getText().toString());
                prefEdit.putString(MainActivity.SENSOR3_KEY, item3.getText().toString());

                prefEdit.putString(SENSOR0AMOUNT_KEY, sensor0amount.getText().toString());
                prefEdit.putString(SENSOR1AMOUNT_KEY, sensor1amount.getText().toString());
                prefEdit.putString(SENSOR2AMOUNT_KEY, sensor2amount.getText().toString());
                prefEdit.putString(SENSOR3AMOUNT_KEY, sensor3amount.getText().toString());

                prefEdit.putFloat(MainActivity.ITEM0LENGTH_KEY, item0length);
                prefEdit.putFloat(MainActivity.ITEM1LENGTH_KEY, item1length);
                prefEdit.putFloat(MainActivity.ITEM2LENGTH_KEY, item2length);
                prefEdit.putFloat(MainActivity.ITEM3LENGTH_KEY, item3length);

                prefEdit.putString(BOXHEIGTH_KEY, boxHeigth.getText().toString());

                prefEdit.apply();
                finish();
            }
        });

        final Button calibrateLength = findViewById(R.id.calibrateLength);
        calibrateLength.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

                    double id = Double.parseDouble(boxHeigth.getText().toString());

                    @Override
                    public Object callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                        ParticleDevice device = null;
                        Boolean productFound = false;

                        for(ParticleDevice iter: ParticleCloudSDK.getCloud().getDevices())
                        {
                            if(productFound)
                                break;
                            if(iter.getName().equals("SimpleStorageSystem"))
                            {
                                device = iter;
                                productFound = true;
                            }
                        }

                        if(!productFound)
                            return -1;

                        if(id > 4)
                        {
                            Toaster.s(OptionActivity.this,"Variable exceeded sensor cap");
                            return -1;
                        }

                        Vector<String> sensor = new Vector<>();
                        sensor.add(Integer.toString((int) id));

                        try {
                            device.callFunction("updateSensor",sensor);
                        } catch (ParticleDevice.FunctionDoesNotExistException e) {
                            e.printStackTrace();
                        }

                        String boxHeightString = "";
                        try {
                            boxHeightString = Double.toString(device.getDoubleVariable("sensor0cm"));
                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            e.printStackTrace();
                        }


                        final String finalBoxHeightString = boxHeightString;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                switch ((int) id) {
                                    case 0:
                                        boxHeigth.setText(finalBoxHeightString);
                                        break;
                                    case 1:
                                        boxHeigth.setText(finalBoxHeightString);
                                        break;
                                    case 2:
                                        boxHeigth.setText(finalBoxHeightString);
                                        break;
                                    case 3:
                                        boxHeigth.setText(finalBoxHeightString);
                                        break;
                                    }
                            }});
                        return 0;
                    }

                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onFailure(ParticleCloudException exception) {
                        Toaster.s(OptionActivity.this,"An error has occurred while calling the Particle Cloud");
                    }
                });
            }
        });

        final Button calibrate0 = findViewById(R.id.sensor0calibrate);
        calibrate0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItemHeight(0,Integer.parseInt(sensor0amount.getText().toString()));
            }
        });

        final Button calibrate1 = findViewById(R.id.sensor1calibrate);
        calibrate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItemHeight(1,Integer.parseInt(sensor1amount.getText().toString()));
            }
        });

        final Button calibrate2 = findViewById(R.id.sensor2calibrate);
        calibrate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateItemHeight(2,Integer.parseInt(sensor2amount.getText().toString()));
            }
        });

        final Button calibrate3 = findViewById(R.id.sensor3calibrate);
        calibrate3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateItemHeight(3,Integer.parseInt(sensor3amount.getText().toString()));
            }
        });
    }

    private void updateItemHeight(final int sensorId, final int amount) {
        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            float maxDistance = 0;

            float tempHeight;

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                if(boxHeigth.getText().toString().isEmpty())
                {
                    Toaster.s(OptionActivity.this,"Box length is invalid");
                    return -3;
                }
                maxDistance = Float.parseFloat(boxHeigth.getText().toString());

                ParticleDevice device = null;
                Boolean productFound = false;
                for(ParticleDevice iter: ParticleCloudSDK.getCloud().getDevices())
                {
                    if(productFound)
                        break;
                    if(iter.getName().equals("SimpleStorageSystem"))
                    {
                        device = iter;
                        productFound = true;
                    }
                }
                if(device == null)
                    return -1;

                Vector<String> id = new Vector<String>();
                id.add(Integer.toString(sensorId));

                tempHeight = 0;
                try {
                    if(device.callFunction("updateSensor", id) == 0)
                    switch (sensorId)
                    {
                        case 0:
                            tempHeight = (float) device.getDoubleVariable("sensor0cm");
                            break;
                        case 1:
                            tempHeight = (float) device.getDoubleVariable("sensor1cm");
                            break;
                        case 2:
                            tempHeight = (float) device.getDoubleVariable("sensor2cm");
                            break;
                        case 3:
                            tempHeight = (float) device.getDoubleVariable("sensor3cm");
                            break;
                    }
                    else
                        Toaster.s(OptionActivity.this,"Error updating sensor value");
                } catch (ParticleDevice.FunctionDoesNotExistException e) {
                    e.printStackTrace();
                    return -2;
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    e.printStackTrace();
                }

                return 0;
            }

            @Override
            public void onSuccess(Object o) {

            }

            @Override
            public void onFailure(ParticleCloudException exception) {

            }

            @Override
            public void onTaskFinished() {
                super.onTaskFinished();
                float height = 0;

                if(tempHeight >= maxDistance || amount <= 0)
                    Toaster.s(OptionActivity.this, "Invalid amount or max length value");
                else if (tempHeight == 0)
                    Toaster.s(OptionActivity.this, "Login session invalid, please login again");
                else
                    height =(maxDistance - tempHeight) / (float)amount;

                switch (sensorId)
                {
                    case 0:
                        item0length = height;
                        break;
                    case 1:
                        item1length = height;
                        break;
                    case 2:
                        item2length = height;
                        break;
                    case 3:
                        item3length = height;
                        break;
                }
                if(height != 0)
                    Toaster.s(OptionActivity.this,"A height of " + Float.toString(height) + " cm has been calculated");
            }
        });
    }
}

