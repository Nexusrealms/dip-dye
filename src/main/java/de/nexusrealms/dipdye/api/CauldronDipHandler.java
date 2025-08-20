package de.nexusrealms.dipdye.api;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Map;

public class CauldronDipHandler {
    private final Map<Item, CauldronDipCallback> MAP = new HashMap<>();
    protected boolean register(Item item, CauldronDipCallback callback){
        if(MAP.containsKey(item)) {
            return false;
        }
        MAP.put(item, callback);
        return true;
    }
    public ItemStack dip(ItemStack stack, ColorCauldronBlockEntity blockEntity, PlayerEntity player, Hand hand){
        if(MAP.containsKey(stack.getItem())){
            return MAP.get(stack.getItem()).dip(stack, blockEntity, player, hand);
        } else if (stack.isIn(ItemTags.DYEABLE)){
            if(blockEntity.isAdditive()){
                stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(blockEntity.getAdditiveColor()));
            } else {
                return CauldronDipApi.setColorWithoutCopying(stack, blockEntity.getDyes());
            }
        }
        return stack;
    }
}
