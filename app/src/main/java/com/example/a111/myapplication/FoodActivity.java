package com.example.a111.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by 111 on 2017/9/12.
 */

public class FoodActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_introduce);
        WebView webview = (WebView) findViewById(R.id.food_web);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        Intent getfoodno =getIntent();
        int foodno= getfoodno.getIntExtra("food",0);
        Toast.makeText(this, String.valueOf(foodno), Toast.LENGTH_SHORT).show();
        String url="file:///android_asset/"+String.valueOf(foodno+1)+".html";
        webview.loadUrl(url);
    }

}
