package com.sndway.studio.aircontrol.ble;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sndway.studio.aircontrol.R;


/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BleScanDeviceActivity extends ListActivity {

    private static final long SCAN_PERIOD = 10000;
    private static final int RESULT_SUCCESS_CODE = 1;
    private static final int RESULT_FIAL_CODE = -1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private Handler mHandler;
    private boolean mScanning;
    private DeviceListAdapter mDeviceListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_main_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mHandler = new Handler();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mDeviceListAdapter = new DeviceListAdapter(this);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.setListAdapter(mDeviceListAdapter);
        checkBluetoothPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDeviceListAdapter.clear();
        scanBLEDevices(false);
    }

    /*

      校验蓝牙权限
     */
    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限

            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                //具有权限
                scanBLEDevices(true);
            }
        } else {
            //系统不高于6.0直接执行
            scanBLEDevices(true);
        }
    }



    public static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1001;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限
                scanBLEDevices(true);
            } else {
                // 权限拒绝
                // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                Toast.makeText(this, "获取权限失败,蓝牙功能无法正常开启!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void scanBLEDevices(final boolean result) {
        progressBar.setVisibility(View.VISIBLE);
        if (result) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    //invalidateOptionsMenu();
                    progressBar.setVisibility(View.GONE);
                    if (mDeviceListAdapter.getCount() == 0) {
                        Toast.makeText(BleScanDeviceActivity.this,R.string.search_device_failed,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BleScanDeviceActivity.this,R.string.choose_device,Toast.LENGTH_SHORT).show();
                    }
                }
            }, SCAN_PERIOD);     //10秒之后停止扫描

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);   //开始扫描设备
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {   //更新线程UI
                        @Override
                        public void run() {
                            mDeviceListAdapter.addNewDevice(device);
                            mDeviceListAdapter.notifyDataSetChanged();   //通知UI改变了
                        }
                    });
                }
            };



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
        if (device == null) return;
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);   //停止扫描,即停止回调
            mScanning = false;
        }
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, device);
        data.putExtras(bundle);   //把设备放入
        setResult(RESULT_SUCCESS_CODE, data);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //点击返回按钮，network是传过来的值
            setResult(RESULT_FIAL_CODE);
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_FIAL_CODE);
        finish();
    }
}
