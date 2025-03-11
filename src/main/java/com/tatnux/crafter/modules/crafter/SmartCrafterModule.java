package com.tatnux.crafter.modules.crafter;


import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.recipe.MechanicalCraftingRecipeBuilder;
import com.tatnux.crafter.config.Config;
import com.tatnux.crafter.lib.module.IModule;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterBlock;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterBlockEntity;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterRenderer;
import com.tatnux.crafter.modules.crafter.client.SmartCrafterScreen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static com.tatnux.crafter.SmartCrafter.REGISTRATE;

public class SmartCrafterModule implements IModule {
    @SuppressWarnings({"removal", "DataFlowIssue"})
    public static final BlockEntry<SmartCrafterBlock> SMART_CRAFTER = REGISTRATE
            .block("smart_crafter", SmartCrafterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(copyNbt("Inventory", "SelectedRecipe", "Recipes", "KeepMode", "GhostSlots"))
            .transform(pickaxeOnly())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .onRegister(it -> BlockStressValues.IMPACTS.register(it, () -> Config.common().crafterStressImpact.get()))
            .addLayer(() -> RenderType::cutoutMipped)
            .item()
            .tab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())
            .transform(customItemModel())
            .recipe((blockDataGenContext, provider) -> MechanicalCraftingRecipeBuilder
                    .shapedRecipe(blockDataGenContext.get())
                    .patternLine("BPB")
                    .patternLine("CTC")
                    .patternLine("HWH")
                    .patternLine("BDB")
                    .key('B', AllBlocks.BRASS_CASING)
                    .key('P', AllItems.PRECISION_MECHANISM)
                    .key('C', AllBlocks.MECHANICAL_CRAFTER)
                    .key('T', AllItems.ELECTRON_TUBE)
                    .key('H', AllBlocks.SMART_CHUTE)
                    .key('W', AllBlocks.COGWHEEL)
                    .key('D', AllBlocks.DEPOT)
                    .build(provider))
            .register();

    public static final BlockEntityEntry<SmartCrafterBlockEntity> SMART_CRAFTER_BLOCK_ENTITY = REGISTRATE
            .blockEntity("smart_crafter", SmartCrafterBlockEntity::new)
            .validBlocks(SMART_CRAFTER)
            .renderer(() -> SmartCrafterRenderer::new)
            .register();

    public static final MenuEntry<SmartCrafterMenu> CRAFTER_MENU = REGISTRATE
            .menu("smart_crafter", SmartCrafterMenu::new, () -> SmartCrafterScreen::new)
            .register();

    public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> copyNbt(String... tags) {
        return b -> b.loot((registrateBlockLootTables, block) -> {
            CopyNbtFunction.Builder builder = CopyNbtFunction
                    .copyData(ContextNbtProvider.BLOCK_ENTITY);
            for (String tag : tags) {
                builder.copy(tag, "BlockEntityTag." + tag);
            }
            registrateBlockLootTables.add(block, registrateBlockLootTables.createSingleItemTable(block).apply(builder));
        }) ;
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
