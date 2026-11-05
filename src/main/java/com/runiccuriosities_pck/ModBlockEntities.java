package com.runiccuriosities_pck;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RunicCuriosities.MODID);

    public static final RegistryObject<BlockEntityType<MagicModTableBlockEntity>> MAGIC_MOD_TABLE =
            BLOCK_ENTITIES.register("magic_mod_table",
                    () -> BlockEntityType.Builder.of(MagicModTableBlockEntity::new,
                            ModBlocks.MAGIC_MOD_TABLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}