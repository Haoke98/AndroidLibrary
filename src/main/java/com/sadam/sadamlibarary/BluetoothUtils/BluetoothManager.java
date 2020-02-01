package com.sadam.sadamlibarary.BluetoothUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.Toast;

import com.sadam.sadamlibarary.BluetoothUtils.BluetoothDevicesArrayList.BluetoothDeviceToString;
import com.sadam.sadamlibarary.BluetoothUtils.BluetoothDevicesArrayList.BluetoothDevicesArrayList;

import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission;

public class BluetoothManager {
    /**
     * 我们要连接的蓝牙设备
     */
    private static final String BLUETOOTHDEVICENAME = "ShouZhiKangFuQi";
    /**
     * 申请位置权限时用到
     */
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BluetoothAdapter bluetoothAdapter;
    /**
     * 用于储存蓝牙设备的信息,已配对的设备信息 或者 附近未配对的设备（为了方便得到String）
     */
    private BluetoothDevicesArrayList bluetoothDevicesArrayList = new BluetoothDevicesArrayList(new BluetoothDeviceToString() {
        @Override
        public String toString(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getName();
        }
    });
    private Activity activity;
    private OnFoundTargetDeviceListener onFoundTargetDeviceListener;
    private ProgressDialog progressDialog;
    /**
     * 广播接收器，专门用来监听有关
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /**
             * 用于ListView显示组建的字符串适配器
             */
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    /*如果没有配对的设备，则把它的信息存起来*/
//                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    /*还没匹配的设备的名字一般都为null,为了避免nullPointException先判断一下*/
//                        if (device.getName() != null) {
                    /*如果是我们的设备才把它显示出来*/
//                            if (device.getName().equals(BLUETOOTHDEVICENAME)) {
//                                getBone_and_Start(device);
//                            } else {
                    /*如果不是我们的设备，则不把它显示，跳过*/
                    Toast.makeText(context, "" + device.getName() + device.getAddress(), Toast.LENGTH_LONG).show();
//                            }
//                        } else {

//                        }
//                    } else {
                    /*如果已经配对过的设备则跳过*/
                    /*如果扫描获取的已配对的设备的名字是我们的设备，则直接进入游戏*/
//                        if (device.getName() == BLUETOOTHDEVICENAME) {
//                            Toast.makeText(context,"已找到目标设备",Toast.LENGTH_LONG).show();
//                            onFoundTargetDeviceListener.handle(device);
//                        }
//                    }
                    bluetoothDevicesArrayList.add(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    progressDialog.dismiss();
                    if (bluetoothDevicesArrayList.size() == 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle("BluetoothManager");
                        dialog.setMessage("扫描结束，附近没有找到可连接的蓝牙设备");
                        dialog.setPositiveButton("重新扫描", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanRemoteDevices();
                            }
                        });
                        dialog.setNegativeButton("放弃挣扎", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        dialog.show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setTitle("附近未配对的蓝牙设备");
                        dialog.setSingleChoiceItems(bluetoothDevicesArrayList.toArray(), 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getBone_and_Start(bluetoothDevicesArrayList.get(which));
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("重新扫描", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanRemoteDevices();
                                dialog.dismiss();
                            }
                        });
                        dialog.setPositiveButton("返回已配对列表", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showBondedDevicesList();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                    break;
//                case BluetoothDevice.ACTION_PAIRING_REQUEST:
//                    try {
//                        setPairingConfirmation(device.getClass(),device,true);
//                        /**/
//                        abortBroadcast();
//                        boolean ret = setPin(device.getClass(),device,"1234");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    break;
            }
        }
    };

    public BluetoothManager(final Activity activity, OnFoundTargetDeviceListener onFoundTargetDeviceListener) {
        this.activity = activity;
        this.onFoundTargetDeviceListener = onFoundTargetDeviceListener;
        IntentFilter intentFilter = new IntentFilter();
        /*动态注册蓝牙广播监听器，当一个设备被发现时被调用onReceive*/
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        /*当搜索结束时调用onReceive*/
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        /*注册监听配对的监听器*/
//        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        activity.registerReceiver(receiver, intentFilter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*判断BluetoothAdapter对象是否为空，如果空，则表明本机没有蓝牙传感器*/
        if (bluetoothAdapter != null) {
//            Toast.makeText(this,"本机拥有蓝牙传感器",Toast.LENGTH_LONG).show();
            /*调用isEnable()方法判断蓝牙打开状态还是关闭状态*/
            if (bluetoothAdapter.isEnabled()) {
                showBondedDevicesList();
            } else {
                /*如果蓝牙关闭状态，则创建一个Intent对象，该对象启动一个Activity,提示用户打开蓝牙，即启动蓝牙适配器*/
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(intent);
            }
        } else {
            Toast.makeText(activity, "本机没有蓝牙传感器", Toast.LENGTH_LONG).show();
        }

        /*为扫描附近的设备请求位置权限，targetSdkVersion超过28,必须这么做*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION, permission.BLUETOOTH_PRIVILEGED}, PERMISSION_REQUEST_COARSE_LOCATION);
        }



        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Bluetooth Manager");
        progressDialog.setMessage("正在扫描附近努力争取附近还未配对的设备...");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (bluetoothAdapter.isDiscovering()) {
                    /*如果是正在扫描，则取消扫描*/
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothDevicesArrayList.clear();
            }
        });
    }

    /**
     * 获取已配对的设备
     */
    public void showBondedDevicesList() {
        if (bluetoothAdapter.isDiscovering()) {
            /*如果是正在扫描，则取消扫描*/
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothDevicesArrayList.clear();
        bluetoothDevicesArrayList.setDevicesFromSet(bluetoothAdapter.getBondedDevices());
        if (bluetoothDevicesArrayList.size() > 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("已配对的蓝牙设备");
            dialog.setSingleChoiceItems(bluetoothDevicesArrayList.toArray(), 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onFoundTargetDeviceListener.handle(bluetoothDevicesArrayList.get(which));
                    dialog.dismiss();
                    activity.unregisterReceiver(receiver);

                }
            });
            dialog.setNegativeButton("扫描附近的可用设备", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scanRemoteDevices();
                }
            });
            dialog.show();

//            for (int i ) {

//                if (device.getName().equals(BLUETOOTHDEVICENAME)) {
//                    getBone_and_Start(device);
//                } else {
            /*如果要连接的设备不是我们想要的设备，则跳过*/
//                    Toast.makeText(this,""+device.getName()+device.getAddress(),Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(activity, "本机还没有已配对的蓝牙设备", Toast.LENGTH_LONG).show();
            scanRemoteDevices();
        }
    }


    private void getBone_and_Start(BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            final ProgressDialog dialog = new ProgressDialog(activity);
            dialog.setMessage("快要进入配对模式,当配对窗口弹出来时输入1234，并确认。");
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            device.createBond();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 2000);
        } else {
            onFoundTargetDeviceListener.handle(device);
        }
    }


    private void scanRemoteDevices() {
        bluetoothDevicesArrayList.clear();
        progressDialog.show();
        /*开始扫描*/
        bluetoothAdapter.startDiscovery();
    }

}
