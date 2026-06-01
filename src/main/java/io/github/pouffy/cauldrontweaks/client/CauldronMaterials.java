package io.github.pouffy.cauldrontweaks.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CauldronMaterials {
    private static final Cache<ResourceLocation, Material> CACHED_MATERIALS = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    //cached materials
    public static Material get(ResourceLocation bockTexture) {
        try {
            return CACHED_MATERIALS.get(bockTexture, () -> new Material(InventoryMenu.BLOCK_ATLAS, bockTexture));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
