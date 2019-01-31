package com.example.fingerprint;

import android.app.KeyguardManager;
import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;

public class LoginManager {
    private final String USER_CREDENTIALS_FILENAME = "SUPER_SECRET_FILE";

    private Context mContext;

    private FingerprintManager mFingerprintHelper;
    private LoginInterface loginInterface;
    private CryptoManager cryptoManager;

    private boolean alreadySignedUp;

    public LoginManager(Context context, LoginInterface loginInterface) {
        this.mContext = context;
        this.loginInterface = loginInterface;
        this.cryptoManager = CryptoManager.getInstance();
        this.alreadySignedUp = FileUtils.fileExists(mContext, USER_CREDENTIALS_FILENAME);
        if (alreadySignedUp){
//            prepareSensor();
        }
    }

    public boolean isUserAuthenticated(){
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

        return keyguardManager.isKeyguardSecure();
    }

    public void prepareSignUp(final String ip, final String socket, final String username) {
        if (!isUserAuthenticated()){
            loginInterface.onExplainingNeed("User is not authenticated.\nYou should set password on your phone to work with this application");
            return;
        }

        if (ip.length() > 0 && socket.length() > 0 && username.length() > 0) {

            UserCredentials userCredentials = new UserCredentials(ip, socket, username);

            String encoded = cryptoManager.encode(userCredentials.toJSON().toString());

            if (FileUtils.writeToFile(mContext, USER_CREDENTIALS_FILENAME, encoded.getBytes())){
                loginInterface.onLoginSuccess(ip, socket ,username);
            } else {
                loginInterface.onExplainingNeed("Some issue with internal file system");
            }
        }
    }

    public boolean isAlreadySignedUp(){
        return alreadySignedUp;
    }

    public void prepareSensor() {
        if (com.example.fingerprint.FingerprintHelper.isSensorStateAt(FingerprintHelper.mSensorState.READY, mContext)) {
            FingerprintManagerCompat.CryptoObject cryptoObject = cryptoManager.getCryptoObject();
            if (cryptoObject != null) {
                loginInterface.onExplainingNeed("use fingerprint to login");
                mFingerprintHelper = new FingerprintManager(mContext);
                mFingerprintHelper.startAuth(cryptoObject);
            } else {
                loginInterface.onExplainingNeed("user is not authenticated. be sure that you enter password within 30 second after phone unlocked");
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

    public void onAuthSucceeded(){
        byte[] encodedFile = FileUtils.readFile(mContext, USER_CREDENTIALS_FILENAME);
        String decodedFile = cryptoManager.decode(new String(encodedFile));

        if (decodedFile == null){
            loginInterface.onExplainingNeed("Key is invalid. Restart Your app.");
            loginInterface.onLoginFailed();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(decodedFile);
            UserCredentials userCredentials = new UserCredentials(jsonObject);

            if (userCredentials.valid()){
                loginInterface.onLoginSuccess(userCredentials.getIp(), userCredentials.getSocket(), userCredentials.getUsername());
            } else {
                loginInterface.onExplainingNeed("decode failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAuthSucceeded(Cipher cipher){
        byte[] encodedFile = FileUtils.readFile(mContext, USER_CREDENTIALS_FILENAME);
        String decodedFile = cryptoManager.decode(new String(encodedFile), cipher);

        if (decodedFile == null){
            loginInterface.onExplainingNeed("Key is invalid. Restart Your app.");
            loginInterface.onLoginFailed();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(decodedFile);
            UserCredentials userCredentials = new UserCredentials(jsonObject);

            if (userCredentials.valid()){
                loginInterface.onLoginSuccess(userCredentials.getIp(), userCredentials.getSocket(), userCredentials.getUsername());
            } else {
                loginInterface.onExplainingNeed("decode failed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            onAuthSucceeded(cipher);
        }

        @Override
        public void onAuthenticationFailed() {
            loginInterface.onExplainingNeed("try fingerprint again");
        }
    }
}
