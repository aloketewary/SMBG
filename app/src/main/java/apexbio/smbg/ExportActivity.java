package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import apexbio.smbgsql.GData;
import apexbio.smbgsql.GdataDAO;

public class ExportActivity extends DashBoardActivity implements OnClickListener{
    private GdataDAO gdataDAO;
    //UI References
    private EditText MeterNameEtxt;
    private EditText DateEtxt;
    private EditText TimeEtxt;
    private EditText GlucoseValueEtxt;
    private EditText HeightEtxt;
    private EditText WeightEtxt;
    private EditText NoteEtxt;
    private String strflag, strHeight, strWeight;
    private String[] flag = {"飯前", "飯後", "睡前", "夜間"};
    private Spinner FlagSpinner;
    private ArrayAdapter<String> FlagList;
    private Context mContext;
    private DatePickerDialog DatePickerDialog;
    private TimePickerDialog TimePickerDialog;
    private Button btnExport, btnClean, btnSubmit;
    private SimpleDateFormat dateFormatter;
    private long uid = 0;
    public ProgressDialog PDialog = null;

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, true, true);
        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        uid = globalvariable.user.getUid();

        strHeight = Double.toString(globalvariable.user.getHeight());
        strWeight = Double.toString(globalvariable.user.getWeight());

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        btnExport = (Button)findViewById(R.id.btnExport);
        btnExport.setBackgroundResource(R.drawable.m_ic_menu_keyin_onpress);

        findViewsById();
        setDateTimeField();
        init();
    }

    private void init() {
        gdataDAO = new GdataDAO(getApplicationContext());
        // TODO Auto-generated method stub
        FlagList = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, flag);
        FlagSpinner.setAdapter(FlagList);
        FlagSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                strflag = flag[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        btnClean.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                DateEtxt.setText("");
                TimeEtxt.setText("");
                MeterNameEtxt.setText("");
                HeightEtxt.setText("");
                GlucoseValueEtxt.setText("");
                WeightEtxt.setText("");
                NoteEtxt.setText("");
            }
        });
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        HeightEtxt.setText(strHeight);
        WeightEtxt.setText(strWeight);
    }

    // 上傳確認視窗
    private void dialog(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String metername = MeterNameEtxt.getText().toString();
            String date = DateEtxt.getText().toString();
            String time = TimeEtxt.getText().toString();
            int value = Integer.parseInt(GlucoseValueEtxt.getText().toString());
            String note = NoteEtxt.getText().toString();
            String msg = "MeterName : " + metername +
                    "\nDate : " + date +
                    "\nTime : " + time +
                    "\nValue : " + value +
                    "\nNote : " + note;

            builder.setMessage(getString(R.string.ExportActivity_UPLOAD_CONFIRM) + msg )
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO Auto-generated method stub
                            PDialog = ProgressDialog.show(ExportActivity.this, "Processing", "Please wait..", true);

                            new Thread(){
                                public void run(){
                                    try{
                                        int did = gdataDAO.getCount() + 1;
                                        String metername = MeterNameEtxt.getText().toString();
                                        String date = DateEtxt.getText().toString();
                                        String time = TimeEtxt.getText().toString();
                                        int value = Integer.parseInt(GlucoseValueEtxt.getText().toString());
                                        String note = NoteEtxt.getText().toString();

                                        gdataDAO.insert(new GData(did, uid, metername
                                                , date, time, strflag, value, note));
                                        Intent i = new Intent(ExportActivity.this, StatisicsActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    finally{
                                        PDialog.dismiss();
                                    }
                                }
                            }.start();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }catch(Exception ex){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please recheck your data.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void findViewsById() {
        mContext = this.getApplicationContext();

        // TODO Auto-generated method stub
        DateEtxt = (EditText) findViewById(R.id.edKdate);
        DateEtxt.setInputType(InputType.TYPE_NULL);
        DateEtxt.requestFocus();

        TimeEtxt = (EditText) findViewById(R.id.edKtime);
        TimeEtxt.setInputType(InputType.TYPE_NULL);

        MeterNameEtxt = (EditText) findViewById(R.id.edKmetername);
        GlucoseValueEtxt = (EditText) findViewById(R.id.edKvalue);
        HeightEtxt = (EditText) findViewById(R.id.edKheight);
        WeightEtxt = (EditText) findViewById(R.id.edKweight);
        NoteEtxt = (EditText) findViewById(R.id.edKnote);

        FlagSpinner = (Spinner)findViewById(R.id.spKflage);

        btnClean = (Button)findViewById(R.id.btnKclean);
        btnSubmit = (Button)findViewById(R.id.btnKsubmit);
    }

    // Date Time Picker
    private void setDateTimeField() {
        DateEtxt.setOnClickListener(this);
        TimeEtxt.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                DateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog = new TimePickerDialog(this, new OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                // TODO Auto-generated method stub
                TimeEtxt.setText(hour + ":" + minute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), statisicsBoolean);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if(view == DateEtxt) {
            DatePickerDialog.show();
        }else if(view == TimeEtxt){
            TimePickerDialog.show();
        }
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
