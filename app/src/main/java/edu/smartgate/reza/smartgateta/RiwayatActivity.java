package edu.smartgate.reza.smartgateta;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {

    private List<User> arrayUser = new ArrayList<User>();
    private ListView listView;
    private GridViewAdapter gridViewAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        //inisiasi
        listView = findViewById(R.id.lvRiwayat);
        gridViewAdapter = new GridViewAdapter(this,arrayUser);
        listView.setAdapter(gridViewAdapter);

        //Progress Bar
        progressBar = findViewById(R.id.progressBar2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        //create volley array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ServerConfig.URL_RIWAYAT, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jObj = response.getJSONObject(i);
                        User user = new User();
                        user.setNama(jObj.getString("nama"));
                        user.setIdStatus(jObj.getInt("idStatus"));
                        user.setWaktu(jObj.getString("waktu"));
                        user.setStatus(jObj.getString("statusgerbang"));

                        //simpan ke dalam array
                        arrayUser.add(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                gridViewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT);
            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        //hide progress bar
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //show list view
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
