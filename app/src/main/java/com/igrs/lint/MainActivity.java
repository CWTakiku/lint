package com.igrs.lint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("test","中文");
        System.out.println("中文");
        String host = "https://www.baidu.com";
        String url = "http://www.baidu.com";

        if (false){
            throw new RuntimeException("http://www.baidu.com");
        }


        Random random1 = new Random();
        random1.nextInt();

        if (false){
            throw new RuntimeException("将无法为回溯、分页管理保存文件");
        }


       String a=  "测试";
    }
    public void random(){

    }
}