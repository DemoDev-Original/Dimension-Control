package com.dimensioncontrol.mixin;

import com.dimensioncontrol.config.DimensionConfig;
import com.dimensioncontrol.permission.PermissionManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true)
    private void onTeleportTo(TeleportTarget teleportTarget, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        
        if (teleportTarget.world() instanceof ServerWorld targetWorld) {
            Identifier dimensionId = targetWorld.getRegistryKey().getValue();
            
            if (!DimensionConfig.getInstance().isDimensionEnabled(dimensionId)) {
                player.sendMessage(Text.literal("§cThis dimension is currently disabled!"), false);
                cir.setReturnValue(false);
                return;
            }

            if (!PermissionManager.hasPermission(player, dimensionId)) {
                player.sendMessage(Text.literal("§cYou don't have permission to enter this dimension!"), false);
                cir.setReturnValue(false);
            }
        }
    }
}