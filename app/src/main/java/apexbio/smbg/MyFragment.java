package apexbio.smbg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.ScatterChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {
    int mNum; //頁號
    static int pageNum;
    View view = null;
    public static MyFragment newInstance(int num) {
        MyFragment fragment = new MyFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //用num區別fragment
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        //pageNum = mNum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //接收ChartActivity過來的資料
        ChartActivity chartactivity = (ChartActivity) getActivity();
        int highdata[] = chartactivity.getHighData();
        int normaldata[] = chartactivity.getNormalData();
        int lowdata[] = chartactivity.getLowData();
        int countdays = chartactivity.getcountDays();
        int gdataValues[] = chartactivity.getGDataValue();

        //pie chart
        double highPercent =  highdata.length / (double)(highdata.length + normaldata.length + lowdata.length) * 100;
        double normalPercent =  normaldata.length / (double)(highdata.length + normaldata.length + lowdata.length) * 100;
        double lowPercent =  lowdata.length / (double)(highdata.length + normaldata.length + lowdata.length) * 100;
        double[] values = new double[] { highPercent, normalPercent, lowPercent };
        int[] piecolors = new int[] { Color.rgb(214, 163, 186)
                , Color.rgb(240, 221, 230)
                , Color.rgb(245, 178, 178)};
        DefaultRenderer pierenderer = buildCategoryRenderer(piecolors);
        pierenderer.setChartTitleTextSize(50);

        //scatter chart
        String[] titles = new String[] { "The normal range", " ", "High", "Normal", "Low"};
        List<int[]> x = new ArrayList<int[]>();
        int[] ax = new int[countdays];
        for(int i = 0; i < countdays; i ++){
            ax[i] = i + 1;
        }
        x.add(ax);
        x.add(ax);
        //for (int i = 0; i < titles.length; i++) {        }
        x.add(getXvalues(chartactivity.getHighgdataDate(), countdays));
        x.add(getXvalues(chartactivity.getNormalgdataDate(), countdays));
        x.add(getXvalues(chartactivity.getLowgdataDate(), countdays));
        //x.add(getXvalues(chartactivity.getHighgdataDate(), countdays));

        List<int[]> scattervalues = new ArrayList<int[]>();
        //scattervalues.add(gdataValues);
        int[] a = new int[countdays];
        for(int i = 0; i < a.length ; i++){
            a[i] = chartactivity.getHigh();
        }
        scattervalues.add(a);
        int[] b = new int[countdays];
        for(int i = 0; i < b.length ; i++){
            b[i] = chartactivity.getLow();
        }
        scattervalues.add(b);
        scattervalues.add(highdata);
        scattervalues.add(normaldata);
        scattervalues.add(lowdata);

        int[] colors = new int[] { Color.LTGRAY , Color.WHITE, Color.RED, Color.rgb(240, 160, 0), Color.GREEN};

        PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.SQUARE, PointStyle.TRIANGLE, PointStyle.DIAMOND, PointStyle.CIRCLE };
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        setChartSettings(renderer, "Glucose Values", "Date", "Value"
                , 1, countdays, findMinintArray(chartactivity.getGDataValue()) - 20
                , findMaxintArray(chartactivity.getGDataValue()) + 20
                , Color.BLACK, Color.BLACK);
        renderer.setPointSize(8);
        renderer.setXLabels(0);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setZoomEnabled(false, false);
        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setPanLimits(new double[]{1 ,countdays
                , findMinintArray(chartactivity.getGDataValue())
                , findMaxintArray(chartactivity.getGDataValue())});

        //自訂日期於X軸
        String[] Xaxis = getXaxis(countdays);
        if(countdays == 30){
            for(int i = 0; i < countdays; i += 2){
                renderer.addXTextLabel(i + 1, Xaxis[i]);
            }
        }else{
            for(int i = 0; i < countdays; i ++){
                renderer.addXTextLabel(i + 1, Xaxis[i]);
            }
        }

        // 不同頁面傳遞, 0:overview, 1:pie chart, 2:scatter chart
        if(mNum == 0){//overview
            view = inflater.inflate(R.layout.activity_overview, null);
            TextView overview_avg_text = (TextView) view.findViewById(R.id.overview_avg);
            TextView overview_high_text = (TextView) view.findViewById(R.id.overview_high);
            TextView overview_low_text = (TextView) view.findViewById(R.id.overview_low);

            float aa = findAvgintArray(gdataValues)*gdataValues.length /(gdataValues.length);
            if (Float.isNaN(aa))
            {
                aa = 0f;
            }
            overview_avg_text.setText(String.format("%.2f", aa));
            overview_high_text.setText("" + findMaxintArray(gdataValues));
            overview_low_text.setText("" + findMinintArray(gdataValues));
            return view;
        }else if(mNum == 1){//pie chart
            view = ChartFactory.getPieChartView(this.getActivity(), buildCategoryDataset("Pie Chart", values), pierenderer);
            view.setBackgroundColor(Color.WHITE);
            return view;
        }else if(mNum == 2){//scatter chart
            XYMultipleSeriesDataset buildDataset = buildDataset(titles, x, scattervalues);
            //view = ChartFactory.getScatterChartView(this.getActivity(), buildDataset, renderer);
            view = ChartFactory.getCombinedXYChartView(this.getActivity()
                    , buildDataset
                    , renderer
                    , new String[]{BarChart.TYPE
                                    , BarChart.TYPE
                                    , ScatterChart.TYPE
                                    , ScatterChart.TYPE
                                    , ScatterChart.TYPE});
            view.setBackgroundColor(Color.WHITE);
            return view;
        }
        return view;
    }

    //scatter function
    private void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle
            , String yTitle, double xMin, double xMax
            , int yMin, int yMax, int axesColor, int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

    private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        setRenderer(renderer, colors, styles);
        return renderer;
    }

    private void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
                             PointStyle[] styles) {
        renderer.setAxisTitleTextSize(30);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setShowLegend(false);
        renderer.setPointSize(5f);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setMargins(new int[]{20, 50, 250, 20}); //top, left, bottom, right
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            renderer.addSeriesRenderer(r);
        }
    }

    private XYMultipleSeriesDataset buildDataset(String[] titles, List<int[]> xValues, List<int[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeries(dataset, titles, xValues, yValues, 0);
        return dataset;
    }

    private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<int[]> xValues, List<int[]> yValues, int scale) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            XYSeries series = new XYSeries(titles[i], scale);
            int[] xV = xValues.get(i);
            int[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
    }

    private String[] getXaxis(int days){
        String[] result = new String[days];
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        Calendar cToDay = Calendar.getInstance();
        cToDay.set(today.year, today.month, today.monthDay);

        for(int i = 1;i < days + 1;i++){
            result[result.length - i] = (cToDay.get(Calendar.MONTH) + 1) + "/"
                    + cToDay.get(Calendar.DAY_OF_MONTH);
            cToDay.add(Calendar.DAY_OF_MONTH, - 1);
        }
        return result;
    }

    private int[] getXvalues(String[] dateArray, int countdays){
        int[] result = new int[dateArray.length];

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        Calendar cNow=Calendar.getInstance();
        Calendar ctmp=Calendar.getInstance();
        cNow.set(today.year, today.month, today.monthDay);

        int now = cNow.get(Calendar.DAY_OF_YEAR);

        for(int i = 0; i < dateArray.length;i++){
            String[] tmp = dateArray[i].split("-");
            ctmp.set(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]) - 1, Integer.parseInt(tmp[2]));
            int inttmp = ctmp.get(Calendar.DAY_OF_YEAR);
            int daydiff = now - inttmp;
            result[i] = countdays - daydiff;
        }
        return result;
    }
    //

    //pie function
    private DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(25);
        renderer.setMargins(new int[] { 20, 30, 15, 0 }); //  top, left, bottom, right
        renderer.setLabelsColor(Color.rgb(114, 113, 113));
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected CategorySeries buildCategoryDataset(String title, double[] values) {
        CategorySeries series = new CategorySeries(title);
        String[] titlearray = {"High", "Normal", "Low"};
        int k = 0;

        for (double value : values) {
            if(Double.isNaN(value)){
                value = 0;
            }
            series.add(titlearray[k++] + "(" + String.format("%.2f", value) + "%)", value);
        }
        return series;
    }
    //

    public int findMaxintArray(int[] intarray){
        int result = 0;
        if(intarray.length != 0){
            result = intarray[0];
            for(int i = 1; i < intarray.length; i++){
                if (intarray[i] > result)
                {
                    result = intarray[i];
                }
            }
            return result;
        }
        return result;
    }

    public int findMinintArray(int[] intarray){
        int result = 0;
        if(intarray.length != 0){
            result = intarray[0];
            for(int i = 1; i < intarray.length; i++){
                if (intarray[i] < result)
                {
                    result = intarray[i];
                }
            }
            return result;
        }
        return result;
    }

    public float findAvgintArray(int[] intarray){
        float result = 0;
        if(intarray.length != 0){
            float sum  = 0;
            for(int i = 0; i < intarray.length; i++){
                sum += intarray[i];
            }
            result = sum / intarray.length;
            return result;
        }
        return result;
    }
    public static int getCurrentPage(){
        return pageNum;
    }
}

