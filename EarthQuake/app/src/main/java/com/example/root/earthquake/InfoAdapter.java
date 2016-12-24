package com.example.root.earthquake;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 23/10/16.
 */
public class InfoAdapter extends ArrayAdapter<Info> {
    InfoAdapter(Activity context, ArrayList<Info> info){
        super(context,0,info);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if(listView==null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,null);
        }
        Info currentInfo = getItem(position);
        TextView mag = (TextView)listView.findViewById(R.id.mag);
        TextView place = (TextView)listView.findViewById(R.id.place);
        TextView date = (TextView)listView.findViewById(R.id.date);
        mag.setText(Double.toString(currentInfo.getMag()));
        place.setText(currentInfo.getPlace());
        date.setText(currentInfo.getDate());
        return listView;
    }
}
