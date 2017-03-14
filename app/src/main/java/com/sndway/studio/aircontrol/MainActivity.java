package com.sndway.studio.aircontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sndway.studio.aircontrol.activity.BaseActivity;
import com.sndway.studio.aircontrol.ble.BleManager;
import com.sndway.studio.aircontrol.ble.BleScanDeviceActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button btnScan;
    private TextView tvContent;
    private static final int REQUEST_ENABLE_BT = 1;
    //蓝牙相关的服务类
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter adapter;

    private final static int RESULT_SUCCESS_CODE = 1;
    private final static int RESULT_FIAL_CODE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = (Button) findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(this);
        tvContent = (TextView) findViewById(R.id.text);

        bluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
        adapter = bluetoothManager.getAdapter();

        if(!BleManager.getInstance(this).isSupportBluetooth()){
            //finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_scan:
                if (!adapter.isEnabled()) {    //蓝牙若没有打开,则打开
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Intent bloIntent = new Intent(this, BleScanDeviceActivity.class);
                    startActivityForResult(bloIntent, 0);
                }
                break;
        }
    }
}
