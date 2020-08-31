package com.hina.complainapp.RecyclerViewClass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hina.complainapp.R;
import com.hina.complainapp.fragments.DetailFragment;

import java.util.ArrayList;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.DatabaseViewHolder> {


    ArrayList<ModelClass> objectModelClassArrayList;

    public AdapterClass(ArrayList<ModelClass> objectModelClassArrayList) {
        this.objectModelClassArrayList = objectModelClassArrayList;
    }

    @NonNull
    @Override
    public DatabaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View singlerow= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        return new DatabaseViewHolder(singlerow);
    }

    @Override
    public void onBindViewHolder(@NonNull DatabaseViewHolder holder, int position) {

        final ModelClass objModelClass=objectModelClassArrayList.get(position);
        holder.categoryTV.setText(objModelClass.getCategory());
        holder.datetimeTV.setText(objModelClass.getDate()+"\t\t"+objModelClass.getTime());
        holder.complainCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(objModelClass.getID()));

                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,detailFragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return objectModelClassArrayList.size();
    }

    public static class DatabaseViewHolder extends RecyclerView.ViewHolder{

        TextView categoryTV,datetimeTV;
        CardView complainCardview;

        public DatabaseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTV=itemView.findViewById(R.id.complainCategorytxt);
            datetimeTV=itemView.findViewById(R.id.complaindateAndtimetxt);
            complainCardview=itemView.findViewById(R.id.complaintCardview);

        }
    }




}
