package com.example.authentication;

import static android.app.Activity.RESULT_OK;

public class AuthManager {
    private static final AuthManager ourInstance = new AuthManager();

    public static AuthManager getInstance() {
        return ourInstance;
    }

    private AuthManager() {
        this.authInterface = null;
    }

    AuthInterface authInterface;

    public void setAuthInterface(AuthInterface authInterface) {
        this.authInterface = authInterface;
    }

    public void startAuth(){
        authInterface.showAuthScreen();
    }

    public void manageAuthResult(int resultCode){
        if (authInterface != null && resultCode == RESULT_OK) {
            authInterface.onAuthSucceed();
        } else {
            authInterface.showAuthScreen();
        }
    }
}
