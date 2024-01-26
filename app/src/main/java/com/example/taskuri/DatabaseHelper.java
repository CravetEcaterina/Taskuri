package com.example.taskuri;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TaskDB.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "tasks"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "task";
    public static final String COLUMN_TIME = "deadline";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tasks (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT, " +
                COLUMN_TIME + " TEXT);");


        // добавление начальных данных
        db.execSQL("INSERT INTO " + TABLE + " (" +
                COLUMN_NAME + ", " +
                COLUMN_TIME  + ") VALUES ('Тестовое задание', '2024-01-01 15:00:00');");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}