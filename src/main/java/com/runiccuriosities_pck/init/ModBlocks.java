package com.runiccuriosities_pck;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RunicCuriosities.MODID);

    public static final RegistryObject<Block> SAVIRITIUM_COMPOUND_BLOCK = BLOCKS.register("saviritium_compound_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(30.0f, 100.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 2)
                    .requiresCorrectToolForDrops()));
}