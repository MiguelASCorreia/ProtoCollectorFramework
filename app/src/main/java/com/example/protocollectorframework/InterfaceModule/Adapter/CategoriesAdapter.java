package com.example.protocollectorframework.InterfaceModule.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.protocollectorframework.R;

import java.util.List;

/**
 * Adapter used on the categorical components
 */

public class CategoriesAdapter extends BaseAdapter {

    private Context context;
    private String[] categories;
    private List<String> tagFocus;

    public CategoriesAdapter(Context context, String[] categories, List<String> tagFocus) {
        this.context = context;
        this.categories = categories;
        this.tagFocus = tagFocus;
    }

    public String[] getCategories(){
        return categories;
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final String category = categories[position];

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.custom_grid_categories, null);
        }

            TextView textView = (TextView) convertView.findViewById(R.id.text_category);
            textView.setText(category);

            if(tagFocus.contains(category)) {
                textView.setBackground(context.getResources().getDrawable(R.drawable.pob_square_selected));
                textView.setTextColor(Color.WHITE);
            }
            else
                textView.setBackground(context.getResources().getDrawable(R.drawable.pob_square));

        return convertView;
    }
}
