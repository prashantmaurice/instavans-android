package com.tinystep.honeybee.honeybee.Utils;

import android.net.Uri;

/**
 * This contains all the routes to our backend server. This is similar to API CONTRACT given to backend team.
 * Any changes with backend API routes will only be reflected by changes in this FIle.
 */
public class Router {

    private static final String DEFAULT_SCHEME = "http";
    private static final String DEFAULT_AUTHORITY = Settings.BASE_AUTHORITY;//"api.maurice.com"

    private static Uri.Builder getNewDefaultBuilder() {
        return new Uri.Builder().scheme(DEFAULT_SCHEME).encodedAuthority(DEFAULT_AUTHORITY);
    }

    public static class Login{
        public static String main(){
            return getNewDefaultBuilder().path("api").appendPath("user").build().toString();
        }
        public static String data(){
            return getNewDefaultBuilder().path("api").appendPath("user").appendPath("mydata").build().toString();
        }
        public static String locationUpdate(){
            return getNewDefaultBuilder().path("api").appendPath("user").appendPath("update").appendPath("location").build().toString();
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
            return getSubPath().appendPath("porter").appendPath("finished").build().toString();
        }
        public static String accept(){
            return getSubPath().appendPath("porter").appendPath("accept").build().toString();
        }

    }

}


