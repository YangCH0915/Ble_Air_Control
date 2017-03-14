package com.sndway.studio.aircontrol.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.sndway.studio.aircontrol.R;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class BleManager {

    private static BleManager instance;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;

    private BleManager(Context context){
        this.mContext = context;
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();//获取蓝牙适配器
    }

    public static BleManager getInstance(Context context){
        if(instance == null){
            instance = new BleManager(context);
        }
        return instance;
    }

    public boolean isSupportBluetooth(){

        if(mBluetoothAdapter == null){
            Toast.makeText(mContext, R.string.not_support_bluetooth, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, R.string.not_support_ble, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
