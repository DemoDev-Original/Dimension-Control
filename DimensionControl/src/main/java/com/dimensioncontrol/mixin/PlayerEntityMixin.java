package com.dimensioncontrol.mixin;

import com.dimensioncontrol.config.DimensionConfig;
import com.dimensioncontrol.permission.PermissionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "getStackInHand", at = @At("RETURN"), cancellable = true)
    private void onGetStackInHand(CallbackInfoReturnable<ItemStack> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        ItemStack stack = cir.getReturnValue();
        if (stack.isEmpty()) return;

        Identifier dimensionId = player.getWorld().getRegistryKey().getValue();
        Identifier itemId = Registries.ITEM.getId(stack.getItem());

        if (DimensionConfig.getInstance().isItemBlocked(dimensionId, itemId)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(
                    Text.literal("§cThis item is blocked in this dimension!"), 
                    true
                );
            }
        }
    }
}