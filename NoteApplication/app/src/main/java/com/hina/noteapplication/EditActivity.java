package com.hina.noteapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText titleedit,contentedit;
    Calendar calendar;
    String todaysDate;
    String currentime;
    NoteDatabase db;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        titleedit = findViewById(R.id.titleEdittxt);
        contentedit = findViewById(R.id.contentEdittxt);

        Intent i = getIntent();
        Long id = i.getLongExtra("ID",0);
        //Toast.makeText(this, "Selected ID -> "+id, Toast.LENGTH_SHORT).show();

         db = new NoteDatabase(this);
         note = db.getNote(id);
        getSupportActionBar().setTitle(note.getTitle());
        titleedit.setText(note.getTitle());
        contentedit.setText(note.getContent());


        Log.d("calender","Date and Time: "+todaysDate +" and "+currentime );

        titleedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    getSupportActionBar().setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //get current date and time
        calendar = Calendar.getInstance();
        todaysDate = calendar.get(Calendar.YEAR)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.DAY_OF_MONTH);
        //currentime = pad(calendar.get(Calendar.HOUR))+":"+pad(calendar.get(Calendar.MINUTE));
        currentime = formatTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));



    }

    public String formatTime(int hourOfDay, int minute) {

        String timeSet;
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            timeSet = "pm";
        } else if (hourOfDay == 0) {
            hourOfDay += 12;
            timeSet = "am";
        } else if (hourOfDay == 12)
            timeSet = "pm";
        else
            timeSet = "am";

        String minutes;
        if (minute < 10)
            minutes = "0" + minute;
        else
            minutes = String.valueOf(minute);

        return String.valueOf(hourOfDay) + ':' + minutes + " " + timeSet;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.update){

                if(titleedit.getText().length() != 0) {
                    note.setTitle(titleedit.getText().toString());
                    note.setContent(contentedit.getText().toString());
                    note.setDate(todaysDate);
                    note.setTime(currentime);
                    long id = db.editNote(note);
                    Log.d("EDITED", "EDIT: id " + id);
                    goToMain();
                    Toast.makeText(this, "Note Updated.", Toast.LENGTH_SHORT).show();
                }
                else{
                    titleedit.setError("Title can't be Empty.");
                }

        }

        return super.onOptionsItemSelected(item);
    }
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
