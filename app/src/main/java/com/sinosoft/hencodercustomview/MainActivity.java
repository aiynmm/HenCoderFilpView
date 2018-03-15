package com.sinosoft.hencodercustomview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://101.200.46.135/railtransit/dataController.do?getUserAll";

    TextView name1;
    TextView name2;

    private FlipMapView flipMapView;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*name1 = (TextView) findViewById(R.id.name1);

        // 01. 定义okhttp
        OkHttpClient okHttpClient_get = new OkHttpClient();
        // 02.请求体
        Request request = new Request.Builder()
                .get()//get请求方式
                .url(URL)//网址
                .build();

        okHttpClient_get.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                name1.setText(response.body().string());
            }
        });*/

        flipMapView = (FlipMapView) findViewById(R.id.flipview);

        //绕Y轴3D旋转45度
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(flipMapView, "degreeY", 0, -45);
        animator1.setDuration(1000);
        animator1.setStartDelay(500);

        //绕Z轴3D旋转270度
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(flipMapView, "degreeZ", 0, 270);
        animator2.setDuration(800);
        animator2.setStartDelay(500);

        //不变的那一半（上半部分）绕Y轴旋转30度（注意，这里canvas已经旋转了270度，计算第三个动效参数时要注意）
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(flipMapView, "fixDegreeY", 0, 30);
        animator3.setDuration(500);
        animator3.setStartDelay(500);

        final AnimatorSet set = new AnimatorSet();
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flipMapView.reset();
                                set.start();
                            }
                        });
                    }
                }, 500);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        /*set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });*/
        set.playSequentially(animator1, animator2, animator3);
        set.start();

    }
}
