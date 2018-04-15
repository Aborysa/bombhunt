package com.bombhunt.game.google;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.bombhunt.game.AndroidLauncher;
import com.bombhunt.game.services.networking.IPlayServices;
import com.bombhunt.game.services.networking.Message;
import com.bombhunt.game.services.networking.RealtimeListener;
import com.bombhunt.game.services.networking.RoomListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.InvitationsClient;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by erlin on 23.03.2018.
 */

public class GoogleCommunication implements IPlayServices {
    public GoogleSignInClient googleSignInClient;
    public GoogleSignInAccount googleSignInAccount;
    public RealTimeMultiplayerClient realTimeMultiplayerClient;
    public RoomConfig mJoinedRoomConfig;
    public InvitationsClient invitationsClient;

    private AndroidLauncher androidLauncher;
    private Activity thisActivity;



    public static final int RC_SIGN_IN = 9001;
    public static final int RC_WAITING_ROOM = 9002;

    public GoogleCommunication(AndroidLauncher androidLauncher) {
        // create client to sign in.

        this.androidLauncher = androidLauncher;
        this.thisActivity = androidLauncher;
        googleSignInClient = GoogleSignIn.getClient(this.androidLauncher, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
    }


    // ----------------- intents -----------------
    public void startSignInIntent() {
        Intent intent = googleSignInClient.getSignInIntent();
        androidLauncher.startActivityForResult(intent, RC_SIGN_IN);



        //if it equals 2 you need to update google play services on your device
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(androidLauncher.getContext());
        System.out.println(result);
        System.out.println(result == ConnectionResult.SUCCESS);

    }


    public boolean mWaitingRoomFinishedFromCode = false;

    private void onStartGameMessageReceived() {
        mWaitingRoomFinishedFromCode = true;
        androidLauncher.finishActivity(RC_WAITING_ROOM);
    }

    // display the waiting room ui
    public void showWaitingRoom(Room room, int maxPlayersToStartGame) {
        Games.getRealTimeMultiplayerClient(androidLauncher, googleSignInAccount)
                .getWaitingRoomIntent(room, maxPlayersToStartGame)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        androidLauncher.startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                });
    }





    // --------- creating multiplayer rooms ------------------
    private static final long ROLE_ANY = 0x0; // can play in any match.
    private static final long ROLE_FARMER = 0x1; // 001 in binary
    private static final long ROLE_ARCHER = 0x2; // 010 in binary
    private static final long ROLE_WIZARD = 0x4; // 100 in binary

    private void startQuickGame(long role) {
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(1, 1, role);

        // build the room config:
        RoomConfig roomConfig =
                RoomConfig.builder(roomUpdateCallback)
                        .setOnMessageReceivedListener(mMessageReceivedHandler)
                        .setRoomStatusUpdateCallback(mRoomStatusCallbackHandler)
                        .setAutoMatchCriteria(autoMatchCriteria)
                        .build();

        // prevent screen from sleeping during handshake
        //androidLauncher.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Save the roomConfig so we can use it if we call leave().
        mJoinedRoomConfig = roomConfig;

        // create room:
        Games.getRealTimeMultiplayerClient(androidLauncher, googleSignInAccount)
                .create(roomConfig);
        System.out.println("Room created");
    }

    private RoomUpdateCallback roomUpdateCallback = new RoomUpdateCallback() {
        @Override
        public void onRoomCreated(int code, @Nullable Room room) {
            // Update UI and internal state based on room updates.
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                System.out.println("Room " + room.getRoomId() + " created.");
                mRoom = room;
                showWaitingRoom(room, 4);
            } else {
                System.out.println("Error creating room: " + code);
                String message = "google play service is dead :)";
                new AlertDialog.Builder(androidLauncher).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();

                // let screen go to sleep
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }

        @Override
        public void onJoinedRoom(int code, @Nullable Room room) {
            // Update UI and internal state based on room updates.
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                System.out.println("Room " + room.getRoomId() + " joined.");
                mRoom = room;
                showWaitingRoom(room, 4);
            } else {
                System.out.println("Error joining room: " + code);
                // let screen go to sleep
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }

        @Override
        public void onLeftRoom(int code, @NonNull String roomId) {
            System.out.println("Left room" + roomId);
        }

        @Override
        public void onRoomConnected(int code, @Nullable Room room) {
            if (code == GamesCallbackStatusCodes.OK && room != null) {
                System.out.println("Room " + room.getRoomId() + " connected.");
            } else {
                System.out.println("Error connecting to room: " + code);
                // let screen go to sleep
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }
        }
    };

    // are we already playing?
    boolean mPlaying = false;

    // at least 2 players required for our game
    final static int MIN_PLAYERS = 2;
    private String mMyParticipantId;

    // returns whether there are enough players to start the game
    boolean shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) {
                ++connectedPlayers;
            }
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    // Returns whether the room is in a state where the game should be canceled.
    boolean shouldCancelGame(Room room) {
        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return false;
    }

    public Room mRoom;
    private RoomStatusUpdateCallback mRoomStatusCallbackHandler = new RoomStatusUpdateCallback() {
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            // Update the UI status since we are in the process of connecting to a specific room.
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            // Update the UI status since we are in the process of matching other players.
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            // Update the UI status since we are in the process of matching other players.
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            // Peer declined invitation, see if game should be canceled
            if (!mPlaying && shouldCancelGame(room)) {
                realTimeMultiplayerClient.leave(mJoinedRoomConfig, room.getRoomId());
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            // Update UI status indicating new players have joined!
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            // Peer left, see if game should be canceled.
            if (!mPlaying && shouldCancelGame(room)) {
                realTimeMultiplayerClient.leave(mJoinedRoomConfig, room.getRoomId());
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            // Connected to room, record the room Id.
            mRoom = room;
            Games.getPlayersClient(thisActivity, GoogleSignIn.getLastSignedInAccount(thisActivity))
                    .getCurrentPlayerId().addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String playerId) {
                    mMyParticipantId = mRoom.getParticipantId(playerId);
                    roomListener.roomConnected();
                }
            });
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            // This usually happens due to a network error, leave the game.
            Games.getRealTimeMultiplayerClient(thisActivity, GoogleSignIn.getLastSignedInAccount(thisActivity))
                    .leave(mJoinedRoomConfig, room.getRoomId());
            androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // show error message and return to main screen
            mRoom = null;
            mJoinedRoomConfig = null;
        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            if (mPlaying) {
                // add new player to an ongoing game
            } else if (shouldStartGame(room)) {
                // start game!
            }
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            if (mPlaying) {
                // do game-specific handling of this -- remove player's avatar
                // from the screen, etc. If not enough players are left for
                // the game to go on, end the game and leave the room.
            } else if (shouldCancelGame(room)) {
                // cancel the game
                Games.getRealTimeMultiplayerClient(thisActivity,
                        GoogleSignIn.getLastSignedInAccount(thisActivity))
                        .leave(mJoinedRoomConfig, room.getRoomId());
                androidLauncher.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        public void onP2PConnected(@NonNull String participantId) {
            // Update status due to new peer to peer connection.
        }

        @Override
        public void onP2PDisconnected(@NonNull String participantId) {
            // Update status due to  peer to peer connection being disconnected.
        }
    };


    // sending data
    @Override
    public void sendToAllReliably(byte[] message) {
        for (String participantId : mRoom.getParticipantIds()) {
            if (!participantId.equals(mMyParticipantId)) {
                Task<Integer> task = Games.
                        getRealTimeMultiplayerClient(androidLauncher, GoogleSignIn.getLastSignedInAccount(androidLauncher))
                        .sendReliableMessage(message, mRoom.getRoomId(), participantId,
                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                // Keep track of which messages are sent, if desired.
                                recordMessageToken(task.getResult());
                            }
                        });
            }
        }
    }

    // send data to specific user ID
    @Override
    public void sendToOneReliably(byte[] message, String userID){
        for(String uID : mRoom.getParticipantIds()) {
            if(uID == userID) {
                Task<Integer> task = Games.
                        getRealTimeMultiplayerClient(androidLauncher, GoogleSignIn.getLastSignedInAccount(androidLauncher))
                        .sendReliableMessage(message, mRoom.getRoomId(), userID,
                                handleMessageSentCallback).addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                // Keep track of which messages are sent, if desired.
                                recordMessageToken(task.getResult());
                            }
                        });
            }
        }
    }

    HashSet<Integer> pendingMessageSet = new HashSet<>();

    synchronized void recordMessageToken(int tokenId) {
        pendingMessageSet.add(tokenId);
    }

    private RealTimeMultiplayerClient.ReliableMessageSentCallback handleMessageSentCallback =
            new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                @Override
                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
                    // handle the message being sent.
                    synchronized (this) {
                        pendingMessageSet.remove(tokenId);
                    }
                }
            };



    private RealtimeListener liveListener;
    private RoomListener roomListener;
    @Override
    public void setRealTimeListener(RealtimeListener listener){
        this.liveListener = listener;
        listener.setSender(this);
    }
    private OnRealTimeMessageReceivedListener mMessageReceivedHandler =
            new OnRealTimeMessageReceivedListener() {
                @Override
                public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
                    Message message = new Message(realTimeMessage.getMessageData(), realTimeMessage.getSenderParticipantId(), realTimeMessage.describeContents());
                    liveListener.handleDataReceived(message);
                }
            };

    @Override
    public void setRoomListener(RoomListener listener) {
        this.roomListener = listener;
    }
    @Override
    public void signIn() {

        // start signing in before starting game
        startSignInIntent();
    }

    @Override
    public void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(androidLauncher,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(androidLauncher,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                    }
                });
    }

    @Override
    public void startMatchMaking() {
        startQuickGame(ROLE_ANY);
    }

    @Override
    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(androidLauncher) != null;
    }

    @Override
    public String getLocalID() {
        return mMyParticipantId;
    }

    @Override
    public ArrayList<String> getRemotePlayers() {
        ArrayList<String> participants = new ArrayList<String>(mRoom.getParticipantIds());
        String localID = getLocalID();
        for(String s : participants) {
            if(s == localID) {
                participants.remove(s);
            }
        }
        return participants;
    }

}
