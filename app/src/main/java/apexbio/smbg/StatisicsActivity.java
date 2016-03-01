package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import apexbio.smbgsql.GData;
import apexbio.smbgsql.GdataAdapter;
import apexbio.smbgsql.GdataDAO;
import apexbio.smbgsql.User;
import apexbio.smbgsql.UserDAO;

public class StatisicsActivity extends DashBoardActivity implements OnItemSelectedListener{
    private TextView textviewstatics;
    private Button btnStatistics;
    private ListView gdataList;
    private GdataDAO gdataDAO;
    private UserDAO userDAO;

    private List<GData> gdatas;

    private Calendar cToDay, FromDay;
    private String cToDayStr, FromDayStr;

    private GdataAdapter gdataAdapter;
    private Spinner FromYearSpinner, FromMonthSpinner, FromDaySpinner,
            ToYearSpinner, ToMonthSpinner, ToDaySpinner;
    private ArrayAdapter<String> YearList, MonthList, DayList;

    private long uid = 0;

    private String[] Year = {"2010", "2011",
            "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020"};
    private String[] Month = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    private String[] Day = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10"
            , "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
            , "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

    private static Boolean isExit = false;
    Timer tExit = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statisics);
        getWindow().setWindowAnimations(0);
        setHeader(true, true, true, true);
        init();
        Globalvariable globalvariable = (Globalvariable)getApplicationContext();
        uid = globalvariable.user.getUid();

        gdataDAO = new GdataDAO(getApplicationContext());
        userDAO =  new UserDAO(getApplicationContext());

        if (gdataDAO.getUserCount(uid) == 0) {
            //gdataDAO.sample(uid);
			/*
			dbFile = new File("gdata.db");
			SQLiteDatabase.deleteDatabase(dbFile);
            */
        }

        //delete all
        //gdataDAO.deleteAll();
        //userDAO.deleteAll();
        //gdatas = gdataDAO.getAll();
        showResult();
    }

    private void init(){
        btnStatistics = (Button)findViewById(R.id.btnStatistics);
        //btnStatistics.setBackgroundResource(R.drawable.m_ic_menu_statistics_onpress);
        btnStatistics.setBackgroundResource(R.drawable.m_ic_menu_overview_onpress);

        gdataList = (ListView)findViewById(R.id.lvGdata);

        textviewstatics = (TextView)findViewById(R.id.textviewstatics);

        FromYearSpinner = (Spinner)findViewById(R.id.FromDateSpinYear);
        FromMonthSpinner = (Spinner)findViewById(R.id.FromDateSpinMonth);
        FromDaySpinner = (Spinner)findViewById(R.id.FromDateSpinDay);
        ToYearSpinner = (Spinner)findViewById(R.id.ToDateSpinYear);
        ToMonthSpinner = (Spinner)findViewById(R.id.ToDateSpinMonth);
        ToDaySpinner = (Spinner)findViewById(R.id.ToDateSpinDay);

        YearList = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, Year);
        MonthList = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, Month);
        DayList = new ArrayAdapter<String>(this,android.R.layout.simple_selectable_list_item, Day);

        FromYearSpinner.setAdapter(YearList);
        FromMonthSpinner.setAdapter(MonthList);
        FromDaySpinner.setAdapter(DayList);
        ToYearSpinner.setAdapter(YearList);
        ToMonthSpinner.setAdapter(MonthList);
        ToDaySpinner.setAdapter(DayList);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        cToDay = Calendar.getInstance();
        FromDay = Calendar.getInstance();

        //cToDay.set(today.year, today.month, today.monthDay);
        //cToDay.set(2015, 3, 1);
        FromDay.add(Calendar.DAY_OF_MONTH, - 7);

        FromYearSpinner.setSelection(FromDay.get(Calendar.YEAR) - 2010);
        FromMonthSpinner.setSelection(FromDay.get(Calendar.MONTH));
        FromDaySpinner.setSelection(FromDay.get(Calendar.DAY_OF_MONTH) -1);
        ToYearSpinner.setSelection(cToDay.get(Calendar.YEAR) - 2010);
        ToMonthSpinner.setSelection(cToDay.get(Calendar.MONTH));
        ToDaySpinner.setSelection(cToDay.get(Calendar.DAY_OF_MONTH) -1);

        FromYearSpinner.setOnItemSelectedListener(this);
        FromMonthSpinner.setOnItemSelectedListener(this);
        FromDaySpinner.setOnItemSelectedListener(this);
        ToYearSpinner.setOnItemSelectedListener(this);
        ToMonthSpinner.setOnItemSelectedListener(this);
        ToDaySpinner.setOnItemSelectedListener(this);
    }

    // 讀取起訖日期
    private void getFdateTdate(){
        String strfYear=FromYearSpinner.getSelectedItem().toString();
        String strfMonth=FromMonthSpinner.getSelectedItem().toString();
        String strfDay=FromDaySpinner.getSelectedItem().toString();
        String strtYear=ToYearSpinner.getSelectedItem().toString();
        String strtMonth=ToMonthSpinner.getSelectedItem().toString();
        String strtDay=ToDaySpinner.getSelectedItem().toString();
        if(Integer.parseInt(strfMonth) < 10){
            cToDayStr =  strfYear + "-0" + strfMonth + "-" + strfDay;
        }else{
            cToDayStr =  strfYear + "-" + strfMonth + "-" + strfDay;
        }
        if(Integer.parseInt(strtMonth) < 10){
            FromDayStr = strtYear + "-0" + strtMonth + "-" + strtDay;
        }else{
            FromDayStr = strtYear + "-" + strtMonth + "-" + strtDay;
        }
    }

    // 顯示結果
    private void showResult(){
        getFdateTdate();
        gdatas = gdataDAO.getFromTo(cToDayStr, FromDayStr, uid);
        gdataAdapter = new GdataAdapter(this, R.layout.single_gdata, gdatas);
        gdataList.setAdapter(gdataAdapter);
    }

    /**
     * This method is fired when something selected on the Spinner.
     */
    public void onItemSelected(AdapterView<?> parentView,View v,int position,long id){
        showResult();
        textviewstatics.setText(cToDayStr + " ~ " + FromDayStr);
    }

    public void onNothingSelected(AdapterView<?> parentView){
        //
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