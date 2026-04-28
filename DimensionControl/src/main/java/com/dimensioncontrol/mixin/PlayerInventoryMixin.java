package com.dimensioncontrol.mixin;

import com.dimensioncontrol.config.DimensionConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void onInsertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (player.getWorld().isClient || stack.isEmpty()) return;

        Identifier dimensionId = player.getWorld().getRegistryKey().getValue();
        Identifier itemId = Registries.ITEM.getId(stack.getItem());

        if (DimensionConfig.getInstance().isItemBlocked(dimensionId, itemId)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(
                    Text.literal("§cYou cannot obtain this item in this dimension!"), 
                    true
                );
            }
            cir.setReturnValue(false);
        }
    }
}