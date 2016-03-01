package apexbio.smbg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by A1302 on 2015/10/16.
 */
public class SettingsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    String[] item;
    String[] value;
    public SettingsAdapter(Context c,String [] item,String [] value){
        inflater = LayoutInflater.from(c);
        this.item = item;
        this.value = value;
    }
    @Override
    public int getCount() {
        return item.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.settings_adapter,viewGroup,false);
        TextView item2,value2;
        item2 = (TextView) view.findViewById(R.id.settings_item);
        value2 = (TextView) view.findViewById(R.id.settings_value);
        item2.setText(item[i]);
        value2.setText(value[i]);
        return view;
    }
}
