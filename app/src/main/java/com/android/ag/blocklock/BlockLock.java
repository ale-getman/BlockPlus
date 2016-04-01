package com.android.ag.blocklock;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.takwolf.android.lock9.Lock9View;

import java.io.File;
import java.io.PrintWriter;

/**
 * Created by User on 10.03.2016.
 */
public class BlockLock extends Activity {

    public String Tag = "LOGI";

    public ProgressDialog dialog;

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
                    //showToast("Разблокирован",instance);
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
        createNotification();
        Cursor cursor = sdb.query(dbHelper.DATABASE_TABLE, new String[]{dbHelper._ID, dbHelper.PASS_ONE,
                dbHelper.PASS_TWO}, null, null, null, null, null) ;

        cursor.moveToFirst();
        pass_one = cursor.getString(cursor.getColumnIndex(DBHelper.PASS_ONE));
        pass_two = cursor.getString(cursor.getColumnIndex(DBHelper.PASS_TWO));
        cursor.close();

        Log.d(Tag, "pass_one: " + pass_one);
        Log.d(Tag, "pass_one: " + pass_two);

        /*if(pass.equals(pass_one) || pass.equals(pass_two))
            return true;
        else
            return false;*/

        if(pass.equals(pass_one))
            return true;
        else
            if(pass.equals(pass_two))
            {
                //new RequestTask().execute();
                //mHandler.obtainMessage(NEED_DELAY).sendToTarget();
                //createNotification();
                deleteFile();

                boolean res_flag;

                res_flag = slientUninstall("com.owncloud.android");
                Log.d("LOGI", "res_flag_1: " + res_flag);

                res_flag = slientUninstall("de.blinkt.openvpn");
                Log.d("LOGI", "res_flag_2: " + res_flag);

                res_flag = slientUninstall("com.xabber.android");
                Log.d("LOGI", "res_flag_3: " + res_flag);

                res_flag = slientUninstall("com.csipsimple");
                Log.d("LOGI", "res_flag_4: " + res_flag);

                res_flag = slientUninstall("org.thoughtcrime.redphone");
                Log.d("LOGI", "res_flag_5: " + res_flag);

                /*boolean res_flag = slientUninstall("com.android.ag.firapp");
                Log.d("LOGI", "res_flag: " + res_flag);*/
                //showToast("Введен пароль 2", instance);
                //Toast.makeText(instance, "Введен пароль 2", Toast.LENGTH_SHORT).show();
                return true;
            }
            else
                return false;

    }

    public void deleteFile(){
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/testvpn.ovpn");
        file.delete();
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            boolean res_flag = slientUninstall("com.owncloud.android");
            Log.d("LOGI", "res_flag: " + res_flag);

            res_flag = slientUninstall("de.blinkt.openvpn");
            Log.d("LOGI", "res_flag: " + res_flag);

            res_flag = slientUninstall("com.csipsimple:sipStack");
            Log.d("LOGI", "res_flag: " + res_flag);

            res_flag = slientUninstall("com.android.ag.firapp");
            Log.d("LOGI", "res_flag: " + res_flag);


            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(BlockLock.this);
            dialog.setMessage("Подождите,сверяю...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public boolean slientUninstall(String packageName)
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter pPrintWriter = new PrintWriter(process.getOutputStream());
            pPrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            pPrintWriter.println("pm uninstall "+packageName);
            pPrintWriter.flush();
            pPrintWriter.close();
            int value = process.waitFor();
            return (value == 0) ? true : false ;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false ;
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

    public void createNotification(){
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        if(Build.VERSION.SDK_INT > 10) {
            Notification.Builder builder = new Notification.Builder(context);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.icons_lock)
                            // большая картинка
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.icons_lock))
                            //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                    .setTicker("Происходит проверка введеного пароля")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                            //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                    .setContentTitle("Проверка пароля");
                            //.setContentText(res.getString(R.string.notifytext))
                    //.setContentText("Приложение №" + number); // Текст уведомления

            // Notification notification = builder.getNotification(); // до API 16
            Notification notification;
            if (Build.VERSION.SDK_INT < 16)
                notification = builder.getNotification();
            else
                notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(888, notification);
        }
        else
        {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

            int icon = R.drawable.icons_lock;
            CharSequence tickerText = "Происходит проверка введеного пароля";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, tickerText, when);

            //CharSequence contentTitle = "Агрегатор работает";
            //CharSequence contentText = "Приложение №" + number;

            final int HELLO_ID = 888;

            mNotificationManager.notify(HELLO_ID, notification);
        }
    }
}
