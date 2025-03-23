package com.tatnux.crafter.modules.crafter.blocks;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.block.IBE;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;

public class SmartCrafterBlock extends HorizontalKineticBlock implements IBE<SmartCrafterBlockEntity>, ICogWheel {

    public SmartCrafterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isCrouching()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player instanceof FakePlayer) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (AllItems.WRENCH.isIn(player.getItemInHand(hand))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        this.withBlockEntityDo(level, pos,
                crafter -> player.openMenu(crafter, crafter::sendToMenu));
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public Class<SmartCrafterBlockEntity> getBlockEntityClass() {
        return SmartCrafterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartCrafterBlockEntity> getBlockEntityType() {
        return SmartCrafterModule.SMART_CRAFTER_BLOCK_ENTITY.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }
}
