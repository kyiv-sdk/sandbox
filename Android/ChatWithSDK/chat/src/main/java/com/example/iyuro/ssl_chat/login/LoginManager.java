package com.example.iyuro.ssl_chat.login;

import android.content.Context;
import android.provider.Settings;

import com.example.internal_storage_utils.InternalStorageUtils;
import com.example.iyuro.ssl_chat.ChatManager;
import com.example.iyuro.ssl_chat.network.ChatMessage;
import com.example.iyuro.ssl_chat.network.MessageProtocol;
import com.example.iyuro.ssl_chat.network.NetworkInterface;
import com.example.iyuro.ssl_chat.network.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginManager implements NetworkInterface {
    private final String USER_CREDENTIALS_FILENAME = "SUPER_SECRET_FILE";

    private Context mContext;

    private boolean alreadySignedUp;
    private LoginInterface loginInterface;
    private UserCredentials userCredentialsForLogin;

    public LoginManager(Context context, LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        this.mContext = context;

        this.alreadySignedUp = InternalStorageUtils.fileExists(USER_CREDENTIALS_FILENAME);
        this.userCredentialsForLogin = null;
    }

    public void prepareLogIn(){

        if (!isAlreadySignedUp()){
            loginInterface.showSignUpScreen();
        } else {
            signIn();
        }
    }

    private void logIn(UserCredentials userCredentials){
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChatManager.getInstance().setCurrentUserID(userCredentials.getUsername());
        ChatManager.getInstance().openConnection(userCredentials.getIp(), Integer.parseInt(userCredentials.getPort()), true, android_id);

        NetworkManager.getInstance().setNetworkInterface(this);

        byte[] request = MessageProtocol.getInstance().createLoginRequest(userCredentials.getUsername()).getBytes();
        if (NetworkManager.getInstance().isConnectionOpen()) {
            NetworkManager.getInstance().send(request);
        } else {
            loginInterface.onConnectionClosed();
        }
    }

    private boolean isAlreadySignedUp(){
        return alreadySignedUp;
    }

    private UserCredentials getCredentials(){
        byte[] file = InternalStorageUtils.readFile(USER_CREDENTIALS_FILENAME);

        try {
            JSONObject jsonObject = new JSONObject(new String(file));
            UserCredentials userCredentials = new UserCredentials(jsonObject);

            if (userCredentials.valid()){
                return userCredentials;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void signIn(){
        userCredentialsForLogin = getCredentials();
        if (userCredentialsForLogin != null) {
            logIn(userCredentialsForLogin);
        } else {
            loginInterface.onExplainingNeed("bad user credentials");
            InternalStorageUtils.deleteFile(USER_CREDENTIALS_FILENAME);
            this.alreadySignedUp = false;
            prepareLogIn();
        }
    }

    public void signUp(String ip, String port, String username){
        if ((ip != null && ip.length() > 0) &&
                (port != null && port.length() > 0) &&
                (username != null && username.length() > 0)) {

            this.userCredentialsForLogin = new UserCredentials(ip, port, username);

            InternalStorageUtils.deleteFile(USER_CREDENTIALS_FILENAME);
            this.alreadySignedUp = false;

            logIn(this.userCredentialsForLogin);
        }
    }

    private boolean saveCredentials(UserCredentials userCredentials) {
        return InternalStorageUtils.writeToFile(USER_CREDENTIALS_FILENAME, userCredentials.toJSON().toString().getBytes());
    }

    @Override
    public void onMessageReceive(final ChatMessage chatMessage) {
        if (chatMessage.getMessage().equals("ok")){
            if ( !isAlreadySignedUp() && !(this.userCredentialsForLogin != null && saveCredentials(this.userCredentialsForLogin)) ){
                loginInterface.onExplainingNeed("Some issue with internal file system. Credentials was not saved.");
            }
            loginInterface.onLoginSuccess();
        } else {
            loginInterface.onExplainingNeed("Wrong log in. Try to change username.");
            if (isAlreadySignedUp()) {
                loginInterface.showSignUpScreen();
            } else {
                loginInterface.onSignUpFailed();
            }
        }
    }
}