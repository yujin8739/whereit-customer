package com.example.myapplication;

import  android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class boardadapter extends RecyclerView.Adapter<boardadapter.BoardViewHolder> {
    private List<Board> datas;//뒷부분 추가
    private Context mcontext;



    public interface  EventListener<QuerySnapshot>{
        boolean onOptionItemSelected(MenuItem item);

        void onItemClicked(int position);
    }



    public boardadapter(Context mcontext,List<Board> datas) {//어댑터에 대한 생성자
        this.datas = datas;
        this.mcontext=mcontext;
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoardViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board data=datas.get(position);
        holder.p_name.setText(datas.get(position).getP_name());
        holder.title.setText(datas.get(position).getTitle());
        holder.contents.setText(datas.get(position).getContents());
        holder.address.setText(datas.get(position).getAddress());
        holder.board_like_text.setText(datas.get(position).getLike());
        holder.price_up.setText(datas.get(position).getprice_up());


        if ( !datas.get(position).getBoard_photo().isEmpty()) {
            Picasso.get()
                    .load(datas.get(position).getBoard_photo())
                    .into(holder.board_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.kakaotalk)
                    .into(holder.board_photo);
        }

        final int posi=holder.getAdapterPosition();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context=v.getContext();
                if(posi!=RecyclerView.NO_POSITION){
                    Intent intent=new Intent(v.getContext(),BoardComment.class);
                    intent.putExtra("title",datas.get(posi).getTitle());
                    intent.putExtra("latitude",datas.get(posi).getLatitude());
                    intent.putExtra("longitude",datas.get(posi).getLongitude());
                    intent.putExtra("content",datas.get(posi).getContents());
                    intent.putExtra("p_name",datas.get(posi).getP_name());
                    intent.putExtra("p_photo",datas.get(posi).getP_photo());
                    intent.putExtra("board_photo",datas.get(posi).getBoard_photo());
                    intent.putExtra("uid",datas.get(posi).getDocumentId());
                    intent.putExtra("board_id",datas.get(posi).getBoard_id());
                    intent.putExtra("number",datas.get(posi).getBoard_num());
                    intent.putExtra("position",posi);
                    //intent.putExtra("like",String.valueOf(datas.get(position).getLike()));
                    intent.putExtra("like",datas.get(posi).getLike());
                    intent.putExtra("writer_id",datas.get(posi).getWriter_id());
                    intent.putExtra("time",datas.get(posi).getDate());
                    intent.putExtra("price_up",datas.get(posi).getprice_up());
                    intent.putExtra("phone",datas.get(posi).getPhone());
                    intent.putExtra("address" ,datas.get(posi).getAddress());
                    //intent.putExtra("title",datas.get(pos).title);
                    mcontext.startActivity(intent);
                }
            }
        });



    }



    @Override
    public int getItemCount() {
        return datas.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView latitude;
        private TextView longitude;
        private TextView contents;
        private TextView address;
        private TextView p_name;
        private ImageView board_photo;
        private TextView board_like_text;
        private TextView price_up;

        public BoardViewHolder(@NonNull final View itemView) {//포스트 뷰홀더의 생성자
            super(itemView);
            title=itemView.findViewById(R.id.board_title);
            contents=itemView.findViewById(R.id.board_contents);
            address=itemView.findViewById(R.id.board_address);
            p_name=itemView.findViewById(R.id.board_writer);
            board_photo=itemView.findViewById(R.id.board_imageView);
            board_like_text = itemView.findViewById(R.id.board_liketext);
            price_up=itemView.findViewById(R.id.price_text2);


        }


    }
}