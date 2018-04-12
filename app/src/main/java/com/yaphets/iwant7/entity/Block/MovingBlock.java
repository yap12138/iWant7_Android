package com.yaphets.iwant7.entity.Block;

/**
 * Created by Yaphets on 2017/8/31.
 */
public class MovingBlock extends BaseBlock {

    public boolean on_left_down;

    public MovingBlock() {
        super();
        this.on_left_down = false;
    }

    public MovingBlock(int x, int y, int value, boolean left_down) {
        super(x, y, value);
        this.on_left_down = left_down;
    }

    /*@Override
    public int compareTo(BaseBlock o) {
        MovingBlock var = (MovingBlock) o;
        if (var.on_left_down)
            return -1;
        else
            return 1;
    }*/

    public static MovingBlock onLeftDown(MovingBlock[] blocks) {
        return blocks[0].on_left_down? blocks[0]:blocks[1];
    }

    public static MovingBlock no_OnLeftDown(MovingBlock[] blocks) {
        return blocks[0].on_left_down? blocks[1]:blocks[0];
    }
}
