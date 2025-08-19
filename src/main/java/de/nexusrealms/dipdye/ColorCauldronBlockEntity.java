package de.nexusrealms.dipdye;

import de.nexusrealms.dipdye.api.CauldronDipApi;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ColorCauldronBlockEntity extends BlockEntity {
    public ColorCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(DipDye.COLOR_CAULDRON_ENTITY, pos, state);
    }
    private boolean additive = false;

    private int additiveRed;
    private int additiveGreen;
    private int additiveBlue;

    private List<Item> dyes = new ArrayList<>();
    public boolean isAdditive(){
        return additive;
    }
    public List<DyeItem> getDyes(){
        if(additive){
            return List.of();
        } else {
            return dyes.stream().collect(ArrayList::new, (dyeItems, item) -> dyeItems.add((DyeItem) item), ArrayList::addAll);
        }
    }
    public int getAdditiveColor(){
        return ColorHelper.Argb.getArgb(additiveRed, additiveGreen, additiveBlue);
    }
    public int getRenderedColor(){
        if(additive){
            return getAdditiveColor();
        } else if (dyes.isEmpty()){
            return world.getBiome(pos).value().getWaterColor();
        } else {
            return CauldronDipApi.getColorFromDyes(getDyes(), null);
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    @Override
    protected void readNbt(NbtCompound view, RegistryWrapper.WrapperLookup registryLookup) {
        additive = view.getBoolean("additive");
        additiveRed = view.getInt("additiveRed");
        additiveGreen = view.getInt("additiveGreen");
        additiveBlue = view.getInt("additiveBlue");
        dyes = Registries.ITEM.getCodec().listOf()
                .xmap(ArrayList::new, Function.identity()).parse(registryLookup.getOps(NbtOps.INSTANCE), view.getList("dyes", NbtElement.STRING_TYPE)).getOrThrow();

    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }
    public void decreaseLevel(){
        LeveledCauldronBlock.decrementFluidLevel(world.getBlockState(pos), getWorld(), pos);
    }

    public void addABitOfWater(){
        int i = getCachedState().get(LeveledCauldronBlock.LEVEL) + 1;
        if(i <= 3){
            BlockState blockState = getCachedState().with(LeveledCauldronBlock.LEVEL, i);
            world.setBlockState(pos, blockState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
        }
    }
    //TODO better sounds
    public ItemStack processDippedStack(ItemStack stack, PlayerEntity player){
        ItemStack stack1 = DipDye.getDipHandler().dip(stack, this, player);
        decreaseLevel();
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
        return stack1;
    }
    //TODO better sounds
    public void processDyeStack(ItemStack stack, boolean decrement){
        if(stack.getItem() instanceof DyeItem dyeItem){
            if(!world.isClient){
                if(decrement) stack.decrement(1);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            dyes.add(dyeItem);
            updateListeners();
        }
    }
    public void processAdditiveStack(ColorDropperItem colorDropperItem){
        additive = true;
        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        if(colorDropperItem.addsRed()){
            additiveRed = MathHelper.clamp(0, additiveRed + colorDropperItem.getSize(), 255);
        }
        if(colorDropperItem.addsGreen()){
            additiveGreen = MathHelper.clamp(0, additiveGreen + colorDropperItem.getSize(), 255);
        }
        if(colorDropperItem.addsBlue()){
            additiveBlue = MathHelper.clamp(0, additiveBlue + colorDropperItem.getSize(), 255);
        }
        updateListeners();
    }

    @Override
    protected void writeNbt(NbtCompound view, RegistryWrapper.WrapperLookup registryLookup) {
        view.putBoolean("additive", additive);
        view.putInt("additiveRed", additiveRed);
        view.putInt("additiveGreen", additiveGreen);
        view.putInt("additiveBlue", additiveBlue);
        view.put("dyes",  Registries.ITEM.getCodec().listOf().encodeStart(registryLookup.getOps(NbtOps.INSTANCE), dyes).getOrThrow());
    }
}
