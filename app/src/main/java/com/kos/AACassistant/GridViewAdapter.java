package com.kos.AACassistant;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    //GRID VIEW LIST OF BUTTONS
    private List<Button> list;

    public GridViewAdapter(List<Button> btnList)
    {
        this.list = btnList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Button btn;
        if(convertView == null)
        {
            btn = list.get(position);
        }
        else {
            btn = (Button)convertView;
        }
        return btn;
    }
}
