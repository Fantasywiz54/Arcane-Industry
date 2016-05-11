package com.firebears.arcaneindustry.client.gui;

import com.firebears.arcaneindustry.tileentity.CrystalTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCrystalTileEntity extends Container {

	private CrystalTileEntity te;
	
	public ContainerCrystalTileEntity(IInventory playerInv, CrystalTileEntity te) {
		this.te = te;
		
		// Tile Entity Input Slots, slot 0-8, slot ids 0-8
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(te, x, 8 + x * 18, 34));
		}
		
		// Tile Entity Output Slots, slot 9-17, slot ids 9-17
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(te, x + 9, 8 + x * 18, 76));
		}
		
		// Player Inventory, slot 9-35, slot ids 17-43
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 111 + y * 18));
			}
		}
		
		// Player Inventory, slot 0-8, slot ids 43-52
		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 169));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.te.isUseableByPlayer(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot) {
		ItemStack previous = null;
	    Slot slot = (Slot) this.inventorySlots.get(fromSlot);

	    if (slot != null && slot.getHasStack()) {
	        ItemStack current = slot.getStack();
	        previous = current.copy();

	        // [...] Custom behaviour
	        if (fromSlot < 9) {
	        	// from TE inv to Player inventory
	        	if (!this.mergeItemStack(current, 9, 45, true)) {
	        		return null;
	        	}
	        } else {
	        	// from player inventory to te inventory
	        	if (!this.mergeItemStack(current, 0, 9, false)) {
	        		return null;
	        	}
	        }

	        if (current.stackSize == 0)
	            slot.putStack((ItemStack) null);
	        else
	            slot.onSlotChanged();

	        if (current.stackSize == previous.stackSize)
	            return null;
	        slot.onPickupFromSlot(player, current);
	    }
	    return previous;
	}
}
