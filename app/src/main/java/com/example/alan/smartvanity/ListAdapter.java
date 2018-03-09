package com.example.alan.smartvanity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ListAdapter extends BaseAdapter {
    Context context;

    Drawable[] icons;
    String[] values;

    ArrayList<Integer> appWidgetIdList;
    /*
    public ListAdapter(Context context, String [] values, String [] numbers, int [] images){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.values = values;
        this.numbers = numbers;
        this.images = images;
    }
    */

    public ListAdapter(Context context, ArrayList<Integer> appWidgetIdList) {
        this.appWidgetIdList = appWidgetIdList;
        this.context = context;

        values = new String[getCount()];
        icons = new Drawable[getCount()];

        AppWidgetProviderInfo info;
        for (int i = 0; i < appWidgetIdList.size(); i++) {
            info = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetInfo(appWidgetIdList.get(i));
            icons[i] = info.loadIcon(context, 160);
            values[i] = info.label;
        }
    }

    @Override
    public int getCount() {
        return appWidgetIdList.size();
    }

    public int getAppWidgetId(int i) {
        return appWidgetIdList.get(i);
    }

    public int getIndex(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconIV);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(values[position]);
        viewHolder.icon.setImageDrawable(icons[position]);

        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        ImageView icon;
    }

}