package edu.smartgate.reza.smartgateta;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Reza on 01-Feb-18.
 */

public class GridViewAdapter extends BaseAdapter {

    //inisiasi
    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<User> users;
    private Context context;
    private String idUser,namaUser,status,timestamp;
    private TextView tvIdStatus, tvNamaUser, tvStatus, tvTimestamp;

    //TODO selesaikan grid view adapter terhadap data di database

    GridViewAdapter(Activity activity, List<User> users) {
        this.activity = activity;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //testing layout grid view
        if (layoutInflater==null) layoutInflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView==null) convertView = layoutInflater.inflate(R.layout.layout_adapter_riwayat, null);

        //reference layout teks
        tvIdStatus = convertView.findViewById(R.id.tvIdStatusInput);
        tvNamaUser = convertView.findViewById(R.id.tvNamaUserInput);
        tvStatus = convertView.findViewById(R.id.tvStatusUserInput);
        tvTimestamp = convertView.findViewById(R.id.tvWaktuUserInput);

        //getting data for row
        User user = users.get(position);
        //set nilai
        tvIdStatus.setText(String.valueOf(user.getIdStatus()));
        tvNamaUser.setText(user.getNama());
        tvTimestamp.setText(user.getWaktu());
        tvStatus.setText(user.getStatus());
        return convertView;
    }
}
