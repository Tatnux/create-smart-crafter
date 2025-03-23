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
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterComponents;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterRenderer;
import com.tatnux.crafter.modules.crafter.client.SmartCrafterScreen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.IEventBus;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static com.tatnux.crafter.SmartCrafter.REGISTRATE;

public class SmartCrafterModule implements IModule {

    private static final String SMART_CRAFTER_ID = "smart_crafter";

    @SuppressWarnings({"removal"})
    public static final BlockEntry<SmartCrafterBlock> SMART_CRAFTER = REGISTRATE
            .block(SMART_CRAFTER_ID, SmartCrafterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(MapColor.TERRACOTTA_YELLOW))
            .transform(pickaxeOnly())
            .loot((lt, block) ->
                    lt.add(block, LootTable.lootTable().withPool(LootPool.lootPool()
                    .when(ExplosionCondition.survivesExplosion())
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block)
                            .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(SmartCrafterComponents.SMART_CRAFTER_INVENTORY)
                                    .include(SmartCrafterComponents.SMART_CRAFTER_KEEP_MODE)
                                    .include(SmartCrafterComponents.SMART_CRAFTER_SELECTED_INDEX)
                                    .include(SmartCrafterComponents.SMART_CRAFTER_RECIPES)
                            )
                    )
            )))
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
            .blockEntity(SMART_CRAFTER_ID, SmartCrafterBlockEntity::new)
            .validBlocks(SMART_CRAFTER)
            .renderer(() -> SmartCrafterRenderer::new)
            .register();

    public static final MenuEntry<SmartCrafterMenu> CRAFTER_MENU = REGISTRATE
            .menu(SMART_CRAFTER_ID, SmartCrafterMenu::new, () -> SmartCrafterScreen::new)
            .register();

    @Override
    public void initConfig(IEventBus eventBus) {
        SmartCrafterComponents.register(eventBus);
        eventBus.addListener(SmartCrafterBlockEntity::registerCapabilities);
    }
}
