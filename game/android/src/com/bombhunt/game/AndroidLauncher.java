package com.bombhunt.game;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bombhunt.game.BombHunt;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class AndroidLauncher extends AndroidApplication {

  private GoogleSignInClient googleSignInClient;
  private GoogleSignInAccount googleSignInAccount;
  private RealTimeMultiplayerClient realTimeMultiplayerClient;
  private InvitationsClient invitationsClient;

  private static final int RC_SIGN_IN = 9001;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // create client to sign in.
    googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

    // start signing in before starting game
    startSignInIntent();

    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new BombHunt(), config);


  }

  public void startSignInIntent() {
    Intent intent = googleSignInClient.getSignInIntent();
    startActivityForResult(intent, RC_SIGN_IN);



    //if it equals 2 you need to update google play services on your device
    int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
    System.out.println(result);
    System.out.println(result == ConnectionResult.SUCCESS);

  }
  private void signOut() {
    GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    signInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                // at this point, the user is signed out.
              }
            });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // The signed in account is stored in the result.
        GoogleSignInAccount signedInAccount = result.getSignInAccount();
        new AlertDialog.Builder(this).setMessage("successfull login")
                .setNeutralButton(android.R.string.ok, null).show();
      } else {
        String message = result.getStatus().getStatusMessage();
        if (message == null || message.isEmpty()) {
          message = "error displaying signin";
        }
        new AlertDialog.Builder(this).setMessage(message)
                .setNeutralButton(android.R.string.ok, null).show();
      }
    }
  }
}
