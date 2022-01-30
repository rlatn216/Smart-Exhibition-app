package com.example.e_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RelativeLayout ll_page = null;
    private RelativeLayout rr_page = null;
    private FrameLayout home_main_view;
    private RelativeLayout home_main_view1;

    private ImageButton btn_mypage;
    private ImageButton btn_menu;
    private ImageButton btn_home;
    private ImageButton btn_message;

    private ImageView message_View;
    private TextView message_text;
    private int messageTriger = 0;



    private int btn_key_h = 0;
    private int btn_key_l = 0;
    private int btn_key_r = 0;

    ListView list_vw;
    ListView list_vw_r;
    public static Context home;

    private Animation ani_left_rpage = null;
    private Animation ani_right_rpage = null;

    private Animation ani_left_lpage = null;
    private Animation ani_right_lpage = null;

    private Button board_btn;

    String log_id;

    private ArrayAdapter<String> adapter_home_l, adapter_home_r;
    List<Object> Array_home_l = new ArrayList<Object>();
    List<Object> Array_home_r = new ArrayList<Object>();

    private FirebaseDatabase mDatabase_1;
    private DatabaseReference mReference;
    private DatabaseReference mDatabase;

    private ChildEventListener mChild;
    int file_num = 0;

    StorageReference storageRef;

    VideoView video_play;

    VideoView videoView2;
    Uri uri_v;

    private View header;

    //태그
    private PendingIntent pendingIntent;
    private static String tagNum = null;
    private NfcAdapter nfc;
    private TextView tagDesc;
    boolean tag_d = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        btn_message = findViewById(R.id.message_btn);
        message_View = findViewById(R.id.m_message_view);
        message_text = findViewById(R.id.m_message_text);

        //태그
        tagDesc = (TextView) findViewById(R.id.tagDesc);
        nfc = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final SlidingAnimationListener listener = new SlidingAnimationListener(rr_page, ll_page, btn_mypage);
        ani_left_rpage.setAnimationListener(listener);
        ani_right_rpage.setAnimationListener(listener);

        list_vw = (ListView) findViewById(R.id.list_vw);
        list_vw_r = (ListView) findViewById(R.id.list_vw_r);

        adapter_home_l = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        adapter_home_r = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        list_vw.setAdapter(adapter_home_l);
        list_vw_r.setAdapter(adapter_home_r);
        log_id = ((MainActivity)MainActivity.main).mEmailText.getText().toString().trim();

        initDatabase();

        //비디오 등록

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://exhibition-9394f.appspot.com").child("video/" + file_num);


        mReference = mDatabase_1.getReference("message");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter_home_l.clear();

                title_and_email title_and_email = new title_and_email();

                for(DataSnapshot messageData : snapshot.getChildren()) {

                    if(messageData.getValue(title_and_email.class).title != null) {

                        String msg2 = messageData.getValue(title_and_email.class).title;
                        Array_home_l.add(msg2);
                        adapter_home_l.add(msg2);
                    }
                    if(messageData.getValue(title_and_email.class).email.equals(log_id)){
                        String my_project_title = messageData.getValue(title_and_email.class).title;
                        Array_home_r.add(my_project_title);
                        adapter_home_r.add(my_project_title);

                        adapter_home_r.notifyDataSetChanged();
                        list_vw_r.setSelection(adapter_home_r.getCount() - 1);
                    }
                }
                adapter_home_l.notifyDataSetChanged();
                list_vw.setSelection(adapter_home_l.getCount() - 1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //list
        board_btn = findViewById(R.id.home_btn_1);

        home = this;

        board_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, BoardActivity.class);
                startActivity(intent);

            }
        });


        btn_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_key_r == 0) {
                    if (btn_key_l == 1) {
                        ll_page.startAnimation(ani_left_lpage);
                        ll_page.setVisibility(View.GONE);
                        btn_key_l = 0;
                    }
                    rr_page.setVisibility(View.VISIBLE);
                    rr_page.startAnimation(ani_left_rpage);

                    btn_key_l = 0;
                    btn_key_h = 0;
                    btn_key_r = 1;

//                    ll_page.startAnimation(ani_right);
                } else if (btn_key_r == 1) {
                    rr_page.startAnimation(ani_right_rpage);
                    rr_page.setVisibility(View.GONE);
                    btn_key_r = 0;
                }
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (btn_key_l == 1) {

                        ll_page.startAnimation(ani_left_lpage);
                        ll_page.setVisibility(View.GONE);
                    }
                    if (btn_key_r == 1) {
                        rr_page.startAnimation(ani_right_rpage);
                        rr_page.setVisibility(View.GONE);
                    }
                    btn_key_l = 0;
                    btn_key_r = 0;
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_key_l == 0) {
                    if (btn_key_r == 1) {
                        rr_page.startAnimation(ani_right_rpage);
                        rr_page.setVisibility(View.GONE);
                        btn_key_r = 0;
                    }
                    ll_page.setVisibility(View.VISIBLE);
                    ll_page.startAnimation(ani_right_lpage);

                    btn_key_l = 1;
                    btn_key_h = 0;
                    btn_key_r = 0;

                } else if (btn_key_l == 1) {
                    ll_page.startAnimation(ani_left_lpage);
                    ll_page.setVisibility(View.GONE);
                    btn_key_l = 0;
                }
            }

        });


        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(messageTriger == 0) {
                    message_View.setVisibility(View.VISIBLE);
                    message_text.setVisibility(View.VISIBLE);
                    messageTriger = 1;
                }
                else if(messageTriger == 1){
                    message_View.setVisibility(View.GONE);
                    message_text.setVisibility(View.GONE);
                    messageTriger = 0;
                }

            }
        });


        //리스트뷰 이벤트
        list_vw.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File_play(i);
            }
        });

    }


    private void initView() {
        rr_page = findViewById(R.id.rr_page);
        ll_page = findViewById(R.id.ll_page);
        btn_mypage = findViewById(R.id.mypage_btn);
        btn_home = findViewById(R.id.home_btn);
        btn_menu = findViewById(R.id.menu_btn);
        ani_left_rpage = AnimationUtils.loadAnimation(this, R.anim.translate_left_rpage);
        ani_right_rpage = AnimationUtils.loadAnimation(this, R.anim.translate_right_rpage);

        ani_left_lpage = AnimationUtils.loadAnimation(this, R.anim.translate_left_lpage);
        ani_right_lpage = AnimationUtils.loadAnimation(this, R.anim.translate_right_lpage);

    }

    private void initDatabase() {

        mDatabase_1 = FirebaseDatabase.getInstance();

        mReference = mDatabase_1.getReference("log");
        mReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReference.removeEventListener(mChild);
    }


    // 태그
    //태그
    protected void onPause() {
        if (nfc != null) {
            nfc.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (nfc != null) {
            nfc.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            byte[] tagId = tag.getId();
            tagDesc.setText("TagID: " + byteArrayToHexString(tagId));
            tagNum = byteArrayToHexString(tagId);
            writeNewTag(tagNum);
        }
    }


// 해시테이블로 NFC 코드 구현

    public static String byteArrayToHexString(byte[] b) {
        int len = b.length;
        String data = new String();

        for (int i = 0; i < len; i++){
            data += Integer.toHexString((b[i] >> 4) & 0xf);
            data += Integer.toHexString(b[i] & 0xf);
        }
        return data;
    }


    private void writeNewUser(String tag) {
        Nfc_tag tag1 = new Nfc_tag(tag);
        mDatabase.child(mDatabase.push().getKey()).setValue(tag);
    }


    private void writeNewTag(final String nfctag) {
//        writeNewUser(nfctag);

//        String key = mDatabase.child("NFC_cord").push().getKey();
//        Nfc_tag post = new Nfc_tag(username, title);
//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/NFC_cord/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);

        //if (!data.getValue(Post.class).author.equals(username))

// NFC 태그 검사

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                int booth_num = 0;
                tag_d = true;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //If email exists then toast shows else store the data on new key
                    booth_num ++;

                    Nfc_tag a = new Nfc_tag();

                    data.getValue();

                    if(data.getValue(Nfc_tag.class).nfctag == null){
                        //Toast.makeText(MainActivity.this, "에러", Toast.LENGTH_SHORT).show();
                    }

                    else if (!data.getValue(Nfc_tag.class).nfctag.equals(nfctag)) {

                    } else {
                        tag_d = false;
                        //Toast.makeText(MainActivity.this, "E-mail already exists.", Toast.LENGTH_SHORT).show();

                        if(data.getValue(Nfc_tag.class).booth_num == 1){

                            File_play(data.getValue(Nfc_tag.class).booth_num - 1);

                        }
                        if(data.getValue(Nfc_tag.class).booth_num == 2){
                            File_play(data.getValue(Nfc_tag.class).booth_num - 1);
                        }
                    }
                }
                if (tag_d == true) {
                    //mDatabase.child(mDatabase.push().getKey()).setValue(new Nfc_tag(nfctag, booth_num));
                    Toast.makeText(HomeActivity.this, "등록되지 않은 태그입니다 관리자에게 문의 하세요.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
            }
        });
    }

    public void File_play(int filenum){

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReferenceFromUrl("gs://exhibition-9394f.appspot.com").child("video/" + filenum).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Intent intent = new Intent(HomeActivity.this, VideoActivity.class);

                String file_uri = uri.toString();

                intent.putExtra("Uri", file_uri);

                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }
}