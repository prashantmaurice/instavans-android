package com.maurice.instavans.Controllers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.maurice.instavans.Models.UserMain;
import com.maurice.instavans.Utils.Logg;
import com.maurice.instavans.Utils.Utils;

import org.json.JSONObject;

public class NetworkController {
    private Context mContext;
    private RequestQueue queue;

    //NETWORK CODES : they are same as ones defines in server
    static final int CODE_SUCCESS = 200;
    static final int CODE_INTERNALSERVER = 500;
    static final int CODE_INVALIDTOKEN = 451;
    static final int CODE_UNSUPPORTEDROUTE = 452;
    static final int CODE_ERROR_CUSTOM_MSG = 454;


    private static NetworkController instance;
    public String TAG = "NETWORKCONTROLLER";
    private UserMain userMain;

    private NetworkController(Context context) {
        mContext = context;
        queue = Volley.newRequestQueue(mContext);
        userMain = UserMain.getInstance(mContext);
    }

    //use this to retreive an instance of FontController
    public static NetworkController getInstance(Context context) {
        if(instance == null) {
            instance = new NetworkController(context);
        }
        return instance;
    }

    public void addNewRequest(final int method, final String url, final JSONObject jsonRequest, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener){

        // TODO - replace below item with alter box
        if (!Utils.isNetworkAvailable(mContext)) {
            ToastMain.showSmartToast(mContext, "Please check your internet connection");
            return;
        }

        Logg.d(TAG, "Calling : " + url);
        final JsonObjectRequest jsonObjectRequestFinal = new JsonObjectRequest(method, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                if (error.networkResponse == null)
                    errorListener.onErrorResponse(error);
//                else{
//                    String data = new String(error.networkResponse.data);//{status:"auth error",message:"baba",result:null}
//                    JSONObject response = new JSONObject();
//                    try {
//                        //Catch custom response data too when server sends an intentional error
//                        response = new JSONObject(data);
//                    } catch (JSONException e) {e.printStackTrace();}
//
//                    switch (error.networkResponse.statusCode) {
//                        case CODE_INVALIDTOKEN:
//                            AsyncTask task = new AsyncTask() {
//                                @Override
//                                protected Object doInBackground(Object... params) {
//                                    ToastMain.showSmartToast(mContext, null, "Token expired, updating it");
//                                    updateLoginTokens();
//                                    try {
//                                        String updatedUrl = getUpdatedUrl(url);
//                                        addNewRequest(method, updatedUrl, jsonRequest, listener, errorListener);
//
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                        Logg.e("ERROR", "Could not update URL(2) : " + url);
//                                    } catch (IOException e) {
//                                        Logg.e("ERROR", "Could not update URL(1) : " + url);
//                                        e.printStackTrace();
//                                    }
//
//                                    return null;
//                                }
//                            };
//                            task.execute((Void) null);
//                            break;
//
//                        case CODE_ERROR_CUSTOM_MSG:
//                            try {
//                                ToastMain.showSmartToast(mContext,""+ response.getString("message"));
//                            } catch (JSONException e) {e.printStackTrace();}
//                            break;
//                        //Not a rectifiable requestcode error, hence pipe it caller activity
//                        default:
//                            errorListener.onErrorResponse(error);
//                    }
//                }
            }
        });


        //EDIT RETRY POLICIES, basically to set retry times to 2
        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequestFinal.setRetryPolicy(retryPolicy);

        queue.add(jsonObjectRequestFinal);
    }

    private boolean isSuccess(int code){
        if(code>=200&&code<300) return true;
        return false;
    }

//    private void updateLoginTokens(){
//        String beforeToken = userMain.token;
//        try {
//            if (userMain.facebookId == null || userMain.facebookId.isEmpty() || userMain.facebookId.equals("null") ) {
//                String token = GoogleAuthUtil.getToken(mContext, userMain.email, "oauth2:" + Scopes.PLUS_LOGIN);
//                Logg.e("TOKENMANAGE", "Google update token : " + token);
//                userMain.token = token;
//                userMain.authProvider = "google";
//                userMain.saveUserDataLocally();
//            }else{
//                AccessToken token = AccessToken.getCurrentAccessToken();
//                if(token.isExpired()){
//                    //TODO : BUILD THIS THING
//                }
//                userMain.token = token.getToken();
//                userMain.authProvider = "facebook";
//                userMain.saveUserDataLocally();
//            }
//            //ToastMain.showSmartToast(mContext,null,"Updated token : "+userMain.token);
//        }catch (RuntimeException | GoogleAuthException | IOException e) {e.printStackTrace();}
//    }

//    //Gets the updated url with new access token
//    private String getUpdatedUrl(String url) throws UnsupportedEncodingException, MalformedURLException {
//        //decode current url
//        String decodedUrl = URLDecoder.decode(url, "UTF-8");
//        URL urlNew = new URL(decodedUrl);
//        Map<String, String> map = Utils.getQueryMap(urlNew.getQuery());
//
//        //Replace accesstoken
//        map.put("token",userMain.token);
//
//        //Reform URL
//        Uri.Builder builder = new Uri.Builder().scheme(urlNew.getProtocol()).authority(urlNew.getAuthority()).path(urlNew.getPath());
//
//        //add params
//        for (String key : map.keySet()){
//            builder.appendQueryParameter(key,map.get(key));
//        }
//
//        return builder.build().toString();
//
//
//    }



}
