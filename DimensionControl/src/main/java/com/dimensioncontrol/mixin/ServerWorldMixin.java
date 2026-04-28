package com.dimensioncontrol.mixin;

import com.dimensioncontrol.config.DimensionConfig;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Inject(method = "spawnEntity", at = @At("HEAD"), cancellable = true)
    private void onSpawnEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        Identifier dimensionId = world.getRegistryKey().getValue();
        Identifier entityId = Registries.ENTITY_TYPE.getId(entity.getType());

        if (DimensionConfig.getInstance().isEntityBlocked(dimensionId, entityId)) {
            cir.setReturnValue(false);
        }
    }
}