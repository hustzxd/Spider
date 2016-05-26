package com.example.hustzxd.spider;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "sss";
    private WifiManager mWifiManager;
    private TextView mInfoTextView;
    private ProgressBar mProgressBar;
    private FloatingActionButton fab;
    private Spinner mBuildingNameSpinner;
    private TextInputEditText mRoomNameTextEdit;
    private TextInputEditText mXLocationTextEdit;
    private TextInputEditText mYLocationTextEdit;
    private String mBuildingName;
    private String mRoomName;
    private Integer mXLocation;
    private Integer mYLocation;
    private boolean mIsUpdateSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bmob.initialize(this, "490fc9fadc396031ddc7ccf4a4c05b8b");
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mInfoTextView = (TextView) findViewById(R.id.tv_info);
        mBuildingNameSpinner = (Spinner) findViewById(R.id.spinner_building_name);
        mRoomNameTextEdit = (TextInputEditText) findViewById(R.id.class_name);
        mXLocationTextEdit = (TextInputEditText) findViewById(R.id.x);
        mYLocationTextEdit = (TextInputEditText) findViewById(R.id.y);
        mIsUpdateSuccess = true;

        fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                    return;
                }

                mBuildingName = mBuildingNameSpinner.getSelectedItem().toString();
                mRoomName = mRoomNameTextEdit.getText().toString();
                if (TextUtils.isEmpty(mRoomName)) {
                    mRoomNameTextEdit.setError("please input room name!");
                    return;
                }
                if (TextUtils.isEmpty(mXLocationTextEdit.getText().toString())) {
                    mXLocationTextEdit.setError("please input x");
                    return;
                }
                mXLocation = Integer.parseInt(mXLocationTextEdit.getText().toString());
                if (TextUtils.isEmpty(mYLocationTextEdit.getText().toString())) {
                    mYLocationTextEdit.setError("please input y");
                    return;
                }
                mYLocation = Integer.parseInt(mYLocationTextEdit.getText().toString());
                new getWifiInfo().execute();
            }
        });


    }

    private class getWifiInfo extends AsyncTask<Void, Integer, String> {
        /**
         * AsyncTask任务开始前，UI线程更新，设置浮动按钮和
         * 并将位置信息存放到全局变量中
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            /**
             * @debug wifi信息
             */
            StringBuffer info = new StringBuffer();
            /**
             * @maxNum 采集次数
             */
            int maxNum = 5;
            /**
             * 采集进度
             */
            int progress = 0;
            /**
             * map用于保存wifi信息
             */
            List<ScanResult> myResults = new ArrayList<>();
            for (int i = 0; i < maxNum; i++) {
                mWifiManager.startScan();
                List<ScanResult> results = mWifiManager.getScanResults();
                for (ScanResult result : results) {
                    if (result.SSID.startsWith("WRTnode")) {
                        if (i == 0) {
                            myResults.add(result);
                        } else {
                            update(myResults, result);
                        }
                    }
                }
                progress += 100 / maxNum / 5;
                publishProgress(progress);
                if (i != maxNum - 1) {
                    try {
                        Thread.sleep(400);
                        progress += 100 / maxNum / 5;
                        publishProgress(progress);
                        Thread.sleep(400);
                        progress += 100 / maxNum / 5;
                        publishProgress(progress);
                        Thread.sleep(400);
                        progress += 100 / maxNum / 5;
                        publishProgress(progress);
                        Thread.sleep(400);
                        progress += 100 / maxNum / 5;
                        publishProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("dxz", "append");
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.setBuildingName(mBuildingName);
            wifiInfo.setRoomName(mRoomName);
            wifiInfo.setX(mXLocation);
            wifiInfo.setY(mYLocation);
            List<String> SSIDs = new ArrayList<>();
            List<String> BSSIDs = new ArrayList<>();
            List<Integer> RSSIs = new ArrayList<>();
            for (ScanResult result : myResults) {
                SSIDs.add(result.SSID);
                BSSIDs.add(result.BSSID);
                RSSIs.add(result.level / maxNum);
            }
            wifiInfo.setSSIDs(SSIDs);
            wifiInfo.setBSSIDs(BSSIDs);
            wifiInfo.setRSSIs(RSSIs);

            wifiInfo.save(getApplicationContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    Log.i("dxz", "upload success");
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.i("dxz", "upload failed" + s);
                    mIsUpdateSuccess = false;
                    cancel(true);
                }
            });
            return info.toString();
        }

        private void update(List<ScanResult> myResults, ScanResult result) {
            for (int i = 0; i < myResults.size(); i++) {
                if (myResults.get(i).BSSID.equals(result.BSSID)) {
                    myResults.get(i).level += result.level;
                    return;
                }
            }
        }

        /**
         * 任务异常结束
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i("dxz", "任务异常结束");
            toast("upload fail!");
            fab.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        /**
         * AsyncTask执行完毕，返回到UI线程,更新UI线程
         *
         * @param info 返回的参数
         */
        @Override
        protected void onPostExecute(String info) {
            super.onPostExecute(info);
            mProgressBar.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            mInfoTextView.setText(info);
            if (!mIsUpdateSuccess) {
                toast("upload fail!");
            } else {
                toast("upload success!");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
            mInfoTextView.setText("loading..." + values[0] + "%");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
