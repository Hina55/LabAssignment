package com.hina.noteapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Note> {

    Context ctx;
    List<Note> myNoteList;
    private LayoutInflater inflater=null;
     Note note;


    public CustomAdapter(Context ctx, int list_item_view, List<Note> myNoteList){
        super(ctx, R.layout.list_item_view,myNoteList);
        this.ctx = ctx;
        this.myNoteList=myNoteList;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.myNoteList.size() ;
    }

    @Override
    public Note getItem(int position) {
        return this.myNoteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_view,null);

            holder = new ViewHolder();
            holder.Ntitle = (TextView)convertView.findViewById(R.id.nTitle);
            holder.Ndate = (TextView)convertView.findViewById(R.id.nDate);
            holder.Ntime = (TextView)convertView.findViewById(R.id.nTime);
           holder.Nid = (TextView) convertView.findViewById(R.id.nId);
           /*holder.deleteImageButton = (ImageButton) convertView.findViewById(R.id.deleteImagebtn);
           holder.editImageButton = (ImageButton) convertView.findViewById(R.id.editImagebtn);*/



            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }


         note = myNoteList.get(position);

        holder.Ntitle.setText(note.getTitle());
        holder.Ndate.setText("Created: "+note.getDate());
        holder.Ntime.setText(note.getTime());
        holder.Nid.setText(String.valueOf(note.getID()));


        convertView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(),DetailsActivity.class);
                i.putExtra("ID",myNoteList.get(position).getID());
                v.getContext().startActivity(i);
            }
        });

        //final ViewHolder finalHolder = holder;
        convertView.setOnLongClickListener(new View.OnLongClickListener(){


            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onLongClick(View v){
                CharSequence options[] = new CharSequence[] {"Edit", "Delete","Share"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent i = new Intent(ctx, EditActivity.class);
                            i.putExtra("ID",myNoteList.get(position).getID());
                            ctx.startActivity(i);

                        }
                        else if(which == 1){
                            NoteDatabase db = new NoteDatabase(getContext());
                            db.deleteNote(myNoteList.get(position).getID());
                            myNoteList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();

                            if(myNoteList.isEmpty()){
                                MainActivity.tv_nothing_to_display.setVisibility(View.VISIBLE);
                                MainActivity.linearLayout.setVisibility(View.VISIBLE);
                            }else{
                                MainActivity.tv_nothing_to_display.setVisibility(View.INVISIBLE);
                                MainActivity.linearLayout.setVisibility(View.INVISIBLE);
                            }
                        }
                        else if(which == 2){
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,myNoteList.get(position).getTitle());
                            shareIntent.putExtra(Intent.EXTRA_TEXT,myNoteList.get(position).getContent());
                            ctx.startActivity(Intent.createChooser(shareIntent,"Share Note via:"));

                        }
                    }
                });

                builder.show();



                /*Snackbar snackbar = Snackbar.make(v, "Do you want to delete?", Snackbar.LENGTH_LONG).setDuration(5000)
                        .setAction("DELETE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                NoteDatabase db = new NoteDatabase(v.getContext());
                                db.deleteNote(myNoteList.get(position).getID());
                                myNoteList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(v.getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();

                                if(myNoteList.isEmpty()){
                                    MainActivity.tv_nothing_to_display.setVisibility(View.VISIBLE);
                                }else{
                                    MainActivity.tv_nothing_to_display.setVisibility(View.INVISIBLE);
                                }



                            }
                        });
                snackbar.setActionTextColor(Color.parseColor("#F77B71"));
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                TextView actionText = sbView.findViewById(R.id.snackbar_action);
                actionText.setCompoundDrawablesRelativeWithIntrinsicBounds(ic_delete_white_24dp, 0, 0, 0);
                snackbar.show();*/



                /*finalHolder.deleteImageButton.setVisibility(VISIBLE);
                finalHolder.editImageButton.setVisibility(VISIBLE);

                finalHolder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoteDatabase db = new NoteDatabase(v.getContext());
                        db.deleteNote(myNoteList.get(position).getID());
                        myNoteList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(v.getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();

                        if(myNoteList.isEmpty()){
                            MainActivity.tv_nothing_to_display.setVisibility(VISIBLE);
                        }else{
                            MainActivity.tv_nothing_to_display.setVisibility(View.INVISIBLE);
                        }
                    }
                });*/

                return true;
            }
        });



        return convertView;
    }

    private static class ViewHolder{
        public TextView Ntitle;
        public TextView Ndate;
        public TextView Ntime;
        public TextView Nid;
       /* public ImageButton editImageButton;
        public ImageButton deleteImageButton;*/

    }



}
