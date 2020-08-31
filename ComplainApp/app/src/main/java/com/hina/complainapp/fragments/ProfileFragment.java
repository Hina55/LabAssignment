package com.hina.complainapp.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hina.complainapp.LoginActivity;
import com.hina.complainapp.R;
import com.hina.complainapp.session.SessionManager;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    TextView fullnametxt, emailtxt, usernametxt;
    EditText fullnameedit, emailedit, passwordedit;
    Button updatebutton;
    DatabaseReference reference;
    String full_name,email_text,user_name,password_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        //hooks
        fullnametxt = view.findViewById(R.id.fullname_text);
        emailtxt = view.findViewById(R.id.email_text);
        usernametxt = view.findViewById(R.id.username_text);

        fullnameedit = view.findViewById(R.id.fullname_edittext);
        passwordedit = view.findViewById(R.id.password_edittext);
        emailedit = view.findViewById(R.id.email_edittext);
        updatebutton = view.findViewById(R.id.updatebtn);

        reference= FirebaseDatabase.getInstance().getReference("users");


        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();

       full_name = userDetails.get(SessionManager.KEY_FULLNAME);
       email_text = userDetails.get(SessionManager.KEY_EMAIL);
       user_name = userDetails.get(SessionManager.KEY_USERNAME);
       password_text = userDetails.get(SessionManager.KEY_PASSWORD);

        fullnametxt.setText(full_name);
        emailtxt.setText(email_text);
        usernametxt.setText(user_name);

        fullnameedit.setText(full_name);
        emailedit.setText(email_text);
        passwordedit.setText(password_text);


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() | !validateEmail() | !validatePassword()) {
                    return;
                } else if (isNamedChanged() || isPasswordChanged() || isEmailChanged()) {
                    Toast.makeText(getContext(), "Data has been Updated!", Toast.LENGTH_SHORT).show();
                    SessionManager sessionManager = new SessionManager(getContext());
                    sessionManager.createLoginSession(full_name,user_name,email_text,password_text);
                } else {
                    Toast.makeText(getContext(), "Data is same & can't be updated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Profile");



        return view;
    }

    private Boolean isNamedChanged() {

        if(!full_name.equals(fullnameedit.getText().toString())){
            reference.child(user_name).child("fullname").setValue(fullnameedit.getText().toString());
            full_name=fullnameedit.getText().toString();
            return true;
        }
        else{
            return false;
        }


    }
    private Boolean isEmailChanged() {
        if(!email_text.equals(emailedit.getText().toString())){
            reference.child(user_name).child("email").setValue(emailedit.getText().toString());
            email_text=emailedit.getText().toString();
            return true;
        }
        else{
            return false;
        }
    }
    private Boolean isPasswordChanged() {
        if(!password_text.equals(passwordedit.getText().toString())){
            reference.child(user_name).child("password").setValue(passwordedit.getText().toString());
            password_text=passwordedit.getText().toString();
            return true;
        }
        else{
            return false;
        }
    }


    private Boolean validateName() {

        String val = fullnameedit.getText().toString();

        if (val.isEmpty()) {
            fullnameedit.setError("Fields Can't be Empty");
            return false;
        } else {
            fullnameedit.setError(null);
            return true;
        }


    }

    private Boolean validateEmail() {
        String val = emailedit.getText().toString();
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]";

        if (val.isEmpty()) {
            emailedit.setError("Field Can't be Empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            emailedit.setError("Invalid Emial Address");
            return false;
        } else {
            emailedit.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = passwordedit.getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            passwordedit.setError("Field Can't be Empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            passwordedit.setError("Password is too Weak");
            return false;
        } else {
            passwordedit.setError(null);
            return true;
        }
    }

}
