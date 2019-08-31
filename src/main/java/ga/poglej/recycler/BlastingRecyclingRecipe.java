package ga.poglej.recycler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BlastingRecyclingRecipe extends BlastingRecipe {
    private int maxOutput;

    public BlastingRecyclingRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookTimeIn, int maxOutput) {
        super(idIn, groupIn, ingredientIn, resultIn, experienceIn, cookTimeIn);
        this.maxOutput = maxOutput;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack result = this.result;
        result.setCount(this.maxOutput + 2);
        return result;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack result = this.result;
        result.setCount(this.maxOutput + 5);
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecyclerMod.BLASTING_RECYCLING;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BlastingRecyclingRecipe> {
        @Override
        public BlastingRecyclingRecipe read(final ResourceLocation recipeId, final JsonObject json) {
            String s = JSONUtils.getString(json, "group", "");
            JsonElement jsonelement = (JsonElement) (JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
            Ingredient ingredient = Ingredient.deserialize(jsonelement);
            //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
            if (!json.has("result"))
                throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack itemstack;
            if (json.get("result").isJsonObject())
                itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            else {
                String s1 = JSONUtils.getString(json, "result");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                itemstack = new ItemStack(Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
                    return new IllegalStateException("Item: " + s1 + " does not exist");
                }));
            }
            float f = JSONUtils.getFloat(json, "experience", 0.0F);
            int i = JSONUtils.getInt(json, "cookingtime", 100);
            int maxOutput = JSONUtils.getInt(json, "max_output", 1);
            return new BlastingRecyclingRecipe(recipeId, s, ingredient, itemstack, f, i, maxOutput);
        }

        @Override
        public BlastingRecyclingRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
            String s = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack itemstack = buffer.readItemStack();
            float f = buffer.readFloat();
            int i = buffer.readVarInt();
            int maxOutput = buffer.readVarInt();
            return new BlastingRecyclingRecipe(recipeId, s, ingredient, itemstack, f, i, maxOutput);
        }

        @Override
        public void write(final PacketBuffer buffer, final BlastingRecyclingRecipe recipe) {
            buffer.writeString(recipe.group);
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.result);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeVarInt(recipe.maxOutput);
        }
    }
}
