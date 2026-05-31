package io.github.pouffy.cauldrontweaks.common.data.interaction;

import net.minecraft.resources.ResourceLocation;

public interface ICIOutputExtension {
    private CIOutput self() {
        return (CIOutput) this;
    }
    void accept(ResourceLocation id, ICauldronInteraction growth);
}
