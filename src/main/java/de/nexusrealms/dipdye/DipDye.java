package de.nexusrealms.dipdye;

import de.nexusrealms.dipdye.api.CauldronDipHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DipDye implements ModInitializer {
	public static final String MOD_ID = "dip-dye";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String name){
		return Identifier.of(MOD_ID, name);
	}

	public static final TagKey<Item> CAULDRON_DIPPABLE = TagKey.of(RegistryKeys.ITEM, id("cauldron_dippable"));

	private static final Identifier COLOR_CAULDRON_KEY = id("color_cauldron");

	public static final Block COLOR_CAULDRON = new ColorCauldronBlock(Biome.Precipitation.NONE, AbstractBlock.Settings.copy(Blocks.WATER_CAULDRON).registryKey(RegistryKey.of(RegistryKeys.BLOCK, COLOR_CAULDRON_KEY)));
	public static final BlockEntityType<ColorCauldronBlockEntity> COLOR_CAULDRON_ENTITY = FabricBlockEntityTypeBuilder.create(ColorCauldronBlockEntity::new, COLOR_CAULDRON).build();

	private static final CauldronDipHandler DIP_HANDLER = new CauldronDipHandler();
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registries.BLOCK, COLOR_CAULDRON_KEY, COLOR_CAULDRON);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_CAULDRON_KEY, COLOR_CAULDRON_ENTITY);
	}
	public static CauldronDipHandler getDipHandler(){
		return DIP_HANDLER;
	}
}