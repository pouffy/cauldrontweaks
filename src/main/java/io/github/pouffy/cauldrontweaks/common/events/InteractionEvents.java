package io.github.pouffy.cauldrontweaks.common.events;

import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionManager;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class InteractionEvents {

    @SubscribeEvent
    public void registerListeners(AddReloadListenerEvent event) {
        event.addListener(CauldronInteractionManager.RELOAD_INSTANCE);
    }

    @SubscribeEvent
    public void blockInteract(UseItemOnBlockEvent event) {
        Player player = event.getPlayer();
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof CauldronBlockEntity cauldron) {
            for (var interaction : CauldronInteractionManager.getInteractions()) {
                ItemInteractionResult result = interaction.interact(cauldron, cauldron.getFluidStack(), player, event.getHand(), event.getItemStack());
                if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
                    event.cancelWithResult(result); break;
                }
            }
        }
    }
}
