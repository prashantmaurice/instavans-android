package com.maurice.instavans;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.maurice.instavans.Controllers.NetworkController;
import com.maurice.instavans.storage.Data;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

/**
 */
public class MainApplication extends Application {

    private static MainApplication sInstance;
    private RequestQueue queue;
    private com.android.volley.toolbox.ImageLoader imageLoader;
    SharedPreferences sharedPreferences;
    public Data data;
    public NetworkController networkController;

    boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        queue = Volley.newRequestQueue(this);
        networkController = NetworkController.getInstance(this);
        data = Data.getInstance(this);
        initImageLoader(getApplicationContext());


//        startService(new Intent(this, TrackingService.class));
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public synchronized static MainApplication getInstance() {
        return sInstance;
    }


    public void addRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        networkController.addNewRequest(method,url,jsonRequest, listener, errorListener);
    }
    /**
     * Used to return the singleton imageloader
     * that utilizes the image lru cache.
     * @return ImageLoader
     */
    public com.android.volley.toolbox.ImageLoader getImageLoader(){
        return imageLoader;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }





}
