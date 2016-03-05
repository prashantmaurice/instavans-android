package com.tinystep.honeybee.honeybee.Utils;

import android.net.Uri;

import com.tinystep.honeybee.honeybee.MainApplication;

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
        return new Uri.Builder().scheme(DEFAULT_SCHEME).encodedAuthority(DEFAULT_AUTHORITY);
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
            return getNewDefaultBuilder().path("api").appendPath("user").build().toString();
        }

    }

    public static class Jobs{

        static Uri.Builder getSubPath(){
            return getNewDefaultBuilder().path("api/porter-request");
        }

        public static String offers(){
            return getSubPath().appendPath("porter").build().toString();
        }
        public static String completed(){
            return getNewDefaultBuilder().path("api/porter-request").build().toString();
        }
        public static String accept(){
            return getNewDefaultBuilder().path("api/porter-request").build().toString();
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

