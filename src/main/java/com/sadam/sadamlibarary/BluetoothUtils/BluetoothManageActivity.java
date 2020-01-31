package com.sadam.sadamlibarary.BluetoothUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sadam.sadamlibarary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.Manifest.permission;

public class BluetoothManageActivity extends AppCompatActivity {
    /**
     * 我们要连接的蓝牙设备
     */
    private static final String BLUETOOTHDEVICENAME = "ShouZhiKangFuQi";
    /**
     * 调用 Log.d(String Tag,String mess)方法时用到，指定该页面
     */
    private static final String TAG = "Main";
    /**
     * 申请位置权限时用到
     */
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Set<BluetoothDevice> bondDevices;
    private ListView listViewSearchedDevices;
    private Button btnShowBondedDevices;
    private Button btnScanUnbondedDevices;
    private BluetoothAdapter bluetoothAdapter;
    /**
     * 用于储存给ListView提供的蓝牙设备的信息,已配对的设备信息
     */
    private List<String> bluetoothDevices = new ArrayList<>();
    /**
     * 用于ListView显示组建的字符串适配器
     */
    private ArrayAdapter<String> arrayAdapter;

    private BluetoothDevice device;
    /**
     * 共享首选项，用来存储暂时性，轻量级数据
     */
    private SharedPreferences sharedPreferences;
    /**
     * 游戏类别，根据值决定要启动 哪一个游戏对应的活动和监听线程
     */
    private String GameMod;
    private TextToSpeech tts;
    /**
     * 广播接收器，专门用来监听有关
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    /*如果没有配对的设备，则把它的信息存起来*/
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        /*还没匹配的设备的名字一般都为null,为了避免nullPointException先判断一下*/
                        if (device.getName() != null) {
                            /*如果是我们的设备才把它显示出来*/
                            if (device.getName().equals(BLUETOOTHDEVICENAME)) {
                                getBone_and_Start(device);
                            } else {
                                /*如果不是我们的设备，则不把它显示，跳过*/
                                Toast.makeText(context, "" + device.getName() + device.getAddress(), Toast.LENGTH_LONG).show();
                            }
                        } else {

                        }
                    } else {
                        /*如果已经配对过的设备则跳过*/
                        /*如果扫描获取的已配对的设备的名字是我们的设备，则直接进入游戏*/
                        if (device.getName() == BLUETOOTHDEVICENAME) {
                            startGame(device);
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    setProgressBarIndeterminateVisibility(false);
                    setTitle("扫描结束，请连接蓝牙设备");
                    if (bluetoothDevices.size() == 0) {
                        bluetoothAdapter.startDiscovery();
                    } else {

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

    /**
     * 给已注册的广播监听在这里取消注册
     * 关闭语音朗读引擎，释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        tts.shutdown();
    }

    /**
     * @param savedInstanceState 是该活动上次保存的所有状态信息 ，Bundle类
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_device_list);


        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        GameMod = sharedPreferences.getString(LaunchActivity.SHAREDPREFERENCESKEY, null);


        tts = new TextToSpeech(BluetoothManageActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                /*如果加载引擎成功*/
                if (status == TextToSpeech.SUCCESS) {
                    /*设置使用中文朗读*/
                    int result = tts.setLanguage(Locale.CHINA);
                    /*如果不支持所设置的语言*/
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE) {
                        Toast.makeText(BluetoothManageActivity.this, "TTS暂时不支持这种语言朗读！", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        /*为扫描附近的设备请求位置权限，targetSdkVersion超过28,必须这么做*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION, permission.BLUETOOTH_PRIVILEGED}, PERMISSION_REQUEST_COARSE_LOCATION);
        }


        listViewSearchedDevices = findViewById(R.id.list_search);

        /*新建一个字符串适配器 ，指定单个Item的layout.XML，并指定该Layout里的给这字符串提供的 组建，指定字符串源（Array List<String>)*/
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevices);
        /*给ListView组建设给一个字符串适配器*/
        listViewSearchedDevices.setAdapter(arrayAdapter);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*判断BluetoothAdapter对象是否为空，如果空，则表明本机没有蓝牙传感器*/
        if (bluetoothAdapter != null) {
//            Toast.makeText(this,"本机拥有蓝牙传感器",Toast.LENGTH_LONG).show();
            /*调用isEnable()方法判断蓝牙打开状态还是关闭状态*/
            if (bluetoothAdapter.isEnabled()) {

            } else {
                /*如果蓝牙关闭状态，则创建一个Intent对象，该对象启动一个Activity,提示用户打开蓝牙，即启动蓝牙适配器*/
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "本机没有蓝牙传感器", Toast.LENGTH_LONG).show();
            finish();
        }







        /*获取已配对的设备*/
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(BLUETOOTHDEVICENAME)) {
                    getBone_and_Start(device);
                } else {
                    /*如果要连接的设备不是我们想要的设备，则跳过*/
//                    Toast.makeText(this,""+device.getName()+device.getAddress(),Toast.LENGTH_LONG).show();
                }
            }
            if (bluetoothDevices.size() == 0) {
                scanRemoteDevices();
            }
        } else {
            scanRemoteDevices();
        }


        btnShowBondedDevices = findViewById(R.id.btnShowBondeDdevice);
        btnShowBondedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarIndeterminateVisibility(true);
                setTitle("正在努力争取已配对设备.....");
                if (bluetoothAdapter.isDiscovering()) {
                    /*如果是正在扫描，则取消扫描*/
                    bluetoothAdapter.cancelDiscovery();
                }
                bluetoothDevices.clear();
                bondDevices = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : bondDevices) {
                    if (device.getName().equals(BLUETOOTHDEVICENAME)) {
                        getBone_and_Start(device);
                    }
                }
