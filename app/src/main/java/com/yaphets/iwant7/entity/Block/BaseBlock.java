package com.yaphets.iwant7.entity.Block;

import android.widget.ImageView;

import com.yaphets.iwant7.activity.MainActivity;

import java.util.Objects;

/**
 * Created by Yaphets on 2017/8/28.
 */
public abstract class BaseBlock implements Cloneable {
    public int m_X;
    public int m_Y;
    public int m_value;

    private Objects TAG;

    public BaseBlock() {
        this(0,0,-1);
    }

    public BaseBlock(int x, int y, int value) {
        this.m_X = x;
        this.m_Y = y;
        this.m_value = value;
    }

    public Objects getTAG() {
        return TAG;
    }
    public void setTAG(Objects TAG) {
        this.TAG = TAG;
    }



}
