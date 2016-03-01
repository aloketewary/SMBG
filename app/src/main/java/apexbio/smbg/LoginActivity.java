package apexbio.smbg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import apexbio.smbgsql.User;
import apexbio.smbgsql.UserDAO;

public class LoginActivity extends Activity {
    private UserDAO userDAO;
    private TextView tvRegister;
    private Button btnLogin, btnExitLogin;
    private EditText edLoginAccount, edLoginPassword;

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setWindowAnimations(0);
        userDAO = new UserDAO(this);
        //userDAO.deleteAll();
        if(userDAO.getUserCount() == 0){
            //userDAO.sample();
        }
        int a = userDAO.getUserCount();
        edLoginAccount = (EditText)findViewById(R.id.edLoginAccount);
        edLoginPassword = (EditText)findViewById(R.id.edLoginPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnExitLogin = (Button)findViewById(R.id.btnExitLogin);
        tvRegister = (TextView)findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User loginUser = userDAO.login(edLoginAccount.getText().toString(),
                        edLoginPassword.getText().toString());
                if(loginUser != null){
                    Globalvariable globalvariable = (Globalvariable)getApplicationContext();
                    globalvariable.user = loginUser;
                    Intent i = new Intent(LoginActivity.this, SettingsActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.LoginActivity_ErrorMsg_TextAccount),Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("TabHost_Index.java onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isExit == false) {
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