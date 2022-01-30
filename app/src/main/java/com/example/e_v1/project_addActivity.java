package com.example.e_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class project_addActivity extends AppCompatActivity {

    private static final String TAG = "project_addActivity";

    private Button btChoose;
    private Button btUpload;
    private VideoView ivPreview;
    private EditText tag_id;
    private EditText title_id;

    ListView list_vw;
    ListView list_vw_r;
    String log_id;

    boolean tag_t = true;


    private DatabaseReference mDatabase;

    private FirebaseDatabase mDatabase_1;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    public static Context pro_addA;

    private Uri filePath;

    public int file_num;

    ArrayAdapter<String> adapter, adapter_2;

    //List<Object> Array = new ArrayList<Object>();
    //List<Object> Array2 = new ArrayList<Object>();
    List<Object> Array;
    List<Object> Array2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_add);

        btChoose = (Button) findViewById(R.id.bt_choose);
        btUpload = (Button) findViewById(R.id.bt_upload);
        ivPreview = (VideoView) findViewById(R.id.iv_preview);
        tag_id = (EditText) findViewById(R.id.tag_id);
        title_id = (EditText) findViewById(R.id.title_id);

        list_vw = ((HomeActivity)HomeActivity.home).list_vw;
        list_vw_r = ((HomeActivity)HomeActivity.home).list_vw_r;


        pro_addA = this;

        Array = ((HomeActivity)HomeActivity.home).Array_home_l;
        Array2 = ((HomeActivity)HomeActivity.home).Array_home_r;

        log_id = ((MainActivity)MainActivity.main).mEmailText.getText().toString().trim();

        initDatabase();

        //ListView a = ((HomeActivity)HomeActivity.home).list_vw;

        mDatabase = FirebaseDatabase.getInstance().getReference();


        adapter = new ArrayAdapter<String>((HomeActivity)HomeActivity.home,android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        adapter_2 = new ArrayAdapter<String>((HomeActivity)HomeActivity.home,android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());




        list_vw.setAdapter(adapter);
        list_vw_r.setAdapter(adapter_2);


        //버튼 클릭 이벤트
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //업로드

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            String tag_id_t = tag_id.getText().toString().trim();
                            tag_t = false;

                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                if(data.getValue(Nfc_tag.class).nfctag == null){
                                    //Toast.makeText(MainActivity.this, "에러", Toast.LENGTH_SHORT).show();
                                }
                                else if (data.getValue(Nfc_tag.class).nfctag.equals(tag_id_t)){
                                    uploadFile();

                                    String msg = title_id.getText().toString();

                                    title_and_email ti_and_em = new title_and_email(msg, log_id);

                                    //title_and_email title_and_email = new title_and_email();

                                    mDatabase.child("message").push().setValue(ti_and_em);

                                    mReference = mDatabase_1.getReference("message");
                                    mReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            adapter.clear();

                                            title_and_email title_and_email = new title_and_email();

                                            for(DataSnapshot messageData : snapshot.getChildren()) {

                                                if(messageData.getValue(title_and_email.class).title != null) {

                                                    String msg2 = messageData.getValue(title_and_email.class).title;
                                                    Array.add(msg2);
                                                    adapter.add(msg2);
                                                }
                                                if(messageData.getValue(title_and_email.class).email.equals(log_id)){
                                                    String my_project_title = messageData.getValue(title_and_email.class).title;
                                                    Array2.add(my_project_title);
                                                    adapter_2.add(my_project_title);

                                                    adapter_2.notifyDataSetChanged();
                                                    list_vw_r.setSelection(adapter_2.getCount() - 1);

                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                            list_vw.setSelection(adapter.getCount() - 1);
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                    tag_t = true;
                                }
                            }
                            if (tag_t == false){
                                Toast.makeText(getApplicationContext(), "태그 번호 일치하지 않음 관리자에게 문의하세요", Toast.LENGTH_SHORT).show();
                                tag_t = true;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });

            }
        });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReferenceFromUrl("gs://exhibition-9394f.appspot.com").child("video");


        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                        }
                        for (StorageReference item : listResult.getItems()) {
                            file_num++;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }
    // 파일 관련
    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                ivPreview.setVideoURI(filePath);
                ivPreview.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 파일 관련
    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String filename = formatter.format(now) + ".mp4";

            //storage 주소와 폴더 파일명을 지정해 준다.

            StorageReference storageRef = storage.getReferenceFromUrl("gs://exhibition-9394f.appspot.com").child("video/" + file_num);
            String file_name = storageRef.getName();
            //올라가기
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                    double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    private void initDatabase() {

        mDatabase_1 = FirebaseDatabase.getInstance();

        mReference = mDatabase_1.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        mReference.addChildEventListener(mChild);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }

}