package com.example.mywheel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    //設定一個時間常量，此常量有兩個作用，1.圓燈檢視顯示與隱藏中間的切換時間；2.指標轉一圈所需要的時間，現設定為500毫秒
    private static final long ONE_WHEEL_TIME = 500;
    //記錄圓燈檢視是否顯示的布林常量
    private boolean lightsOn = true;
    //開始轉動時候的角度，初始值為0
    private int startDegree = 0;

    private ImageView lightIv;
    private ImageView pointIv;
    private ImageView wheelIv;

    //指標轉圈圈數資料來源
    private int[] laps = { 5, 7, 10, 15 };
    //指標所指向的角度資料來源，因為有6個選項，所有此處是6個值
    private int[] angles = { 0, 60, 120, 180, 240, 300 };
    //轉盤內容陣列
    private String[] lotteryStr = { "謝謝參與", "OPPO MP3", "索尼PSP", "5元紅包",
            "10元紅包", "DNF錢包", };

    //子執行緒與UI執行緒通訊的handler物件
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (lightsOn) {
                        // 設定lightIv不可見
                        lightIv.setVisibility(View.INVISIBLE);
                        lightsOn = false;
                    } else {
                        // 設定lightIv可見
                        lightIv.setVisibility(View.VISIBLE);
                        lightsOn = true;
                    }
                    break;

                default:
                    break;
            }
        };

    };

    //監聽動畫狀態的監聽器
    private Animation.AnimationListener al = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            String name = lotteryStr[startDegree % 360 / 60];
            Toast.makeText(MainActivity.this, name, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        flashLights();

        pointIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int lap = laps[(int) (Math.random() * 4)];
                int angle = angles[(int) (Math.random() * 6)];
                //每次轉圈角度增量
                int increaseDegree = lap * 360 + angle;
                //初始化旋轉動畫，後面的四個引數是用來設定以自己的中心點為圓心轉圈
                RotateAnimation rotateAnimation = new RotateAnimation(
                        startDegree, startDegree + increaseDegree,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                //將最後的角度賦值給startDegree作為下次轉圈的初始角度
                startDegree += increaseDegree;
                //計算動畫播放總時間
                long time = (lap + angle / 360) * ONE_WHEEL_TIME;
                //設定動畫播放時間
                rotateAnimation.setDuration(time);
                //設定動畫播放完後，停留在最後一幀畫面上
                rotateAnimation.setFillAfter(true);
                //設定動畫的加速行為，是先加速後減速
                rotateAnimation.setInterpolator(MainActivity.this,
                        android.R.anim.accelerate_decelerate_interpolator);
                //設定動畫的監聽器
                rotateAnimation.setAnimationListener(al);
                //開始播放動畫
                pointIv.startAnimation(rotateAnimation);
            }
        });

    }

    private void setupViews(){
        lightIv = (ImageView) findViewById(R.id.light);
        pointIv = (ImageView) findViewById(R.id.point);
        wheelIv = (ImageView) findViewById(R.id.main_wheel);
    }

    //控制燈圈動畫的方法
    private void flashLights() {

        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                // 向UI執行緒傳送訊息
                mHandler.sendEmptyMessage(0);

            }
        };

        // 每隔ONE_WHEEL_TIME毫秒執行tt物件的run方法
        timer.schedule(tt, 0, ONE_WHEEL_TIME);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
}