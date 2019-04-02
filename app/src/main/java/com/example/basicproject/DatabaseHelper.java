package com.example.basicproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Todos";
    private static final String ID = "Id";
    private static final String VALUE = "ToDoElement";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VALUE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addItem(String item) {
        final String it = item;
        new Thread(new Runnable() {
            public void run() {
                SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(VALUE, it);
                long result = db.insert(TABLE_NAME, null, contentValues);
                db.close();
            }
        }).start();
    }

    public void deleteItem(String item) {
        final String it = item;
        new Thread(new Runnable(){
            public void run() {
                SQLiteDatabase db = DatabaseHelper.this.getWritableDatabase();
                String command = "DELETE FROM " + TABLE_NAME + " WHERE ID = (SELECT ID FROM " + TABLE_NAME + " WHERE " + VALUE + " = '" + it + "' LIMIT 1)";
                db.execSQL(command);
                db.close();
            }
        }).start();
    }

        public ArrayList<String> SelectAllToDos ()
        {
            ArrayList<String> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String Command = "SELECT " + VALUE + " FROM " + TABLE_NAME;

            Cursor cursor = db.rawQuery(Command, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String item = cursor.getString(cursor.getColumnIndex(VALUE));

                    list.add(item);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            db.close();
            return list;
        }
    }
