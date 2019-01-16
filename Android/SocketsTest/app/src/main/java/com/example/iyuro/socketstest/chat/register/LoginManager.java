package com.example.iyuro.socketstest.chat.register;
import com.example.iyuro.socketstest.chat.messenger.NetworkInterface;
import com.example.iyuro.socketstest.chat.messenger.NetworkManager;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginManager implements NetworkInterface {

    LoginInterface loginInterface;

    public LoginManager(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
        NetworkManager.getInstance().setNetworkInterface(this);
    }

    public void logIn(String username){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", "login");
            jsonObject.put("username", username);

            NetworkManager.getInstance().send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceive(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            try {
                String keyAction = jsonObject.getString("keyAction");
                if (keyAction.equals("login")){
                    String response = jsonObject.getString("message");
                    if (response.equals("ok")){
                        loginInterface.onLoginSuccess();
                    } else {
                        loginInterface.onLoginFailed();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
