package com.example.myapplication;

import static com.example.myapplication.Database.name;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, boardadapter.EventListener {

private FirebaseAuth mAuth = FirebaseAuth.getInstance();
private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

private RecyclerView mBoardRecyclerView;
private String writerId;
private boardadapter mAdapter;
private List<Board> mDatas;
private Button s_btn;
private String edit_s;//검색어 저장용도
private EditText search_edit;//검색어 에딧
private String board_n;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);







        Log.d("확인","여기는 노티스:"+board_n);


        if(mAuth.getCurrentUser()!=null){
                mStore.collection("user").document(mAuth.getCurrentUser().getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.getResult()!=null){
                                                writerId=(String)task.getResult().getData().get(Database.documentId);
                                        }
                                }
                        });
        }
        setContentView(R.layout.activity_main);
        search_edit=findViewById(R.id.edit_search);
        edit_s=search_edit.getText().toString();
        mBoardRecyclerView = findViewById(R.id.recyclerview);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        }




@Override
public boolean onOptionItemSelected(MenuItem item) {
        Log.d("확인", "선택하세요");
        switch (item.getItemId()) {
default:
        return super.onOptionsItemSelected(item);
        }
        }

@Override
protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
                @Override
                public void run() {
                        mainstart();
                }
        }).start();

        }
public void mainstart(){
                if(writerId != null) {
                        Intent intent = getIntent();
                        mDatas = new ArrayList<>();//
                        mStore.collection("Board")//리사이클러뷰에 띄울 파이어베이스 테이블 경로
                                .whereEqualTo("documentId", writerId)
                                .orderBy(Database.timestamp, Query.Direction.DESCENDING)//시간정렬순으로
                                .addSnapshotListener(

                                        new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                        if (queryDocumentSnapshots != null) {
                                                                mDatas.clear();//미리 생성된 게시글들을 다시 불러오지않게 데이터를 한번 정리
                                                                for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                                                                        Log.d("확인", "현재 사용자 uid입니다:" + writerId);
                                                                        Map<String, Object> shot = snap.getData();
                                                                        String documentId = String.valueOf(shot.get(Database.documentId));
                                                                        String title = String.valueOf(shot.get(Database.title));
                                                                        String phone = String.valueOf(shot.get(Database.phone));
                                                                        String price_up = String.valueOf(shot.get(Database.price_up));
                                                                        String contents = String.valueOf(shot.get(Database.contents));
                                                                        String address = String.valueOf(shot.get(Database.address));
                                                                        String p_name = String.valueOf(shot.get(name));
                                                                        String p_photo = String.valueOf(shot.get(Database.p_photo));
                                                                        String board_photo = String.valueOf(shot.get(Database.board_photo));
                                                                        String like = String.valueOf(shot.get(Database.like));
                                                                        //int like = FirebaseID.like;
                                                                        String board_id = String.valueOf(shot.get(Database.board_id));
                                                                        String writer_id = String.valueOf(shot.get(Database.writer_id));
                                                                        String Latitude = String.valueOf(shot.get(Database.latitude));
                                                                        String Longitude = String.valueOf(shot.get(Database.longitude));
                                                                        Board data = new Board(documentId, title, contents, address, p_name, p_photo, board_n, board_photo, board_id, writer_id, like, Latitude, Longitude,price_up,phone);
                                                                        mDatas.add(data);//여기까지가 게시글에 해당하는 데이터 적용

                                                                }
                                                                mAdapter = new boardadapter(MainActivity.this, mDatas);//mDatas라는 생성자를 넣어줌
                                                                mBoardRecyclerView.setAdapter(mAdapter);
                                                        }
                                                }
                                        });
                }
                else {
                        new Thread(new Runnable() {
                                @Override
                                public void run() {
                                        mainstart();
                                }
                        }).start();
                }
                }




@Override
public void onClick(View v) {
        switch (v.getId()){
        case R.id.btn_edit:
        Intent intent2=new Intent(this,boardupActivity.class);
        //board_n=intent2.getStringExtra("board");
        intent2.putExtra("board",board_n);
        Log.d("확인","여기는 게시글:"+board_n);
        startActivity(intent2);
        break;
        case R.id.btn_search:
        Intent intent=new Intent(this,SearchActivity.class);
        intent.putExtra("search",search_edit.getText().toString());//검색어와 관련된 것을 추리는 곳에 보냄
        intent.putExtra("board",board_n);
        startActivity(intent);
                Log.d("확인","현재 사용자 uid입니다:"+writerId);
        Log.d("확인","여기는 포스트 코멘트:"+search_edit.getText().toString());
        break;

        }

        }

@Override
public void onItemClicked(int position) {
        Toast.makeText(this, "몇 번째" + position, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,BoardComment.class));
        }


        }