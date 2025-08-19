package de.nexusrealms.dipdye.mixin;

import de.nexusrealms.dipdye.ColorCauldronBlockEntity;
import de.nexusrealms.dipdye.DipDye;
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
			if(stack.getItem() instanceof DyeItem && state.get(LeveledCauldronBlock.LEVEL) > 0){
				BlockState newState = DipDye.COLOR_CAULDRON.getStateWithProperties(state);
				world.setBlockState(pos, newState);
				if (world.getBlockEntity(pos) instanceof ColorCauldronBlockEntity colorCauldronBlockEntity) {
					if (stack.getItem() instanceof DyeItem) {
						colorCauldronBlockEntity.processAddedStack(stack, !player.isInCreativeMode());
					}
					cir.setReturnValue(ActionResult.SUCCESS);
				} else {
					cir.setReturnValue(ActionResult.PASS);
				}
			}
		}
	}
}