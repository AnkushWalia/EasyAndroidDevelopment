package com.android.socialLogin;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class GPlusLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final int RC_SIGN_IN = 123;
    private static final int G_PLUS_SIGNIN_REQ_CODE = 1212;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private SocialLogin socialLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialLogin = SocialLogin.getInstance();
        createGoogleClient();
    }

    private void createGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void resetAndLogin() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        signInWithGplus();
                    }
                });
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnected()) {
            resolveSignInError();
        } else {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, G_PLUS_SIGNIN_REQ_CODE);
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            }
        } else {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == G_PLUS_SIGNIN_REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct != null)
                    socialLogin.gPlusLoginDone(acct);
                finish();
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.mConnectionResult = connectionResult;
        resolveSignInError();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        resetAndLogin();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
