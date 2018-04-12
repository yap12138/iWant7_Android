package com.yaphets.iwant7.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yaphets.iwant7.R;

/**
 * Created by Yaphets on 2017/8/30.
 */
public class testActivity extends AppCompatActivity {

    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度
    private GestureDetector m_gestureDetector;

    private Button m_setting;
    private ImageView m_gamearea;

    private ImageView test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        m_gestureDetector = new GestureDetector(this,new gameGestureListener());

        initFindView();
        initListener();
    }

    private void initFindView() {

        m_setting = (Button) findViewById(R.id.btn_setting);
        m_gamearea = (ImageView) findViewById(R.id.bg_gamearea);


        test = (ImageView) findViewById(R.id.testimage);
        /*ViewGroup.LayoutParams tlp = test.getLayoutParams();
        ViewGroup.LayoutParams llp = m_gamearea.getLayoutParams();
        tlp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68, getResources().getDisplayMetrics());
        tlp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 68, getResources().getDisplayMetrics());
        llp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 435, getResources().getDisplayMetrics());
        llp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 630, getResources().getDisplayMetrics());
        test.setLayoutParams(tlp);
        m_gamearea.setLayoutParams(llp);*/
        /*test.setX(m_gamearea.getX() +26);
        test.setY(m_gamearea.getY() +48);*/
    }

    private void initListener() {

        m_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*test.setTranslationX(test.getWidth() + test.getTranslationX());
                test.setTranslationY(test.getHeight() + test.getTranslationY());*/
                WindowManager windowManager = getWindowManager();

                Log.d("   MainActivity:  ", "windows width: " + windowManager.getDefaultDisplay().getWidth() + "  windows height: "
                        +  windowManager.getDefaultDisplay().getHeight());
                ViewGroup.LayoutParams llp = m_gamearea.getLayoutParams();
                Log.d("   MainActivity:  ", "gamearea width: " + llp.width + "  windows height: "
                        +  llp.height);
                Log.d("   MainActivity:  ", "DisplayMetrics ===> width: " + v.getContext().getResources().getDisplayMetrics().widthPixels + "  height: "
                        +  v.getContext().getResources().getDisplayMetrics().heightPixels);
                ViewGroup.LayoutParams tlp = test.getLayoutParams();
                Log.d("   MainActivity:  ", "gamearea width: " + tlp.width + "  windows height: "
                        +  tlp.height);
            }
        });

        m_gamearea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return m_gestureDetector.onTouchEvent(event);
            }
        });
    }

    private class gameGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            //Log.d("onSingleTapUp",ev.toString());
            return true;
        }

        @Override
        public void onShowPress(MotionEvent ev) {
            //Log.d("onShowPress",ev.toString());
        }

        @Override
        public void onLongPress(MotionEvent ev) {
            //Log.d("onLongPress",ev.toString());
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.d("onScroll",e1.toString());
            return true;
        }

        @Override
        public boolean onDown(MotionEvent ev) {
            //Log.d("onDownd",ev.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //Log.d("d",e1.toString());
            //Log.d("e2",e2.toString());

            float directionX = e2.getX() - e1.getX();
            float directionY = e2.getY() - e1.getY();

            if (directionY > 0
                    && (Math.abs(directionY) > Math.abs(directionX))
                    && Math.abs(directionY) > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                //向下滑動
                test.setY(test.getY() + test.getWidth());

            } else if (directionY < 0
                    && (Math.abs(directionY) > Math.abs(directionX))
                    && Math.abs(directionY) > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                //向上滑动
                test.setY(test.getY() - test.getWidth());

            } else if (directionX > 0
                    && (Math.abs(directionX) > Math.abs(directionY))
                    && Math.abs(directionX) > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //向右滑动
                test.setX(test.getX() + test.getWidth());

            } else if (directionX < 0
                    && (Math.abs(directionX) > Math.abs(directionY))
                    && Math.abs(directionX) > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //向左滑动
                test.setX(test.getX() - test.getWidth());

            }

            return true;
        }
    }
}
