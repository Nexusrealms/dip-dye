package de.nexusrealms.dipdye.api;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface CauldronDipCallback {
    ItemStack dip(ItemStack stack, ColorCauldronBlockEntity blockEntity, PlayerEntity player, Hand hand);
}
