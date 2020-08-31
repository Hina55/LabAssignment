package com.hina.noteapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class NoteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notedb";
    private static final String DATABASE_TABLE = "notestable";

    //columns names
    private static final String KEY_ID ="id";
    private static final String KEY_TITLE ="title";
    private static final String KEY_CONTENT ="content";
    private static final String KEY_DATE ="dates";
    private static final String KEY_TIME ="time";

    NoteDatabase (Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table nametable(id INT PRIMARY KEY,title TEXT, content TEXT, date TEXT, time TEXT);
        String query = "CREATE TABLE "+DATABASE_TABLE+" ("+
                KEY_ID+" INTEGER PRIMARY KEY,"+
                KEY_TITLE+" TEXT,"+
                KEY_CONTENT+" TEXT,"+
                KEY_DATE+" TEXT,"+
                KEY_TIME+" TEXT"
                +" )";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion >= newVersion){
            return;
        }
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);

    }

    public long addNote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        c.put(KEY_TITLE,note.getTitle());
        c.put(KEY_CONTENT, note.getContent());

        long ID = db.insert(DATABASE_TABLE,null,c);
        Log.d("Inserted","ID -> " + ID);
        return ID;

    }
    public Note getNote(long id){
        // select * from databaseTable where id=1
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_TIME}, KEY_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

       Note note = new Note(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
        return note;
    }
    public ArrayList<Note> getNotes(){
        SQLiteDatabase db = this.getReadableDatabase();
       ArrayList<Note> allNotes = new ArrayList<>();
        // select * from database
        String query = "SELECT * FROM " +DATABASE_TABLE;

        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {

                long noteid = cursor.getLong(0);   //0 is the number of id column in database table
                String notetitle = cursor.getString(1);
                String notecontent = cursor.getString(2);
                String notedate = cursor.getString(3);
                String notetime = cursor.getString(4);

                Note newNote = new Note(noteid,notetitle,notecontent,notedate,notetime);
                allNotes.add(newNote);

            }while(cursor.moveToNext());
        }
        return allNotes;
    }

    public int editNote(Note note){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        Log.d("Edited","Edited Title: -> "+ note.getTitle() + "\n ID -> " + note.getID());
        c.put(KEY_TITLE,note.getTitle());
        c.put(KEY_CONTENT,note.getContent());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());

        return db.update(DATABASE_TABLE,c,KEY_ID+"=?",new String[]{String.valueOf(note.getID())});

    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + DATABASE_TABLE);
        db.close();
    }

    void deleteNote(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE,KEY_ID+"=?",new String[]{String.valueOf(id)});

        db.close();




    }

}
