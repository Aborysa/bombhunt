package com.bombhunt.game;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bombhunt.game.BombHunt;
import com.bombhunt.game.google.GoogleCommunication;
import com.bombhunt.game.networking.PlayServices;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.List;


public class AndroidLauncher extends AndroidApplication {

  private GoogleCommunication googleCommunication;


  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    googleCommunication = new GoogleCommunication(this);

    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    initialize(new BombHunt(googleCommunication), config);
  }

  // result from intents
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // result from a sign in intent
    if (requestCode == googleCommunication.RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // The signed in account is stored in the result.
        googleCommunication.googleSignInAccount = result.getSignInAccount();
        new AlertDialog.Builder(this).setMessage("successfull login")
                .setNeutralButton(android.R.string.ok, null).show();
        // signed up, set the realtimemultiplayerclient
        googleCommunication.realTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleCommunication.googleSignInAccount);

      } else {
        String message = result.getStatus().getStatusMessage();
        if (message == null || message.isEmpty()) {
          message = "error displaying signin " + String.valueOf(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getContext()));
        }
        new AlertDialog.Builder(this).setMessage(message)
                .setNeutralButton(android.R.string.ok, null).show();
      }

      // result from waiting room intent
    } else if (requestCode == googleCommunication.RC_WAITING_ROOM) {
      // Look for finishing the waiting room from code, for example if a
      // "start game" message is received.  In this case, ignore the result.
      if (googleCommunication.mWaitingRoomFinishedFromCode) {
        return;
      }

      if (resultCode == Activity.RESULT_OK) {
        // Start the game!
      } else if (resultCode == Activity.RESULT_CANCELED) {
        // Waiting room was dismissed with the back button. The meaning of this
        // action is up to the game. You may choose to leave the room and cancel the
        // match, or do something else like minimize the waiting room and
        // continue to connect in the background.

        // in this example, we take the simple approach and just leave the room:
        Games.getRealTimeMultiplayerClient(this,
                GoogleSignIn.getLastSignedInAccount(this))
                .leave(googleCommunication.mJoinedRoomConfig, googleCommunication.mRoom.getRoomId());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
        // player wants to leave the room.
        Games.getRealTimeMultiplayerClient(this,
                GoogleSignIn.getLastSignedInAccount(this))
                .leave(googleCommunication.mJoinedRoomConfig, googleCommunication.mRoom.getRoomId());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      }
    }
  }

}
