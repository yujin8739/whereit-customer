package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.ToggleButton;

import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardComment extends AppCompatActivity implements View.OnClickListener {
    SharedPreferences.Editor prefEditor;
    SharedPreferences prefs;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView com_title;
    private TextView com_text;
    private TextView com_nick;
    private ImageView com_photo;
    private ImageView com_photo2;
    private TextView price_text, address_text, phone;
    private EditText editName;
    private BoardContentAdapter contentAdapter;
    private RecyclerView mCommentRecyclerView;
    private List<Content> mcontent;
    private EditText com_edit;
    private String comment_p,board_t,board_num,comment_board;//
    String sub_pos;//코멘트에 들어가있는 게시글의 위치
    int com_pos = 0;//게시글의 등록된 위치
    int like=0;
    private ToggleButton likeButton; //좋아요 버튼
    private TextView likeText; //좋아요 갯수보여주는 텍스트
    //private Button first,second;
    private String photoUrl,uid,board_id,writer_id_board,current_user; //사진 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        com_nick = (TextView) findViewById(R.id.Comment_nickname);//본문 작성자
        editName = (EditText) findViewById(R.id.editText_name);
        phone = (TextView) findViewById(R.id.phone_text2);
        address_text = (TextView) findViewById(R.id.address_text);
        com_title = (TextView) findViewById(R.id.Comment_title);//제목
        com_text = (TextView) findViewById(R.id.Comment_text);
        price_text = (TextView) findViewById(R.id.price_text);
        com_edit = (EditText) findViewById(R.id.Edit_comment);//댓글 작성 내용
        com_photo = (ImageView) findViewById(R.id.Comment_photo); //작성자 프로필 이미지
        com_photo2 =  (ImageView) findViewById(R.id.Comment_photo2); //작성자가 올린 이미지
        likeButton = (ToggleButton) findViewById(R.id.like_button); //좋아요 버튼
        likeText = (TextView) findViewById(R.id.like_text); // 좋아요 개수 보여주는 텍스트
        mCommentRecyclerView = findViewById(R.id.comment_recycler);//코멘트 리사이클러뷰
        Intent intent = getIntent();//데이터 전달받기
        address_text.setText(intent.getStringExtra("address"));
        com_pos = intent.getExtras().getInt("position");
        phone.setText(intent.getStringExtra("phone"));
        com_nick.setText(intent.getStringExtra("name"));
        com_text.setText(intent.getStringExtra("content"));
        com_title.setText(intent.getStringExtra("title"));
        //first = findViewById(R.id.first);
        //second = findViewById(R.id.second);
        price_text.setText(intent.getStringExtra("price_up"));
        //likeText.setText(intent.getStringExtra("like").toString());
        like= Integer.parseInt(intent.getStringExtra("like"));
        likeText.setText(intent.getStringExtra("like").toString());
        uid=intent.getStringExtra("uid");//게시글 작성자의 uid를 받아옴
        board_id=intent.getStringExtra("board_id");
        writer_id_board=intent.getStringExtra("writer_id");
        board_num=intent.getStringExtra("number");

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean tgpref = preferences.getBoolean("tgpref", false);  //default is true

        likeButton.setChecked(tgpref);
        //사진 불러오기
        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {

            if (user.getPhotoUrl() == null) {


            }
            if (user.getPhotoUrl() != null) {
                photoUrl = user.getPhotoUrl().toString();
            }
        }

        if ( !intent.getExtras().getString("p_photo").isEmpty()) {
            Log.d("피포토", intent.getExtras().getString("p_photo"));
            Picasso.get()
                    .load(intent.getStringExtra("p_photo"))
                    .into(com_photo);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_add_photo)
                    .into(com_photo);
        }
        String sboard_photo=intent.getExtras().getString("board_photo");
        Log.d("String sboard값", sboard_photo);
        if ( !sboard_photo.equals("null")) {
            Log.d("피포토사진있음", intent.getExtras().getString("board_photo"));
            Picasso.get()
                    .load(intent.getStringExtra("board_photo"))
                    .into(com_photo2);
        }
        else
        {
            Log.d("사진빔", "사진이 비어있어요");
            com_photo2.getLayoutParams().height= 0;
            com_photo2.setVisibility(View.GONE);
        }





        board_t=intent.getStringExtra("title");//게시글의 위치
        board_t=intent.getStringExtra("latitude");
        board_t=intent.getStringExtra("longitude");
        //time=(String)intent.getSerializableExtra("time");//해당 게시글의 등록 시간



        findViewById(R.id.comment_button).setOnClickListener(this);//댓글 입력 버튼

        if(mAuth.getCurrentUser()!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                comment_p=(String)task.getResult().getData().get(Database.name);//
                                current_user=(String)task.getResult().getData().get(Database.documentId);
                            }
                        }
                    });
        }

        likeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(likeButton.isChecked()){
                    Log.d("토글", "켜짐");
                    like++;
                    mStore.collection("Board").document(board_id)
                            .update("like",Integer.toString(like));
                    Log.d("토글", "켜짐"+like);
                    likeText.setText(Integer.toString(like));
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", true); // value to store
                    editor.commit();
                }
                else{
                    Log.d("토글", "꺼짐");
                    like--;
                    mStore.collection("Board").document(board_id)
                            .update("like", Integer.toString(like));
                    Log.d("토글", "꺼짐"+like);
                    likeText.setText(Integer.toString(like));
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("tgpref", false); // value to store
                    editor.commit();
                }
            }
        });
        /*second.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         if (writer_id_board.equals(current_user)) {
                                             mStore.collection("Board").document(board_id)
                                                     .delete()
                                                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                         @Override
                                                         public void onSuccess(Void aVoid) {
                                                             Log.d("확인", "삭제되었습니다.");
                                                             finish();
                                                         }
                                                     });
                                         } else {
                                             Toast.makeText(getApplicationContext(), "작성자가 아닙니다.", Toast.LENGTH_LONG).show();
                                         }
                                     }
                                 });

                first.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                if(writer_id_board.equals(current_user)) {
                        Intent intent=new Intent(BoardComment.this,ReviseActivity.class);
                        intent.putExtra("Boardid",board_id);
                        intent.putExtra("number",board_num);
                        startActivity(intent);//게시글 수정
                    }
                else
                    {
                        Toast.makeText(getApplicationContext(),"작성자가 아닙니다.",Toast.LENGTH_SHORT).show();
                    }

                }

            });*/

    }




    @Override
    protected void onStart() {
        super.onStart();

        //if(Integer.toString(com_pos)==comment_p) {//게시글의 위치와 댓글의 위치가 같은거를 보여줌
        mcontent = new ArrayList<>();//리사이클러뷰에 표시할 댓글 목록

        mStore.collection("Comment")
                .whereEqualTo("title", board_t)//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                .orderBy(Database.timestamp, Query.Direction.ASCENDING)//시간정렬순으로 이건 처음에 작성한게 제일 위로 올라감 게시글과 반대
                .addSnapshotListener(new EventListener<QuerySnapshot>(){
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            mcontent.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                            for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                Map<String, Object> shot = snap.getData();
                                Intent intent = getIntent();
                                String documentId = String.valueOf(shot.get(Database.documentId));
                                String comment = String.valueOf(shot.get(Database.comment));
                                String c_name = String.valueOf(shot.get(Database.name));
                                String phone = String.valueOf(shot.get(Database.phone));
                                String c_photo = String.valueOf(shot.get(Database.c_photo));
                                String num_comment=String.valueOf(shot.get(Database.comment_board));
                                String Latitude = String.valueOf(shot.get(Database.latitude));
                                String Longitude = String.valueOf(shot.get(Database.longitude));
                                String price_up = String.valueOf(shot.get(Database.price_up));
                                String address = String.valueOf(shot.get(Database.address));
                                Content data = new Content(documentId, c_name, comment,  Integer.toString(com_pos),board_t,c_photo,num_comment,Latitude, Longitude, price_up, address, phone);
                                mcontent.add(data);//여기까지가 게시글에 해당하는 데이터 적용
                            }
                        }
                        contentAdapter = new BoardContentAdapter(mcontent);//mDatas라는 생성자를 넣어줌
                        mCommentRecyclerView.setAdapter(contentAdapter);
                    }

                });
        mapFragment mapFragment = new mapFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.linearLayout10, mapFragment).commit();
    }


    @Override
    public void onClick(View v) {
        if (mAuth.getCurrentUser() != null) {//새로 Comment란 컬렉션에 넣어줌
            Map<String, Object> data = new HashMap<>();
            data.put(Database.documentId, mAuth.getCurrentUser().getUid());//유저 고유번호
            data.put(Database.comment, com_edit.getText().toString());//게시글 내용
            data.put(Database.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
            data.put(Database.name, editName.getText().toString());
            data.put(Database.c_photo,photoUrl);
            Intent intent = getIntent();//데이터 전달받기
            data.put(Database.title,board_t);//게시글의 제목을 넣어준다 비교하기위해서
            //Log.d("확인",po)
            comment_board=intent.getStringExtra("board_id");
            com_pos = intent.getExtras().getInt("position");//Board 콜렉션의 게시글 등록위치를 전달받아옴
            //Log.d("확인","위치"+com_pos);
            data.put(Database.board_position, Integer.toString(com_pos));//작성된 게시판의 위치를 댓글에 저장
            data.put(Database.comment_board,comment_board);
            mStore.collection("Comment").add(data);//댓글 콜렉션에 저장
            View view = this.getCurrentFocus();//작성버튼을 누르면 에딧텍스트 키보드 내리게 하기
            if (view != null) {//댓글작성시 키보드 내리고 댓글에 작성한 내용 초기화
                InputMethodManager hide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                hide.hideSoftInputFromWindow(view.getWindowToken(), 0);
                com_edit.setText("");
            }
            finish();
            startActivity(intent);
        }
    }
}