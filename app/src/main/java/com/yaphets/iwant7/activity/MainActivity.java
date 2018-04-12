package com.yaphets.iwant7.activity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yaphets.iwant7.R;
import com.yaphets.iwant7.application.Iwant7App;
import com.yaphets.iwant7.entity.Block.Block;
import com.yaphets.iwant7.entity.Block.MovingBlock;
import com.yaphets.iwant7.service.GameThread;
import com.yaphets.iwant7.service.Signal;
import com.yaphets.iwant7.service.game.Game;
import com.yaphets.iwant7.util.Util;
import com.yaphets.iwant7.util.iImgView;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private Iwant7App m_app;
    private float current_density_times;

    private static final int FLING_MIN_DISTANCE = 20;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度
    private GestureDetector m_gestureDetector;
    private AssetManager m_AssetManager;
    /**
     * UI
     */
    private RelativeLayout m_root;
    private ImageButton m_startgame;
    private ImageView m_logo;
    private Button m_setting;
    private ImageView m_gamearea;

    private ImageView standPoint;
    private ImageView[] m_creatingBlock;
    private Vector<Vector<iImgView>> imgVector;

    private ViewPropertyAnimator animator0;
    private ViewPropertyAnimator animator1;
    /**
     * 逻辑层
     */
    private Game gameService;
    private Thread uiThread;
    //private Thread workThread;
    private Lock lock;
    private Condition changeUi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_app = (Iwant7App) getApplication();
        m_gestureDetector = new GestureDetector(this,new gameGestureListener());
        m_AssetManager = getAssets();

        initialization();
    }

    /**
     * 初始化操作
     */
    private void initialization() {
        gameService = new Game();
        uiThread = Thread.currentThread();
        lock = new ReentrantLock();
        changeUi = lock.newCondition();
        initAppearance();
        initFindView();
        initListener();
        imgVector = new Vector<>();
        for (int i = 0; i < Game.sizeX; i++) {
            Vector<iImgView> temp = new Vector<>();
            imgVector.add(temp);
        }
    }

    private void initAppearance() {
        if (getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_XXXHIGH) {
            current_density_times = Iwant7App.xxxhdpi_density;
        } else {
            current_density_times = Iwant7App.xxhdpi_density;
        }

        Util.BlockSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(68*current_density_times), getResources().getDisplayMetrics());
    }

    private void initFindView() {
        m_root = (RelativeLayout) findViewById(R.id.ma_root);
        m_startgame = (ImageButton) findViewById(R.id.btn_startgame);
        m_logo = (ImageView) findViewById(R.id.ic_logo);
        m_setting = (Button) findViewById(R.id.btn_setting);
        m_gamearea = (ImageView) findViewById(R.id.bg_gamearea);

        m_creatingBlock = new ImageView[2];

        standPoint = (ImageView) findViewById(R.id.testimage);

        /*ViewGroup.LayoutParams tlp = standPoint.getLayoutParams();
        ViewGroup.LayoutParams llp = m_gamearea.getLayoutParams();
        tlp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(68*current_density_times), getResources().getDisplayMetrics());
        tlp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(68*current_density_times), getResources().getDisplayMetrics());
        llp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(435*current_density_times), getResources().getDisplayMetrics());
        llp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Math.round(630*current_density_times), getResources().getDisplayMetrics());
        standPoint.setLayoutParams(tlp);
        m_gamearea.setLayoutParams(llp);*/
        /*standPoint.setX(m_gamearea.getX());
        standPoint.setY(m_gamearea.getY());*/
    }

    private void initListener() {
        m_startgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilInit();

                createBlock();
                gameService.gStatus = Game.Status.Gaming;
                //设置分数

                //设置记录

                //分数和记录的UI显示

                m_logo.setVisibility(View.INVISIBLE);
                v.setVisibility(View.INVISIBLE);

                //设置影子

            }
        });

        m_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*WindowManager windowManager = getWindowManager();
                Log.d("   MainActivity:  ", "windows width: " + windowManager.getDefaultDisplay().getWidth() + "  windows height: "
                        +  windowManager.getDefaultDisplay().getHeight());

                ViewGroup.LayoutParams llp = m_gamearea.getLayoutParams();
                Log.d("   MainActivity:  ", "gamearea width: " + llp.width + "  windows height: "
                        +  llp.height);

                Log.d("   MainActivity:  ", "DisplayMetrics ===> width: " + v.getContext().getResources().getDisplayMetrics().widthPixels + "  height: "
                        +  v.getContext().getResources().getDisplayMetrics().heightPixels);*/

                ViewGroup.LayoutParams tlp = standPoint.getLayoutParams();
                Log.d("   MainActivity:  ", "block width: " + tlp.width + "  block height: "
                        +  tlp.height);
                Log.d("   MainActivity:  ", "density: " + getResources().getDisplayMetrics().densityDpi);
                /*int[] temp = Util.getAssetsWidthHeight(m_AssetManager, Util.getNumCanonicalPath(String.valueOf(3)));
                Log.d("   MainActivity:  ", "number block px ====> width: " + temp[0] + "  height: "
                        +  temp[1]);

                int[] temp2 = Util.getResourceWidthHeight(getResources(), R.drawable.ma_btn_theme);
                Log.d("   MainActivity:  ", "resource px ====> width: " + temp2[0] + "  height: "
                        +  temp2[1]);*/
            }
        });

        m_gamearea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gameService.gStatus == Game.Status.Waitting)
                    return false;
                return m_gestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * Util 常量初始化设置
     */

    private void utilInit() {
        Util.Edge_Up = (int)(standPoint.getY() - standPoint.getHeight());
        Util.Edge_Down = (int)(standPoint.getY() + standPoint.getHeight()*8);

        Util.Edge_Left = (int) (standPoint.getX() - standPoint.getWidth()*2);
        Util.Edge_Right = (int) (standPoint.getX() + standPoint.getWidth()*4);
        /*Log.d("Util.Edge_Left: ", "  " + Util.Edge_Left);
        Log.d("Util.Edge_Right: ", "  " + Util.Edge_Right);*/
    }

    /**
     * 界面处理
     */

    private Handler m_Handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            lock.lock();
            try {
                switch (msg.what) {
                    case Signal.CreateBlock:
                        createBlock();
                        gameService.printMap();
                        //System.out.println(Thread.currentThread().getId());
                        //System.out.println("UI Thread Priority: "+uiThread.getPriority());
                        new GameThread(5, new Runnable() {
                            @Override
                            public void run() {
                                gameService.iWant7(m_Handler, uiThread);
                            }
                        }).start();

                        break;
                    case Signal.MixLabelReady:
                        //mixImage();
                        new GameThread(5, new Runnable() {
                            @Override
                            public void run() {
                                mixImage();
                            }
                        }).start();
                        //uiThread.notify();
                        break;
                    case Signal.Clear7Ready:
                        new GameThread(5, new Runnable() {
                            @Override
                            public void run() {
                                clear7Image();
                            }
                        }).start();
                        //uiThread.notify();
                        break;
                    case Signal.GameOver:
                        break;
                    case Signal.Finish:
                        break;

                    case Signal.FlashImage:
                        Bitmap bm = (Bitmap) msg.obj;
                        ImageView imgV = getPair(msg.arg1, msg.arg2).second;
                        imgV.setImageBitmap(bm);
                        //uiThread.notify();
                        changeUi.signal();
                        break;
                    case Signal.DeleteImage:
                        ImageView temp = getPair(msg.arg1, msg.arg2).second;
                        temp.setVisibility(View.GONE);
                        m_root.removeView(temp);
                        //uiThread.notify();
                        changeUi.signal();
                        break;
                    case Signal.AlterPoint:
                        fallImage(msg.arg1, msg.arg2, (ImageView) msg.obj);
                        //uiThread.notify();
                        changeUi.signal();
                        break;
                }
            } finally {
                lock.unlock();
            }
            //super.handleMessage(msg);
        }
    };

    private void createBlock() {
        m_creatingBlock[0] = new ImageView(MainActivity.this);
        m_creatingBlock[1] = new ImageView(MainActivity.this);
        animator0 = m_creatingBlock[0].animate().setDuration(80);
        animator1 = m_creatingBlock[1].animate().setDuration(80);

        gameService.randomBlock();

        //Log.d("movingBlock[0]:", "x:" + gameService.movingBlock[0].m_X + " y:" + gameService.movingBlock[0].m_Y);

        Bitmap bm0 = Util.getAssetsPic(m_AssetManager, Util.getNumCanonicalPath(String.valueOf(gameService.movingBlock[0].m_value)));
        Bitmap bm1 = Util.getAssetsPic(m_AssetManager, Util.getNumCanonicalPath(String.valueOf(gameService.movingBlock[1].m_value)));
        m_creatingBlock[0].setImageBitmap(bm0);
        m_creatingBlock[1].setImageBitmap(bm1);

        m_root.addView(m_creatingBlock[0]);
        m_root.addView(m_creatingBlock[1]);

        ViewGroup.LayoutParams lp0 =  m_creatingBlock[0].getLayoutParams();
        lp0.width = Util.BlockSize;
        lp0.height = lp0.width;
        m_creatingBlock[0].setLayoutParams(lp0);
        m_creatingBlock[0].setX(standPoint.getX());
        m_creatingBlock[0].setY(standPoint.getY());

        ViewGroup.LayoutParams lp1 =  m_creatingBlock[1].getLayoutParams();
        lp1.width = Util.BlockSize;
        lp1.height = lp1.width;
        m_creatingBlock[1].setLayoutParams(lp1);
        m_creatingBlock[1].setX(standPoint.getX() + standPoint.getWidth());
        m_creatingBlock[1].setY(standPoint.getY());

        m_creatingBlock[0].setVisibility(View.VISIBLE);
        m_creatingBlock[1].setVisibility(View.VISIBLE);


    }

    private void mixImage() {
        synchronized (uiThread) {
            try {
                deleteAndConvert7();
                alterImagePiont();
                uiThread.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*deleteAndConvert7();
        alterImagePiont();*/
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alterImagePiont();
            }
        });*/
        /*try {
            uiThread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private void deleteAndConvert7() throws InterruptedException {
        lock.lock();
        try {
            //int count;
            for (int i = 0; i < Game.sizeX; i++) {
                //count = 0;
                for (int j = 0; j < imgVector.get(i).size(); j++) {
                    if (gameService.supportMap[i][j] != getPair(i, j).first) {
                        if (gameService.supportMap[i][j] == -1) {
                        /*ImageView temp = getPair(i, j).second;
                        temp.setVisibility(View.GONE);
                        m_root.removeView(temp);*/

                            //m_Handler.sendMessage( m_Handler.obtainMessage(Signal.DeleteImage, i, j) );
                            Message msg = new Message();
                            msg.what = Signal.DeleteImage;
                            msg.arg1 = i;
                            msg.arg2 = j;
                            m_Handler.sendMessage(msg);
                            changeUi.await();
                            //uiThread.wait();
                            //Thread.currentThread().sleep(20);
                        }

                        if (gameService.supportMap[i][j] == 7) {
                            Bitmap bm = Util.getAssetsPic(m_AssetManager, Util.getNumCanonicalPath("7_" + gameService.getBlockInMap(i, j).grade));
                        /*ImageView imgV = getPair(i, j).second;
                        imgV.setImageBitmap(bm);*/

                            //m_Handler.sendMessage( m_Handler.obtainMessage(Signal.FlashImage, i, j, bm) );
                            Message msg = new Message();
                            msg.what = Signal.FlashImage;
                            msg.arg1 = i;
                            msg.arg2 = j;
                            msg.obj = bm;
                            m_Handler.sendMessage(msg);
                            changeUi.await();
                            //uiThread.wait();
                            //Thread.currentThread().sleep(20);
                        }
                        //gameService.getBlockInMap(i, j).setTag(new Pair<>(gameService.supportMap[i][j], getPair(i, j).second));
                        iImgView var = new iImgView(gameService.supportMap[i][j], getPair(i, j).second);
                        imgVector.get(i).insertElementAt(var, j);
                        imgVector.get(i).remove(j + 1);
                    }
                /*if (gameService.supportMap[i][j] != -1)
                    count++;*/
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void alterImagePiont() throws InterruptedException {
        lock.lock();
        try {
            int count;      //记录第i列有几个还存在的Block
            for (int i = 0; i < Game.sizeX; i++) {
                count = 0;
                for (int j = 0; j < imgVector.get(i).size(); j++) {
                    //如果不是已经清除的方块则下降
                    if (gameService.supportMap[i][j] != -1) {
                        ImageView var = getPair(i, j).second;
                        //fallImage(i, count, var);
                        //m_Handler.sendMessage( m_Handler.obtainMessage(Signal.AlterPoint, i, count, var));
                        Message msg = new Message();
                        msg.what = Signal.AlterPoint;
                        msg.arg1 = i;
                        msg.arg2 = count;
                        msg.obj = var;
                        m_Handler.sendMessage(msg);
                        changeUi.await();
                        //uiThread.wait();
                        count++;
                    }

                }
            }

            for (int i = 0; i < Game.sizeX; i++) {
                for (int j = 0; j < imgVector.get(i).size(); j++) {
                    if (getPair(i, j).first == -1) {
                        imgVector.get(i).remove(j);
                        j--;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void clear7Image() {
        synchronized (uiThread) {
            try {
                deleteAndConvertScore();
                alterImagePiont();
                uiThread.notify();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //设置分数 TODO
    }

    private void deleteAndConvertScore() throws InterruptedException {
        lock.lock();
        try {
            for (int i = 0; i < Game.sizeX; i++) {
                for (int j = 0; j < imgVector.get(i).size(); j++) {
                    if (gameService.supportMap[i][j] != getPair(i, j).first) {
                        if (gameService.supportMap[i][j] == -1) {
                            Bitmap bm = Util.getAssetsPic(m_AssetManager, Util.getScoreCanonicalPath(gameService.countScoreUi++));
                        /*ImageView imgV = getPair(i, j).second;
                        imgV.setImageBitmap(bm);*/
                            //imgV.invalidate();
                            //m_Handler.sendMessage( m_Handler.obtainMessage(Signal.FlashImage, i, j, bm));
                            Message msg = new Message();
                            msg.what = Signal.FlashImage;
                            msg.arg1 = i;
                            msg.arg2 = j;
                            msg.obj = bm;
                            m_Handler.sendMessage(msg);
                            changeUi.await();
                            //uiThread.wait();
                        }

                        //gameService.getBlockInMap(i, j).setTag(new Pair<>(gameService.supportMap[i][j], getPair(i, j).second));
                        iImgView var = new iImgView(gameService.supportMap[i][j], getPair(i, j).second);
                        imgVector.get(i).insertElementAt(var, j);
                        imgVector.get(i).remove(j + 1);
                    }
                }
            }
        /*try {
            uiThread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
            //Thread.currentThread().sleep(100);

            for (int i = 0; i < Game.sizeX; i++) {
                for (int j = 0; j < imgVector.get(i).size(); j++) {
                    if (getPair(i, j).first == -1) {
                    /*ImageView var = getPair(i, j).second;
                    var.setVisibility(View.GONE);
                    m_root.removeView(var);*/
                        //m_Handler.sendMessage( m_Handler.obtainMessage(Signal.DeleteImage, i, j) );
                        Message msg = new Message();
                        msg.what = Signal.DeleteImage;
                        msg.arg1 = i;
                        msg.arg2 = j;
                        m_Handler.sendMessage(msg);
                        //uiThread.wait();
                        changeUi.await();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 内部类 gameGestureListener
     * 处理手势的操作
     */
    private class gameGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent ev) {
            //Log.d("onSingleTapUp",ev.toString());
            onChangeTap();
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
                onFlingDown();

            } else if (directionY < 0
                    && (Math.abs(directionY) > Math.abs(directionX))
                    && Math.abs(directionY) > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                //向上滑动


            } else if (directionX > 0
                    && (Math.abs(directionX) > Math.abs(directionY))
                    && Math.abs(directionX) > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //向右滑动
                onFlingRight();

            } else if (directionX < 0
                    && (Math.abs(directionX) > Math.abs(directionY))
                    && Math.abs(directionX) > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                //向左滑动
                onFlingLeft();

            }

            return true;
        }
    }

    /**
     * 手势处理函数
     */
    private void onChangeTap() {
        //移动方块变形
        int oldx0 = gameService.movingBlock[0].m_X;
        int oldy0 = gameService.movingBlock[0].m_Y;
        int oldx1 = gameService.movingBlock[1].m_X;
        int oldy1 = gameService.movingBlock[1].m_Y;
        gameService.rotateBlock();

        //Log.d("movingBlock[0]:", "x:" + gameService.movingBlock[0].m_X + " y:" + gameService.movingBlock[0].m_Y);

        m_creatingBlock[0].setX(m_creatingBlock[0].getX() + m_creatingBlock[0].getWidth()*(gameService.movingBlock[0].m_X-oldx0) );
        m_creatingBlock[0].setY(m_creatingBlock[0].getY() - m_creatingBlock[0].getWidth()*(gameService.movingBlock[0].m_Y-oldy0) );

        m_creatingBlock[1].setX(m_creatingBlock[1].getX() + m_creatingBlock[1].getWidth()*(gameService.movingBlock[1].m_X-oldx1) );
        m_creatingBlock[1].setY(m_creatingBlock[1].getY() - m_creatingBlock[1].getWidth()*(gameService.movingBlock[1].m_Y-oldy1) );

        //影子位置重设
    }

    private void onFlingLeft() {
        //判断动画冲突

        //获取左下的block
        MovingBlock temp1 = MovingBlock.onLeftDown(gameService.movingBlock);

        if (temp1.m_X == 0)
            return;

        //算法block移动
        gameService.movingBlock[0].m_X--;
        gameService.movingBlock[1].m_X--;

        /*动画block移动*/
        /*animator0.x(m_creatingBlock[0].getX() - m_creatingBlock[1].getWidth());
        animator1.x(m_creatingBlock[1].getX() - m_creatingBlock[1].getWidth());*/

        //Log.d("animator Duration", " " + animator0.getDuration());

        animator0.x(Util.Edge_Left + m_creatingBlock[0].getWidth()*gameService.movingBlock[0].m_X);
        animator1.x(Util.Edge_Left + m_creatingBlock[1].getWidth()*gameService.movingBlock[1].m_X);
    }

    private void onFlingRight() {
        //判断动画冲突

        //获取非左下的block
        MovingBlock temp1 = MovingBlock.no_OnLeftDown(gameService.movingBlock);

        if (temp1.m_X+1 >= Game.sizeX)
            return;

        //算法block移动
        gameService.movingBlock[0].m_X++;
        gameService.movingBlock[1].m_X++;

        /*动画block移动*/
        /*animator0.x(m_creatingBlock[0].getX() + m_creatingBlock[0].getWidth());
        animator1.x(m_creatingBlock[1].getX() + m_creatingBlock[1].getWidth());*/

        animator0.x(Util.Edge_Left + m_creatingBlock[0].getWidth()*gameService.movingBlock[0].m_X);
        animator1.x(Util.Edge_Left + m_creatingBlock[1].getWidth()*gameService.movingBlock[1].m_X);
    }

    private void onFlingDown() {
        //隐藏影子

        /*动画block移动*/

        MovingBlock temp0 = MovingBlock.onLeftDown(gameService.movingBlock);
        MovingBlock temp1 = MovingBlock.no_OnLeftDown(gameService.movingBlock);

        ViewPropertyAnimator animatorT0;
        ViewPropertyAnimator animatorT1;



        //推入Block0
        Block blockToMap0 = new Block(temp0.m_value);
        blockToMap0.priority++;

        gameService.push_in_lineX(temp0.m_X, blockToMap0);

        int time0 = Util.Ful_Speed - Util.Per_Speed*(gameService.getX_size(temp0.m_X));
        int dis0 = Util.Edge_Down - Util.BlockSize*(gameService.getX_size(temp0.m_X));

        if (temp0 == gameService.movingBlock[0]) {
            animatorT0 = animator0;
            animatorT1 = animator1;
            /*blockToMap0.setTag( new Pair<>(temp0.m_value, m_creatingBlock[0]) );
            blockToMap1.setTag( new Pair<>(temp1.m_value, m_creatingBlock[1]) );*/
            pushPair(temp0.m_X, temp0.m_value, m_creatingBlock[0]);
            pushPair(temp1.m_X, temp1.m_value, m_creatingBlock[1]);
        }
        else {
            animatorT0 = animator1;
            animatorT1 = animator0;
            /*blockToMap0.setTag( new Pair<>(temp1.m_value, m_creatingBlock[1]) );
            blockToMap1.setTag( new Pair<>(temp0.m_value, m_creatingBlock[0]) );*/
            pushPair(temp0.m_X, temp0.m_value, m_creatingBlock[1]);
            pushPair(temp1.m_X, temp1.m_value, m_creatingBlock[0]);
        }

        //推入Block1
        Block blockToMap1 = new Block(temp1.m_value);
        blockToMap1.priority++;

        gameService.push_in_lineX(temp1.m_X, blockToMap1);

        int time1 = Util.Ful_Speed - Util.Per_Speed*(gameService.getX_size(temp1.m_X));
        int dis1 = Util.Edge_Down - Util.BlockSize*(gameService.getX_size(temp1.m_X));

        if (temp0.m_X == temp1.m_X) {
            time1 = time0;
        }



        animatorT0.y(dis0).setDuration(time0);
        animatorT1.y(dis1).setDuration(time1);

        /*Log.d("animator ", "time0:" + time0 + " time1:" + time1
                + "\n interpolator:  " + "\npolator0:" + animatorT0.getInterpolator() + "\npolator1:" + animatorT1.getInterpolator());*/

        Message msg = m_Handler.obtainMessage(Signal.CreateBlock);
        m_Handler.sendMessageDelayed(msg, time0>time1?time0+10:time1+10);
    }

    /**
     * img管理
     */
    private iImgView getPair(int i, int j) {
        return imgVector.get(i).get(j);
    }
    private void pushPair(int x, int value, ImageView img) {
        iImgView temp = new iImgView(value, img);
        imgVector.get(x).add(temp);
    }
    private void fallImage(int x, int y, ImageView img) {
        synchronized (uiThread) {
            //如果在本来的点上，则不移动这块label
            boolean ok = img.getY() == Util.Edge_Down - (y + 1) * Util.BlockSize;
            if (ok)
                return;

            int time = (int) ((Util.Edge_Down - (y + 1) * Util.BlockSize - img.getY()) / img.getHeight() * 20);
            img.animate().y(Util.Edge_Down - (y + 1) * Util.BlockSize).setDuration(time).start();
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
