package com.hina.complainapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hina.complainapp.models.UserHelperClass;
import com.hina.complainapp.session.SessionManager;

public class SignupActivity extends AppCompatActivity {


    TextInputLayout fullnameedit,usernameedit,emailedit,passwordedit;
    Button signbtn,loginbtn,back_btn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar progressBar;

    String name ;
    String username ;
    String email ;
    String password ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        View view = findViewById(android.R.id.content);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(1000);
        view.startAnimation(mLoadAnimation);

        fullnameedit = findViewById(R.id.fullname);
        usernameedit=findViewById(R.id.username);
        emailedit=findViewById(R.id.email);
        passwordedit=findViewById(R.id.password);
        signbtn=findViewById(R.id.btnsignup);
        loginbtn=findViewById(R.id.btnlogin);
        progressBar = findViewById(R.id.regsiterProgressBar);
        back_btn=findViewById(R.id.backbtn);

        rootNode=FirebaseDatabase.getInstance();
        reference=rootNode.getReference("users");
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

    }

    private Boolean validateName(){

        String val = fullnameedit.getEditText().getText().toString();

        if(val.isEmpty()){
            fullnameedit.setError("Fields Can't be Empty");
            return false;
        }else{
            fullnameedit.setError(null);
            fullnameedit.setErrorEnabled(false);
            return true;
        }


    }
    private Boolean validateUserName(){

        String val = usernameedit.getEditText().getText().toString();
        String noWhiteSpaces = "\\A\\w{4,20}\\z";
        if(val.isEmpty()){
            usernameedit.setError("Field Can't be Empty");
            return false;
        }
        else if(val.length()>=15){
            usernameedit.setError("Username too long");
            return false;
        }
        else if(!val.matches(noWhiteSpaces)){
            usernameedit.setError("White Spaces are not allowed");
            return false;
        }
        else{
            usernameedit.setError(null);
            usernameedit.setErrorEnabled(false);
            return true;
        }

    }
    private Boolean validateEmail(){
        String val = emailedit.getEditText().getText().toString();
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]";

        if(val.isEmpty()){
            emailedit.setError("Field Can't be Empty");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            emailedit.setError("Invalid Emial Address");
            return false;
        }

        else{
            emailedit.setError(null);
            emailedit.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = passwordedit.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if(val.isEmpty()){
            passwordedit.setError("Field Can't be Empty");
            return false;
        }
        else if(!val.matches(passwordVal)){
           passwordedit.setError("Password is too Weak");
            return false;
        }

        else{
            passwordedit.setError(null);
            passwordedit.setErrorEnabled(false);
            return true;
        }
    }


    public  void registerUser(View view){

        if(!validateName() | !validateEmail() | !validateUserName() | !validatePassword()){
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            name = fullnameedit.getEditText().getText().toString();
            username = usernameedit.getEditText().getText().toString();
            email = emailedit.getEditText().getText().toString();
            password = passwordedit.getEditText().getText().toString();

            reference = FirebaseDatabase.getInstance().getReference("users");
            Query checkUser = reference.orderByChild("username").equalTo(username);

            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        usernameedit.setError("User already exist!");
                        progressBar.setVisibility(View.INVISIBLE);
                    }else{
                        UserHelperClass helperClass = new UserHelperClass(name, username, email, password);
                        reference.child(username).setValue(helperClass);


                        //create a session
                        SessionManager sessionManager = new SessionManager(SignupActivity.this);
                        sessionManager.createLoginSession(name,username,email,password);
                        startActivity(new Intent(SignupActivity.this, UserHomeActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SignupActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        }


    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return super.onKeyDown(keyCode, event);
    }*/

}
