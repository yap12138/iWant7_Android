package com.yaphets.iwant7.service.game;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.yaphets.iwant7.activity.MainActivity;
import com.yaphets.iwant7.entity.Block.Block;
import com.yaphets.iwant7.entity.Block.MovingBlock;
import com.yaphets.iwant7.service.Signal;
import com.yaphets.iwant7.util.Util;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Created by Yaphets on 2017/9/12.
 */
public class Game {

    public static int sizeX = 6, sizeY = 9;

    public enum Status{
        Waitting,Gaming
    }
    public Status gStatus;

    private int gameScore;
    private int scoreRecord;
    private PriorityQueue<Block> mixQueue;
    private Vector<Block> scoreList;
    private Vector<Block> tempList;

    private int countScore;
    public int countScoreUi;

    public MovingBlock[] movingBlock;
    public Vector<Vector<Block>> map;
    public int[][] supportMap;

    public Game() {
        this.gStatus = Status.Waitting;
        this.gameScore = 0;

        movingBlock = new MovingBlock[2];
        movingBlock[0] = new MovingBlock();
        movingBlock[1] = new MovingBlock();

        initMaps();

        this.map.ensureCapacity(sizeX);

        //TODO
        //文件读写
    }

    /**
     * 地图操作
     */

    private void initMaps() {
        map = new Vector<>();
        for (int i = 0; i < sizeX; i++) {
            Vector<Block> var = new Vector<>();
            map.add(var);
        }

        supportMap = new int[sizeX][sizeY];
        memsetSM(-1);

        mixQueue = new PriorityQueue<>(priorComparator);
        tempList = new Vector<>();
        scoreList = new Vector<>();
    }



    public int getX_size(int X) {
        return this.map.get(X).size();
    }

    public void push_in_lineX(int X, Block newBlock) {
        this.supportMap[X][getX_size(X)] = newBlock.m_value;
        this.map.get(X).add(newBlock);
    }

    public void erase_in_lineX(int X, int index) {
        this.map.get(X).remove(index);
    }

    public void clearMap_in_lineX(int X) {
        this.map.get(X).clear();
    }

