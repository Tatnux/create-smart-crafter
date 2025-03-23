package com.tatnux.crafter.lib.list;

import net.minecraft.core.NonNullList;

import java.util.function.Supplier;

public class NonNullListFactory {

    public static <T> NonNullList<T> fill(int size, Supplier<T> factory) {
        NonNullList<T> list = NonNullList.createWithCapacity(size);
        for (int i = 0; i < size; i++) {
            list.add(factory.get());
        }
        return list;
    }

}
