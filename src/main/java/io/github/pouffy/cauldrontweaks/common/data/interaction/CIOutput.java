package io.github.pouffy.cauldrontweaks.common.data.interaction;

import net.minecraft.resources.ResourceLocation;

public interface CIOutput extends ICIOutputExtension {
    default void accept(ResourceLocation location, ICauldronInteraction interaction) {
        accept(location, interaction);
    }
}
