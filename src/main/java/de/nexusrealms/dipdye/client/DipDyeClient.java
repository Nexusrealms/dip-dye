package de.nexusrealms.dipdye.client;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import de.nexusrealms.dipdye.DipDye;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColorProvider;

public class DipDyeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if(world.getBlockEntity(pos) instanceof ColorCauldronBlockEntity colorCauldronBlockEntity){
                return colorCauldronBlockEntity.getRenderedColor();
            }
            return 0xffffff;
        }, DipDye.COLOR_CAULDRON);
    }
}
