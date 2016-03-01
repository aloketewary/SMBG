package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import apexbio.smbgsql.GData;
import apexbio.smbgsql.GdataDAO;

public class ChartActivity extends DashBoardActivity {
    static final int NUM_ITEMS = 3;
    private int countDays  = 30;
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private Calendar FromDay;
    private RadioButton days_7, days_14, days_30;
    private RadioGroup rgroup_days;
    private List<GData> gdatas;
    int highData[], lowData[], normalData[], gdataValueArray[];
    private String gdataDateStrArray[], gdataHighDateStrArray[], gdataNormalDateStrArray[], gdataLowDateStrArray[];
    private GdataDAO gdataDAO;
    private long uid = 0;
    public Globalvariable globalvariable;
    private int high, low;

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, true, true);
        globalvariable = (Globalvariable)getApplicationContext();
        uid = globalvariable.user.getUid();
        high = globalvariable.user.getHigh();
        low = globalvariable.user.getLow();

        days_7 = (RadioButton) findViewById(R.id.days_7);
        days_14 = (RadioButton) findViewById(R.id.days_14);
        days_30 = (RadioButton) findViewById(R.id.days_30);
        days_30.setChecked(true);

        Button btnStatistics = (Button)findViewById(R.id.btnStatistics);
        btnStatistics.setBackgroundResource(R.drawable.m_ic_menu_chart_onpress);

        getDBdata();

        rgroup_days = (RadioGroup) findViewById(R.id.rgroup_days);
        rgroup_days.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                //int p = group.indexOfChild((RadioButton) findViewById(checkedId));
                //int count = group.getChildCount();
                switch (checkedId) {
                    case R.id.days_7:
                        countDays = 7;
                        getDBdata();
                        break;
                    case R.id.days_14:
                        countDays = 14;
                        getDBdata();
                        break;
                    case R.id.days_30:
                        countDays = 30;
                        getDBdata();
                        break;
                }
            }});
    }

    // 抓取資料
    private void getDBdata(){
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        FromDay = Calendar.getInstance();
        FromDay.set(today.year, today.month, today.monthDay);

        String tdate = "";
        String fdate = "";
        int inttmonth = FromDay.get(Calendar.MONTH) + 1;
        String strmonth = "0" + inttmonth;
        int inttday = FromDay.get(Calendar.DAY_OF_MONTH);
        String strday = "0" + inttday;

        if(inttmonth > 9){
            strmonth = "" + inttmonth;
        }

        if(inttday > 9){
            strday = "" + inttday;
        }

        tdate = FromDay.get(Calendar.YEAR) +"-" + strmonth + "-"
                + strday;

        FromDay.add(Calendar.DAY_OF_MONTH, - countDays);

        //抓取資料庫資料
        gdataDAO = new GdataDAO(getApplicationContext());
        if(gdataDAO != null){
            //(high, low, date)

            int intfmonth = FromDay.get(Calendar.MONTH) + 1;
            int intfday = FromDay.get(Calendar.DAY_OF_MONTH);
            String strfmonth = "0" + intfmonth;
            String strfday = "0" + intfday;

            if(intfmonth > 9){
                strfmonth = "" + intfmonth;
            }

            if(intfday > 9){
                strfday = "" + intfday;
            }

            fdate = FromDay.get(Calendar.YEAR) +"-" + strfmonth + "-"
                    + strfday;

            List<GData> NormalData =
                    gdataDAO.getNormalValue(globalvariable.user.getHigh()
                            , globalvariable.user.getLow(), fdate, tdate, uid);;
            normalData = new int[NormalData.size()];
            gdataNormalDateStrArray = new String[NormalData.size()];
            for(int i = 0;i < NormalData.size();i++){
                normalData[i] = NormalData.get(i).getGvalue();
                gdataNormalDateStrArray[i] = NormalData.get(i).getGDataDate();
            }

            List<GData> HighData = gdataDAO.getHighValue(globalvariable.user.getHigh(), fdate, tdate, uid);
            highData = new int[HighData.size()];
            gdataHighDateStrArray = new String[HighData.size()];
            for(int i = 0;i < HighData.size();i++){
                highData[i] = HighData.get(i).getGvalue();
                gdataHighDateStrArray[i] = HighData.get(i).getGDataDate();
            }

            List<GData> LowData = gdataDAO.getLowValue(globalvariable.user.getLow(), fdate, tdate, uid);
            lowData = new int[LowData.size()];
            gdataLowDateStrArray = new String[LowData.size()];
            for(int i = 0;i < LowData.size();i++){
                lowData[i] = LowData.get(i).getGvalue();
                gdataLowDateStrArray[i] = LowData.get(i).getGDataDate();
            }

            gdatas = gdataDAO.getFromTo(fdate, tdate, uid);

            gdataDateStrArray = new String[gdatas.size()];
            gdataValueArray = new int[gdatas.size()];

            for(int i=0;i<gdatas.size();i++){
                gdataDateStrArray[i] = gdatas.get(i).getGDataDate();
                gdataValueArray[i] = gdatas.get(i).getGvalue();
            }
        }

        mAdapter =  new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        //fragment 頁數
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance(position);
        }
    }

    // 抓取血糖高值
    public int[] getHighData(){
        return highData;
    }

    // 抓取血糖低值
    public int[] getLowData(){
        return lowData;
    }

    // 抓取血糖正常值
    public int[] getNormalData(){
        return normalData;
    }

    // 抓取血糖資料
    public int[] getGDataValue(){
        return gdataValueArray;
    }

    // 抓取血糖日期資料
    public String[] getGDataDate(){
        return gdataDateStrArray;
    }

    // 抓取血糖低值日期資料
    public String[] getLowgdataDate(){
        return gdataLowDateStrArray;
    }

    // 抓取血糖高值日期資料
    public String[] getHighgdataDate(){
        return gdataHighDateStrArray;
    }

    // 抓取血糖正常值日期資料
    public String[] getNormalgdataDate(){
        return gdataNormalDateStrArray;
    }

    // 抓取日期範圍
    public int getcountDays(){
        return countDays;
    }

    // 抓取高值
    public int getHigh(){
        return high;
    }

    // 抓取低值
    public int getLow(){
        return low;
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
