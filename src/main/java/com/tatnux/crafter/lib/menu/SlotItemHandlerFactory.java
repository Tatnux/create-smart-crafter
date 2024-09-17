package com.tatnux.crafter.lib.menu;

import com.tatnux.crafter.modules.crafter.blocks.slots.ItemValidator;
import net.minecraftforge.items.SlotItemHandler;

public interface SlotItemHandlerFactory {

    SlotItemHandler on(ItemValidator itemHandler, int index, int x, int y);

}
