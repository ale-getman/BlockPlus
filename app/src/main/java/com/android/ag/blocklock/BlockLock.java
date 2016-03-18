package com.android.ag.blocklock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.takwolf.android.lock9.Lock9View;

/**
 * Created by User on 10.03.2016.
 */
public class BlockLock extends Activity {

    public String Tag = "LOGI";

    public Lock9View lock9View;
    private LockLayer lockLayer;
    private View lockView;

    private final int NEED_DELAY = 1;
    private long delay = 100l;

    private static Context instance = null;
    public static boolean isShown = false;

    private DBHelper dbHelper;
    public SQLiteDatabase sdb;

    private void showToast(String str, Context context) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.block_lock_layout);

        instance = BlockLock.this;

        isShown = true;

        dbHelper = new DBHelper(this, "lockbase.db", null, 1);
        sdb = dbHelper.getWritableDatabase();

        lockView = View.inflate(this, R.layout.block_lock_layout, null);

        lockLayer = LockLayer.getInstance(this);
        lockLayer.setLockView(lockView);
        lockLayer.lock();

        lock9View = (Lock9View) lockView.findViewById(R.id.lock_9_view);

        lock9View.setCallBack(new Lock9View.CallBack() {

            @Override
            public void onFinish(String password) {
                boolean flag = checkPass(password);
                if(flag) {
                    lockLayer.unlock();
                    finish();
                }
                else
                    Toast.makeText(instance, "Введен неправильный пароль", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean checkPass(String pass) {
        String pass_one = "";
        String pass_two = "";

        Cursor cursor = sdb.query(dbHelper.DATABASE_TABLE, new String[]{dbHelper._ID, dbHelper.PASS_ONE,
                dbHelper.PASS_TWO}, null, null, null, null, null) ;

        cursor.moveToFirst();
        pass_one = cursor.getString(cursor.getColumnIndex(DBHelper.PASS_ONE));
        pass_two = cursor.getString(cursor.getColumnIndex(DBHelper.PASS_TWO));
        cursor.close();

        Log.d(Tag, "pass_one: " + pass_one);
        Log.d(Tag, "pass_one: " + pass_two);

        if(pass.equals(pass_one) || pass.equals(pass_two))
            return true;
        else
            return false;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case NEED_DELAY:
                    mHandler.postDelayed(finishCurAct, delay);
                    break;

                default:
                    break;
            }
        };
    };

    private Runnable finishCurAct = new Runnable() {

        @Override
        public void run() {
            lockLayer.unlock();

            BlockLock.this.finish();
            showToast("Разблокировать успех ~", BlockLock.this);
        }
    };

    @Override
    protected void onDestroy() {
        isShown = false;
        instance = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.screen_lock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    public static Context getInstance() {
        return instance;
    }
}
