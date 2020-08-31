package com.hina.complainapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hina.complainapp.Database.ComplaintDatabase;
import com.hina.complainapp.R;
import com.hina.complainapp.RecyclerViewClass.ModelClass;
import com.hina.complainapp.UserHomeActivity;
import com.hina.complainapp.models.ComplaintHelperClass;
import com.hina.complainapp.session.SessionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends Fragment {

    CardView postComplain,listComplain;
    ComplaintDatabase complaintDatabase;
    ModelClass modelClass;
    LinearLayout recentcomplainLayout;
    TextView complainIdText,usernameText,categoryText,severityText,dateText,timeText,  norecentTextview;;

    private OnFragmentItemSelectedListener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);


        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Dashboard");

        complaintDatabase = new ComplaintDatabase(getContext());
        modelClass = complaintDatabase.getLastRecord();

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
       String user_name = userDetails.get(SessionManager.KEY_USERNAME);


        postComplain=view.findViewById(R.id.postcomplaint);
        listComplain=view.findViewById(R.id.compliantslist);
        recentcomplainLayout=view.findViewById(R.id.recentComplainLayout);
        norecentTextview=view.findViewById(R.id.noRecentComplainstxt);
        complainIdText=view.findViewById(R.id.compidtxt);
        usernameText=view.findViewById(R.id.usernametxt);
        categoryText = view.findViewById(R.id.categorytxt);
        severityText=view.findViewById(R.id.severitytxt);
        dateText=view.findViewById(R.id.datetxt);
        timeText=view.findViewById(R.id.timetxt);


        if(complaintDatabase.getLastRecord()!=null){

            if(modelClass.getUsername().equals(user_name)) {
                complainIdText.setText(String.valueOf(modelClass.getID()));
                usernameText.setText(modelClass.getUsername());
                categoryText.setText(modelClass.getCategory());
                severityText.setText(modelClass.getSeverity());
                dateText.setText(modelClass.getDate());
                timeText.setText(modelClass.getTime());
                recentcomplainLayout.setVisibility(View.VISIBLE);
                norecentTextview.setVisibility(View.INVISIBLE);
            }
        }


        postComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPostComplainButtonSelected();
            }
        });
        listComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListComplainButtonSelected();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnFragmentItemSelectedListener){
            listener = (OnFragmentItemSelectedListener) context;

        }else {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    public interface  OnFragmentItemSelectedListener{
        public void onPostComplainButtonSelected();
        public void onListComplainButtonSelected();
    }



}
