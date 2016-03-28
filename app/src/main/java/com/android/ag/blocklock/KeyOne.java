package com.android.ag.blocklock;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.takwolf.android.lock9.Lock9View;

/**
 * Created by User on 10.03.2016.
 */
public class KeyOne extends Activity {

    public Lock9View lock9View;
    public TextView keySpace;
    public Button saveBtn;

    private DBHelper dbHelper;
    public SQLiteDatabase sdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.key_set_layout);

        keySpace = (TextView) findViewById(R.id.key_space);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        lock9View = (Lock9View) findViewById(R.id.lock_9_view);

        dbHelper = new DBHelper(this, "lockbase.db", null, 1);
        sdb = dbHelper.getWritableDatabase();

        lock9View.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password) {
                Toast.makeText(KeyOne.this, password, Toast.LENGTH_SHORT).show();
                keySpace.setText(password);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePass();
                finish();
            }
        });
    }

    void savePass() {
        ContentValues newValues = new ContentValues();

        newValues.put(DBHelper.PASS_ONE, keySpace.getText().toString());

        sdb.update(DBHelper.DATABASE_TABLE, newValues, "_id = ?", new String[]{Integer.toString(1)});
    }
}
