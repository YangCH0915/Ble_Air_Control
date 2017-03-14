package com.sndway.studio.aircontrol.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sndway.studio.aircontrol.R;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class DeviceListAdapter extends BaseAdapter{

    private Context mContext;
    private List<BluetoothDevice> mList;
    private LayoutInflater mInflater;

    public DeviceListAdapter(Context context){
        super();
        this.mContext = context;
        this.mList = new ArrayList<>();
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addNewDevice(BluetoothDevice device){
        if(!mList.contains(device)){
            mList.add(device);
        }
    }

    public BluetoothDevice getDevice(int position){
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
           convertView = mInflater.inflate(R.layout.device_list_layout,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = mList.get(position);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0){
            viewHolder.deviceName.setText(deviceName);
        }else{
            viewHolder.deviceName.setText(R.string.unknown_device);
        }
        viewHolder.deviceAddress.setText(device.getAddress());

        return convertView;
    }

    public void clear(){
        mList.clear();
    }

    class ViewHolder{
        TextView deviceName,deviceAddress;
    }
}
