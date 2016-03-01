package apexbio.smbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import apexbio.smbgsql.GData;
import apexbio.smbgsql.GdataDAO;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class CableActivity extends DashBoardActivity {

    public ProgressDialog PDialog = null;

    private GdataDAO gdataDAO;
    private Button btnExport;
    private long uid = 0;
    ArrayList<String> listCableData=new ArrayList<String>();
    ArrayAdapter<String> CableDataadapter;

    private static final boolean SHOW_DEBUG = true;

    PL2303Driver mSerial;

    //    private ScrollView mSvText;
    //   private StringBuilder mText = new StringBuilder();

    String TAG = "PL2303HXD_APLog";

    //private TextView etRead;
    //BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
    private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B19200;
    private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
    private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
    private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
    private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;

    private static final String ACTION_USB_PERMISSION = "comapexbiord.apexbioapp.USB_PERMISSION";

    // Linefeed
    public int PL2303HXD_BaudRate;
    public String PL2303HXD_BaudRate_str="B4800";

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cable);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, true, true);
        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        uid = globalvariable.user.getUid();
        gdataDAO = new GdataDAO(getApplicationContext());

        CableDataadapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listCableData);

        btnExport = (Button)findViewById(R.id.btnExport);
        btnExport.setBackgroundResource(R.drawable.m_ic_menu_cable_onpress);

        Button mButton01 = (Button)findViewById(R.id.btnTest);

        mButton01.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                openUsbSerial();
            }
        });
        mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, ACTION_USB_PERMISSION);
    }

    protected void onStop() {
        Log.d(TAG, "Enter onStop");
        super.onStop();
        Log.d(TAG, "Leave onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Enter onDestroy");

        if(mSerial!=null) {
            mSerial.end();
            mSerial = null;
        }

        super.onDestroy();
        Log.d(TAG, "Leave onDestroy");
    }

    public void onStart() {
        Log.d(TAG, "Enter onStart");
        super.onStart();
        Log.d(TAG, "Leave onStart");
    }

    public void onResume() {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action =  getIntent().getAction();
        Log.d(TAG, "onResume:"+action);

        //if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        if(!mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "New instance : " + mSerial);
            }

            if( !mSerial.enumerate() ) {
                Toast.makeText(this, getString(R.string.CableActivity_NO_MORE_DEVICES_FOUND), Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d(TAG, "onResume:enumerate succeeded!");
            }
        }//if isConnected

        Log.d(TAG, "Leave onResume");
    }

    // 打開 usb port
    private void openUsbSerial() {
        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported()) {
            Toast.makeText(this, getString(R.string.CableActivity_NO_SUPPORT_USB_HOST_API), Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "No Support USB host API");
            mSerial = null;
        }
        Log.d(TAG, "Leave onCreate");

        Log.d(TAG, "Enter openUsbSerial");
        if(null==mSerial)
            return;
        int res = 0;
        try {
            res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if( res<0 ) {
            Log.d(TAG, "fail to setup");
            Toast.makeText(this, "fail to setup : "  + res, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "openUsbSerial : isConnected ");
            }
            // if (!mSerial.InitByBaudRate(mBaudrate)) {
            if (!mSerial.InitByBaudRate(mBaudrate, 700)) {
                if(!mSerial.PL2303Device_IsHasPermission()) {
                    Toast.makeText(this, getString(R.string.CableActivity_CANNONT_OPEN), Toast.LENGTH_SHORT).show();
                }
                if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
                    Toast.makeText(this, getString(R.string.CableActivity_CHIP_HAS_NO_SUPPORT), Toast.LENGTH_SHORT).show();
                }
            } else {
                writeDataToSerial();
            }
        }//isConnected
        Log.d(TAG, "Leave openUsbSerial");
    }//openUsbSerial

    // 將資料寫入 Usb port
    private void writeDataToSerial() {
        Log.d(TAG, "Enter writeDataToSerial");
        if(null==mSerial) {
            return;
        }
        if(!mSerial.isConnected()) {
            return;
        }
        byte[] byteArray={(byte)170,(byte)16,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)16 };
        int res = mSerial.write(byteArray, 11);
        if (SHOW_DEBUG) {
            Log.d(TAG, "PL2303Driver Write 2(" + byteArray.length + ") : " + byteArray);
        }
        if( res<0 ) {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
            return;
        }
        Toast.makeText(this, "Write length: "+byteArray.length+" bytes", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Leave writeDataToSerial");

        PDialog = ProgressDialog.show(CableActivity.this, "Processing", "Please wait..", true);
        new Thread(){
            public void run(){
                try{
                    readDataFromSerial();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    PDialog.dismiss();
                }
            }
        }.start();
    }//writeDataToSerial

    // 從 usb port 讀取資料
    private void readDataFromSerial() {
        int len;
        // byte[] rbuf = new byte[4096];
        byte[] rbuf = new byte[300 * 11];
        StringBuffer sbHex = new StringBuffer();
        Log.d(TAG, "Enter readDataFromSerial");
        if(null==mSerial)
            return;
        if(!mSerial.isConnected())
            return;
        len = mSerial.read(rbuf);
        if(len<0) {
            Log.d(TAG, "Fail to bulkTransfer(read data)");
            return;
        }
        if (len > 0) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "read len : " + len);
            }
            for (int j = 0; j < len; j++) {
                String temp=Integer.toHexString(rbuf[j]&0x000000FF);
                Log.i(TAG, "str_rbuf["+j+"]="+temp);
                int decimal = Integer.parseInt(temp, 16);
                Log.i(TAG, "dec["+j+"]="+decimal);
                if(temp.length() < 2){
                    sbHex.append("0" + temp);
                }else{
                    sbHex.append(temp);
                }
            }
            final String[] result = decade(sbHex.toString());

            runOnUiThread(new Runnable() {
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CableActivity.this);
                    builder.setMessage("共上傳" + result.length + "筆資料，將跳轉到統計頁面")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(CableActivity.this, StatisicsActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }
        else {
            if (SHOW_DEBUG) {
                Log.d(TAG, "read len : 0 ");
            }
            return;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial

    //
    String[] decade(String data){
        String[] result = new String[data.length()/22];

        for(int i = 0; i < data.length()/22; i++){
            result[i] = data.substring(i*22, i*22 + 22);
        }

        for(int i = 0; i < result.length ; i++){
            result[i] = returnStrResult(result[i]);
        }
        return result;
    }

    // 解析血糖資料
    String returnStrResult(String s){
        String result = "";
        if(s != null){
            byte[] bytetemp = new byte[s.length()/2];
            for(int i=0;i < s.length();i+=2) {
                bytetemp[i/2] = (Integer.decode("0x"+s.charAt(i)+s.charAt(i+1))).byteValue();
            }

            int MYear = (bytetemp[2] & 0x3F) + 2000; //Year
            int MMonthr = (bytetemp[3] & 0xF0) >> 4; //Month
            int intflag = (bytetemp[3] & 0x06)/2; //Month
            String strflag = "";
            if (intflag == 1)
            {
                strflag = "After Meal";
            }
            else if(intflag == 2)
            {
                strflag = "None";
            }
            else if (intflag == 3)
            {
                strflag = "Before Meal";
            }
            int MDay = (bytetemp[4] & 0x1F);//day
            int MH = (((bytetemp[5] & 0x03) * 256) >> 5) + ((int)(bytetemp[4] & 0xE0) >> 5); //Hour;
            int MM = (bytetemp[5] & 0xFC) >> 2; //Minute
            int MBGC = (((int)bytetemp[6] & 0xFF) + ((int)bytetemp[7] * 256)); //BGC
            int MUnit = (bytetemp[2] & 0x80) >> 7; //Unit

            int intControl = (bytetemp[3] & 0x01);
            int intControlErrorFlag =  (bytetemp[3] & 0x08) >> 3;
            String rDate = MYear + "/" + String.format("%02d", MMonthr) + "/" + String.format("%02d", MDay);
            String rtime = MH + ":" + MM;

            result = rDate + " " + rtime + " Value: " + MBGC + " " + ", " + strflag;

            //insert to DB
            int did = gdataDAO.getCount() + 1;
            gdataDAO.insert(new GData(did, uid, "G3"
                    , MYear + "-" + String.format("%02d", MMonthr) + "-" + String.format("%02d", MDay)
                    , rtime ,strflag, MBGC, "Update via Cable"));
        }else{
            result = s;
        }
        return result;
    }

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
