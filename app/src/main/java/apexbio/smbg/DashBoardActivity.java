package apexbio.smbg;

/**
 * Created by A1302 on 2015/7/7.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class DashBoardActivity extends FragmentActivity{
    boolean statisicsBoolean = false;
    boolean ExportBoolean = false;

    private PopupWindow popupWindow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(0);
        popupWindow = new PopupWindow();
    }

    // 設定選單列
    public void setHeader(boolean btnStatisticsVisible, boolean btnExportVisible, boolean btnGameVisible, boolean btnSettingsVisible)
    {
        ViewStub stub = (ViewStub)findViewById(R.id.vsHeader);
        View inflated = stub.inflate();
        Button btnStatistics = (Button) inflated.findViewById(R.id.btnStatistics);
        if(!btnStatisticsVisible)
            btnStatistics.setEnabled(false);
        Button btnExport = (Button) inflated.findViewById(R.id.btnExport);
        if(!btnExportVisible)
            btnExport.setEnabled(false);
        Button btnSettings = (Button) inflated.findViewById(R.id.btnSettings);
        if(!btnSettingsVisible)
            btnSettings.setEnabled(false);
    }

    // 選單事件
    public void menuOnClicker(View view){
        switch(view.getId()){
            case R.id.btnStatistics:
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }

                if(statisicsBoolean==false){
                    PopupStaticis();
                }else{
                    statisicsBoolean=false;
                    popupWindow.dismiss();
                }
                break;

            case R.id.btnExport:
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }

                if(ExportBoolean==false){
                    PopupExport();
                }else{
                    ExportBoolean=false;
                    popupWindow.dismiss();
                }
                break;

            case R.id.btnSettings:
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentSettings);
                finish();
                break;
        }
    }

    // 彈出統計選單列視窗
    public void PopupStaticis(){
        statisicsBoolean=true;
        ExportBoolean=false;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Button btnStatistics = (Button)findViewById(R.id.btnStatistics);
        // TODO Auto-generated method stub
        LayoutInflater layoutInflater=(LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView=layoutInflater.inflate(R.layout.popupwindow, null);
        popupWindow = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        Button btnOverView = (Button)popupView.findViewById(R.id.btnOverView);
        Button btnChart = (Button)popupView.findViewById(R.id.btnChart);
        btnOverView.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentStatistics = new Intent(getApplicationContext(), StatisicsActivity.class);
                startActivity(intentStatistics);
                finish();
                popupWindow.dismiss();
            }});

        btnChart.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentStatistics = new Intent(getApplicationContext(), ChartActivity.class);
                startActivity(intentStatistics);
                finish();
                /** can undo to last activity **/
                popupWindow.dismiss();
            }});
        popupWindow.showAsDropDown(btnStatistics, 0, (int)(btnStatistics.getY() - btnStatistics.getHeight()*3));
    }

    //彈出資料上傳選單列視窗
    public void PopupExport(){
        ExportBoolean=true;
        statisicsBoolean=false;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        Button btnExport = (Button)findViewById(R.id.btnExport);
        // TODO Auto-generated method stub
        LayoutInflater layoutInflater=(LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View ExportpopupView=layoutInflater.inflate(R.layout.exportpopupwindow, null);
        popupWindow = new PopupWindow(
                ExportpopupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        Button btnKeyin = (Button)ExportpopupView.findViewById(R.id.btnKeyin);
        Button btnCable = (Button)ExportpopupView.findViewById(R.id.btnCable);
        Button btnBLE = (Button)ExportpopupView.findViewById(R.id.btnBLE);
        btnKeyin.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentExport = new Intent(getApplicationContext(), ExportActivity.class);
                startActivity(intentExport);
                finish();
                popupWindow.dismiss();
            }
        });

        btnCable.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentStatistics = new Intent(getApplicationContext(), CableActivity.class);
                startActivity(intentStatistics);
                finish();
                popupWindow.dismiss();
            }
        });

        btnBLE.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentExport = new Intent(getApplicationContext(), BLEActivity.class);
                startActivity(intentExport);
                finish();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(btnExport, 0, (int)(btnExport.getY() - btnExport.getHeight()*4));
    }
}
