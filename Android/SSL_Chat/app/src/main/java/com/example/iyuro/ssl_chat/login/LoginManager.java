package com.example.iyuro.ssl_chat.login;

import android.app.KeyguardManager;
import android.content.Context;
import android.widget.Toast;

import com.example.crypto.CryptoManager;
import com.example.internal_storage_utils.InternalStorageUtils;
import com.example.iyuro.ssl_chat.ChatManager;
import com.example.iyuro.ssl_chat.network.ChatMessage;
import com.example.iyuro.ssl_chat.network.MessageProtocol;
import com.example.iyuro.ssl_chat.network.NetworkInterface;
import com.example.iyuro.ssl_chat.network.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class LoginManager implements NetworkInterface {
    private final String USER_CREDENTIALS_FILENAME = "SUPER_SECRET_FILE";

    private Context mContext;

    private CryptoManager cryptoManager;
    private LoginInterface loginInterface;

    private boolean alreadySignedUp;

    private String android_id;

    public LoginManager(Context context, LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        this.mContext = context;
        this.cryptoManager = CryptoManager.getInstance();

        this.alreadySignedUp = InternalStorageUtils.fileExists(mContext, USER_CREDENTIALS_FILENAME);
    }

    private void logIn(String username){
        NetworkManager.getInstance().setNetworkInterface(this);

//        ChatManager.getInstance().openConnection(userCredentials.getIp(), Integer.parseInt(userCredentials.getPort()), true, android_id);
        
        byte[] request = MessageProtocol.getInstance().createLoginRequest(username).getBytes();
        if (NetworkManager.getInstance().isConnectionOpen()) {
            NetworkManager.getInstance().send(request);
        } else {
            loginInterface.onConnectionClosed();
        }
    }

    @Override
    public void onMessageReceive(final ChatMessage chatMessage) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
                if (chatMessage.getMessage().equals("ok")){
                    loginInterface.onLoginSuccess();
                } else {
                    loginInterface.onLoginFailed();
                }
//            }
//        });
    }

    private boolean isDeviceSecure(){
        KeyguardManager keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

        return keyguardManager.isKeyguardSecure();
    }

    private void saveCredentials(String ip, String port, String username) {
        if (!isDeviceSecure()){
            loginInterface.onExplainingNeed("User is not authenticated.\nYou should set password on your phone to work with this application");
            return;
        }

        if ((ip != null && ip.length() > 0) &&
                (port != null && port.length() > 0) &&
                (username != null && username.length() > 0)) {

            UserCredentials userCredentials = new UserCredentials(ip, port, username);

            String encoded = cryptoManager.encode(userCredentials.toJSON().toString());

            if (InternalStorageUtils.writeToFile(mContext, USER_CREDENTIALS_FILENAME, encoded.getBytes())){
                logIn(username);
            } else {
                loginInterface.onExplainingNeed("Some issue with internal file system");
            }
        }
    }

    private boolean isAlreadySignedUp(){
        return alreadySignedUp;
    }

    private UserCredentials getCredentials(){
        byte[] encodedFile = InternalStorageUtils.readFile(mContext, USER_CREDENTIALS_FILENAME);
        String decodedFile = cryptoManager.decode(new String(encodedFile));

        if (decodedFile == null){
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(decodedFile);
            UserCredentials userCredentials = new UserCredentials(jsonObject);

            if (userCredentials.valid()){
                return userCredentials;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void signIn(int resultCode){
        if (resultCode == RESULT_OK) {
            UserCredentials userCredentials = getCredentials();
            logIn(userCredentials.getUsername());
        } else {
            loginInterface.showAuthScreen();
        }
    }

    public void signUp(String ip, String port, String username){

    }

    public void prepareLogIn(String android_id){
        if (!isDeviceSecure()){
            loginInterface.onDeviceNotSecure();
            return;
        }

        if (!isAlreadySignedUp()){
            loginInterface.showSignUpScreen();
        } else {
            loginInterface.showAuthScreen();
        }
    }
}
