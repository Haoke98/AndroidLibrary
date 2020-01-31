package com.sadam.sadamlibarary.BluetoothUtils;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class BluetoothBroadCastReciver extends BroadcastReceiver {

    private BluetoothDevice device;

    static public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
            Log.e("returnValue", "" + returnValue);
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    static public void setPairingConfirmation(Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws Exception {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                try {
                    /**/
                    Toast.makeText(context, "1", Toast.LENGTH_LONG);
//                    setPairingConfirmation(device.getClass(),device,true);
                    Toast.makeText(context, "2", Toast.LENGTH_LONG);
//                    abortBroadcast();
                    Toast.makeText(context, "3", Toast.LENGTH_LONG);
                    boolean ret = setPin(device.getClass(), device, "1234");
                    Toast.makeText(context, "4" + ret, Toast.LENGTH_LONG);
//                    ClsUtils.cancelBondProcess(device.getClass(),device);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}


/**************** 蓝牙配对函数 ***************/


class ClsUtils {
    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    static public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str) throws Exception {
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]
                            {str.getBytes()});
            Log.e("returnValue", "" + returnValue);
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class<?> btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
//        cancelBondProcess(btClass, device);
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    // 取消配对
    static public boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    //确认配对

    static public void setPairingConfirmation(Class<?> btClass, BluetoothDevice device, boolean isConfirm) throws Exception {
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device, isConfirm);
    }
}


//    /**
//     *
//     * @param clsShow
//     */
//    static public void printAllInform(Class clsShow) {
//        try {
//             取得所有方法
//            Method[] hideMethod = clsShow.getMethods();
//            int i = 0;
//            for (; i < hideMethod.length; i++) {
//                Log.e("method name", hideMethod[i].getName() + ";and the i is:"+ i);
//            }
//             取得所有常量
//            Field[] allFields = clsShow.getFields();
//            for (i = 0; i < allFields.length; i++) {
//                Log.e("Field name", allFields[i].getName());
//            }
//        } catch (SecurityException e) {
//             throw new RuntimeException(e.getMessage());
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//             throw new RuntimeException(e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//             TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//}
