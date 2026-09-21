package com.runiccuriosities_pck;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(RunicCuriosities.MODID);

    public static final DeferredBlock<Block> SAVIRITIUM_COMPOUND_BLOCK = BLOCKS.register("saviritium_compound_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(30.0f, 100.0f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .lightLevel(state -> 2)
                    .requiresCorrectToolForDrops()));
}