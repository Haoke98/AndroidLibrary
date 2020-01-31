package com.sadam.sadamlibarary.BluetoothUtils.Reader;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class Reader extends Thread {
    public static final int PER_VARIABLE_MAX = 254;
    protected static final int PER_VARIABLE_MIN = 1;
    public static final float INTERVAL_SIZE = PER_VARIABLE_MAX - PER_VARIABLE_MIN;
    private static final String TAG = "Readder";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OnReadListener onReadListener;
    private InputStream inputStream;
    private String address;
    private boolean RUN_STATE = true;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    public Reader(String addressj, OnReadListener onReadListener) {
        this.address = address;
    }

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "n0" + hex;
        }
        return hex;
    }


    private void bluetoothConnect() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
//            Toast.makeText(MyApplication.getContext(), "stream成功", Toast.LENGTH_LONG).show();
            Log.d(TAG, "getInputStream successfull.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (inputStream != null && bluetoothSocket.isConnected()) {
//            Toast.makeText(MyApplication.getContext(), "ok", Toast.LENGTH_LONG).show();
            Log.d(TAG, "ok");
        } else {
//            Toast.makeText(MyApplication.getContext(), "not", Toast.LENGTH_LONG).show();
            Log.d(TAG, "no");
        }
    }

    /**
     * 这是专门用来接受Serial.print()传送的数据的
     * 因为print按照ASSICII来传送的东西
     * 0~7对应的都不是printable字符，但是Arduino的Serial.write()不能传入0，但是256相当于0，
     * 所以0可以当作信息头，每次Serial.print()之前可以Serial.write(256)一次
     * 1可以当作一次消息中各个元素之间的分割符
     * 0	0x00			32	0x20	[空格]
     * 1	0x01			33	0x21	!
     * 2	0x02			34	0x22	"
     * 3	0x03			35	0x23	#
     * 4	0x04			36	0x24	$
     * 5	0x05			37	0x25	%
     * 6	0x06			38	0x26	&
     * 7	0x07			39	0x27	'
     * 8	0x08	**		40	0x28	(
     * 9	0x09	**		41	0x29	)
     * 10	0x0A	**		42	0x2A	*
     * 11	0x0B			43	0x2B	+
     * 12	0x0C			44	0x2C	,
     * 13	0x0D	**		45	0x2D	-
     * 14	0x0E			46	0x2E	.
     * 15	0x0F			47	0x2F	/
     * 16	0x10			48	0x30	0
     * 17	0x11			49	0x31	1
     * 18	0x12			50	0x32	2
     * 19	0x13			51	0x33	3
     * 20	0x14			52	0x34	4
     * 21	0x15			53	0x35	5
     * 22	0x16			54	0x36	6
     * 23	0x17			55	0x37	7
     * 24	0x18			56	0x38	8
     * 25	0x19			57	0x39	9
     * 26	0x1A			58	0x3A	:
     * 27	0x1B			59	0x3B	;
     * 28	0x1C			60	0x3C	<
     * 29	0x1D			61	0x3D	=
     * 30	0x1E			62	0x3E	>
     * 31	0x1F			63	0x3F	?
     * 十进制	十六进制	字符		十进制	十六进制	字符
     * 64	0x40	@		96	0x60	`
     * 65	0x41	A		97	0x61	a
     * 66	0x42	B		98	0x62	b
     * 67	0x43	C		99	0x63	c
     * 68	0x44	D		100	0x64	d
     * 69	0x45	E		101	0x65	e
     * 70	0x46	F		102	0x66	f
     * 71	0x47	G		103	0x67	g
     * 72	0x48	H		104	0x68	h
     * 73	0x49	I		105	0x69	i
     * 74	0x4A	J		106	0x6A	j
     * 75	0x4B	K		107	0x6B	k
     * 76	0x4C	L		108	0x6C	l
     * 77	0x4D	M		109	0x6D	m
     * 78	0x4E	N		110	0x6E	n
     * 79	0x4F	O		111	0x6F	o
     * 80	0x50	P		112	0x70	p
     * 81	0x51	Q		113	0x71	q
     * 82	0x52	R		114	0x72	r
     * 83	0x53	S		115	0x73	s
     * 84	0x54	T		116	0x74	t
     * 85	0x55	U		117	0x75	u
     * 86	0x56	V		118	0x76	v
     * 87	0x57	W		119	0x77	w
     * 88	0x58	X		120	0x78	x
     * 89	0x59	Y		121	0x79	y
     * 90	0x5A	Z		122	0x7A	z
     * 91	0x5B	[		123	0x7B	{
     * 92	0x5C	\		124	0x7C	|
     * 93	0x5D	]		125	0x7D	}
     * 94	0x5E	^		126	0x7E	~
     * 95	0x5F	_		127	0x7F	
     * 下表列出了字符集中的 128 - 255 (0x80 - 0xFF)。
     * <p>
     * 十进制	十六进制	字符		十进制	十六进制	字符
     * 128	0x80	€		160	0xA0	[空格]
     * 129	0x81			161	0xA1	¡
     * 130	0x82	‚		162	0xA2	¢
     * 131	0x83	ƒ		163	0xA3	£
     * 132	0x84	„		164	0xA4	¤
     * 133	0x85	…		165	0xA5	¥
     * 134	0x86	†		166	0xA6	¦
     * 135	0x87	‡		167	0xA7	§
     * 136	0x88	ˆ		168	0xA8	¨
     * 137	0x89	‰		169	0xA9	©
     * 138	0x8A	Š		170	0xAA	ª
     * 139	0x8B	‹		171	0xAB	«
     * 140	0x8C	Œ		172	0xAC	¬
     * 141	0x8D			173	0xAD
     * 142	0x8E	Ž		174	0xAE	®
     * 143	0x8F			175	0xAF	¯
     * 144	0x90			176	0xB0	°
     * 145	0x91	‘		177	0xB1	±
     * 146	0x92	’		178	0xB2	²
     * 147	0x93	“		179	0xB3	³
     * 148	0x94	”		180	0xB4	´
     * 149	0x95	•		181	0xB5	µ
     * 150	0x96	–		182	0xB6	¶
     * 151	0x97	—		183	0xB7	·
     * 152	0x98	˜		184	0xB8	¸
     * 153	0x99	™		185	0xB9	¹
     * 154	0x9A	š		186	0xBA	º
     * 155	0x9B	›		187	0xBB	»
     * 156	0x9C	œ		188	0xBC	¼
     * 157	0x9D			189	0xBD	½
     * 158	0x9E	ž		190	0xBE	¾
     * 159	0x9F	Ÿ		191	0xBF	¿
     * 十进制	十六进制	字符		十进制	十六进制	字符
     * 192	0xC0	À		224	0xE0	à
     * 193	0xC1	Á		225	0xE1	á
     * 194	0xC2	Â		226	0xE2	â
     * 195	0xC3	Ã		227	0xE3	ã
     * 196	0xC4	Ä		228	0xE4	ä
     * 197	0xC5	Å		229	0xE5	å
     * 198	0xC6	Æ		230	0xE6	æ
     * 199	0xC7	Ç		231	0xE7	ç
     * 200	0xC8	È		232	0xE8	è
     * 201	0xC9	É		233	0xE9	é
     * 202	0xCA	Ê		234	0xEA	ê
     * 203	0xCB	Ë		235	0xEB	ë
     * 204	0xCC	Ì		236	0xEC	ì
     * 205	0xCD	Í		237	0xED	í
     * 206	0xCE	Î		238	0xEE	î
     * 207	0xCF	Ï		239	0xEF	ï
     * 208	0xD0	Ð		240	0xF0	ð
     * 209	0xD1	Ñ		241	0xF1	ñ
     * 210	0xD2	Ò		242	0xF2	ò
     * 211	0xD3	Ó		243	0xF3	ó
     * 212	0xD4	Ô		244	0xF4	ô
     * 213	0xD5	Õ		245	0xF5	õ
     * 214	0xD6	Ö		246	0xF6	ö
     * 215	0xD7	×		247	0xF7	÷
     * 216	0xD8	Ø		248	0xF8	ø
     * 217	0xD9	Ù		249	0xF9	ù
     * 218	0xDA	Ú		250	0xFA	ú
     * 219	0xDB	Û		251	0xFB	û
     * 220	0xDC	Ü		252	0xFC	ü
     * 221	0xDD	Ý		253	0xFD	ý
     * 222	0xDE	Þ		254	0xFE	þ
     * 223	0xDF	ß		255	0xFF	ÿ
     * ** 数值 8、9、10 和 13 可以分别转换为退格符、制表符、换行符和回车符。这些字符都没有图形表示，
     */
    @SuppressLint("LongLogTag")
    public void run() {
        Log.d(TAG, "reader created and now is running.");
        bluetoothConnect();
        super.run();
        int num;
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> strings = new ArrayList<>();
        /*先扫过几遍，找出正确的消息头部，再取接收消息*/
        while (RUN_STATE) {
            if (inputStream != null) {

                try {
                    num = inputStream.read();
                    if (num == 0) {
                        stringBuilder.delete(0, stringBuilder.length());
                        Log.e(TAG, Arrays.toString(strings.toArray()));
                        String[] s = new String[strings.size()];
                        strings.toArray(s);
                        onReadListener.handle(s);
                        strings.clear();
                        continue;
                    } else if (num == 1) {
                        strings.add(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                    } else {
                        char c = (char) num;
                        stringBuilder.append(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 这是专门用来接受以Serial.write()传送的信息。
     */
//    @Override
//    public void run() {
//        Log.d(TAG,"reader created and now is running.");
//        bluetoothConnect();
//        super.run();
//        int[] fiveInt = new int[5];
//        int num;
//        /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//        while (RUN_STATE) {
//            try {
//                num = inputStream.read();
//                Log.e(TAG, num + "");
//                if (num == 255) {
//                    for (int i = 0; i < fiveInt.length; i++) {
//                        num = inputStream.read();
//                        fiveInt[i] = num;
//                    }
//                    Log.d(TAG, Arrays.toString(fiveInt));
//                    this.doing(fiveInt[0],fiveInt[1],fiveInt[2],fiveInt[3],fiveInt[4]);
//                } else {
//                    continue;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    @Override
//    public void run() {
//        super.run();
//        byte[] fiveByte = new byte[5];
//        while (RUN_STATE) {
//            byte[] oneByte = new byte[1];
//            for (int i = 0; i < fiveByte.length; i++) {
//                try {
//                    int num = inputStream.read(oneByte, 0, oneByte.length);
//                    if (num != oneByte.length) {
//                        Log.d(TAG, "continue");
//                        continue;
//                    } else {
//                        fiveByte[i] = oneByte[0];
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
////        String res = "num:"+num+" "+Arrays.toString(fiveByte);
//            String res = Arrays.toString(fiveByte);
//            Log.d(TAG, res);
////            try {
////                this.doing(fiveByte[0], fiveByte[1], fiveByte[2], fiveByte[3], fiveByte[4]);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//        }
//    }
//@Override
//public void run() {
//        super.run();
//        byte[] fiveByte = new byte[6];
//        byte[] oneByteforHead = new byte[1];
//        byte[] oneByte = new byte[1];
//        int num;
//        while (RUN_STATE) {
//            try {
//                num = inputStream.read(oneByteforHead,0,oneByteforHead.length);
//                /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//                if(oneByteforHead[0]== -128){
//                    Log.d(TAG,"this is the head of the message:"+Arrays.toString(oneByteforHead));
//                    /*如果是消息头部就开始挨个提取五个字节*/
//                    for (int i = 0; i < fiveByte.length; i++) {
//                        num = inputStream.read(oneByte,0,oneByte.length);
//                        if (num != oneByte.length) {
//                                Log.d(TAG, "continue");
//                                /*如果没法正确的读取一个字节的数据，则再做一次（虽然这一行永远不可能执行，但还是以防万一）*/
//                                i--;
//                                continue;
//                        } else {
//                            /*如果成功地读取一个字节，则把这个一个字节放到那个五个字节内对应的位置*/
//                            fiveByte[i] = oneByte[0];
//                        }
//                    }
//                    String res = Arrays.toString(fiveByte);
//                    Log.d(TAG, res);
//                    this.doing(fiveByte[0], fiveByte[1], fiveByte[2], fiveByte[3], fiveByte[4]);
//                }else{
//                    /*如果不是消息头部，则跳过*/
//                    continue;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    @Override
//    public void run() {
//        super.run();
//        byte[] fiveByte = new byte[6];
////        byte[] oneByteforHead = new byte[1];
//        byte[] oneByte = new byte[1];
//        int num;
//        while (RUN_STATE) {
//            try {
//                num = inputStream.read(oneByte,0,oneByte.length);
//                /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//                if(oneByte[0]== -128){
//                    Log.d(TAG,"this is the head of the message:"+Arrays.toString(oneByte));
//                    /*如果是消息头部就开始挨个提取五个字节*/
//                    for (int i = 0; i < fiveByte.length; i++) {
//                        num = inputStream.read(oneByte,0,oneByte.length);
//                        if (num != oneByte.length) {
//                            Log.d(TAG, "continue");
//                            /*如果没法正确的读取一个字节的数据，则再做一次（虽然这一行永远不可能执行，但还是以防万一）*/
//                            i--;
//                            continue;
//                        } else {
//                            /*如果成功地读取一个字节，则把这个一个字节放到那个五个字节内对应的位置*/
//                            fiveByte[i] = oneByte[0];
//                        }
//                    }
//                    String res = Arrays.toString(fiveByte);
//                    Log.d(TAG, res);
//                    this.doing(fiveByte[0], fiveByte[1], fiveByte[2], fiveByte[3], fiveByte[4]);
//                }else{
//                    /*如果不是消息头部，则跳过*/
//                    Log.d(TAG,"this is not head of Mess:"+Arrays.toString(oneByte));
//                    continue;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    @Override
//    public void run() {
//        super.run();
//        byte[] fiveByte = new byte[5];
////        byte[] oneByteforHead = new byte[1];
//        byte[] oneByte = new byte[1];
//        int num;
//        /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//        while(true){
//            try {
//                num = inputStream.read(oneByte);
//                if(oneByte[0]== -128){
//                    Log.d(TAG,"this is the head of the message:"+Arrays.toString(oneByte));
//                    /*如果是消息头部就开始挨个提取五个字节*/
//                    break;
//                }else{
//                    /*如果不是消息头部，则跳过*/
//                    Log.d(TAG,"this is not head of Mess:"+Arrays.toString(oneByte));
//                    continue;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        while (RUN_STATE) {
//            try {
////                if(oneByte[0]== -128){
////                    Log.d(TAG,"this is the head of the message:"+Arrays.toString(oneByte));
////                    /*如果是消息头部就开始挨个提取五个字节*/
//                for (int i = 0; i < fiveByte.length; i++) {
//                        num = inputStream.read(oneByte,0,oneByte.length);
//                        if (num != oneByte.length) {
//                            Log.d(TAG, "continue");
//                            /*如果没法正确的读取一个字节的数据，则再做一次（虽然这一行永远不可能执行，但还是以防万一）*/
//                            i--;
//                            continue;
//                        } else {
//                            /*如果成功地读取一个字节，则把这个一个字节放到那个五个字节内对应的位置*/
//                            fiveByte[i] = oneByte[0];
//                        }
//                    }
//                String res = Arrays.toString(fiveByte);
//                Log.d(TAG, res);
//                this.doing(fiveByte[0], fiveByte[1], fiveByte[2], fiveByte[3], fiveByte[4]);
////                }else{
//
////                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


//    @Override
//    public void run() {
//        super.run();
//        int[] fiveByte = new int[5];
//        int num;
//        /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//        while(true){
//            try {
//                num = inputStream.read();
//                inputStream.skip(1);
//                if(num == 255){
//                    for(int i=0;i<fiveByte.length;i++){
//                        num = inputStream.read();
//                        fiveByte[i] = num;
//                    }
//                    this.doing(fiveByte[0],fiveByte[1],fiveByte[2],fiveByte[3],fiveByte[4]);
//                    Log.d(TAG,Arrays.toString(fiveByte));
//                }else{
//                    Log.d(TAG,"this is not a head mess:"+num);
//                    continue;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//@Override
//public void run() {
//    super.run();
//    int[] fiveInt = new int[5];
//    int num;
//    /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//    while (RUN_STATE) {
//        try {
//            num = inputStream.read();
////            inputStream.skip(1);
//            Log.d(TAG,num+"");
//            if (num == 255) {
//                for (int i = 0; i < fiveInt.length; i++) {
//                    num = inputStream.read();
//                    fiveInt[i] = num;
//                }
//                Log.d(TAG, Arrays.toString(fiveInt));
//                this.doing(fiveInt[0],fiveInt[1],fiveInt[2],fiveInt[3],fiveInt[4]);
//            } else {
//                continue;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

//    @Override
//    public void run() {
//        super.run();
//        int[] fiveInt = new int[5];
//        int num;
//        /*先扫过几遍，找出正确的消息头部，再取接收消息*/
//        while (RUN_STATE) {
//            try {
//                num = inputStream.read();
////            inputStream.skip(1);
//                Log.d(TAG,num+"");
////                if (num == 255) {
////                    for (int i = 0; i < fiveInt.length; i++) {
////                        num = inputStream.read();
////                        fiveInt[i] = num;
////                    }
////                    Log.d(TAG, Arrays.toString(fiveInt));
////                    this.doing(fiveInt[0],fiveInt[1],fiveInt[2],fiveInt[3],fiveInt[4]);
////                } else {
////                    continue;
////                }
//            } catch (IOException e) {
//                Log.d(TAG,e.toString());
//            }
//        }
//    }
    public void desTroy() {
        this.RUN_STATE = false;
        try {
            this.inputStream.close();
            this.bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void run() {
//        super.run();
//        int num;
//        int n;
//        String smsg = "";
//        while (true) {
//            try {
//                num = this.inputStream.read(buffer);
//                Log.d("reader,readBuffer.num=", "" + num + "byte[]:" + Arrays.toString(buffer));
//                n = n0;
//                String s = byteToHexString(buffer);/*因为单片机发出的是16进制的数据，直接读取会处乱吗，得进行转换*/
//                smsg +=s.trim();
//                if(inputStream.available()==n0){
//                    break;
//                }
//                Message msg = new Message();
//                msg.obj = smsg;
//                this.handler.sendMessage(msg);
//            } catch (IOException e) {

//                e.printStackTrace();

//            }
//        }
//    }
//    @Override
//    public void run() {
//        super.run();
//        while(true){
//            try {
//                String s = dataInputStream.readUTF();
//                Log.d("datainputStream:",s);
//                Message msg = new Message();
//                msg.obj = s;
//                this.handler.sendMessage(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//        }

//    }
    //    @Override
//    public void run() {
//        super.run();
//        byte[] fiveByte = new byte[5];
//        while(true){
//            try {
//                int num = inputStream.read(fiveByte,0,5);
//                if(num!=5){
//                    continue;
//                }
//                String res = "num:"+num+" "+Arrays.toString(fiveByte);
//                Log.d("data",res);
//                this.doing(fiveByte[0],fiveByte[1],fiveByte[2],fiveByte[3],fiveByte[4]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    }
//    @Override
//    public void run() {
//        byte[] oneByte = new byte[n1];
//        super.run();
//        while(true){
//            try {
//                int num = inputStream.read(oneByte,n0,oneByte.length);
//                String data = new String(oneByte,n0,num,"utf-n8");
//                char c = data.charAt(n0);
//                int integer = c;
//                String res = data+"  c:"+c+"   integer:"+integer;
//                Log.d("data",res);
//                Message msg = new Message();
//                msg.obj = res;
//                this.handler.sendMessage(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    //    @Override
//    public void run() {
//        super.run();
//        System.out.println("readerThread has been started.");
//        int num = n0;
//        byte[] oneByte = new byte[n1];
//        while(true){
//            try {
//                num = this.inputStream.read(buffer);
//                String s = new String(oneByte);
//                char c = s.charAt(n0);
//                int integer = c;
//                Log.d("num:",num+"   s:"+s+"  s.legth:"+s.length()+"  c:"+c+"  intger:"+integer);
//                String mess = String.valueOf(integer);
//                Message msg = new Message();
//                msg.obj = mess;
//                this.handler.sendMessage(msg);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//        @Override
//    public void run() {
//        System.out.println("readerThread has been started.");
//        super.run();
//        byte[] oneByte = new byte[n1];
//        int size ;
//            while(true){
//                try {
//                    size = inputStream.read(oneByte, n0, oneByte.length);
//                    if (size != n1) {
//                        return;
//                    }
//                    Log.d("oneByte:", Arrays.toString(oneByte));
//                    String s = byteToHex(oneByte[n0]);
//                    Message msg = new Message();
//                    msg.obj = s;
//                    this.handler.sendMessage(msg);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    @Override
//    public void run() {
//        super.run();
//        int size = n0;
//        byte[] oneByte = new byte[n3];
//        while (true){
//            try {
//                size = inputStream.read(oneByte, n0, oneByte.length);
//                if (size != oneByte.length) {
//                    return;
//                }
//                String ss = "";
//                for (int i = n0; i < oneByte.length; i++) {
//                    String s = byteToHex(oneByte[i]);
//                    Log.d("Reaer:ssss",s);
//                    ss += s;
//                }
//                Message msg = new Message();
//                msg.obj = ss;
//                this.handler.sendMessage(msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
