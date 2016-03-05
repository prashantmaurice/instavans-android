package com.maurice.instavans.Utils;

import android.net.Uri;

import com.maurice.instavans.MainApplication;

/**
 * This contains all the routes to our backend server. This is similar to API CONTRACT given to backend team.
 * Any changes with backend API routes will only be reflected by changes in this FIle.
 */
public class Router {

    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_AUTHORITY = Settings.BASE_AUTHORITY;//"api.maurice.com"
    private static final String API_VERSION = "2.3.2";

    public static String getServerToken() {
        return MainApplication.getInstance().data.userMain.userId;
    };

    private static Uri.Builder getNewDefaultBuilder() {
        return new Uri.Builder().scheme(DEFAULT_SCHEME).encodedAuthority(DEFAULT_AUTHORITY).appendQueryParameter("version",API_VERSION);
    }


    public static class Data{

        public static String transactions(){
            return getNewDefaultBuilder().path("data/transactions")
                    .appendQueryParameter("access_token", getServerToken()).build().toString();
        }

        public static String mycards(){
            return getNewDefaultBuilder().path("data/mycards")
                    .appendQueryParameter("access_token", getServerToken()).build().toString();
        }

        public static String regexs(){
            return getNewDefaultBuilder().path("data/allregexs")
                    .appendQueryParameter("access_token", getServerToken()).build().toString();
        }
    }
    public static class Login{

        public static String main(){
            return getNewDefaultBuilder().path("login").build().toString();
        }

    }

    public static class Jobs{

        public static String offers(){
            return getNewDefaultBuilder().path("offers").build().toString();
        }
        public static String completed(){
            return getNewDefaultBuilder().path("done").build().toString();
        }

    }

    public static class Curate{

        public static String editBucket(){
            return getNewDefaultBuilder().path("data/editbucket")
                    .appendQueryParameter("access_token", getServerToken()).build().toString();
        }
        public static String deleteRegex(String regexId){
            return getNewDefaultBuilder().path("data/deleteregex")
                    .appendQueryParameter("regexId", regexId)
                    .appendQueryParameter("access_token", getServerToken()).build().toString();
        }

    }
}


