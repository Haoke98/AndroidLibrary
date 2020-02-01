package com.sadam.sadamlibarary.BluetoothUtils;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public interface OnFoundTargetDeviceListener extends Serializable {
    void handle(BluetoothDevice bluetoothDevice);
}
