package edu.smartgate.reza.smartgateta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.RecoverySystem;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegisterNama,etRegisterEmail,etRegisterPassword;
    Button btnRegister,btnInputFoto;
    ImageView ivFotoPilihan;
    Intent intentPilihFoto;
    Bitmap bitmap;
    ProgressBar progressBar;

    private static final String TAG = "RegisterActivity";
    int IMG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //deklarasi
        etRegisterNama = findViewById(R.id.etNama);
        etRegisterEmail = findViewById(R.id.etEmail);
        etRegisterPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnInputFoto = findViewById(R.id.btnInputFoto);
        ivFotoPilihan = findViewById(R.id.ivFotoTerpilih);
        progressBar = findViewById(R.id.pbRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = etRegisterNama.getText().toString().trim();
                String email = etRegisterEmail.getText().toString().trim();
                String password = etRegisterPassword.getText().toString().trim();

                if (TextUtils.isEmpty(nama)) {
                    etRegisterNama.setError("Please input your name!");
                    etRegisterNama.requestFocus();
                } else if (TextUtils.isEmpty(email)) {
                    etRegisterEmail.setError("Please input your email!");
                    etRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    etRegisterPassword.setError("Please input your password!");
                    etRegisterPassword.requestFocus();
                } else registerUser(nama,email,password);
            }
        });

        btnInputFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
    }

    //method pemilih gambar
    private void showImageChooser() {
        intentPilihFoto = new Intent();
        intentPilihFoto.setType("image/*");
        intentPilihFoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentPilihFoto,"Select Picture"), IMG_REQUEST);
    }

    //activity untuk memilih gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                ivFotoPilihan.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //converting image to base64 string
    protected String getStringImage (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    private void registerUser(final String name, final String email,
                              final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        //Progress Bar Active
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        btnRegister.setVisibility(View.INVISIBLE);

        //finalisasi variabel string image
        final String image = getStringImage(bitmap);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Registration success. Get the success message
                        String successMsg = jObj.getString("success_msg");
                        Toast.makeText(getApplicationContext(),successMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Register Success!");
                        //Progress Bar Done
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        btnRegister.setVisibility(View.VISIBLE);
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("namauser", name);
                params.put("emailuser", email);
                params.put("passworduser", password);
                params.put("fotouser",image);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
