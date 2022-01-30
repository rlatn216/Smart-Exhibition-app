package com.example.e_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class BoardActivity extends AppCompatActivity {

    WebView webvw;
    WebSettings webSettings;
    Button project_add_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        webvw = (WebView) findViewById(R.id.webvw);

        project_add_btn = (Button) findViewById(R.id.project_add_btn);

        webvw.setWebViewClient(new WebViewClient());

        webSettings = webvw.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webvw.loadUrl("https://sw.daegu.ac.kr/hakgwa_home/sw/sub.php?mode=view&idx=362040&page=1&menu=page&menu_id=30&search_field=&search_key=&search_cate=");

        project_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardActivity.this, project_addActivity.class);
                startActivity(intent);


            }
        });



    }
}