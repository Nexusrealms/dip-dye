package de.nexusrealms.dipdye;

import net.minecraft.item.Item;


public class ColorDropperItem extends Item {
    private final int size;
    private final byte rgb;
    public ColorDropperItem(Settings settings, byte rgb, int size) {
        super(settings);
        this.size = size;
        this.rgb = rgb;
    }
    public boolean addsRed(){
        return  ((rgb) & 1) != 0;
    }
    public boolean addsGreen(){
        return  ((rgb >> 1) & 1) != 0;
    }
    public boolean addsBlue(){
        return  ((rgb) >> 2) != 0;
    }

    public int getSize() {
        return size;
    }
}
