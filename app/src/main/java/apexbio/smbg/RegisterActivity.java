package apexbio.smbg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import apexbio.smbgsql.User;
import apexbio.smbgsql.UserDAO;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private UserDAO userDAO;
    private Button btnRegisterOK, btnRegisterCancel;
    private EditText edRaccount, edRpwd, edRpwdconfirm, edRname, edRbirthday
            , edRheight, edRweight, edRemail, edRhigh, edRlow;
    private RadioButton rmale, rfemale;
    private RadioGroup rgroup_Rgender;
    private int rgender;
    private DatePickerDialog DatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setWindowAnimations(0);
        // 使用user資料庫
        userDAO = new UserDAO(this);
        // 抓取UI
        findViewsById();
        setDateTimeField();

        // 確認後檢查輸入資訊並註冊
        btnRegisterOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strRpwd = edRpwd.getText().toString();
                String strRpwdconfirm = edRpwdconfirm.getText().toString();
                if(strRpwd.equals(strRpwdconfirm)){
                    dialog();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.RegisterActivity_ErrorMsg_PwdConfirm), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegisterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        rgroup_Rgender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbMale:
                        rgender = 1;
                        break;
                    case R.id.rbFemale:
                        rgender = 0;
                        break;
                }
            }
        });
    }

    // 註冊資料確認對話框
    private void dialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String strRaccount = edRaccount.getText().toString();
            String strRpwd = edRpwd.getText().toString();
            String strRname = edRname.getText().toString();
            String strGender;
            if(rgender == 0){
                strGender = getString(R.string.activity_register_GENDER_FEMALE_TEXT);
            }else if(rgender == 1){
                strGender = getString(R.string.activity_register_GENDER_MALE_TEXT);
            }else{
                strGender = "null";
            }

            String strRheight = edRheight.getText().toString();
            String strRweight = edRweight.getText().toString();
            String strRemail = edRemail.getText().toString();
            String ubirthday = edRbirthday.getText().toString();

            String msg =  getString(R.string.activity_register_ACCOUNT_TEXT) + strRaccount +
                    "\n" + getString(R.string.activity_register_PASSWORD_TEXT) + strRpwd +
                    "\n" + getString(R.string.activity_register_NAME_TEXT) + strRname +
                    "\n" + getString(R.string.activity_register_GENDER_TEXT) + strGender +
                    "\n" + getString(R.string.activity_register_BIRTHDAY_TEXT) + ubirthday +
                    "\n" + getString(R.string.activity_register_HEIGHT_TEXT) + strRheight +
                    "\n" + getString(R.string.activity_register_WEIGHT_TEXT) + strRweight +
                    "\n" + getString(R.string.activity_register_EMAIL_TEXT) + strRemail;

            builder.setMessage(getString(R.string.RegisterActivity_CheckUserInfo) + msg)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO Auto-generated method stub
                            int uid = userDAO.getUserCount() + 1;
                            String strRaccount, strRpwd, strRname, strRemail, ubirthday, strRhigh, strRlow;
                            Float fRheight, fRweight;
                            strRaccount = edRaccount.getText().toString();
                            strRpwd = edRpwd.getText().toString();
                            strRname = edRname.getText().toString();
                            strRemail = edRemail.getText().toString();
                            ubirthday = edRbirthday.getText().toString();
                            strRhigh = edRhigh.getText().toString();
                            strRlow = edRlow.getText().toString();
                            if (edRheight.getText().toString().length() == 0) {
                                fRheight = (float) 0;
                            } else {
                                fRheight = Float.parseFloat(edRheight.getText().toString());
                            }

                            if (edRweight.getText().toString().length() == 0) {
                                fRweight = (float) 0;
                            } else {
                                fRweight = Float.parseFloat(edRweight.getText().toString());
                            }

                            //檢查帳號重複，被使用過則true
                            boolean CheckUserAccount = userDAO.CheckUserAccount(strRaccount);

                            if (strRaccount.length() < 6 || strRpwd.length() < 6 ||
                                    strRname.length() == 0 || strRemail.length() == 0 ||
                                    ubirthday.length() == 0 || strRhigh.length() == 0 || strRlow.length() == 0) {
                                Toast.makeText(getApplicationContext(), R.string.RegisterActivity_CHECK_YOUR_ACCOUNT_INFO, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            } else if(CheckUserAccount) {
                                Toast.makeText(getApplicationContext(), R.string.RegisterActivity_DoubleAccount, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            } else {
                            userDAO.insert(new User(uid, strRaccount, strRpwd, strRname
                                    , rgender, ubirthday, fRheight, fRweight, strRemail, Integer.valueOf(strRhigh), Integer.valueOf(strRlow)));
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
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
            builder.setMessage(getString(R.string.RegisterActivity_RecheckData))
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
        btnRegisterOK = (Button)findViewById(R.id.btnRegisterOK);
        btnRegisterCancel = (Button)findViewById(R.id.btnRegisterCancel);

        edRaccount = (EditText)findViewById(R.id.edRaccount);
        edRpwd = (EditText)findViewById(R.id.edRpwd);
        edRpwdconfirm = (EditText)findViewById(R.id.edRpwdconfirm);
        edRname = (EditText)findViewById(R.id.edRname);
        edRheight = (EditText)findViewById(R.id.edRheight);
        edRweight = (EditText)findViewById(R.id.edRweight);
        edRemail = (EditText)findViewById(R.id.edRemail);
        edRhigh = (EditText)findViewById(R.id.edRhigh);
        edRlow = (EditText)findViewById(R.id.edRlow);
        edRbirthday = (EditText)findViewById(R.id.edRbirthday);
        edRbirthday.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        rgroup_Rgender = (RadioGroup)findViewById(R.id.rgroup_Rgender);
        rmale = (RadioButton)findViewById(R.id.rbMale);
        rfemale = (RadioButton)findViewById(R.id.rbFemale);

        //btnRegisterOK.setEnabled(false);
        edRaccount.addTextChangedListener(new GenericTextWatcher(edRaccount));
        edRpwd.addTextChangedListener(new GenericTextWatcher(edRpwd));
        edRpwdconfirm.addTextChangedListener(new GenericTextWatcher(edRpwdconfirm));
        edRname.addTextChangedListener(new GenericTextWatcher(edRname));
        edRheight.addTextChangedListener(new GenericTextWatcher(edRheight));
        edRweight.addTextChangedListener(new GenericTextWatcher(edRweight));
        edRemail.addTextChangedListener(new GenericTextWatcher(edRemail));
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

    // Date Picker
    private void setDateTimeField() {
        edRbirthday.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edRbirthday.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if(view == edRbirthday) {
            DatePickerDialog.show();
        }
    }
}