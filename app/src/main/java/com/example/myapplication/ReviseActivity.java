package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



public class ReviseActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//사용자 정보 가져오기
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mTitle, mContents, price_up, phone;//제목, 내용
    private String p_name;//게시판에 표기할 닉네잉 //이게 가져온 값을 저장하는 임시 변수
    private ImageButton board_photo;
    private String mlatitude;
    private String mlongitude;
    private EditText mAddress;
    private Button mBoard_save;
    private String photoUrl; //사진 저장 변수
    private String board_num, board_id, writer_id, comment_board;
    private Uri uriProfileImage;
    private ImageView board_imageView;
    private String boardImageUrl;
    private static final int CHOOSE_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise);

        mTitle = findViewById(R.id.Board_write_title);//제목 , item_board.xml의 변수와 혼동주의
        mContents = findViewById(R.id.Board_write_contents);
        price_up = findViewById(R.id.edittext_price);
        phone = findViewById(R.id.Board_write_phone2);
        mAddress=findViewById(R.id.Board_write_address);
        mBoard_save=findViewById(R.id.Board_save);
        board_photo = findViewById(R.id.Board_photo);
        board_imageView = findViewById(R.id.board_imageview);
        board_imageView.setVisibility(View.INVISIBLE);


                mBoard_save.setOnClickListener(new View.OnClickListener() {
                    @Override
            public void onClick (View view){
                Log.d("tag", mBoard_save.getText().toString());
                if (mBoard_save.getText().toString().equals("Save")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            requestGeocode();

                        }
                    }).start();
                    mBoard_save.setBackgroundColor(Color.GREEN);
                    mBoard_save.setText("Upload");
                    return;

                } else if (mBoard_save.getText().toString().equals("Upload")) {
                    mBoard_save.setText("Save");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updba();
                        }
                    }).start();


                }

            }
        });

        if (mAuth.getCurrentUser() != null) {//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())//
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                p_name = (String) task.getResult().getData().get(Database.name);//
                                writer_id = (String) task.getResult().getData().get(Database.documentId);
                                Log.d("확인", "현재 사용자 uid입니다:" + writer_id);
                            }
                        }
                    });
        }

        board_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        //사진 불러오기
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            if (user.getPhotoUrl() == null) {
                Log.d("사진", "포토유알엘이 비어있어요.");

            }
            if (user.getPhotoUrl() != null) {
                photoUrl = user.getPhotoUrl().toString();
            }
        }

        Intent intent = getIntent();
        board_num = intent.getStringExtra("board");
        Log.d("확인", "여기는 게시글 작성위:" + board_num);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriProfileImage);
                board_imageView.setImageBitmap(bitmap);
                board_imageView.setVisibility(View.VISIBLE);

                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {
            // progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    boardImageUrl = task.getResult().toString();
                                    Log.i("boardURL", boardImageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //progressBar.setVisibility(View.GONE);

                        }
                    });
        }
    }

    public void requestGeocode() {
        try {
            BufferedReader bufferedReader;
            StringBuilder stringBuilder = new StringBuilder();
            String addr = mAddress.getText().toString();
            Log.d("확인", "여기는 게시글 작성위:" + mAddress.getText().toString());
            String query = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + URLEncoder.encode(addr, "UTF-8");
            URL url = new URL(query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//            String clientId = msosjymfct;// 애플리케이션 클라이언트 아이디값";
//            String clientSecret = VCYEpF3IUCmS4S4cHseYugeRTctP0fwLaQCLsIhW" ;// 애플리케이션 클라이언트 시크릿값";

            if (conn != null) {
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "msosjymfct");
                conn.setRequestProperty("X-NCP-APIGW-API-KEY", "VCYEpF3IUCmS4S4cHseYugeRTctP0fwLaQCLsIhW");
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                //textView.setText(stringBuilder);

                int indexFirst;
                int indexLast;

                indexFirst = stringBuilder.indexOf("\"x\":\"");
                indexLast = stringBuilder.indexOf("\",\"y\":");
                String x = stringBuilder.substring(indexFirst + 5, indexLast);

                indexFirst = stringBuilder.indexOf("\"y\":\"");
                indexLast = stringBuilder.indexOf("\",\"distance\":");
                String y = stringBuilder.substring(indexFirst + 5, indexLast);
                mlatitude = y;
                mlongitude = x;
                Log.d("확인", "여기는 dw 작성위:" + mlatitude);
                Log.d("확인", "여기는 dw 작성위:" + mlongitude);

                bufferedReader.close();
                conn.disconnect();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Board Image"), CHOOSE_IMAGE);
    }



    public void updba(){
        if(mTitle ==null || mAddress == null || photoUrl == null || mContents == null || price_up == null || phone == null)
        {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(ReviseActivity.this,"공란 확인 필요",Toast.LENGTH_SHORT).show();
                    Log.d("확인", "여기는 게시글 작성:" + board_num);
                }
            }, 0);

        }
        else if(mAuth.getCurrentUser()!=null){
            //String BoardID=mStore.collection("Board").document().getId();//제목이 같아도 게시글이 겹치지않게
            Intent intent=getIntent();
            board_num=intent.getStringExtra("number");
            board_id=intent.getStringExtra("Boardid");
            Log.d("확인","여기는 게시글 작성:"+board_num);
            Map<String,Object> data=new HashMap<>();
            data.put(Database.documentId,mAuth.getCurrentUser().getUid());//유저 고유번호
            data.put(Database.title,mTitle.getText().toString());//게시글제목
            data.put(Database.contents,mContents.getText().toString());//게시글 내용
            data.put(Database.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
            data.put(Database.name,p_name);
            data.put(Database.address, mAddress.getText().toString());
            data.put(Database.price_up, price_up.getText().toString());
            data.put(Database.phone, phone.getText().toString());
            data.put(Database.p_photo,photoUrl);
            data.put(Database.board_num,board_num);
            data.put(Database.board_id,intent.getStringExtra("Boardid"));//게시글 ID번호
            data.put(Database.writer_id,writer_id);

            if(!TextUtils.isEmpty(boardImageUrl))
            {
                data.put(Database.board_photo,boardImageUrl);
            }
            mStore.collection("Board").document(board_id).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Update complite",Toast.LENGTH_SHORT).show();
                }
            });//Board라는 테이블에 데이터를 입력하는것/ 문서 이름을 BoardID로 등록
            //startActivity(new Intent(this,NoticeBoardActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {

    }
}