package com.hina.noteapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
   public static TextView tv_nothing_to_display;
  public static  LinearLayout linearLayout;
    CustomAdapter myCustomAdapter;
    List<Note> noteList;
    Button btnimage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        listView = findViewById(R.id.listview);
        tv_nothing_to_display = findViewById(R.id.tv_nothing_to_display);
        linearLayout=findViewById(R.id.nothing_to_show_image);
        btnimage=findViewById(R.id.btnimgtext);
        fillListview();

        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddNoteActivity.class));
            }
        });




        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class));
            }
        });*/

    }

    public void fillListview() {
        listView = findViewById(R.id.listview);
        NoteDatabase dbhelper = new NoteDatabase(this);

        noteList = dbhelper.getNotes();
        myCustomAdapter = new CustomAdapter(this, R.layout.list_item_view, noteList);
        listView.setAdapter(myCustomAdapter);

        if(noteList.isEmpty()){
            tv_nothing_to_display.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            tv_nothing_to_display.setVisibility(View.INVISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add){
            startActivity(new Intent(this, AddNoteActivity.class));
            //Toast.makeText(this, "Add button is clicked", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.delete_all_notes){
            NoteDatabase db = new NoteDatabase(this );
            db.deleteAll();
            myCustomAdapter.myNoteList.clear();
            myCustomAdapter.notifyDataSetChanged();

            if(myCustomAdapter.myNoteList.size() > 0){
                Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
            }else if(myCustomAdapter.myNoteList.size() == 0 && tv_nothing_to_display.getVisibility() == View.VISIBLE){
                Toast.makeText(this, "No Notes to be Deleted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "All Notes Deleted", Toast.LENGTH_SHORT).show();
            }


            if(noteList.isEmpty()){
                tv_nothing_to_display.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                tv_nothing_to_display.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
        return super.onKeyDown(keyCode, event);
    }
}
