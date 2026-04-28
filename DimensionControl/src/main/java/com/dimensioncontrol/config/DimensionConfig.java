package com.dimensioncontrol.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DimensionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("dimensioncontrol.json");
    private static DimensionConfig instance;

    public Map<String, DimensionRestrictions> dimensions = new HashMap<>();
    public boolean enablePermissionSystem = true;
    public String defaultPermissionLevel = "default";

    public static class DimensionRestrictions {
        public boolean enabled = true;
        public Set<String> blockedItems = new HashSet<>();
        public Set<String> blockedBlocks = new HashSet<>();
        public Set<String> blockedEntities = new HashSet<>();
        public Set<String> allowedPermissions = new HashSet<>();
    }

    public static DimensionConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public static DimensionConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try (var reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                instance = GSON.fromJson(reader, DimensionConfig.class);
                if (instance != null) return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        instance = createDefault();
        save();
        return instance;
    }

    private static DimensionConfig createDefault() {
        DimensionConfig config = new DimensionConfig();
        
        DimensionRestrictions overworld = new DimensionRestrictions();
        overworld.enabled = true;
        config.dimensions.put("minecraft:overworld", overworld);

        DimensionRestrictions nether = new DimensionRestrictions();
        nether.enabled = true;
        nether.blockedItems.add("minecraft:netherite_ingot");
        nether.allowedPermissions.add("dimensioncontrol.nether");
        config.dimensions.put("minecraft:the_nether", nether);

        DimensionRestrictions end = new DimensionRestrictions();
        end.enabled = true;
        end.blockedItems.add("minecraft:elytra");
        end.allowedPermissions.add("dimensioncontrol.end");
        config.dimensions.put("minecraft:the_end", end);

        return config;
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(instance), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    public DimensionRestrictions getRestrictions(Identifier dimensionId) {
        return dimensions.computeIfAbsent(dimensionId.toString(), k -> new DimensionRestrictions());
    }

    public boolean isDimensionEnabled(Identifier dimensionId) {
        DimensionRestrictions restrictions = dimensions.get(dimensionId.toString());
        return restrictions == null || restrictions.enabled;
    }

    public boolean isItemBlocked(Identifier dimensionId, Identifier itemId) {
        DimensionRestrictions restrictions = dimensions.get(dimensionId.toString());
        return restrictions != null && restrictions.blockedItems.contains(itemId.toString());
    }

    public boolean isBlockBlocked(Identifier dimensionId, Identifier blockId) {
        DimensionRestrictions restrictions = dimensions.get(dimensionId.toString());
        return restrictions != null && restrictions.blockedBlocks.contains(blockId.toString());
    }

    public boolean isEntityBlocked(Identifier dimensionId, Identifier entityId) {
        DimensionRestrictions restrictions = dimensions.get(dimensionId.toString());
        return restrictions != null && restrictions.blockedEntities.contains(entityId.toString());
    }
}