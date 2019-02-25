package com.good.mygoodsample.login;

import android.content.Context;
import android.provider.Settings;

import com.example.internal_storage_utils.InternalStorageUtils;
import com.good.mygoodsample.ChatManager;
import com.good.mygoodsample.network.ChatMessage;
import com.good.mygoodsample.network.MessageProtocol;
import com.good.mygoodsample.network.NetworkInterface;
import com.good.mygoodsample.network.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginManager implements NetworkInterface {
    private static final String PATH = "/";
    private static final String EXTENSION = ".txt";
    private final String USER_CREDENTIALS_FILENAME = "SUPER_SECRET_FILE";

    private Context mContext;

    private boolean alreadySignedUp;
    private LoginInterface loginInterface;
    private UserCredentials userCredentialsForLogin;

    public LoginManager(Context context, LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        this.mContext = context;

        this.alreadySignedUp = InternalStorageUtils.fileExists(PATH + USER_CREDENTIALS_FILENAME + EXTENSION);
        this.userCredentialsForLogin = null;
    }

    public void prepareLogIn(){

        if (!isAlreadySignedUp()){
            if (loginInterface != null){
                loginInterface.showSignUpScreen();
            }
        } else {
            signIn();
        }
    }

    private boolean logIn(UserCredentials userCredentials){
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChatManager.getInstance().setCurrentUserID(userCredentials.getUsername());
        ChatManager.getInstance().openConnection(userCredentials.getIp(), Integer.parseInt(userCredentials.getPort()), true, android_id);

        NetworkManager.getInstance().setNetworkInterface(this);

        byte[] request = MessageProtocol.getInstance().createLoginRequest(userCredentials.getUsername()).getBytes();
        if (NetworkManager.getInstance().isConnectionOpen()) {
            NetworkManager.getInstance().send(request);
            return true;
        } else {
            if (loginInterface != null){
                loginInterface.onConnectionClosed();
            }
        }
        return false;
    }

    private boolean isAlreadySignedUp(){
        return alreadySignedUp;
    }

    private UserCredentials getCredentials(){
        byte[] file = InternalStorageUtils.readFile(PATH + USER_CREDENTIALS_FILENAME + EXTENSION);

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

    public boolean signIn(){
        userCredentialsForLogin = getCredentials();
        if (userCredentialsForLogin != null) {
            return logIn(userCredentialsForLogin);
        } else {
            if (loginInterface != null){
                loginInterface.onExplainingNeed("bad user credentials");
            }
            InternalStorageUtils.deleteFile(PATH + USER_CREDENTIALS_FILENAME + EXTENSION);
            this.alreadySignedUp = false;
            prepareLogIn();
        }

        return false;
    }

    public void signUp(String ip, String port, String username){
        if ((ip != null && ip.length() > 0) &&
                (port != null && port.length() > 0) &&
                (username != null && username.length() > 0)) {

            this.userCredentialsForLogin = new UserCredentials(ip, port, username);

            InternalStorageUtils.deleteFile(PATH + USER_CREDENTIALS_FILENAME + EXTENSION);
            this.alreadySignedUp = false;

            logIn(this.userCredentialsForLogin);
        }
    }

    private boolean saveCredentials(UserCredentials userCredentials) {
        return InternalStorageUtils.writeToFile(PATH + USER_CREDENTIALS_FILENAME + EXTENSION, userCredentials.toJSON().toString().getBytes());
    }

    @Override
    public void onMessageReceive(final ChatMessage chatMessage) {
        if (loginInterface != null){
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
                    loginInterface.onLoginFailed();
                }
            }
        }
    }

    @Override
    public void closeConnection() {
        NetworkManager.getInstance().closeConnection();
    }
}
