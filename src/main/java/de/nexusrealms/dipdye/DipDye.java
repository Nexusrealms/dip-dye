package de.nexusrealms.dipdye;

import de.nexusrealms.dipdye.api.CauldronDipHandler;
import de.nexusrealms.dipdye.network.DipDyeNetwork;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
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
	private static final RegistryKey<Item> VIAL_KEY = RegistryKey.of(RegistryKeys.ITEM, id("glass_vial"));
	public static final Item VIAL = new Item(new Item.Settings().registryKey(VIAL_KEY));
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registries.BLOCK, COLOR_CAULDRON_KEY, COLOR_CAULDRON);
		Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_CAULDRON_KEY, COLOR_CAULDRON_ENTITY);
		Registry.register(Registries.ITEM, VIAL_KEY, VIAL);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(VIAL));
		registerPipettes("red", (byte) 0b001);
		registerPipettes("green", (byte) 0b010);
		registerPipettes("blue", (byte) 0b100);
		registerPipettes("white", (byte) 0b111);
		registerAntiPipettes("black", (byte) 0b111);

		DipDyeNetwork.init();
	}
	public static CauldronDipHandler getDipHandler(){
		return DIP_HANDLER;
	}

	private void registerPipettes(String colorName, byte rgb){
		RegistryKey<Item> bottleKey = RegistryKey.of(RegistryKeys.ITEM, id(colorName + "_dye_bottle"));
		Item bottle = Registry.register(Registries.ITEM, bottleKey, new ColorDropperItem(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).registryKey(bottleKey), rgb, 16));
		RegistryKey<Item> vialKey = RegistryKey.of(RegistryKeys.ITEM, id(colorName + "_vial"));
		Item pipette = Registry.register(Registries.ITEM, vialKey, new ColorDropperItem(new Item.Settings().recipeRemainder(VIAL).registryKey(vialKey), rgb, 1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.add(bottle);
			entries.add(pipette);
		});
	}
	private void registerAntiPipettes(String colorName, byte rgb){
		RegistryKey<Item> bottleKey = RegistryKey.of(RegistryKeys.ITEM, id(colorName + "_dye_bottle"));
		Item bottle = Registry.register(Registries.ITEM, bottleKey, new ColorDropperItem(new Item.Settings().recipeRemainder(Items.GLASS_BOTTLE).registryKey(bottleKey), rgb, -16));
		RegistryKey<Item> vialKey = RegistryKey.of(RegistryKeys.ITEM, id(colorName + "_vial"));
		Item pipette = Registry.register(Registries.ITEM, vialKey, new ColorDropperItem(new Item.Settings().recipeRemainder(VIAL).registryKey(vialKey), rgb, -1));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.add(bottle);
			entries.add(pipette);
		});
	}
}