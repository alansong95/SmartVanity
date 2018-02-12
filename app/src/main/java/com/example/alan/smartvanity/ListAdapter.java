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

import java.util.List;

@SuppressWarnings("deprecation")
public class ListAdapter extends BaseAdapter {

    Context context;
    AppWidgetManager manager;
    List<AppWidgetProviderInfo> infoList;

    Drawable[] images;
    Drawable[] icons;

    String[] values;
    /*
    public ListAdapter(Context context, String [] values, String [] numbers, int [] images){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.values = values;
        this.numbers = numbers;
        this.images = images;
    }
    */

    public ListAdapter(Context context) {
        this.context = context;

        manager = AppWidgetManager.getInstance(context);
        infoList = manager.getInstalledProviders();

        images = new Drawable[getCount()];
        values = new String[getCount()];
        icons = new Drawable[getCount()];


        for (int i = 0; i < infoList.size(); i++) {
            images[i] = infoList.get(i).loadPreviewImage(context, 160);
            if (images[i] == null) {
                images[i] = infoList.get(i).loadIcon(context, 160);
            }
            icons[i] = infoList.get(i).loadIcon(context, 160);
            values[i] = infoList.get(i).label;
        }
    }

    @Override
    public int getCount() {
        return infoList.size();
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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.appIconIV);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iconIV);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(values[position]);
        viewHolder.img.setImageDrawable(images[position]);
        viewHolder.icon.setImageDrawable(icons[position]);

        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        ImageView img;
        ImageView icon;
    }

}