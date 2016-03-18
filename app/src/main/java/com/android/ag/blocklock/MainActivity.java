package com.android.ag.blocklock;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public String TAG = "LOGI";
    public Button enableBtn, settingsBtn;

    private DBHelper dbHelper;
    public SQLiteDatabase sdb;

    public PowerManager pm;
    public PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");

        enableBtn = (Button) findViewById(R.id.enableBtn);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);

        /*PackageManager pm1 = getPackageManager();
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        MainActivity.class);
        pm1.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);*/

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wl.acquire();
                startService(new Intent(MainActivity.this, LocalService.class));
                finish();
            }
        });

        dbHelper = new DBHelper(this, "lockbase.db", null, 1);
        sdb = dbHelper.getWritableDatabase();
        CheckAndInitTable();
    }

    private void CheckAndInitTable() {
        String querySelect = "SELECT * FROM " + DBHelper.DATABASE_TABLE;
        Cursor cursorSelect = sdb.rawQuery(querySelect,null);
        if(cursorSelect.moveToFirst())
            Log.d(TAG, "Таблица существует");
        else {
            Log.d(TAG, "Таблица не существует");
            ContentValues newValues = new ContentValues();
            newValues.put(DBHelper.PASS_ONE, "1234");
            newValues.put(DBHelper.PASS_TWO, "4321");
            sdb.insert(DBHelper.DATABASE_TABLE, null, newValues);
        }
        cursorSelect.close();
    }

    @Override
    protected void onDestroy() {
        //wl.release();
        super.onDestroy();
    }
}
