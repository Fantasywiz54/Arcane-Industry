package com.firebears.arcaneindustry.tileentity;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CrystalTileEntity extends TileEntity implements ITickable, IInventory {

	private enum InventorySide {
		Import, Export;
	}
	
	private enum FilterType {
		Import, Export;
	}
	
	private ItemStack[] inventory;
	private String customName;
    private int transferCooldown = -1;
    
    private BlockPos linkedInventory;
    private BlockPos parentCrystal;
    private ArrayList<BlockPos> childCrystals = new ArrayList<BlockPos>();
    
    public ItemStack stackToLookFor;
    public boolean pingParent;
    
    public EnumFacing simultatedDirection = EnumFacing.NORTH;
	
	public CrystalTileEntity() {
		this.inventory = new ItemStack[this.getSizeInventory()];
		setCustomName("Crystal");
	}

	@Override
	public void update() {
		if (this.worldObj != null && !this.worldObj.isRemote) {

            --this.transferCooldown;
            
            if (!this.isOnTransferCooldown()) {
                this.setTransferCooldown(0);
            }
            
            // if this crystal has a linked inventory, try to pull items out of it and place items in it.
            // base on filters
            if (!this.isOnTransferCooldown() && linkedInventory != null) {
				IInventory inv = null;

				TileEntity te = this.worldObj.getTileEntity(linkedInventory);
				
				if (te instanceof IInventory) {
					inv = (IInventory)te;
					
					// search attached inventory for items to take
					for (int i = 0; i < inv.getSizeInventory(); i++) {
						boolean foundItem = false;
						
						if (doesItemMatchFilter(FilterType.Export, inv.getStackInSlot(i))) {
							foundItem = pullItemFromSlot(this, inv, i, null);
						}

						this.setTransferCooldown(8);
						
						if (foundItem) {
							break;
						}
					}
					
					// search import inventory for items to place in attached inventory
					for (int i = 0; i < getLastImportInvID() + 1; i++) {
						boolean foundItem = false;
						
						if (doesItemMatchFilter(FilterType.Import, this.getStackInSlot(i))) {
							foundItem = pullItemFromSlot(inv, this, i, null);
						}
						
						this.setTransferCooldown(8);
						
						if (foundItem) {
							break;
						}
					}
				}
            }
            
            // talk to parent crystal and try to get an item from it
            // if the export inventory isn't full
            if (!this.isOnTransferCooldown() && this.parentCrystal != null) {
            	System.out.println(parentCrystal);
            	CrystalTileEntity parent = (CrystalTileEntity)this.worldObj.getTileEntity(parentCrystal);
            	
            	for (int i = 0; i < CrystalTileEntity.getLastExportInvID(); i++) {
            		boolean foundItem = false;
					
					if (doesItemMatchFilter(FilterType.Import, this.getStackInSlot(i))) {
						foundItem = pullItemFromSlot(parent, this, i, null);
					}
					
					this.setTransferCooldown(8);
					
					if (foundItem) {
						break;
					}
            	}
            }
		}
	}
	
	private boolean doesItemMatchFilter(FilterType filter, ItemStack stack) {
		if (filter == FilterType.Import) {
			// will the import filter allow this crystal to push items into its linked inventory
			boolean itemNotInFilter = true;
			boolean filterEmpty = true;
			
			if (stack != null) {
					for (int i = 9; i < 12; i++) {
						if (inventory[i] != null) {
							filterEmpty = false;
		
							if (canCombine(inventory[i], stack)) {
								itemNotInFilter = false;
							}
						}
					}
					
					// if the stack matches one of the filter slots, allow it in
					if (!itemNotInFilter || filterEmpty) {
						return true;
					}
			} else {
				return false;
			}
		} else if (filter == FilterType.Export) {
			// will the export filter allow this crystal to pull items out of its linked inventory or pass items to other crystals
			boolean itemInFilter = false;
			boolean filterEmpty = true;
			
			if (stack != null) {
				for (int i = 21; i < 24; i++) {
					if (inventory[i] != null) {
						filterEmpty = false;
						
						if (canCombine(inventory[i], stack)) {
							itemInFilter = true;
						}
					}
				}
				
				// if the filter is empty any item can be moved
				// if the filter has the item trying to be moved, it can be moved
				if (filterEmpty || itemInFilter) {
					return true;
				}
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	// move items between import and export inventories
	private boolean switchInventories(int index) {
		// the item is in the import inventory
		if (index < 9) {
			ItemStack stack = inventory[index];
			
			if (stack != null) {
				for (int i = 12; i < 21 && stack != null && stack.stackSize > 0; i++) {
					stack = insertStack(this, stack, i, (EnumFacing)null);
				}
			}
		} else if (index > 12 && index < 21) {
			ItemStack stack = inventory[index];
			
			if (stack != null) {
				for (int i = 0; i < 9 && stack != null && stack.stackSize > 0; i++) {
					stack = insertStack(this, stack, i, (EnumFacing)null);
				}
			}
		}
		
		return false;
	}
	
	// check if parent crystal has the itemstack in its inventory
	// if the stack is in the import inventory, move 1 to the export inventory and send it to the asking crystal
	public boolean checkParentForStack(ItemStack stack) {
		for (int i = 0; i < getSizeInventory(); i++) {
			if (canCombine(stack, inventory[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	// pull items from inventories
	private static boolean pullItemFromSlot(IInventory inventoryOut , IInventory inventoryIn, int index, EnumFacing direction) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);

        if (itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2;
            
            // if the inventory for the items to be placed in is an instanceof crystaltileentity use one method, else use the other
            if (inventoryOut instanceof CrystalTileEntity) {
            	itemstack2 = putStackInCrystalInventoryAllSlots(inventoryOut, inventoryIn.decrStackSize(index, 1), direction);
            } else {
            	itemstack2 = putStackInInventoryAllSlots(inventoryOut, inventoryIn.decrStackSize(index, 1), direction);
            }

            if (itemstack2 == null || itemstack2.stackSize == 0) {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, itemstack1);
        }

        return false;
    }
	
	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
	        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
	}
	
	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
        return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }
	
    public static ItemStack putStackInCrystalInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory && side != null) {
        	
            ISidedInventory isidedinventory = (ISidedInventory)inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k = 0; k < aint.length && stack != null && stack.stackSize > 0; ++k) {
                stack = insertStack(inventoryIn, stack, aint[k], side);
            }
        }
        else {
            int i = inventoryIn.getSizeInventory();
            
            for (int j = 12; j < i && stack != null && stack.stackSize > 0 && j < getLastExportInvID() + 1; ++j) {
            	stack = insertStack(inventoryIn, stack, j, side);
            }
        }

        if (stack != null && stack.stackSize == 0) {
            stack = null;
        }

        return stack;
    }
    
    public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side) {
        if (inventoryIn instanceof ISidedInventory && side != null) {
        	
            ISidedInventory isidedinventory = (ISidedInventory)inventoryIn;
            int[] aint = isidedinventory.getSlotsForFace(side);

            for (int k = 0; k < aint.length && stack != null && stack.stackSize > 0; ++k) {
                stack = insertStack(inventoryIn, stack, aint[k], side);
            }
        }
        else {
            int i = inventoryIn.getSizeInventory();
            
            for (int j = 0; j < i && stack != null && stack.stackSize > 0; ++j) {
            	stack = insertStack(inventoryIn, stack, j, side);
            }
        }

        if (stack != null && stack.stackSize == 0) {
            stack = null;
        }

        return stack;
    }
	
	private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
        ItemStack itemstack = inventoryIn.getStackInSlot(index);

        if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
            boolean flag = false;

            if (itemstack == null) {
                //Forge: BUGFIX: Again, make things respect max stack sizes.
                int max = Math.min(stack.getMaxStackSize(), inventoryIn.getInventoryStackLimit());
                if (max >= stack.stackSize) {
	                inventoryIn.setInventorySlotContents(index, stack);
	                stack = null;
                } else {
                    inventoryIn.setInventorySlotContents(index, stack.splitStack(max));
                }
                flag = true;
            } else if (canCombine(itemstack, stack)) {
                //Forge: BUGFIX: Again, make things respect max stack sizes.
                int max = Math.min(stack.getMaxStackSize(), inventoryIn.getInventoryStackLimit());
                if (max > itemstack.stackSize) {
	                int i = max - itemstack.stackSize;
	                int j = Math.min(stack.stackSize, i);
	                stack.stackSize -= j;
	                itemstack.stackSize += j;
	                flag = j > 0;
                }
            }

            if (flag) {
                if (inventoryIn instanceof CrystalTileEntity) {
                	CrystalTileEntity tileentitycrystal = (CrystalTileEntity)inventoryIn;

                    if (tileentitycrystal.mayTransfer()) {
                    	tileentitycrystal.setTransferCooldown(8);
                    }
                    inventoryIn.markDirty();
                }
                inventoryIn.markDirty();
            }
        }

        return stack;
    }

    public boolean mayTransfer() {
        return this.transferCooldown <= 1;
    }

    public void setTransferCooldown(int ticks) {
        this.transferCooldown = ticks;
    }

    public boolean isOnTransferCooldown() {
        return this.transferCooldown > 0;
    }
    
    // inventory checking
    private boolean isFull(InventorySide side) {
    	if (side == InventorySide.Import) {
			for (int i = 0; i < 9; i++) {
				if (inventory[i] == null || inventory[i].stackSize == 0) {
					return false;
				}
			}
    	} else if (side == InventorySide.Export) {
			for (int i = 12; i < 21; i++) {
				if (inventory[i] == null || inventory[i].stackSize == 0) {
					return false;
				}
			}
    	}
    	
    	return true;
    }
    
    // ping parent crystal looking for an item
    public ItemStack pingParentCrystal() {
    	CrystalTileEntity cte = (CrystalTileEntity)this.worldObj.getTileEntity(parentCrystal);
    	ItemStack stack;
    	
    	return null;
    }
    
    // linking methods
    public BlockPos getLinkedInventory() {
    	return linkedInventory;
    }
    
    public void setLinkedInventory(BlockPos pos) {
    	linkedInventory = pos;
    }
    
    public BlockPos getParentCrystal() {
    	return parentCrystal;
    }
    
    public void setParentCrystal(BlockPos pos) {
    	parentCrystal = pos;
    }
    
    public ArrayList<BlockPos> getChildCrystals() {
    	return childCrystals;
    }
    
    public void addChildCrystal(BlockPos pos) {
    	boolean notInList = true;
    	
    	for (int i = 0; i < childCrystals.size(); i++) {
	    	if (childCrystals.get(i).getX() == pos.getX() && childCrystals.get(i).getY() == pos.getY() && childCrystals.get(i).getZ() == pos.getZ()) {
	    		notInList = false;
	    	} else if (childCrystals.get(i).getX() == this.getPos().getX() && childCrystals.get(i).getY() == this.getPos().getY() && childCrystals.get(i).getZ() == this.getPos().getZ()) {
	    		notInList = false;
	    	}
    	}
    	
    	if (notInList) {
    		childCrystals.add(pos);
    	}
    }
    
    public void removeLinkedCrystal(BlockPos pos) {
    	for (int i = 0; i < childCrystals.size(); i++) {
    		if (childCrystals.get(i).getX() == pos.getX() && childCrystals.get(i).getY() == pos.getY() && childCrystals.get(i).getZ() == pos.getZ()) {
    			childCrystals.remove(i);
    		}
    	}
    }
    
    public void clearLinkedCrystals() {
    	childCrystals.clear();
    }
    
	// tile entity methods
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (this.getStackInSlot(i) != null) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte("Slot", (byte) i);
				this.getStackInSlot(i).writeToNBT(stackTag);
				list.appendTag(stackTag);
			}
		}
		nbt.setTag("Items", list);
		
		if (this.hasCustomName()) {
			nbt.setString("CustomName", this.getCustomName());
		}

        nbt.setInteger("TransferCooldown", this.transferCooldown);

        nbt.setInteger("LinkedInvX", linkedInventory.getX());
        nbt.setInteger("LinkedInvY", linkedInventory.getY());
        nbt.setInteger("LinkedInvZ", linkedInventory.getZ());
        
        NBTTagList crystals = new NBTTagList();
        for (int i = 0; i < this.childCrystals.size(); i++) {
        	NBTTagCompound linkCryTag = new NBTTagCompound();
        	linkCryTag.setInteger("ChildCrystalX", this.childCrystals.get(i).getX());
        	linkCryTag.setInteger("ChildCrystalY", this.childCrystals.get(i).getY());
        	linkCryTag.setInteger("ChildCrystalZ", this.childCrystals.get(i).getZ());
        	crystals.appendTag(linkCryTag);
        }
        nbt.setTag("ChildCrystals", crystals);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		
		NBTTagList list = nbt.getTagList("Items", 10);
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			int slot = stackTag.getByte("Slot") & 255;
			this.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(stackTag));
		}
		
		if (nbt.hasKey("CustomName", 8)) {
			this.setCustomName(nbt.getString("CustomName"));
		}

        this.transferCooldown = nbt.getInteger("TransferCooldown");
        
        if (nbt.hasKey("LinkedInvX") && nbt.hasKey("LinkedInvY") && nbt.hasKey("LinkedInvZ")) {
        	this.linkedInventory = new BlockPos(nbt.getDouble("LinkedInvX"), nbt.getDouble("LinkedInvY"), nbt.getDouble("LinkedInvZ"));
        }
        
        NBTTagList crystals = nbt.getTagList("ChildCrystals", net.minecraftforge.common.util.Constants.NBT.TAG_INT);
        for (int i = 0; i < crystals.tagCount(); i++) {
        	NBTTagCompound linkCryTag = crystals.getCompoundTagAt(i);
        	addChildCrystal(new BlockPos(linkCryTag.getInteger("ChildCrystalX"), linkCryTag.getInteger("ChildCrystalY"), linkCryTag.getInteger("ChildCrystalZ")));
        }
	}

	public String getCustomName() {
		return this.customName;
	}
	
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	public static int getLastImportInvID() {
		return 8;
	}
	
	public static int getLastExportInvID() {
		return 20;
	}

	@Override
	public String getName() {
		return this.hasCustomName() ? this.customName : "container.crystal_tile_entity";
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null && !this.customName.equals("");
	}
	@Override
	
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

	@Override
	public int getSizeInventory() {
		return 24;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < 0 || index >= this.getSizeInventory()) {
			return null;
		}
		return this.inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.getStackInSlot(index) != null) {
			ItemStack itemstack;
			
			if (this.getStackInSlot(index).stackSize <= count) {
				itemstack = this.getStackInSlot(index);
				this.setInventorySlotContents(index, null);
				this.markDirty();
				return itemstack;
			} else {
				itemstack = this.getStackInSlot(index).splitStack(count);
				
				if (this.getStackInSlot(index).stackSize <= 0) {
					this.setInventorySlotContents(index, null);
				} else {
					// Just to show that changes happened
					this.setInventorySlotContents(index, this.getStackInSlot(index));
				}
				
				this.markDirty();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (!(index >= 9 && index < 12) || !(index >= 21 && index < 24)) {
			ItemStack stack = this.getStackInSlot(index);
			this.setInventorySlotContents(index, null);
			
			return stack;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index < 0 || index >= this.getSizeInventory()) {
			return;
		}
		
		if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		if (stack != null && stack.stackSize == 0) {
			stack = null;
		}
		
		this.inventory[index] = stack;
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.getPos()) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (!(index >= 9 && index < 12) || !(index >= 21 && index < 24)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			this.setInventorySlotContents(i, null);
		}
	}
}
