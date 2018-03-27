package edu.smartgate.reza.smartgateta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Intent intentLogin;
    EditText etEmail,etPassword;
    Button btnLogin;
    ProgressBar pbLogin;
    SessionManager smLogin;
    Bundle bundle;
    private static final String TAG = "LoginActivity";
    public static final int MULTIPLE_PERMISSIONS = 3;

    @Override
    protected void onStart() {
        checkPermission();
        super.onStart();
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //intentLogin = new Intent(getApplicationContext(),AutentikasiActivity.class);
        bundle = new Bundle();

        //progress dialog
        pbLogin = findViewById(R.id.progressBar);

        //session manager
        smLogin = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (smLogin.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Check for empty data in the form
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Please enter your name!");
                    etEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)){
                    etPassword.setError("Please enter your password!");
                    etPassword.requestFocus();
                } else {
                    checkLogin(email,password);
                }
                //startActivity(intentLogin);
            }
        });

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    //PERMISSIONS
    //list array permission, sesuaikan dengan yang di manifest
    String[] permissions = new String[] {
            Manifest.permission_group.STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //Progress Bar Active
        pbLogin.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        btnLogin.setVisibility(View.INVISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session

                        //ambil data user dan simpan ke shared preferences
                        jObj=jObj.getJSONObject("user");
                        String namauser=jObj.getString("nama");
                        String emailuser=jObj.getString("email");
                        int iduser=jObj.getInt("idUser");
                        Log.d(TAG,"Nilai iduser dari response = "+iduser);
                        String iduserstring = String.valueOf(iduser);
                        Log.d(TAG,"Nilai iduser dari response diubah ke string = "+iduser);
                        String direktorifoto=jObj.getString("direktorifoto");
                        smLogin.setLogin(true,namauser,emailuser,iduserstring,direktorifoto);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MenuActivity.class);

                        //Progress Bar Done
                        pbLogin.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        //Progress Bar Done
                        pbLogin.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        btnLogin.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("emailuser", email);
                params.put("passworduser", password);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    /*
    private void showDialog() {
        if (!pbLogin.isShowing())
            pbLogin.show();
    }

    private void hideDialog() {
        if (pbLogin.isShowing())
            pbLogin.dismiss();
    }
    */
    private boolean checkPermission() {
        int result;
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(p);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                //return;
            }
        }
    }
    //end permissions

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
