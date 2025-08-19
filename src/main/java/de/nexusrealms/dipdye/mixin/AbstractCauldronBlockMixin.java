package de.nexusrealms.dipdye.mixin;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import de.nexusrealms.dipdye.DipDye;
import de.nexusrealms.dipdye.ColorDropperItem;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {
	@Inject(at = @At("HEAD"), method = "onUseWithItem", cancellable = true)
	private void turnToColorCauldron(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
		if(Registries.BLOCK.getId((AbstractCauldronBlock) (Object) this).equals(Identifier.of("water_cauldron"))){
			if((stack.getItem() instanceof DyeItem || stack.getItem() instanceof ColorDropperItem) && state.get(LeveledCauldronBlock.LEVEL) > 0){
				BlockState newState = DipDye.COLOR_CAULDRON.getStateWithProperties(state);
				world.setBlockState(pos, newState);
				if (world.getBlockEntity(pos) instanceof ColorCauldronBlockEntity colorCauldronBlockEntity) {
					if (stack.getItem() instanceof DyeItem) {
						colorCauldronBlockEntity.processDyeStack(stack, !player.isInCreativeMode());
					} else if(stack.getItem() instanceof ColorDropperItem colorDropperItem) {
						colorCauldronBlockEntity.processAdditiveStack(colorDropperItem);
						if(!player.isCreative()) {
							stack.decrement(1);
							player.giveItemStack(colorDropperItem.getRecipeRemainder());
						}
					}
					cir.setReturnValue(ActionResult.SUCCESS);
				} else {
					cir.setReturnValue(ActionResult.PASS);
				}
			}
		}
	}
}