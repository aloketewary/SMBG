package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import apexbio.smbgsql.User;
import apexbio.smbgsql.UserDAO;

public class SettingsActivity extends DashBoardActivity{
    private UserDAO userDAO;
    private User user;
    private ListView List_Settings;
    private String [] value, item;
    private String password_text ,gender_text;
    private Dialog dialog;

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, true, false);

        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        long uid = globalvariable.user.getUid();

        userDAO = new UserDAO(getApplicationContext());
        user = userDAO.get(uid);

        item= new String[] {getString(R.string.activity_settings_ACCOUNT_TEXT)
                ,getString(R.string.activity_settings_NEW_PASSWORD_TEXT)
                ,getString(R.string.activity_settings_BIRTHDAY_TEXT)
                ,getString(R.string.activity_settings_EMAIL_TEXT)
                ,getString(R.string.activity_settings_GENDER_TEXT)
                ,getString(R.string.activity_settings_HEIGHT_TEXT)
                ,getString(R.string.activity_settings_WEIGHT_TEXT)
                ,getString(R.string.activity_settings_HIGH_TEXT)
                ,getString(R.string.activity_settings_LOW_TEXT)};
        List_Settings = (ListView)findViewById(R.id.list_settings);
        gender_text = getString(R.string.activity_settings_GENDER_MALE_TEXT);
        if(user.getGender() == 0){
            gender_text = getString(R.string.activity_settings_GENDER_FEMALE_TEXT);
        }
        password_text = user.getPwd().substring(0,1);
        for(int i = 1;i < user.getPwd().length() - 1;i++){
            password_text += "*";
        }
        password_text += user.getPwd().substring(user.getPwd().length() - 1, user.getPwd().length());

        value = new String[]{user.getAccount()
                , password_text
                , user.getUbirthday()
                , user.getEmail()
                , gender_text
                , String.valueOf(user.getHeight())
                , String.valueOf(user.getWeight())
                , String.valueOf(user.getHigh())
                , String.valueOf(user.getLow())};

        List_Settings.setAdapter(new SettingsAdapter(SettingsActivity.this, item, value));
        List_Settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (i == 0 || i == 4) {

                } else {
                    dialog = new Dialog(SettingsActivity.this, R.style.selectorDialog);
                    dialog.setContentView(R.layout.settings_single_dialog);//指定自定義layout
                    dialog.setTitle("修改" + item[i]);
                    final EditText settings_value_editText = (EditText) dialog.getWindow().findViewById(R.id.settings_single_dialog_value);
                    Button btnOK = (Button) dialog.getWindow().findViewById(R.id.btn_settings_single_dialog_ok);
                    Button btnCancel = (Button) dialog.getWindow().findViewById(R.id.btn_settings_single_dialog_cancel);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 根據List順序做對應操作
                            switch (i) {
                                // 密碼
                                case 1:
                                    user.setPwd(settings_value_editText.getText().toString());
                                    userDAO.update(user);
                                    password_text = user.getPwd().substring(0,1);
                                    for(int i = 1;i < user.getPwd().length() - 1;i++){
                                        password_text += "*";
                                    }
                                    password_text += user.getPwd().substring(user.getPwd().length() - 1, user.getPwd().length());
                                    value = new String[]{user.getAccount()
                                            , password_text
                                            , user.getUbirthday()
                                            , user.getEmail()
                                            , gender_text
                                            , String.valueOf(user.getHeight())
                                            , String.valueOf(user.getWeight())
                                            , String.valueOf(user.getHigh())
                                            , String.valueOf(user.getLow())};
                                    List_Settings.setAdapter(new SettingsAdapter(SettingsActivity.this, item, value));
                                    dialog.dismiss();
                                    break;
                                // 生日
                                case 2:
                                    user.setUbirthday(settings_value_editText.getText().toString());
                                    updateUser();
                                    break;
                                // Email
                                case 3:
                                    user.setEmail(settings_value_editText.getText().toString());
                                    updateUser();
                                    break;
                                // 身高
                                case 5:
                                    user.setHeight(Double.parseDouble(settings_value_editText.getText().toString()));
                                    updateUser();
                                    break;
                                // 體重
                                case 6:
                                    user.setWeight(Double.parseDouble(settings_value_editText.getText().toString()));
                                    updateUser();
                                    break;
                                // 血糖高值
                                case 7:
                                    user.setHigh(Integer.valueOf(settings_value_editText.getText().toString()));
                                    updateUser();
                                    break;
                                // 血糖低值
                                case 8:
                                    user.setLow(Integer.valueOf(settings_value_editText.getText().toString()));
                                    updateUser();
                                    break;
                            }
                        }
                    });
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    settings_value_editText.setText(value[i]);
                    dialog.show();
                }
            }
        });
    }

    // 更新使用者資料
    private void updateUser() {
        userDAO.update(user);
        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        globalvariable.user = user;
        value = new String[]{user.getAccount()
                , password_text
                , user.getUbirthday()
                , user.getEmail()
                , gender_text
                , String.valueOf(user.getHeight())
                , String.valueOf(user.getWeight())
                , String.valueOf(user.getHigh())
                , String.valueOf(user.getLow())};
        List_Settings.setAdapter(new SettingsAdapter(SettingsActivity.this, item, value));
        dialog.dismiss();
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