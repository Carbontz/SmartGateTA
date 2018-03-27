package edu.smartgate.reza.smartgateta;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    Button btnBuka,btnTutup,btnDaftar,btnLogout,btnRiwayat;
    ToggleButton tbStatus;
    Intent intentMenu;
    SessionManager smMenu;
    TextView tvUser;
    ImageView ivUser;
    String userActive, infoIdUser,imageUser;
    String bukaref="BUKA";
    String tutupref="TUTUP";
    String sumberref="Android";
    private static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //deklarasi
        btnBuka = findViewById(R.id.btnBuka);
        btnTutup = findViewById(R.id.btnTutup);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnLogout = findViewById(R.id.btnLogOut);
        btnRiwayat = findViewById(R.id.btnRiwayat);
        tvUser = findViewById(R.id.tvNamaUserInput);
        ivUser = findViewById(R.id.ivUser);
        tbStatus = findViewById(R.id.tbGerbang);
        smMenu = new SessionManager(getApplicationContext());

        //check login
        if(!smMenu.isLoggedIn()) {
            logoutUser();
        }

        //ambil data user dari shared preferences
        HashMap<String, String> user = smMenu.getUserDetails();
        userActive = user.get(SessionManager.KEY_NAME);
        infoIdUser = user.get(SessionManager.KEY_ID);
        imageUser = user.get(SessionManager.KEY_URLIMAGE);
        tvUser.setText(userActive);

        //request gambar dari server
        requestFotoUser(imageUser);

        //request status gerbang
        requestStatusGerbang();
        Log.d(TAG,"Nilai userActive = "+userActive);
        Log.d(TAG,"Nilai infoIdUser (iduser dari session manager) = "+ infoIdUser);
        Log.d(TAG, "Nilai imageUser "+ imageUser);

        //disable toggle button clickable
        tbStatus.setClickable(false);

        btnBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perintahBuka(infoIdUser,bukaref,sumberref);
            }
        });

        btnTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perintahTutup(infoIdUser,tutupref,sumberref);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentMenu = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intentMenu);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        btnRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentMenu = new Intent(getApplicationContext(), RiwayatActivity.class);
                startActivity(intentMenu);
            }
        });
    }

    private void requestStatusGerbang() {
        //Tag request status
        String tag_string_req = "req_gerbang";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConfig.URL_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Status Gerbang : " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {
                                String status = jsonObject.getString("status");
                                if (status.equals(bukaref)) {
                                    //toggle button
                                    tbStatus.setChecked(true);
                                    tbStatus.setText(getResources().getString(R.string.terbuka));
                                } else {
                                    //toggle button
                                    tbStatus.setChecked(false);
                                    tbStatus.setText(getResources().getString(R.string.tertutup));
                                }
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jsonObject.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException error) {
                            // JSON error
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Status Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    private void requestFotoUser(String imageUser) {
        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(
                imageUser, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        // Do something with response
                        ivUser.setImageBitmap(response);
                        // Save this downloaded bitmap to internal storage
                        Uri uri = saveImageToInternalStorage(response);
                        // Display the internal storage saved image to image view
                        ivUser.setImageURI(uri);
                    }
                },
                64, // Image width
                64, // Image height
                ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                        Log.e(TAG, "Download picture error " + error.getMessage());
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(imageRequest);
    }

    private Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images",MODE_PRIVATE);
        // Create a file to save the image
        file = new File(file, userActive+".jpg");

        try {
            // Initialize a new OutputStream
            OutputStream stream = null;
            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);
            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            // Flushes the stream
            stream.flush();
            // Closes the stream
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
        // Return the saved image Uri
        return savedImageURI;
    }

    private void perintahBuka(final String infoUserId,final String buka, final String sumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerConfig.URL_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Update success. Get the success message
                        String successMsg = jObj.getString("report");
                        Toast.makeText(getApplicationContext(),successMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, successMsg+"Perintah Buka Success!");
                        //toggle button
                        tbStatus.setChecked(true);
                        tbStatus.setText(getResources().getString(R.string.terbuka));
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("report");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,errorMsg+"Perintah Buka Gagal!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("idUser", infoUserId);
                params.put("statusGbg", buka);
                params.put("sumber", sumber);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void perintahTutup(final String infoUserId,final String tutup, final String sumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerConfig.URL_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Update success. Get the success message
                        String successMsg = jObj.getString("report");
                        Toast.makeText(getApplicationContext(),successMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Perintah Tutup Success!");
                        //toggle button
                        tbStatus.setChecked(false);
                        tbStatus.setText(getResources().getString(R.string.tertutup));
                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("report");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"Perintah Tutup Gagal!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("idUser", infoUserId);
                params.put("statusGbg", tutup);
                params.put("sumber", sumber);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void logoutUser() {
        smMenu.setLogOut();
        // Launching the login activity
        intentMenu = new Intent(getApplicationContext(),LoginActivity.class);
        finish();
    }
}