//                bluetoothAdapter.startDiscovery();/*开始扫描*/
            }
        });


        btnScanUnbondedDevices = findViewById(R.id.btnScanUnBondDevice);
        btnScanUnbondedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanRemoteDevices();
            }
        });


        listViewSearchedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s = arrayAdapter.getItem(position);
                assert s != null;
                String[] ss = s.split("\n");
                String address = ss[1];
                device = bluetoothAdapter.getRemoteDevice(address);
                getBone_and_Start(device);
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        /*动态注册蓝牙广播监听器，当一个设备被发现时被调用onReceive*/
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        /*当搜索结束时调用onReceive*/
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        /*注册监听配对的监听器*/
//        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        this.registerReceiver(receiver, intentFilter);


    }

    private void getBone_and_Start(BluetoothDevice device) {
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            tts.speak("快要进入配对模式,当配对窗口弹出来时输入1234，并确认。", TextToSpeech.QUEUE_FLUSH, null, "111");
        } else {
            startGame(device);
        }
    }


    private void startGame(BluetoothDevice device) {
        String address = device.getAddress();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.apply();
        Intent intent;
        switch (GameMod) {
            case LaunchActivity.GAME1:
                intent = new Intent(BluetoothManageActivity.this, Plant_recognition_activity.class);
                startActivity(intent);
                break;
            case LaunchActivity.GAME2:
                break;
        }
        finish();
    }


    private void scanRemoteDevices() {
        setProgressBarIndeterminateVisibility(true);
        setTitle("正在扫描附近的蓝牙设备......");
        /*开始扫描*/
        bluetoothAdapter.startDiscovery();
    }


    /**
     * 用来指定该活动右上角上的菜单的 menu.XML 文件
     * @param menu  系统给出的 menu 占位符
     * @return 布尔值返回给系统看
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


    /**
     * 处理右上角的小菜单的点击事件时被调用；
     *
     * @param item 是菜单上的元素
     * @return 布尔值返回给系统看
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


}
