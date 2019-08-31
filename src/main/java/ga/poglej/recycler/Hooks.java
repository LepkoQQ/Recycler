package ga.poglej.recycler;

import ga.poglej.recycler.recipe.BlastingRecyclingRecipe;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class Hooks {
    /**
     * @return 1 if can be smelted, 0 if it can not, -1 to let the original method handle it
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static int canSmelt(AbstractFurnaceTileEntity tileEntity, @Nullable IRecipe<IInventory> recipe, NonNullList<ItemStack> items) {
        if (recipe instanceof BlastingRecyclingRecipe) {
            ItemStack input = items.get(0);
            if (input.isEmpty()) {
                return 0;
            }
            ItemStack result = recipe.getRecipeOutput();
            if (result.isEmpty() && recipe.isDynamic()) {
                result = recipe.getCraftingResult(new Inventory(input));
            }
            if (result.isEmpty()) {
                return 0;
            }
            ItemStack outputSlotStack = items.get(2);
            if (outputSlotStack.isEmpty() || outputSlotStack.isItemEqual(result)) {
                int newCount = outputSlotStack.getCount() + result.getCount();
                if (newCount <= tileEntity.getInventoryStackLimit() && newCount <= outputSlotStack.getMaxStackSize()) {
                    return 1;
                }
            }
            return 0;
        }
        return -1;
    }

    /**
     * @return true if handled, false to let the original method handle it
     */
    @SuppressWarnings("unused")
    public static boolean finishSmelting(AbstractFurnaceTileEntity tileEntity, @Nullable IRecipe<IInventory> recipe, NonNullList<ItemStack> items) {
        if (recipe instanceof BlastingRecyclingRecipe) {
            if (canSmelt(tileEntity, recipe, items) == 1) {
                ItemStack input = items.get(0);
                ItemStack result = recipe.getRecipeOutput();
                if (result.isEmpty() && recipe.isDynamic()) {
                    result = recipe.getCraftingResult(new Inventory(input));
                }
                ItemStack outputSlotStack = items.get(2);
                if (outputSlotStack.isEmpty()) {
                    items.set(2, result.copy());
                } else if (outputSlotStack.isItemEqual(result)) {
                    outputSlotStack.grow(result.getCount());
                }
                if (tileEntity.getWorld() != null && !tileEntity.getWorld().isRemote) {
                    tileEntity.setRecipeUsed(recipe);
                }
                input.shrink(1);
            }
            return true;
        }
        return false;
    }
}
