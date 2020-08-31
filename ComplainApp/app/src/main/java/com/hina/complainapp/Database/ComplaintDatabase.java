package com.hina.complainapp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.hina.complainapp.RecyclerViewClass.ModelClass;
import com.hina.complainapp.models.ComplaintHelperClass;

import java.io.ByteArrayOutputStream;


public class ComplaintDatabase extends SQLiteOpenHelper {


    private static final String DATABASE_NAME ="Complaints.db";
    private static final int DATABASE_VERSION= 2;


    private String queryToCreateDatabase = "create table Complains (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username VARCHAR(40), " +
            "category VARCHAR(60), " +
            "severity VARCHAR(40), " +
            "description VARCHAR(255), " +
            "latitude REAL, " +
            "longitude REAL, " +
            "image VARCHAR(500), " +
            "date TEXT, " +
            "time TEXT" +
            ")";

    Context context;

    //columns names
    private static final String KEY_ID ="id";
    private static final String KEY_username ="username";
    private static final String KEY_category ="category";
    private static final String KEY_severity ="severity";
    private static final String KEY_description ="description";
    private static final String KEY_latitude ="latitude";
    private static final String KEY_longitude ="longitude";
    private static final String KEY_image ="image";
    private static final String KEY_date ="date";
    private static final String KEY_time ="time";

    public ComplaintDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(queryToCreateDatabase);
            Toast.makeText(context, "Table is created successfuly", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "Error while creating table", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ModelClass getComplaint(int id){
        // select * from databaseTable where id=1
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Complains", new String[]{KEY_ID, KEY_username, KEY_category, KEY_severity, KEY_description,KEY_latitude,KEY_longitude,KEY_image,KEY_date,KEY_time}, KEY_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

        ModelClass complaintModel = new ModelClass(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getDouble(5),
                cursor.getDouble(6),
                cursor.getString(7),
                cursor.getString(8),
                cursor.getString(9));
        return complaintModel;
    }

    public void storeValues(ComplaintHelperClass objectModelClass){
        try{
            SQLiteDatabase ObjectSqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("username",objectModelClass.getUsername());
            contentValues.put("category",objectModelClass.getCategory());
            contentValues.put("severity",objectModelClass.getSeverity());
            contentValues.put("description",objectModelClass.getDescription());
            contentValues.put("latitude",objectModelClass.getLatitude());
            contentValues.put("longitude",objectModelClass.getLongitude());
            contentValues.put("image",objectModelClass.getImage());
            contentValues.put("date",objectModelClass.getDate());
            contentValues.put("time",objectModelClass.getTime());

            long checkIfQueryRuns = ObjectSqLiteDatabase.insert("Complains",null, contentValues);
            if(checkIfQueryRuns != -1){
                Toast.makeText(context, "Complaint Registerd Sucessfully", Toast.LENGTH_SHORT).show();
                ObjectSqLiteDatabase.close();
            }
            else{
                Toast.makeText(context, "Failed to add data", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(context, "Error while creating table", Toast.LENGTH_SHORT).show();
        }
    }

    public ModelClass getLastRecord(){

        SQLiteDatabase db = this.getReadableDatabase();
        ModelClass complaintModel = null;

            String selectQuery = "SELECT  * FROM Complains";
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToPosition(cursor.getCount() - 1);

        if (cursor.moveToFirst() && cursor.getCount() > 0){
            complaintModel = new ModelClass(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9));
        }
            return complaintModel;



    }

    boolean tableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
