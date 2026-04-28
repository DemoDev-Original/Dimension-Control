package com.dimensioncontrol.gui;

import com.dimensioncontrol.DimensionControl;
import com.dimensioncontrol.config.DimensionConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ConfigScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final String dimensionId;
    private final String configurationType; // "items", "blocks", or "entities"

    public ConfigScreenHandler(int syncId, PlayerInventory playerInventory, Identifier dimensionId) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.dimensionId = dimensionId.toString();
        this.configurationType = "items"; // Default to items configuration
        this.inventory = new SimpleInventory(54);
        
        loadCurrentConfig();

        // Add slots for the configuration inventory (6 rows x 9 columns)
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new ConfigSlot(inventory, row * 9 + col, 8 + col * 18, 18 + row * 18));
            }
        }

        // Add player inventory slots
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Add player hotbar slots
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    private void loadCurrentConfig() {
        DimensionConfig.DimensionRestrictions restrictions = 
            DimensionControl.getConfig().getRestrictions(Identifier.of(dimensionId));
        
        int index = 0;
        if ("items".equals(configurationType)) {
            for (String itemId : restrictions.blockedItems) {
                if (index >= 54) break;
                try {
                    ItemStack stack = new ItemStack(Registries.ITEM.get(Identifier.of(itemId)));
                    inventory.setStack(index++, stack);
                } catch (Exception e) {
                    DimensionControl.LOGGER.warn("Failed to load item: " + itemId, e);
                }
            }
        } else if ("blocks".equals(configurationType)) {
            for (String blockId : restrictions.blockedBlocks) {
                if (index >= 54) break;
                try {
                    ItemStack stack = new ItemStack(Registries.BLOCK.get(Identifier.of(blockId)));
                    inventory.setStack(index++, stack);
                } catch (Exception e) {
                    DimensionControl.LOGGER.warn("Failed to load block: " + blockId, e);
                }
            }
        }
    }

    public void saveConfig() {
        DimensionConfig.DimensionRestrictions restrictions = 
            DimensionControl.getConfig().getRestrictions(Identifier.of(dimensionId));
        
        if ("items".equals(configurationType)) {
            restrictions.blockedItems.clear();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    Identifier itemId = Registries.ITEM.getId(stack.getItem());
                    restrictions.blockedItems.add(itemId.toString());
                }
            }
        } else if ("blocks".equals(configurationType)) {
            restrictions.blockedBlocks.clear();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    Identifier blockId = Registries.ITEM.getId(stack.getItem());
                    restrictions.blockedBlocks.add(blockId.toString());
                }
            }
        }
        
        DimensionConfig.save();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.hasPermissionLevel(2);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slot);
        
        if (clickedSlot != null && clickedSlot.hasStack()) {
            ItemStack originalStack = clickedSlot.getStack();
            newStack = originalStack.copy();
            
            if (slot < 54) {
                // Moving from config inventory to player inventory
                if (!this.insertItem(originalStack, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to config inventory
                if (!this.insertItem(originalStack, 0, 54, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (originalStack.isEmpty()) {
                clickedSlot.setStack(ItemStack.EMPTY);
            } else {
                clickedSlot.markDirty();
            }
        }
        
        return newStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient) {
            saveConfig();
        }
    }

    private static class ConfigSlot extends Slot {
        public ConfigSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public int getMaxItemCount() {
            return 1;
        }
    }
}