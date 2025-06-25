package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.naver.maps.map.overlay.InfoWindow;


public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
    private final Context mContext;
    private final ViewGroup mParent;
    private String title, content, price_up, address;


    public pointAdapter(@NonNull Context context, ViewGroup parent)
    {
        super(context);
        mContext = context;
        mParent = parent;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow)
    {

        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_point, mParent, false);
        Intent intent = ((Activity) mContext).getIntent();

        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);
        TextView text3 = (TextView) view.findViewById(R.id.text3);
        TextView text4 = (TextView) view.findViewById(R.id.text4);
        TextView text5 = (TextView) view.findViewById(R.id.text5);



        text1.setText(intent.getStringExtra("title"));
        text2.setText(intent.getStringExtra("address"));
        text3.setText(intent.getStringExtra("content"));
        text4.setText(intent.getStringExtra("price_up"));
        text5.setText(intent.getStringExtra("phone"));



        return view;
    }
}