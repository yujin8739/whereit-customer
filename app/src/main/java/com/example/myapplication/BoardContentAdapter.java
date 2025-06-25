package com.example.myapplication;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Content;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BoardContentAdapter extends RecyclerView.Adapter<BoardContentAdapter.BoardContentViewHolder> {

    private List<Content> mcontent_data;

    public BoardContentAdapter(List<Content> mcontent_data){
        this.mcontent_data=mcontent_data;
    }

    @NonNull
    @Override
    public BoardContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoardContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoardContentViewHolder holder, int position) {
        Content data=mcontent_data.get(position);
        holder.c_name.setText(mcontent_data.get(position).getC_name());
        holder.comment.setText(mcontent_data.get(position).getComment());
        Log.d("씨포토2",mcontent_data.get(position).getC_photo());
        if ( !mcontent_data.get(position).getC_photo().isEmpty()) {
            Picasso.get()
                    .load(mcontent_data.get(position).getC_photo())
                    .into(holder.c_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.kakaotalk)
                    .into(holder.c_photo);
        }
    }

    @Override
    public int getItemCount() {
        return mcontent_data.size();
    }

    class BoardContentViewHolder extends RecyclerView.ViewHolder{

        private TextView c_name;
        private TextView comment;
        private ImageView c_photo;
        public BoardContentViewHolder(@NonNull View itemView) {
            super(itemView);
            c_name=itemView.findViewById(R.id.comment_item_nickname);
            comment=itemView.findViewById(R.id.comment_contents);
            c_photo=itemView.findViewById(R.id.comment_item_photo);
        }
    }
}

