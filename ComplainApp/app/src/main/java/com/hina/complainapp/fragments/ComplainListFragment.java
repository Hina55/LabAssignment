package com.hina.complainapp.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hina.complainapp.Database.ComplaintDatabase;
import com.hina.complainapp.R;
import com.hina.complainapp.RecyclerViewClass.AdapterClass;
import com.hina.complainapp.RecyclerViewClass.ModelClass;

import com.hina.complainapp.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComplainListFragment extends Fragment {

    ComplaintDatabase complaintDatabase;
    private RecyclerView recyclerView;
    ArrayList<ModelClass> objModelClassArrayList;
    AdapterClass databaseAdapter;
    String username,user_name;
    LinearLayout nothing_to_show_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complain_list_fragment,container,false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Complaint History");

        complaintDatabase = new ComplaintDatabase(getContext());
        recyclerView = view.findViewById(R.id.comlpainListRecycler);
        objModelClassArrayList=new ArrayList<>();
        nothing_to_show_layout=view.findViewById(R.id.nothing_to_show_image);

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        user_name = userDetails.get(SessionManager.KEY_USERNAME);

        showValuesFromDatabase();

        return view;
    }



    public void showValuesFromDatabase(){
        try {

            SQLiteDatabase objectSqLiteDatabase = complaintDatabase.getReadableDatabase();
            if(objectSqLiteDatabase!=null){

                Cursor objCursor = objectSqLiteDatabase.rawQuery("select * from Complains",null);

                if(objCursor.getCount()==0){
                    Toast.makeText(getContext(), "No data is returned", Toast.LENGTH_SHORT).show();
                }else{
                    while(objCursor.moveToNext()){

                        int ID = objCursor.getInt(0);   //0 is the number of id column in database table
                        username = objCursor.getString(1);
                        String category = objCursor.getString(2);
                        String severity = objCursor.getString(3);
                        String description = objCursor.getString(4);
                        String latitude = objCursor.getString(5);
                        String longitude = objCursor.getString(6);
                        String image = objCursor.getString(7);
                        String date = objCursor.getString(8);
                        String time = objCursor.getString(9);


                        if(username.equals(user_name)){
                            objModelClassArrayList.add(new ModelClass(ID,
                                    username,category,severity,description,
                                    Double.parseDouble(latitude),Double.parseDouble(longitude),
                                    image,date,time));
                            nothing_to_show_layout.setVisibility(View.INVISIBLE);
                        }

                    }

                    databaseAdapter=new AdapterClass(objModelClassArrayList);
                    recyclerView.hasFixedSize();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(databaseAdapter);
                }

            }else{
                Toast.makeText(getContext(), "Database is null", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){

        }
    }


}
