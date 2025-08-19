package de.nexusrealms.dipdye;

import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ColorCauldronBlock extends LeveledCauldronBlock implements BlockEntityProvider {
    private static final CauldronBehavior.CauldronBehaviorMap BEHAVIOR_MAP = Util.make(() -> {
        CauldronBehavior.CauldronBehaviorMap cauldronBehaviorMap = CauldronBehavior.createMap("color");
        Map<Item, CauldronBehavior> map = cauldronBehaviorMap.map();
        CauldronBehavior.registerBucketBehavior(map);
        map.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContentsComponent != null && potionContentsComponent.matches(Potions.WATER)) {
                if (!world.isClient) {
                    Item item = stack.getItem();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.incrementStat(Stats.USE_CAULDRON);
                    player.incrementStat(Stats.USED.getOrCreateStat(item));
                    if (world.getBlockEntity(pos) instanceof ColorCauldronBlockEntity colorCauldronBlock) {
                        colorCauldronBlock.addABitOfWater();
                    }
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
        });
        map.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), (statex) -> statex.get(LeveledCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL));
        return cauldronBehaviorMap;
    });
    public ColorCauldronBlock(Biome.Precipitation precipitation, Settings settings) {
        super(precipitation, BEHAVIOR_MAP, settings);

    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ColorCauldronBlockEntity colorCauldronBlockEntity) {
            if (stack.isIn(DipDye.CAULDRON_DIPPABLE)) {
                if(!world.isClient){
                    ItemStack stack1 = colorCauldronBlockEntity.processDippedStack(stack, player);
                    player.setStackInHand(hand, stack1);
                    player.getItemCooldownManager().set(stack1, 40);
                }
                return ActionResult.SUCCESS_SERVER;
            } else if (stack.getItem() instanceof DyeItem) {
                colorCauldronBlockEntity.processAddedStack(stack, !player.isInCreativeMode());
                return ActionResult.SUCCESS_SERVER;
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ColorCauldronBlockEntity(pos, state);
    }

}

