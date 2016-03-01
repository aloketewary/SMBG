package apexbio.smbg;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import apexbio.smbgsql.UserDAO;

public class SplashActivity extends Activity {
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userDAO = new UserDAO(this);

        //getWindow().setWindowAnimations(0);
        Thread timer= new Thread()
        {
            public void run()
            {
                try {
                    //Display for 2 seconds
                    sleep(2000);
                }
                catch (InterruptedException e)
                {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                finally
                {
                    // 第一次登入或未有User註冊即顯示
                    if(userDAO.getUserCount() == 0){
                        Intent intent = new Intent(SplashActivity.this, TourActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }
}
