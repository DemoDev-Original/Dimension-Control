package com.dimensioncontrol.mixin;

import com.dimensioncontrol.config.DimensionConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "onBreak", at = @At("HEAD"), cancellable = true)
    private void onBlockBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (world.isClient) return;

        Identifier dimensionId = world.getRegistryKey().getValue();
        Identifier blockId = Registries.BLOCK.getId(state.getBlock());

        if (DimensionConfig.getInstance().isBlockBlocked(dimensionId, blockId)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(
                    Text.literal("§cYou cannot break this block in this dimension!"), 
                    true
                );
            }
            ci.cancel();
        }
    }
}