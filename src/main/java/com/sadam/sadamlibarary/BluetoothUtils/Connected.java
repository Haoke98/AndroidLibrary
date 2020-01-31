package com.sadam.sadamlibarary.BluetoothUtils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sadam.sadamlibarary.BluetoothUtils.Reader.Reader;

public abstract class Connected extends AppCompatActivity {
    private final static String TAG = "Connected";
    protected Reader reader;
    private SharedPreferences sharedPreferences;
    private String address;

    protected Connected() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        address = sharedPreferences.getString("address", null);


        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "蓝牙地址为空", Toast.LENGTH_LONG).show();
        }

        this.reader = startGame(address);
        this.reader.start();
    }

    /**
     * 每一个游戏退出时，把游戏有关的线程，活动，socket等都得结束掉
     * 先后循序非常重要，这样才能保证这个数据的完整性
     * 1先正常结束掉线程
     * 2再关inputStream
     * 3关Socket
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            this.reader.desTroy();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public abstract Reader startGame(String address);
}
