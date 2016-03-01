package apexbio.smbg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import apexbio.smbgble.BleUtil;
import apexbio.smbgble.BleUuid;
import apexbio.smbgble.DeviceAdapter;
import apexbio.smbgble.ScannedDevice;
import apexbio.smbgsql.GData;
import apexbio.smbgsql.GdataDAO;

public class BLEActivity extends DashBoardActivity implements BluetoothAdapter.LeScanCallback {
    private static final String TAG = "BLEDevice";
    private static final int REQUEST_ENABLE_BT = 1;
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private boolean mIsScanning;
    private int mStatus;
    private int gmCount;
    private boolean enabled = true;
    private DeviceAdapter mDeviceAdapter;
    private ListView deviceListView;
    private GdataDAO gdataDAO;
    final Context context = this;
    private AlertDialog dialog;
    private ScannedDevice item;
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue
            = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> characteristicReadQueue
            = new LinkedList<BluetoothGattCharacteristic>();
    private Button btnExport, btnBLEScan;
    private long uid = 0;
    public ProgressDialog PDialog = null;
    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, false, true);

        gdataDAO = new GdataDAO(getApplicationContext());
        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        uid = globalvariable.user.getUid();

        btnBLEScan = (Button)findViewById(R.id.btnBLEScan);
        btnBLEScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(mConnGatt != null){
                        PDialog = ProgressDialog.show(BLEActivity.this, "Processing", "Please wait..", true);
                        writeRACPchar();
                    }else{
                        startScan();
                        dialog_scaneddevice();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        init();
    }


    //*** 寫入 RACP CHAR ***//
    private void writeRACPchar() {
        //***PUSH 0x01 TO RECORD ACCESS CONTROL POINT
        BluetoothGattCharacteristic writeRACPchar =
                mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                        .getCharacteristic(UUID.fromString(BleUuid.CHAR_RECORD_ACCESS_CONTROL_POINT_STRING));
        byte[] data = new byte[2];
        data[0] = (byte)0x01;
        data[1] = (byte)0x01;
        writeRACPchar.setValue(data);
        mConnGatt.writeCharacteristic(writeRACPchar);
    }

    private void init() {
        btnExport = (Button)findViewById(R.id.btnExport);
        btnExport.setBackgroundResource(R.drawable.m_ic_menu_ble_onpress);
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this,"ble not support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();

            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, "ble unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // init listview & scaned dialog
        View viewScaned=View.inflate(this,R.layout.scaned_list, deviceListView);
        deviceListView = (ListView) viewScaned.findViewById(R.id.list);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device,
                new ArrayList<ScannedDevice>());
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    connect2Gatt();
                    dialog.cancel();
                    // stop before change Activity
                    stopScan();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(viewScaned).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                stopScan();
                dialog.cancel();
            }
        });
        dialog = builder.create();
        stopScan();
    }

    public void dialog_scaneddevice(){
        dialog.show();
    }

    @Override
    public void onLeScan(final BluetoothDevice newDeivce, final int newRssi,
                         final byte[] newScanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceAdapter.update(newDeivce, newRssi, newScanRecord);
            }
        });
    }

    private void startScan() {
        if ((mBTAdapter != null) && (!mIsScanning)) {
            mBTAdapter.startLeScan(this);
            mIsScanning = true;
            setProgressBarIndeterminateVisibility(true);
            invalidateOptionsMenu();
        }
    }

    private void stopScan() {
        if (mBTAdapter != null) {
            mBTAdapter.stopLeScan(this);
        }
        mIsScanning = false;
        setProgressBarIndeterminateVisibility(false);
        invalidateOptionsMenu();
    }

    private void connect2Gatt(){
        // check BluetoothDevice
        if (mDevice == null) {
            mDevice = item.getDevice();
            if (mDevice == null) {
                finish();
                return;
            }
        }
        // connect to Gatt
        if ((mConnGatt == null)	&& (mStatus == BluetoothProfile.STATE_DISCONNECTED))
        {
            // try to connect
            mConnGatt = mDevice.connectGatt(this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                Log.e(TAG, "state error");
                finish();
                return;
            }
        }
    }

    //connect to gatt
    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                runOnUiThread(new Runnable() {
                    public void run() {
                        //未連接時的操作
                        close();
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }

                //*** 設置 Glucose measurement, RACP 等 Notification ***//
                if (BleUuid.SERVICE_GLUCOSE.equalsIgnoreCase(service
                        .getUuid().toString())) {

                    BluetoothGattCharacteristic charGM =
                            mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                                    .getCharacteristic(UUID.fromString(BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING));
                    mConnGatt.setCharacteristicNotification(charGM, enabled);
                    BluetoothGattDescriptor descGM = charGM.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                    descGM.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    writeGattDescriptor(descGM);

                    BluetoothGattCharacteristic charRACP =
                            mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE))
                                    .getCharacteristic(UUID.fromString(BleUuid.CHAR_RECORD_ACCESS_CONTROL_POINT_STRING));
                    mConnGatt.setCharacteristicNotification(charRACP, enabled);
                    BluetoothGattDescriptor descRACP = charRACP.getDescriptor(UUID.fromString(BleUuid.CHAR_CLIENT_CHARACTERISTIC_CONFIG_STRING));
                    descRACP.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    writeGattDescriptor(descRACP);
                }
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    btnBLEScan.setText("Upload All Data");
                }
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            characteristicReadQueue.remove();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
            else{
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }
            if(characteristicReadQueue.size() > 0)
                mConnGatt.readCharacteristic(characteristicReadQueue.element());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
            }
            else{
                Log.d(TAG, "Callback: Error writing GATT Descriptor: "+ status);
            }
            descriptorWriteQueue.remove();  //pop the item that we just finishing writing
            //if there is more to write, do it!
            if(descriptorWriteQueue.size() > 0)
                mConnGatt.writeDescriptor(descriptorWriteQueue.element());
            else if(characteristicReadQueue.size() > 0)
                mConnGatt.readCharacteristic(characteristicReadQueue.element());
        }
    };

    public void readCharacteristic(String characteristicName) {
        if (mBTAdapter == null || mConnGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattService s = mConnGatt.getService(UUID.fromString(BleUuid.SERVICE_GLUCOSE));
        BluetoothGattCharacteristic c = s.getCharacteristic(UUID.fromString(characteristicName));
        //put the characteristic into the read queue
        characteristicReadQueue.add(c);
        //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
        //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
        if((characteristicReadQueue.size() == 1) && (descriptorWriteQueue.size() == 0))
            mConnGatt.readCharacteristic(c);
    }

    private void writeGattDescriptor(BluetoothGattDescriptor d){
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if(descriptorWriteQueue.size() == 1) mConnGatt.writeDescriptor(d);
    }

    //*** 接收資料後處理 ***//
    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        //final Intent intent = new Intent(action);
        if(BleUuid.CHAR_GLUCOSE_MEASUREMENT_STRING
                .equalsIgnoreCase(characteristic.getUuid().toString())){
            final byte[] dataGM = characteristic.getValue();
            final String strGM = DecadeGMdata(dataGM);
            //displayResult(strGM);
        } else if(BleUuid.CHAR_RECORD_ACCESS_CONTROL_POINT_STRING
                .equalsIgnoreCase(characteristic.getUuid().toString())){
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));

                PDialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BLEActivity.this);
                        builder.setMessage("共有 " + gmCount + " 筆資料上傳")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(BLEActivity.this, StatisicsActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                //displayResult(stringBuilder.toString());
            }
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                //displayResult(stringBuilder.toString());
            }
        }
    }


    // 解析 Glucose Data
    String DecadeGMdata(byte[] dataByte){
        gmCount++;
        String result = null;
        // For all other profiles, writes the data formatted in HEX.
        if (dataByte != null && dataByte.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(dataByte.length);
            for(byte byteChar : dataByte)
                stringBuilder.append(String.format("%02X ", byteChar));
            String strGM = stringBuilder.toString();
            String[] split = strGM.split(" ");

            String strYear = String.valueOf(Integer.parseInt(split[4],16)*256 + Integer.parseInt(split[3],16));
            String strMonth = String.valueOf(Integer.parseInt(split[5],16));
            if(Integer.parseInt(split[5],16) < 10){
                strMonth = "0" + String.valueOf(Integer.parseInt(split[5],16));
            }

            String strDay = String.valueOf(Integer.parseInt(split[6],16));
            if(Integer.parseInt(split[6],16) < 10){
                strDay = "0" + String.valueOf(Integer.parseInt(split[6],16));
            }

            String strHour = String.valueOf(Integer.parseInt(split[7],16));
            String strMin = String.valueOf(Integer.parseInt(split[8],16));
            String strTime = strHour + ":" + strMin;
            int intValue = Integer.parseInt(split[12], 16);
            result = strYear + "/" + strMonth + "/" + strDay + " " + strTime + " " + intValue;
            String strflag = "None";
            int did = gdataDAO.getCount() + 1;
            gdataDAO.insert(new GData(did, uid, "G3"
                    , strYear + "-" + strMonth + "-" + strDay
                    , strTime ,strflag, intValue, "Update via Cable"));
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    public void close() {
        if (mConnGatt == null) {
            return;
        }
        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mConnGatt.close();
        mConnGatt = null;
        mDevice = null;
    }

    // 連按兩次返回鍵後退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("TabHost_Index.java onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isExit == false ) {
                isExit = true;
                Toast.makeText(this, R.string.CLICK_BACK_AGAIN_TO_EXIT
                        , Toast.LENGTH_SHORT).show();
                TimerTask task = null;
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                };
                tExit.schedule(task, 2000);
            } else {
                finish();
                System.exit(0);
            }
        }
        return false;
    }
}
