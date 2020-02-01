package com.sadam.sadamlibarary.BluetoothUtils.BluetoothDevicesArrayList;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDevicesArrayList extends ArrayList<BluetoothDevice> {
    private BluetoothDeviceToString bluetoothDeviceToString;

    public BluetoothDevicesArrayList(BluetoothDeviceToString bluetoothDeviceToString) {
        if (bluetoothDeviceToString == null) {
            this.bluetoothDeviceToString = new BluetoothDeviceToString() {
                @Override
                public String toString(BluetoothDevice bluetoothDevice) {

                    String s = "[" + bluetoothDevice.getName() + "][" + ((bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) ? "BONDED" : "UNBONDED") + "][" + bluetoothDevice.getType() + "][" + bluetoothDevice.getBluetoothClass() + "]";
                    return s;
                }
            };
        } else {
            this.bluetoothDeviceToString = bluetoothDeviceToString;
        }

    }


    public void setDevicesFromSet(Set<BluetoothDevice> bluetoothDevices_set) {
        for (BluetoothDevice bluetoothDevice : bluetoothDevices_set) {
            add(bluetoothDevice);
        }
    }

    @Nullable
    @Override
    public String[] toArray() {
        String[] strings = new String[size()];
        for (int i = 0; i < size(); i++) {
            strings[i] = bluetoothDeviceToString.toString(get(i));
        }
        return strings;
    }
}
