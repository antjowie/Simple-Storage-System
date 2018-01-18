package com.simpelstoragesystem.simplestoragesystem;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class LoginActivity extends AppCompatActivity {

    public static final String PHOTONID_TYPE = "com.simplestoragesystem.simplestoragesystem.photonId";
    public static final String USER_TYPE = "com.simplestoragesystem.simplestoragesystem.userId";
    public static final String PASSWORD_TYPE = "com.simplestoragesystem.simplestoragesystem.passwordId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParticleCloudSDK.init(LoginActivity.this);
        setContentView(R.layout.activity_login);

        final Button signIn = findViewById(R.id.button);
        signIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String email = ((EditText) findViewById(R.id.editTextMail)).getText().toString();
                final String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

                Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud,Object>() {

                    ParticleDevice mDevice;
                    Boolean productFound = false;

                    @Override
                    public Object callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {

                        ParticleCloudSDK.getCloud().logIn(email,password);
                        List<ParticleDevice> devices = ParticleCloudSDK.getCloud().getDevices();
                        for(ParticleDevice device:devices)
                            if(device.getName().equals("SimpleStorageSystem"))
                            {
                                mDevice = device;
                                productFound = true;
                            }

                        return -1;
                    }

                    @Override
                    public void onSuccess(Object o) {
                        Toaster.s(LoginActivity.this,"Succeeded, logged in with account id " + ParticleCloudSDK.getCloud().getLoggedInUsername());
                        if(!productFound)
                            Toaster.l(LoginActivity.this,"Login cancelled, the product photon has not been found");
                        else
                        {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.putExtra(PHOTONID_TYPE,mDevice.getID());
                            intent.putExtra(USER_TYPE,email);
                            intent.putExtra(PASSWORD_TYPE,password);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(ParticleCloudException exception) {
                        Toaster.s(LoginActivity.this,exception.getBestMessage());
                    }
                });
            }
        });
    }
}
