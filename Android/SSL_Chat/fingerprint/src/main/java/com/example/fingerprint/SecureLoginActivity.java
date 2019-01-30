package com.example.fingerprint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecureLoginActivity extends AppCompatActivity implements LoginInterface {

    private EditText mPasswordEditText;

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_login);

        mPasswordEditText = findViewById(R.id.secure_activity_password_edittext);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loginManager = new LoginManager(this, sharedPreferences, this);

        if (!loginManager.isUserAuthenticated()){
            onExplainingNeed("User is not authenticated.\nYou should set password on your phone to work with this application");
        }

        Button enter_password_button = findViewById(R.id.secure_activity_enter_password_button);

        enter_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loginManager.isUserAuthenticated()){
                    onExplainingNeed("User is not authenticated.\nYou should set password on your phone to work with this application");
                } else {
                    loginManager.prepareLogin(mPasswordEditText.getText().toString());
                }
            }
        });

        if (!sharedPreferences.contains(loginManager.getPasswordKeyword())){
            enter_password_button.setText("sign up");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginManager.close();
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onLoginSuccess() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onAuthSucceeded(String password) {
        mPasswordEditText.setText(password);
        loginManager.prepareLogin(mPasswordEditText.getText().toString());
    }

    @Override
    public void onExplainingNeed(String explaining) {
        Toast.makeText(this, explaining, Toast.LENGTH_LONG).show();
    }
}
