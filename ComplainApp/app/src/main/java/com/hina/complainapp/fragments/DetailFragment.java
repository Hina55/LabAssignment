package com.hina.complainapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hina.complainapp.Database.ComplaintDatabase;
import com.hina.complainapp.R;
import com.hina.complainapp.RecyclerViewClass.ModelClass;

import java.io.File;

public class DetailFragment extends Fragment implements OnMapReadyCallback{

    ComplaintDatabase db;
    ModelClass modelClass;
    int idComp;
    TextView complainIdText, usernameText, categoryText,severityText,descriptionText,dateText,timeText;
    ImageView capturedImage;

    SupportMapFragment supportMapFragment;



    public static  DetailFragment newInstance(){
        DetailFragment detailFragment = new DetailFragment();
        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment,container,false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Complaint Detail");
        complainIdText=view.findViewById(R.id.compidtxt);
        usernameText=view.findViewById(R.id.usernametxt);
        categoryText = view.findViewById(R.id.categorytxt);
        severityText=view.findViewById(R.id.severitytxt);
        descriptionText=view.findViewById(R.id.descriptiontxt);
        dateText=view.findViewById(R.id.datetxt);
        timeText=view.findViewById(R.id.timetxt);
        capturedImage=view.findViewById(R.id.capturedPhoto);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            String id = bundle.get("id").toString();
            //Toast.makeText(getContext(), "ID:- "+id, Toast.LENGTH_SHORT).show();
            idComp=Integer.parseInt(id);
        }

        db = new ComplaintDatabase(getContext());
        modelClass = db.getComplaint(idComp);

        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.google_map_fragment);
        supportMapFragment.getMapAsync(this);

        complainIdText.setText(String.valueOf(modelClass.getID()));
        usernameText.setText(modelClass.getUsername());
        categoryText.setText(modelClass.getCategory());
        severityText.setText(modelClass.getSeverity());
        descriptionText.setText(modelClass.getDescription());
        dateText.setText(modelClass.getDate());
        timeText.setText(modelClass.getTime());


        capturedImage.setImageURI(Uri.parse(String.valueOf(new File(modelClass.getImage()))));

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng latLng = new LatLng(modelClass.getLatitude(),modelClass.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("Complain ID: "+modelClass.getID());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        googleMap.addMarker(markerOptions).showInfoWindow();
    }
}