    public void unionMap() {
        //将辅助地图整合到map
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                map.get(i).get(j).clear();
                map.get(i).get(j).m_value = this.supportMap[i][j];
            }
        }
        //将map里值为-1的Block推出
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                if (map.get(i).get(j).m_value == -1) {
                    this.erase_in_lineX(i,j);
                    Log.d("unionMap", "Line  i:" + i + " j:" + j);
                    if (getBlockInMap(i, j) != null)
                        getBlockInMap(i, j).priority++;
                    j--;
                }
            }
        }
        //调整supportMap
        memsetSM(-1);
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                supportMap[i][j] = map.get(i).get(j).m_value;
            }
        }
    }

    /**
     * 核心方法
     */

    public void iWant7(Handler handler, Thread UiThread) {
        //System.out.println(Thread.currentThread().getId());
        synchronized (UiThread) {
            countScore = 1;
            countScoreUi = 1;
            scanMap();
            while ( !(mixQueue.isEmpty()) ) {
                mixBlock(handler);

                handler.sendMessage( handler.obtainMessage(Signal.MixLabelReady) );
                try {
                    UiThread.wait();
                    //UiThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Log.d("iWant7", "first unionMap");
                System.out.println("iWant7"+"first unionMap");
                unionMap();

                scanMap();
                if ( mixQueue.isEmpty() ) {
                    scanScore();
                    computeScore();
                    handler.sendMessage( handler.obtainMessage(Signal.Clear7Ready) );
                    try {
                        UiThread.wait();
                        //UiThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Log.d("iWant7", "second unionMap");
                    System.out.println("iWant7" + "second unionMap");
                    unionMap();
                    scanMap();
                }
            }
            unionMap();

            //判断是否game over
            if ( isGameOver() )
                handler.sendMessage( handler.obtainMessage(Signal.GameOver) );
            else
                handler.sendMessage( handler.obtainMessage(Signal.Finish) );
        }
    }

    //扫描地图
    private void scanMap() {
        //遍历一遍可合成的方块，并且记录进mixQueue等待合成
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                //分别判断上下左右四个方块，能否合成
                if ( isMix(i, j, i+1, j) )
                    map.get(i).get(j).absorb++;
                if ( isMix(i, j, i, j-1) )
                    map.get(i).get(j).absorb++;
                if ( isMix(i, j, i-1, j) )
                    map.get(i).get(j).absorb++;
                if ( isMix(i, j, i, j+1) )
                    map.get(i).get(j).absorb++;

                if (map.get(i).get(j).absorb != 0 && map.get(i).get(j).m_value != 0)   //有可以合成且不等于0则进队列
                {
                    Block temp = map.get(i).get(j);   //副本进队列
                    temp.m_X = i;
                    temp.m_Y = j;
                    mixQueue.offer(temp);
                }
            }
        }
    }

    //合成方块
    private void mixBlock(Handler handler) {
        int i,j;
        int absorb;
        while (!mixQueue.isEmpty())               //获取可以合成的block
        {
            i = mixQueue.peek().m_X;
            j = mixQueue.peek().m_Y;
            absorb = 0;

            if ( supportMap[i][j] != -1 )           //block如果没有被合成，清算可以与多少个block合成7
            {
                if ( isMix(i, j, i+1, j) )
                {
                    supportMap[i+1][j] = -1;
                    absorb++;
                }
                if ( isMix(i, j, i, j-1) )
                {
                    supportMap[i][j-1] = -1;
                    absorb++;
                }
                if ( isMix(i, j, i-1, j) )
                {
                    supportMap[i-1][j] = -1;
                    absorb++;
                }
                if ( isMix(i, j, i, j+1) )
                {
                    supportMap[i][j+1] = -1;
                    absorb++;
                }
                if (absorb != 0)                //可合成数如果不等于0，则合成
                {
                    map.get(i).get(j).grade = map.get(i).get(j).absorb;
                    supportMap[i][j] = 7;
                }
            }

            mixQueue.poll();
        }
    }



    //扫描可否得分
    private void scanScore() {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                if ( supportMap[i][j] != 7 )
                    continue;
                if ( isScore(i,j) >= 3 ) {
                    for (int k = 0; k < tempList.size(); k++)
                        scoreList.add(tempList.get(k));
                    tempList.clear();
                }
                else
                    tempList.clear();
            }
        }
    }

    private void computeScore() {
        for (int i = 0; i < scoreList.size(); i++) {
            this.gameScore += countScore;
            supportMap[scoreList.get(i).m_X][scoreList.get(i).m_Y] = -1;
            map.get(scoreList.get(i).m_X).get(scoreList.get(i).m_Y).clear();
        }
        scoreList.clear();
    }

    /**
     * 逻辑判断
     */
    //判断越界
    private boolean isCrossLine(int x, int y) {
        if (x < 0 || x > 5 || y <0 || y > 7)
            return true;
        else
            return false;
    }

    //判断合成
    private boolean isMix(int x1, int y1, int x2, int y2) {
        if (isCrossLine(x1,y1) || isCrossLine(x2,y2))
            return false;
        if (supportMap[x1][y1] == -1 || supportMap[x2][y2] == -1)
            return false;
        if (supportMap[x1][y1] + supportMap[x2][y2] != 7)
            return false;
        return true;
    }

    //递归判断连接的7超过3个？
    private int isScore(int x, int y) {
        int score = 1;
        if ( !isCrossLine(x,y) ) {
            if (supportMap[x][y] == -1 || supportMap[x][y] != 7)
                return 0;
            if (map.get(x).get(y).haveScan == false) {
                Block temp = map.get(x).get(y);
                temp.m_X = x;
                temp.m_Y = y;
                tempList.add(temp);
                map.get(x).get(y).haveScan = true;
                return score + isScore(x-1, y) + isScore(x+1, y) + isScore(x, y-1) + isScore(x, y+1);
            }
            else
                return 0;
        }
        else
            return 0;
    }

    private boolean isGameOver() {
        for (int i = 0; i < sizeX; i++) {
            if (supportMap[i][7] != -1) {       //在每一列第8个如果还有方块则游戏结束

                if (this.gameScore > this.scoreRecord) {    //当前分数大于最高分，则写入文档

                }
                return true;
            }
        }
        return false;
    }

    /**
     *属性设置
     */

    public int getGameScore() {
        return gameScore;
    }

    public int getScoreRecord() {
        return scoreRecord;
    }

    public void clearScore() {
        this.gameScore = 0;
    }

    /**
     * 对外接口
     */

    //生成随机数字
    public void randomBlock() {
        int x0 = numberRate((int) (Math.random()*100));
        int x1 = numberRate((int) (Math.random()*100));
        while (x0 + x1 == 7)
            x0 = numberRate((int) (Math.random()*100));

        this.movingBlock[0].m_X = 2;
        this.movingBlock[0].m_Y = 0;
        this.movingBlock[0].m_value = x0;
        this.movingBlock[0].on_left_down = true;

        this.movingBlock[1].m_X = 3;
        this.movingBlock[1].m_Y = 0;
        this.movingBlock[1].m_value = x1;
        this.movingBlock[1].on_left_down = false;
    }

    private int numberRate(int num) {
        if (this.gameScore <= 50)
        {
            if (num>=0 && num <=4)
                num = 0;
            else if (num <= 20)
                num = 1;
            else if (num <= 36)
                num = 2;
            else if (num <= 52)
                num = 3;
            else if (num <= 68)
                num = 4;
            else if (num <= 84)
                num = 5;
            else
                num = 6;
        }
        else if (this.gameScore <= 200)
        {
            if (num>=0 && num <=10)
                num = 0;
            else if (num <= 25)
                num = 1;
            else if (num <= 40)
                num = 2;
            else if (num <= 55)
                num = 3;
            else if (num <= 70)
                num = 4;
            else if (num <= 85)
                num = 5;
            else
                num = 6;
        }
        else
        {
            if (num>=0 && num <=16)
                num = 0;
            else if (num <= 30)
                num = 1;
            else if (num <= 44)
                num = 2;
            else if (num <= 59)
                num = 3;
            else if (num <= 73)
                num = 4;
            else if (num <= 86)
                num = 5;
            else
                num = 6;
        }
        return num;
    }
    //旋转方块
    public void rotateBlock() {
        MovingBlock temp1 = MovingBlock.onLeftDown(this.movingBlock);
        MovingBlock temp2 = MovingBlock.no_OnLeftDown(this.movingBlock);

        if (temp1.m_X == temp2.m_X) {
            if (temp1.m_X == 5)
                temp1.m_X--;
            temp2.m_X = temp1.m_X + 1;
            temp2.m_Y = 0;
        }
        else {
            temp1.m_Y = 1;
            temp1.on_left_down = false;

            temp2.m_X = temp1.m_X;
            temp2.on_left_down = true;
        }
    }

    public Block getBlockInMap(int i, int j) {
        if (map.get(i).size() > j)
            return map.get(i).get(j);
        else
            return null;
    }

    /**
     * private util
     */
    private void memsetSM(int setNum) {
        for (int[] row :
                supportMap) {
            for (int i = 0; i < row.length; i++) {
                row[i] = setNum;
            }
        }
    }

    private Comparator<Block> priorComparator = new Comparator<Block>() {
        @Override
        public int compare(Block o1, Block o2) {
            if (o1.absorb == o2.absorb){
                if (o1.priority < o2.priority)
                    return 1;
                else
                    return o1.priority == o2.priority? 0:-1;
            }
            else {
                if (o1.absorb < o2.absorb)
                    return 1;
                else
                    return -1;
            }
        }
    };

    public void printMap() {
        System.out.println("Game.printMap");
        /*for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                System.out.print(getBlockInMap(i,j).m_value + " ");
            }
            System.out.println();
        }*/
        for (int i = 0; i <sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                //System.out.print(supportMap[i][j] + " ");
                System.out.printf("%2d  ", supportMap[i][j]);
            }
            System.out.println();
        }
    }
}
