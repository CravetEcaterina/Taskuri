package com.example.taskuri;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListView spisok;
    EditText poisk; // Added EditText for filtering
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spisok = findViewById(R.id.spisokZataci);
        poisk = findViewById(R.id.tekst_poisk); // Initialize EditText

        poisk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String textToFind = charSequence.toString().trim();
                poiskSadaci(textToFind);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    private void refreshList() {
        db = databaseHelper.getReadableDatabase();
        userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);
        userAdapter.changeCursor(userCursor);
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getReadableDatabase();
        userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);

        String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_TIME};
        int[] views = new int[]{android.R.id.text1, android.R.id.text2};

        userAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                userCursor,
                headers,
                views,
                0
        );

        userAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text2) {
                    TextView detailsTextView = (TextView) view;

                    int IndexVremeni = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME);

                    if (IndexVremeni != -1) {
                        String vremea = cursor.getString(IndexVremeni);
                        String opisanie = String.format(Locale.getDefault(), "Выполнить задание до: %s", vremea);
                        detailsTextView.setText(opisanie);
                    }
                    return true;
                }
                return false;
            }

        });
        spisok.setAdapter(userAdapter);
    }

    public void onAddTask(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }
    private void poiskSadaci(String textToFind) {
        db = databaseHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + textToFind + "%"};
        userCursor = db.query(DatabaseHelper.TABLE, null, selection, selectionArgs, null, null, null);
        userAdapter.changeCursor(userCursor);
    }
}