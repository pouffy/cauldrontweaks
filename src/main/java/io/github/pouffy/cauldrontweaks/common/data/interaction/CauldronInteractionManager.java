package io.github.pouffy.cauldrontweaks.common.data.interaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.helpers.RegistryAccessJsonReloadListener;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class CauldronInteractionManager extends RegistryAccessJsonReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create(); //json object that will write stuff

    public static final CauldronInteractionManager RELOAD_INSTANCE = new CauldronInteractionManager();

    private static final Map<ResourceLocation, ICauldronInteraction> INTERACTIONS = new HashMap<>();

    public static List<ICauldronInteraction> getInteractions() {
        return INTERACTIONS.values().stream().toList();
    }

    public CauldronInteractionManager() {
        super(GSON, "cauldron_interactions");
    }

    @Override
    public void parse(Map<ResourceLocation, JsonElement> jsonMap, RegistryAccess registryAccess) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        for (var e : jsonMap.entrySet()) {
            map.put(e.getKey(), e.getValue().deepCopy());
        }

        Map<ResourceLocation, ICauldronInteraction> interactions = new HashMap<>();

        for (var e : map.entrySet()) {
            var json = e.getValue();

            var result = ICauldronInteraction.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), json);
            var o = result.resultOrPartial(error -> CauldronTweaks.LOGGER.error("Failed to read cauldron interaction JSON object for {} : {}", e.getKey(), error));

            o.ifPresent((interaction) -> interactions.put(e.getKey(), interaction));
        }
        CauldronTweaks.LOGGER.info("Loaded {} cauldron interaction configurations", interactions.size());

        INTERACTIONS.clear();
        INTERACTIONS.putAll(interactions);
    }
}
