package com.tinystep.honeybee.honeybee.Activities.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tinystep.honeybee.honeybee.Activities.Main.MainActivity;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.UserMain;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Utils.Router;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends FragmentActivity {
    static final String TAG = "LOGINACTIVITY";

    EditText editText;
    UserMain userMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userMain = MainApplication.getInstance().data.userMain;
        goToMainApp();


        setContentView(R.layout.activity_login);
        if(getActionBar()!=null) getActionBar().hide();


        //Bind  Login
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLoggingIn();
            }
        });

        editText = (EditText) findViewById(R.id.editText);


    }

    private void goToMainApp(){
        if(userMain.loggedIn()){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void tryLoggingIn() {
        String name = editText.getText().toString().trim();
        String url = Router.Login.main();

        JSONObject obj = new JSONObject();
        try {
            obj.put("userId",name);
        } catch (JSONException e) {e.printStackTrace();}

        MainApplication.getInstance().addRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logg.d(TAG, "USER DATA : " + jsonObject.toString());
                try {
                    JSONObject result = jsonObject.getJSONObject("result");
                    String userId = result.getString("userId");
                    userMain.userId = userId;
                    userMain.saveUserDataLocally();

                    goToMainApp();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Logg.e(TAG, "ERROR : " + volleyError);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
