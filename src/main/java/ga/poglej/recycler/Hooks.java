package ga.poglej.recycler;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class Hooks {
    /**
     * @return 1 if can be smelted, 0 if it can not, -1 to let the original method handle it
     */
    public static int canSmelt(AbstractFurnaceTileEntity tileEntity, @Nullable IRecipe recipe, NonNullList<ItemStack> items) {
        System.out.println("HELLO FROM HOOK canSmelt");
        return -1;
    }

    /**
     * @return true if handled, false to let the original method handle it
     */
    public static boolean finishSmelting(AbstractFurnaceTileEntity tileEntity, @Nullable IRecipe recipe, NonNullList<ItemStack> items) {
        System.out.println("HELLO FROM HOOK finishSmelting");
        return false;
    }
}
