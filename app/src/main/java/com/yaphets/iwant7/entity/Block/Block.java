package com.yaphets.iwant7.entity.Block;

import android.util.Pair;
import android.widget.ImageView;

/**
 * Created by Yaphets on 2017/8/31.
 */
public class Block extends BaseBlock {

    public int absorb;
    public int priority;
    public int grade;
    public boolean haveScan;
    //存放图片对象
    private Pair<Integer, ImageView> tag;

    public Block(int x, int y, int value) {
        super(x, y, value);
    }

    public Block(int value) {
        this(0,0,value);
    }

    /*@Override
    public int compareTo(BaseBlock o) {
        Block var = (Block) o;
        if (this.absorb == var.absorb){
            if (this.priority < var.priority)
                return -1;
            else
                return this.priority == var.priority? 0:1;
        }
        else {
            if (this.absorb < var.absorb)
                return -1;
            else
                return 1;
        }
    }*/

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Block block = (Block) super.clone();
        block.setTAG(this.getTAG());
        return block;
    }

    public void clear() {
        this.m_value = -1;
        this.absorb = 0;
        this.priority = 0;
        this.haveScan = false;
    }

    public Pair<Integer, ImageView> getTag() {
        return tag;
    }

    public void setTag(Pair<Integer, ImageView> tag) {
        this.tag = tag;
    }
}
