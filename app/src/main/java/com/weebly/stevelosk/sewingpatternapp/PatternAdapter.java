package com.weebly.stevelosk.sewingpatternapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by steve on 2/1/2018.
 */

public class PatternAdapter extends BaseAdapter {

    private Context myContext;
    private ArrayList<Pattern> myPatterns;
    private LayoutInflater myInflater;

    PatternAdapter(Context context, ArrayList<Pattern> patterns) {
        myContext = context;
        myPatterns = patterns;
        myInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myPatterns.size();
    }

    @Override
    public Object getItem(int pos) {
        return myPatterns.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        MyViewHolder viewHolder;
        // if convertView is null, create new view
        if (convertView == null) {
            convertView = myInflater.inflate(R.layout.pattern_view_item, null);
            viewHolder = new MyViewHolder();
            // Get view components
            viewHolder.imageView = convertView.findViewById(R.id.thumbNailView);
            viewHolder.brandTextView = convertView.findViewById(R.id.brandLabel);
            viewHolder.patternNumberTextView = convertView.findViewById(R.id.patternNumberLabel);

            convertView.setTag(viewHolder);
        }
        // otherwise, reuse view
        else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        // Assign values from Pattern instance
        Pattern p = myPatterns.get(pos);
        viewHolder.brandTextView.setText(p.getBrand());
        viewHolder.patternNumberTextView.setText(p.getPatternNumber());

        // Image
        if (p.getFrontImgBytes() != null) {
            Bitmap image = BitmapFactory.decodeByteArray(p.getFrontImgBytes(), 0,
                    p.getFrontImgBytes().length);
            viewHolder.imageView.setImageBitmap(image);
        }

        return convertView;
    }
}

class MyViewHolder {
    ImageView imageView;
    TextView brandTextView;
    TextView patternNumberTextView;
}
