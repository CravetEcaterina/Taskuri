package com.example.taskuri;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    EditText pole_opisanie;
    TimePicker vremea; // Assuming the DatePicker has the ID 'deadline'
    Button knopka_udaliti;
    Button knopka_sohraniti;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long userId = 0;

    @SuppressLint({"Range", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        pole_opisanie = findViewById(R.id.opisanie);
        vremea = findViewById(R.id.vibiraika_vremeni);
        knopka_udaliti = findViewById(R.id.btn_udaliti);
        knopka_sohraniti = findViewById(R.id.btn_sohraniti);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }

        // If userId > 0, it means we are editing an existing entry
        if (userId > 0) {
            userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE +
                    " WHERE " + DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
            if (userCursor.moveToFirst()) {
                pole_opisanie.setText(userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));

                // Assuming that COLUMN_DEADLINE is a DATETIME column, you need to parse the date
                String deadlineString = userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_TIME));
                // Parse the deadlineString into a Date object or use it as needed

                int hourOfDay = 0;
                int minute = 0;

                String[] timeParts = deadlineString.split(" ");
                if (timeParts.length > 0) {
                    String[] timeComponents = timeParts[0].split(":");
                    if (timeComponents.length == 2) {
                        hourOfDay = Integer.parseInt(timeComponents[0]);
                        minute = Integer.parseInt(timeComponents[1]);
                        // Use hourOfDay and minute as needed
                    } else {
                        // Handle invalid time format
                    }
                } else {
                    // Handle invalid deadlineString format
                }


                // Assuming deadlineTimePicker is your TimePicker widget
                vremea.setIs24HourView(true); // Set to true if you want 24-hour format
                vremea.setCurrentHour(hourOfDay);
                vremea.setCurrentMinute(minute);

            }
            userCursor.close();
        } else {
            // If userId <= 0, it means we are adding a new entry
            // Hide the delete button since there's nothing to delete
            knopka_udaliti.setVisibility(View.GONE);
        }
    }



    public void onSaveBtnClick(View view) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, pole_opisanie.getText().toString());

        int hourOfDay = vremea.getHour();
        int minute = vremea.getMinute();
        String deadline = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);


        cv.put(DatabaseHelper.COLUMN_TIME, deadline);

        if (userId > 0) {
            db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + userId, null);
        } else {
            db.insert(DatabaseHelper.TABLE, null, cv);
        }
        goHome();
    }


    public void onUdalitiBtnClick(View view){
        db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(userId)});
        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}