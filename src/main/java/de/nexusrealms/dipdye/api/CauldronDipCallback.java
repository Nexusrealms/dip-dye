package de.nexusrealms.dipdye.api;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface CauldronDipCallback {
    ItemStack dip(ItemStack stack, ColorCauldronBlockEntity blockEntity, PlayerEntity player);
}
