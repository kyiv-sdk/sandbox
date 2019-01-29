package com.example.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import javax.crypto.Cipher;

public class LoginManager {

    private static final String PASSWORD = "password";
    private SharedPreferences mPreferences;
    private FingerprintManager mFingerprintHelper;
    private Context mContext;

    private LoginInterface loginInterface;

    public LoginManager(Context context, SharedPreferences mPreferences, LoginInterface loginInterface) {
        this.mContext = context;
        this.mPreferences = mPreferences;
        this.loginInterface = loginInterface;

        if (mPreferences.contains(getPasswordKeyword())){
            prepareSensor();
        }
    }

    public String getPasswordKeyword() {
        return PASSWORD;
    }

    public boolean isUserAuthenticated(){
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

        return keyguardManager.isKeyguardSecure();
    }

    public void prepareLogin(final String password) {
        if (password.length() > 0) {
            if (!mPreferences.contains(PASSWORD)){
                if (isNewPasswordCorrect(password)){
                    savePassword(password);

                    loginInterface.onLoginSuccess();
                } else {
                    loginInterface.onExplainingNeed("password is incorrect. Be sure that it has more than 5 symbols");
                }
            } else {
                if (isPasswordCorrect(password)){
                    loginInterface.onLoginSuccess();
                } else {
                    loginInterface.onExplainingNeed("password is incorrect");
                }
            }
        } else {
            loginInterface.onExplainingNeed("password is empty");
        }
    }

    public boolean isPasswordCorrect(String password){
        try {
            String encoded = mPreferences.getString(PASSWORD, null);
            String decoded = CryptoManager.decryptData(encoded);

            if (decoded != null && decoded.equals(password)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isNewPasswordCorrect(String password){
        return password.length() >= 5;
    }

    public void savePassword(String password) {
        String encoded = CryptoManager.encode(password);
        mPreferences.edit().putString(PASSWORD, encoded).apply();

        loginInterface.onExplainingNeed("password saved");
    }

    public void prepareSensor() {
        if (com.example.fingerprint.FingerprintHelper.isSensorStateAt(FingerprintHelper.mSensorState.READY, mContext)) {
            FingerprintManagerCompat.CryptoObject cryptoObject = CryptoManager.getCryptoObject();
            if (cryptoObject != null) {
                loginInterface.onExplainingNeed("use fingerprint to login");
                mFingerprintHelper = new FingerprintManager(mContext);
                mFingerprintHelper.startAuth(cryptoObject);
            } else {
                mPreferences.edit().remove(PASSWORD).apply();
                loginInterface.onExplainingNeed("new fingerprint enrolled. enter password again");
            }

        } else {
            loginInterface.onExplainingNeed("sensor is not ready");
        }
    }

    public void close(){
        cancelFingerprint();
    }

    private void cancelFingerprint(){
        if (mFingerprintHelper != null) {
            mFingerprintHelper.cancel();
        }
    }

    private class FingerprintManager extends FingerprintManagerCompat.AuthenticationCallback{
        private Context mContext;
        private CancellationSignal mCancellationSignal;

        FingerprintManager(Context context) {
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
            loginInterface.onExplainingNeed(errString.toString());
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            loginInterface.onExplainingNeed(helpString.toString());
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            Cipher cipher = result.getCryptoObject().getCipher();
            String encoded = mPreferences.getString(PASSWORD, null);
            String decoded = CryptoManager.decode(encoded, cipher);
            if (decoded != null) {
                loginInterface.onExplainingNeed("fingerprint success");
            } else {
                loginInterface.onExplainingNeed("decoding failed");
            }
            loginInterface.onAuthSucceeded(decoded);
        }

        @Override
        public void onAuthenticationFailed() {
            loginInterface.onExplainingNeed("try fingerprint again");
        }
    }
}
