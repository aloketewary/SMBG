package apexbio.smbgsql; /**
 * Created by A1302 on 2015/7/7.
 */

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import apexbio.smbg.R;

public class GdataAdapter extends ArrayAdapter<GData>{
    // 畫面資源編號
    private int resource;
    // 包裝的血糖資料
    private List<GData> Gdatas;
    public GdataAdapter(Context context, int resource, List<GData> Gdatas) {
        super(context, resource, Gdatas);
        // TODO Auto-generated constructor stub
        this.resource = resource;
        this.Gdatas = Gdatas;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout GdataView;
        // 讀取目前位置的血糖物件
        final GData Gdatas = getItem(position);
        if (convertView == null) {
            // 建立項目畫面元件
            GdataView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)
                    getContext().getSystemService(inflater);
            li.inflate(resource, GdataView, true);
        }
        else {
            GdataView = (LinearLayout) convertView;
        }
        // 讀取layout物件
        TextView GdatadateView = (TextView) GdataView.findViewById(R.id.gdatadate);
        TextView GdatatimeView = (TextView) GdataView.findViewById(R.id.gdatatime);
        TextView GdataflagView = (TextView) GdataView.findViewById(R.id.gdataflag);
        TextView GdatavalueView = (TextView) GdataView.findViewById(R.id.gdatavalue);
        // 設定資料內容
        GdatadateView.setText(Gdatas.getGDataDate());
        GdatatimeView.setText(Gdatas.getGDataTime());
        GdataflagView.setText(Gdatas.getGDataFlag());
        GdatavalueView.setText("" + Gdatas.getGvalue());
        return GdataView;
    }
    // 設定指定編號的血糖資料
    public void set(int index, GData Gdata) {
        if (index >= 0 && index < Gdatas.size()) {
            Gdatas.set(index, Gdata);
            notifyDataSetChanged();
        }
    }
    // 讀取指定編號的血糖資料
    public GData get(int index) {
        return Gdatas.get(index);
    }
}

