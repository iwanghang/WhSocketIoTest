package com.jialianiot.socket0204;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
//    private String mUsername = "112233";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        listenSignalEvents();

        mSocket.connect();

//        Log.d("TTT", "mSocket.on login");
//        mSocket.on("join", onLogin);

//         mSocket.emit("add user", "111111");
//        mSocket.emit("join", "111111");
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("TTT", "Emitter.Listener onLogin");
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
                Log.d("TTT", "Emitter.Listener numUsers = " + numUsers);
            } catch (JSONException e) {
                Log.d("TTT", "Emitter.Listener e");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", "111111");
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };


    private static final String TAG = "TTT";
    //侦听从服务器收到的消息
    private void listenSignalEvents() {

        if (mSocket == null) {
            return;
        }

        mSocket.on("new message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String username;
                String message;
                try {
                    username = data.getString("username");
                    message = data.getString("message");
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
                Log.e(TAG, "new message: " + username + "：" + message);
            }
        });

        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                Log.e(TAG, "onConnectError: " + args.toString());
                Log.e(TAG, "onConnectError: " + args[0]);
            }
        });

        mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "onError: " + args[0]);
            }
        });

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String sessionId = mSocket.id();
                Log.i(TAG, "onConnected");
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onConnected();
//                }
            }
        });

        mSocket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "onConnecting");
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onConnecting();
//                }
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, "onDisconnected");
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onDisconnected();
//                }
            }
        });

        mSocket.on("joined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                String userId = (String) args[1];
//                if (/*!mUserId.equals(userId) &&*/ mOnSignalEventListener != null) {
//                    //mOnSignalEventListener.onRemoteUserJoined(userId);
//                    mOnSignalEventListener.onUserJoined(roomName, userId);
//                }
                //Log.i(TAG, "onRemoteUserJoined: " + userId);
                Log.i(TAG, "onUserJoined, room:" + roomName + "uid:" + userId);
            }
        });

        mSocket.on("leaved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                String userId = (String) args[1];
//                if (/*!mUserId.equals(userId) &&*/ mOnSignalEventListener != null) {
//                    //mOnSignalEventListener.onRemoteUserLeft(userId);
//                    mOnSignalEventListener.onUserLeaved(roomName, userId);
//                }
                Log.i(TAG, "onUserLeaved, room:" + roomName + "uid:" + userId);
            }
        });

        mSocket.on("otherjoin", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                String userId = (String) args[1];
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onRemoteUserJoined(roomName);
//                }
                Log.i(TAG, "onRemoteUserJoined, room:" + roomName + "uid:" + userId);
            }
        });

        mSocket.on("bye", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String) args[0];
                String userId = (String) args[1];
//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onRemoteUserLeaved(roomName, userId);
//                }
                Log.i(TAG, "onRemoteUserLeaved, room:" + roomName + "uid:" + userId);

            }
        });

        mSocket.on("full", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                //释放资源
                mSocket.disconnect();
                mSocket.close();
                mSocket = null;

                String roomName = (String) args[0];
                String userId = (String) args[1];

//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onRoomFull(roomName, userId);
//                }

                Log.i(TAG, "onRoomFull, room:" + roomName + "uid:" + userId);

            }
        });

        mSocket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String roomName = (String)args[0];
                JSONObject msg = (JSONObject) args[1];

//                if (mOnSignalEventListener != null) {
//                    mOnSignalEventListener.onMessage(msg);
//                }

                Log.i(TAG, "onMessage, room:" + roomName + "data:" + msg);

            }
        });
    }

}
