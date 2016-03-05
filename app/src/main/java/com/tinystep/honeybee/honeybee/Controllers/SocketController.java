package com.tinystep.honeybee.honeybee.Controllers;

import android.content.Context;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Utils.NetworkCallback;
import com.tinystep.honeybee.honeybee.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Handles all login in a Chat
 */
public class SocketController {
    private static final String TAG = "SOCKET-CONTROLLER";
    private Context mContext;
    static private Socket mSocket;
    static SocketController instance;
    long SERVER_PING_INTERVAL = 25*1000;
    boolean loggedIn = false;

    private SocketController(Context context) {
        this.mContext = context;
        reconnectToServer();
    }

    public static SocketController getInstance(Context context){
        if(instance==null) instance = new SocketController(context);
        return instance;
    }

    private void reconnectToServer() {
        //TODO : Stop existing socket connections
        try {
            if(mSocket==null){
                mSocket = getSocket();
                setupSockConnListeners();
            }
            connectIfNotConnected();
        } catch (URISyntaxException e) {e.printStackTrace();}
    }

    private void setupSockConnListeners() {
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                mSocket.emit("foo", "hi");
                Logg.e(TAG, "Socket : connected");

            }

        }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_ERROR");
            }
        }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_MESSAGE");
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_CONNECT_ERROR");
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_CONNECT_TIMEOUT");
            }
        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_RECONNECT");
            }
        }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_RECONNECT_ERROR");
            }
        }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_RECONNECT_FAILED");
            }
        }).on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_RECONNECT_ATTEMPT");
            }
        }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : EVENT_RECONNECTING");
            }
        }).on("requestCreate", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Logg.d(TAG, "Socket : requestCreate");
                MainApplication.getInstance().data.completePullFromServer(new NetworkCallback() {
                    @Override
                    public void onSuccess() {
                        NotificationController.getInstance(mContext).updateOfferNotification();
                    }

                    @Override
                    public void onError() {
                    }
                });
//                JSONObject data = (JSONObject) args[0];
//                JSONObject obj = new JSONObject(data);
//                try {
//                    handleIncomingXmppMessage(data);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Logg.d(TAG, "Socket : requestCreate : " + data.toString());
            }
        }).on("login", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    boolean success = data.getBoolean("success");
                    if(success){
                        loggedIn = true;
                    }else{
                        loggedIn = false;
                        Utils.showDebugToast(mContext,"Socket Login failed");
                        mSocket.close();
                    }
                } catch (JSONException e) {e.printStackTrace();}
            }
        }).on("ping", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String text = "";
                try {
                    text+= " "+data.getString("beat");
                } catch (JSONException e) {e.printStackTrace();}
                final String finalText = text;
                Logg.d(TAG, "Ping received " + text);
                //TODO :add randomness here...
//                mSocket.emit("pong", finalText);
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mSocket != null) mSocket.emit("pong", finalText);
//                    }
//                }, (long) ((Math.random() * SERVER_PING_INTERVAL) / 5));//Send after some time so that these wont bombard server
                if (mSocket != null&&loggedIn) mSocket.emit("pong", finalText);
            }

        }).on("pong", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Logg.d(TAG,"Pong Socket");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Logg.e(TAG, "Socket : disconnected : " + args[0].toString());
                String reason = args[0].toString();
                if(reason.equals("ping timeout")){
                    mSocket.connect();
                }
            }

        });
    }





    private Socket getSocket() throws URISyntaxException {
        IO.Options opts = new IO.Options();
//        opts.timeout = 30*1000;
//        opts.reconnectionDelay = 1000;
//        opts.reconnectionDelayMax = 5000;
//        opts.reconnectionAttempts = 5;
//        opts.forceNew = true;
//        opts.reconnection = true;
        Logg.e(TAG, "A Brand new Socked created : " + opts.query);
        return IO.socket("http://192.168.6.171:4000/request",opts);
    }

    public void connectIfNotConnected(){
        if(!mSocket.connected()){
            mSocket.connect();
        }
    }

    public void forceNewConnection() {
        if(mSocket!=null){
            mSocket.close();
            mSocket.off();
        }
        mSocket = null;
        reconnectToServer();

    }
}
