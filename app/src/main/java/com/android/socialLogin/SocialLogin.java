package com.android.socialLogin;

import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

public class SocialLogin {

    private static final SocialLogin instance = new SocialLogin();
    private SocialLoginListener listener;

    public static SocialLogin getInstance() {
        return instance;
    }

    public void setListener(SocialLoginListener listener) {
        this.listener = listener;
    }

    public void facebookLoginDone(JSONObject object, GraphResponse response) {
        if (listener != null)
            listener.onFacebookLoginDone(object, response);
    }

    public void gPlusLoginDone(GoogleSignInAccount currentPerson) {
        if (listener != null)
            listener.onGPlusLoginDone(currentPerson);
    }

    public interface SocialLoginListener {
        void onFacebookLoginDone(JSONObject object, GraphResponse response);

        void onGPlusLoginDone(GoogleSignInAccount currentPerson);
    }
}
