package de.nexusrealms.dipdye.api;

import de.nexusrealms.dipdye.DipDye;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.ColorHelper;

import java.util.List;
import java.util.Optional;

public interface CauldronDipApi {
    static int getColorFromDyes(List<DyeItem> dyes, DyedColorComponent dyedColorComponent){
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        int m = 0;
        if (dyedColorComponent != null) {
            int n = ColorHelper.Argb.getRed(dyedColorComponent.rgb());
            int o = ColorHelper.Argb.getGreen(dyedColorComponent.rgb());
            int p = ColorHelper.Argb.getBlue(dyedColorComponent.rgb());
            l += Math.max(n, Math.max(o, p));
            i += n;
            j += o;
            k += p;
            ++m;
        }

        for(DyeItem dyeItem : dyes) {
            int p = dyeItem.getColor().getEntityColor();
            int q = ColorHelper.Argb.getRed(p);
            int r = ColorHelper.Argb.getGreen(p);
            int s = ColorHelper.Argb.getBlue(p);
            l += Math.max(q, Math.max(r, s));
            i += q;
            j += r;
            k += s;
            ++m;
        }

        int n = i / m;
        int o = j / m;
        int p = k / m;
        float f = (float)l / (float)m;
        float g = (float)Math.max(n, Math.max(o, p));
        n = (int)((float)n * f / g);
        o = (int)((float)o * f / g);
        p = (int)((float)p * f / g);
        return ColorHelper.Argb.getArgb(0, n, o, p);
    }
    static boolean register(Item item, CauldronDipCallback callback){
        return DipDye.getDipHandler().register(item, callback);
    }
}
