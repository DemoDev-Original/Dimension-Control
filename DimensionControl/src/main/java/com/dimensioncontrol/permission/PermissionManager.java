package com.dimensioncontrol.permission;

import com.dimensioncontrol.config.DimensionConfig;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionManager {
    private static final Map<UUID, String> playerPermissions = new HashMap<>();

    public static boolean hasPermission(ServerPlayerEntity player, Identifier dimensionId) {
        if (!DimensionConfig.getInstance().enablePermissionSystem) {
            return true;
        }

        if (player.hasPermissionLevel(2)) {
            return true;
        }

        DimensionConfig.DimensionRestrictions restrictions = 
            DimensionConfig.getInstance().getRestrictions(dimensionId);

        if (restrictions.allowedPermissions.isEmpty()) {
            return true;
        }

        String playerPerm = playerPermissions.getOrDefault(
            player.getUuid(), 
            DimensionConfig.getInstance().defaultPermissionLevel
        );

        return restrictions.allowedPermissions.contains(playerPerm);
    }

    public static void setPermission(UUID playerId, String permission) {
        playerPermissions.put(playerId, permission);
    }

    public static String getPermission(UUID playerId) {
        return playerPermissions.getOrDefault(
            playerId, 
            DimensionConfig.getInstance().defaultPermissionLevel
        );
    }

    public static void removePermission(UUID playerId) {
        playerPermissions.remove(playerId);
    }

    public static void clearAll() {
        playerPermissions.clear();
    }
}