package io.github.pouffy.cauldrontweaks.init;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.client.CauldronRenderer;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber
public class CauldronBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> HELPER = ModUtils.createRegister(Registries.BLOCK_ENTITY_TYPE);

    public static List<Block> validBlocks = new ArrayList<>();

    static {
        Consumer<Block> addCauldron = validBlocks::add;
        addCauldron.accept(Blocks.CAULDRON);
        addCauldron.accept(Blocks.WATER_CAULDRON);
        addCauldron.accept(Blocks.LAVA_CAULDRON);
        addCauldron.accept(Blocks.POWDER_SNOW_CAULDRON);
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CauldronBlockEntity>> CAULDRON = HELPER.register("cauldron", () -> BlockEntityType.Builder.of(CauldronBlockEntity::new, validBlocks.toArray(new Block[0])).build(null));

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Block Entity Registry");
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        CauldronBlockEntity.registerCapabilities(event);
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientOnly {
        @SubscribeEvent
        public static void blockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CauldronBlockEntities.CAULDRON.get(), CauldronRenderer::new);
        }
    }
}
