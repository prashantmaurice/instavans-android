package com.tinystep.honeybee.honeybee.Utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by maurice on 27/07/15.
 */
public class Utils {

    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static void showDebugToast(final Context context, final String text){
        if(!Settings.showDebugToasts) return;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Spanned highlightText(String search, String originalText) {
        if (search == null || search.isEmpty()) return Html.fromHtml(originalText);
        if (originalText == null || originalText.isEmpty()) Html.fromHtml("");
        if (!originalText.toLowerCase().contains(search.toLowerCase())) return Html.fromHtml(originalText);
        int startPos = originalText.toLowerCase().indexOf(search.toLowerCase());
        if (startPos == -1) return Html.fromHtml("");
        int endPos = startPos + search.length();
        Spannable spannable = new SpannableString(originalText);
        ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.rgb(0, 188, 212) });
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
        spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
    public static Spanned highlightText(ArrayList<String> searchList, String originalText) {
        if (searchList == null || searchList.size()==0) return Html.fromHtml(originalText);
        if (originalText == null || originalText.isEmpty()) Html.fromHtml("");

        Spannable spannable = new SpannableString(originalText);
        for(String search : searchList){
            if (!originalText.toLowerCase().contains(search.toLowerCase())) continue;
            int startPos = originalText.toLowerCase().indexOf(search.toLowerCase());
            if (startPos == -1) continue;
            int endPos = startPos + search.length();
            ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.rgb(0, 188, 212) });
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }
    public static Spanned highlightTextIndex(String originalText, int start, int end){
        if (originalText == null || originalText.isEmpty()) Html.fromHtml("");
        if(end>originalText.length()) end = originalText.length();
        if(start<0) start=0;
        Spannable spannable = new SpannableString(originalText);
        ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.rgb(0, 188, 212) });
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
        spannable.setSpan(highlightSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
