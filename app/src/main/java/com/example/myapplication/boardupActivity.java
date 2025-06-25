package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;



public class boardupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseFirestore mStore=FirebaseFirestore.getInstance();
    private EditText mTitle, price_up, mphone;
    private EditText mContents;
    private Button mBoard_save;
    private EditText mAddress;
    private String mlatitude;
    private String mlongitude;
    private String p_name;
    private ImageButton board_photo;
    private ProgressBar board_progressBar;
    private String photoUrl;
    private String board_num,board_id,writer_id;
    private Uri uriProfileImage;
    private ImageView board_imageView;
    private String boardImageUrl;
    private static final int CHOOSE_IMAGE = 101;
    String BoardID = mStore.collection("Board").document().getId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up);
        price_up=findViewById(R.id.edittext_price);
        mphone=findViewById(R.id.Board_phone);
        mTitle=findViewById(R.id.Board_write_title);
        mContents=findViewById(R.id.Board_write_contents);
        mAddress=findViewById(R.id.Board_write_address);
        mBoard_save = findViewById(R.id.Board_save);
        mBoard_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("tag",mBoard_save.getText().toString());
                if(mBoard_save.getText().toString().equals("Save")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       requestGeocode();

                    }
                }).start();
                    mBoard_save.setBackgroundColor(Color.GREEN);
                    mBoard_save.setText("Upload");
                    return;


            }else if(mBoard_save.getText().toString().equals("Upload")){
                mBoard_save.setText("Save");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updb();
                    }
                }).start();
            }
              /*  if(mBoard_save.getText().toString().equals("Save")){
                    mBoard_save.setText("Upload");
                    mBoard_save.setBackgroundColor(Color.GREEN);
                    run().requestGeocode().start();
                    return;

                }else if(mBoard_save.getText().toString().equals("Upload")){
                    mBoard_save.setText("Save");
                    updb();

                }*/

            }
        });
        board_photo =findViewById(R.id.Board_photo);
        board_imageView = findViewById(R.id.board_imageview);
        board_imageView.setVisibility(View.INVISIBLE);
        board_progressBar = findViewById(R.id.board_progressbar);
        if(mAuth.getCurrentUser()!=null){
            mStore.collection("user").document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                p_name=(String)task.getResult().getData().get(Database.name);

                                writer_id=(String)task.getResult().getData().get(Database.documentId);
                                Log.d("확인","현재 사용자 uid입니다:"+writer_id);
                            }
                        }
                    });
        }

        board_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        //사진 불러오기
        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {

            if (user.getPhotoUrl() == null) {
                Log.d("사진", "포토유알엘이 비어있어요.");

            }
            if (user.getPhotoUrl() != null) {
                photoUrl = user.getPhotoUrl().toString();
            }
        }

        Intent intent=getIntent();
        board_num=intent.getStringExtra("board");
        Log.d("확인","여기는 게시글 작성위:"+board_num);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!= null)
        {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriProfileImage);
                board_imageView.setImageBitmap(bitmap);
                board_imageView.setVisibility(View.VISIBLE);

                uploadImageToFirebaseStorage();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis() + ".jpg");

        if(uriProfileImage !=null)
        {
            board_progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            board_progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    boardImageUrl=task.getResult().toString();
                                    Log.i("boardURL",boardImageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            board_progressBar.setVisibility(View.GONE);

                        }
                    });
        }
    }

    public void requestGeocode(){
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
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Board Image"), CHOOSE_IMAGE);
    }



    @Override
    public void onClick(View v) {

   /*     if(mBoard_save.getText()=="save"){
            requestGeocode();
            mBoard_save.setText("upload");
            return;
        }
        if(mBoard_save.getText()=="upload"){
            updb();
            return;
        }
*/


        /*Thread thread1 = new Thread(()->{
            requestGeocode();

        });
        thread1.start();


        Thread thread2 = new Thread(()->{
            updb();

        });

        thread2.start();
*/

    }
    public void updb() {
    if(mTitle.getText().toString() ==null || mAddress.getText().toString() == null || mContents.getText().toString() == null || price_up.getText().toString() == null || mphone.getText().toString() == null ) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(boardupActivity.this, "공란 확인 필요", Toast.LENGTH_SHORT).show();
                Log.d("확인", "여기는 게시글 작성:" + mphone);
                Log.d("확인", "여기는 게시글 작성:" + photoUrl);
                Log.d("확인", "여기는 게시글 작성:" + mAddress);
                Log.d("확인", "여기는 게시글 작성:" + mTitle);
                Log.d("확인", "여기는 게시글 작성:" + mContents);
                Log.d("확인", "여기는 게시글 작성:" + price_up);
            }
        }, 0);
    }
        else if (mAuth.getCurrentUser() != null) {
            //제목이 같아도 게시글이 겹치지않게
            Intent intent = getIntent();
            board_num = intent.getStringExtra("number");
            board_id = intent.getStringExtra("boardid");
            Log.d("확인", "여기는 게시글 작성:" + board_num);
            Map<String, Object> data = new HashMap<>();
            data.put(Database.documentId, mAuth.getCurrentUser().getUid());//유저 고유번호
            data.put(Database.title, mTitle.getText().toString());//게시글제목
            data.put(Database.contents, mContents.getText().toString());
            data.put(Database.address, mAddress.getText().toString());
            data.put(Database.timestamp, FieldValue.serverTimestamp());//파이어베이스 시간을 저장 그래야 게시글 정렬이 시간순가능
            data.put(Database.name, p_name);
            data.put(Database.price_up, price_up.getText().toString());
            data.put(Database.phone, mphone.getText().toString());
            data.put(Database.p_name, p_name);
            data.put(Database.p_photo, photoUrl);
            Log.d("확인", "여기는 게시글45909045 작성:" + mlatitude);
            data.put(Database.board_id, BoardID);//게시글 ID번호
            data.put(Database.board_num, board_num);
            data.put(Database.like, "0"); //like의 개수를 0으로 초기화

            data.put(Database.writer_id, writer_id);
            data.put(Database.latitude, mlatitude);
            data.put(Database.longitude, mlongitude);

            if (!TextUtils.isEmpty(boardImageUrl)) {
                data.put(Database.board_photo, boardImageUrl);
            }
            mStore.collection("Board").document(BoardID).set(data);//Board라는 테이블에 데이터를 입력하는것/ 문서 이름을 BoardID로 등록
            finish();
        }
         else{

         }




    }


    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}
