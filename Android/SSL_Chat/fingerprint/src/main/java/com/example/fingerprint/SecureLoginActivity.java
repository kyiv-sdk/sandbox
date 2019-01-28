package com.example.fingerprint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.crypto.Cipher;

public class SecureLoginActivity extends AppCompatActivity {

    private static final String PIN = "pin";
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private SharedPreferences mPreferences;
    private FingerprintHelper mFingerprintHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_login);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPasswordEditText = findViewById(R.id.secure_activity_password_edittext);
        mUsernameEditText = findViewById(R.id.secure_activity_username_edittext);

        String oldUsername = mPreferences.getString(getString(R.string.current_username_entry_key), null);
        if (oldUsername != null){
            mUsernameEditText.setText(oldUsername);
        }

        findViewById(R.id.secure_activity_enter_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareLogin();
            }
        });
    }

    public static boolean checkFingerprintCompatibility(@NonNull Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    public enum SensorState {
        NOT_SUPPORTED,
        NOT_BLOCKED, // если устройство не защищено пином, рисунком или паролем
        NO_FINGERPRINTS, // если на устройстве нет отпечатков
        READY
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreferences.contains(PIN)) {
            prepareSensor();
        } else {
            Toast.makeText(this, "There is no old pin", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFingerprintHelper != null) {
            mFingerprintHelper.cancel();
        }
    }

    private void prepareLogin() {
        final String pin = mPasswordEditText.getText().toString();
        if (pin.length() > 0) {
            if (isPinCorrect(pin)){
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Toast.makeText(this, "Pin is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPinCorrect(String pin){
        if (pin.length() > 5){
            String username = mUsernameEditText.getText().toString();
            saveUsername(username);
            savePin(pin);

            Toast.makeText(this, "Pin saved.", Toast.LENGTH_SHORT).show();

            madeLogin(username, pin);

        } else {
            Toast.makeText(this, "Pin is incorrect. Be sure that it has more than 5 symbols.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void madeLogin(String username, String password){

    }

    private void saveUsername(String username){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(getString(R.string.current_username_entry_key), username).apply();
    }

    private void savePin(String pin) {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, this)) {
            String encoded = CryptoUtils.encode(pin);
            mPreferences.edit().putString(PIN, encoded).apply();
        }
    }

    private void prepareSensor() {
        if (FingerprintUtils.isSensorStateAt(FingerprintUtils.mSensorState.READY, this)) {
            FingerprintManagerCompat.CryptoObject cryptoObject = CryptoUtils.getCryptoObject();
            if (cryptoObject != null) {
                Toast.makeText(this, "use fingerprint to login", Toast.LENGTH_LONG).show();
                mFingerprintHelper = new FingerprintHelper(this);
                mFingerprintHelper.startAuth(cryptoObject);
            } else {
                mPreferences.edit().remove(PIN).apply();
                Toast.makeText(this, "new fingerprint enrolled. enter pin again", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "sensor is not ready", Toast.LENGTH_LONG).show();
        }
    }

    public class FingerprintHelper extends FingerprintManagerCompat.AuthenticationCallback{
        private Context mContext;
        private CancellationSignal mCancellationSignal;

        FingerprintHelper(Context context) {
            mContext = context;
        }

        void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
            mCancellationSignal = new CancellationSignal();
            FingerprintManagerCompat manager = FingerprintManagerCompat.from(mContext);
            manager.authenticate(cryptoObject, 0, mCancellationSignal, this, null);
        }

        void cancel() {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            Cipher cipher = result.getCryptoObject().getCipher();
            String encoded = mPreferences.getString(PIN, null);
            String decoded = CryptoUtils.decode(encoded, cipher);
            mPasswordEditText.setText(decoded);
            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(mContext, "try again", Toast.LENGTH_SHORT).show();
        }
    }
}
