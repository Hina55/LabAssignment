package com.hina.complainapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hina.complainapp.session.SessionManager;


public class LoginActivity extends AppCompatActivity {


    Button callSignUp, login_btn;
    ImageView image;
    TextView sloganText,logoText;
    TextInputLayout usernameText,passwordText;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(R.id.btn_signup);
        image = findViewById(R.id.imageViewlogin);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        usernameText = findViewById(R.id.username);
        passwordText=findViewById(R.id.password);
        login_btn=findViewById(R.id.btn_login);
        progressBar=findViewById(R.id.progress_bar);
        View view = findViewById(android.R.id.content);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(2000);
        view.startAnimation(mLoadAnimation);



        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private Boolean validateUserName(){

        String val = usernameText.getEditText().getText().toString();
        if(val.isEmpty()){
           usernameText.setError("Field Can't be Empty");
            return false;
        }

        else{
            usernameText.setError(null);
            usernameText.setErrorEnabled(false);
            return true;
        }

    }
    private Boolean validatePassword(){
        String val = passwordText.getEditText().getText().toString();

        if(val.isEmpty()){
            passwordText.setError("Field Can't be Empty");
            return false;
        }

        else{
            passwordText.setError(null);
            passwordText.setErrorEnabled(false);
            return true;
        }
    }


    public void loginUser(View view) {

        if(!validateUserName() | !validatePassword()){
            return;
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            isUser();
        }


    }

    private void isUser() {

        if(!isConnected(this)){
            showCustomDialog();
        }


        final String username = usernameText.getEditText().getText().toString().trim();
        final String password = passwordText.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    usernameText.setError(null);
                    usernameText.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(username).child("password").getValue(String.class);

                    if(passwordFromDB.equals(password)){

                        passwordText.setError(null);
                        passwordText.setErrorEnabled(false);

                        String fullnameFromDB = dataSnapshot.child(username).child("fullname").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(username).child("username").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(username).child("email").getValue(String.class);


                        //create a session
                        SessionManager sessionManager = new SessionManager(LoginActivity.this);
                        sessionManager.createLoginSession(fullnameFromDB,usernameFromDB,emailFromDB,passwordFromDB);


                        /*Intent intent = new Intent(getApplicationContext(),UserHomeActivity.class);

                        intent.putExtra("fullname", fullnameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);*/

                        startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                        finish();


                    }
                    else {
                        passwordText.setError("Wrong Password");
                        passwordText.requestFocus();
                        progressBar.setVisibility(View.INVISIBLE);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            KeyboardUtils.addKeyboardToggleListener(LoginActivity.this, new KeyboardUtils.SoftKeyboardToggleListener()
                            {
                                @Override
                                public void onToggleSoftKeyboard(boolean isVisible)
                                {
                                    if(isVisible){
                                        passwordText.setErrorEnabled(false);
                                    }
                                }
                            });
                        }

                    }
                }
                else{
                    usernameText.setError("No such User exists");
                    passwordText.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please connect to the internet to proceed further.")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        builder.show();
    }

    private boolean isConnected(LoginActivity loginActivity) {

        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wificonn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileconn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if((wificonn != null && wificonn.isConnected()) || (mobileconn != null && mobileconn.isConnected())){
            return true;
        }else{
            return false;
        }

    }
}
