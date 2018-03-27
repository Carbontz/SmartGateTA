package edu.smartgate.reza.smartgateta;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Reza on 23-Dec-17.
 */

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "SmartGate";
    // All shared preferences keys
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    // user name
    protected static final String KEY_NAME = "name";
    // email
    protected static final String KEY_EMAIL = "email";
    //iduser
    protected static final String KEY_ID = "id";
    //direktori foto dari server
    protected static final String KEY_URLIMAGE = "img";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //setting login
    public void setLogin(boolean isLoggedIn, String nama, String email,String id,String direktorifoto) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_NAME,nama);
        editor.putString(KEY_EMAIL,email);
        Log.d(TAG,"Nilai bawaan dari response = "+id);
        editor.putString(KEY_ID,id);
        Log.d(TAG,"Nilai id user disession manager ="+id);
        editor.putString(KEY_URLIMAGE,direktorifoto);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    //setting logout
    public void setLogOut(){
        editor.clear();
        editor.commit();
    }

    //cek sudah login atau belum
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    //ambil data dari shared preferences
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        // user email
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        // user id
        user.put(KEY_ID, pref.getString(KEY_ID,null));
        // user image url
        user.put(KEY_URLIMAGE,pref.getString(KEY_URLIMAGE,null));
        // return user
        return user;
    }
}
