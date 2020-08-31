package com.hina.complainapp.session;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {


    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIN";


    public static final String KEY_FULLNAME ="fullname";
    public static final String KEY_USERNAME ="username";
    public static final String KEY_EMAIL ="email";
    public static final String KEY_PASSWORD ="password";

    public SessionManager(Context _context){
        context = _context;
        userSession= context.getSharedPreferences("userLoginSession",Context.MODE_PRIVATE);
        editor = userSession.edit();
    }


    public void createLoginSession(String fullname, String username, String email, String password){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_FULLNAME,fullname);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_USERNAME,username);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailsFromSession(){
        HashMap<String,String> userData = new HashMap<String,String>();
        userData.put(KEY_FULLNAME,userSession.getString(KEY_FULLNAME,null));
        userData.put(KEY_USERNAME,userSession.getString(KEY_USERNAME,null));
        userData.put(KEY_EMAIL,userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PASSWORD,userSession.getString(KEY_PASSWORD,null));

        return userData;
    }

    public boolean checkLogin(){
        if(userSession.getBoolean(IS_LOGIN,false)){
            return true;
        }
        else{
            return false;
        }
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }
}
