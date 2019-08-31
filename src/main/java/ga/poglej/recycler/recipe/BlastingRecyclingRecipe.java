package ga.poglej.recycler.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import ga.poglej.recycler.RecyclerMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BlastingRecyclingRecipe extends BlastingRecipe {
    private int maxOutput;
    private NonNullList<ItemStack> results;

    private BlastingRecyclingRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, NonNullList<ItemStack> results, float experienceIn, int cookTimeIn, int maxOutput) {
        super(idIn, groupIn, ingredientIn, ItemStack.EMPTY, experienceIn, cookTimeIn);
        this.maxOutput = maxOutput;
        this.results = results;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        ItemStack input = inv.getStackInSlot(0);
        double percentDamage = (double) (input.getMaxDamage() - input.getDamage()) / input.getMaxDamage();
        double percentResources = percentDamage * this.maxOutput;
        List<Double> resultAmounts = this.results.stream()
                .map(stack -> stack.getCount() * percentResources)
                .collect(Collectors.toList());
        Optional<Integer> index = IntStream.range(0, resultAmounts.size()).boxed()
                .filter(i -> resultAmounts.get(i) >= 1)
                .min(Comparator.comparing(resultAmounts::get));
        if (!index.isPresent()) {
            index = IntStream.range(0, this.results.size()).boxed()
                    .filter(i -> !this.results.get(i).isEmpty())
                    .max(Comparator.comparing((i) -> this.results.get(i).getCount()));
        }
        if (!index.isPresent()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = this.results.get(index.get()).copy();
        result.setCount(Math.max(resultAmounts.get(index.get()).intValue(), 1));
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecyclerMod.Objects.BLASTING_RECYCLING;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BlastingRecyclingRecipe> {
        @Override
        public BlastingRecyclingRecipe read(final ResourceLocation recipeId, final JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            JsonElement jsonIngredient = JSONUtils.isJsonArray(json, "ingredient")
                    ? JSONUtils.getJsonArray(json, "ingredient")
                    : JSONUtils.getJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.deserialize(jsonIngredient);

            JsonArray jsonResults = JSONUtils.getJsonArray(json, "results");
            if (jsonResults.size() < 1) {
                throw new JsonSyntaxException("Need at least one result in results array");
            }
            NonNullList<ItemStack> results = NonNullList.withSize(jsonResults.size(), ItemStack.EMPTY);
            for (int i = 0; i < jsonResults.size(); i++) {
                JsonElement jsonResult = jsonResults.get(i);
                if (jsonResult.isJsonObject()) {
                    results.set(i, ShapedRecipe.deserializeItem((JsonObject) jsonResult));
                } else {
                    String itemName = JSONUtils.getString(jsonResult, "result");
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                    if (item == null)
                        throw new JsonSyntaxException("Unknown item '" + itemName + "'");
                    results.set(i, new ItemStack(item));
                }
            }

            float experience = JSONUtils.getFloat(json, "experience", 0.0F);
            int cookTime = JSONUtils.getInt(json, "cookingtime", 100);
            int maxOutput = JSONUtils.getInt(json, "max_output", 1);
            return new BlastingRecyclingRecipe(recipeId, group, ingredient, results, experience, cookTime, maxOutput);
        }

        @Override
        public BlastingRecyclingRecipe read(final ResourceLocation recipeId, final PacketBuffer buffer) {
            String group = buffer.readString(32767);
            Ingredient ingredient = Ingredient.read(buffer);
            int resultsSize = buffer.readVarInt();
            final NonNullList<ItemStack> results = NonNullList.withSize(resultsSize, ItemStack.EMPTY);
            for (int i = 0; i < results.size(); i++) {
                results.set(i, buffer.readItemStack());
            }
            float experience = buffer.readFloat();
            int cookTime = buffer.readVarInt();
            int maxOutput = buffer.readVarInt();
            return new BlastingRecyclingRecipe(recipeId, group, ingredient, results, experience, cookTime, maxOutput);
        }

        @Override
        public void write(final PacketBuffer buffer, final BlastingRecyclingRecipe recipe) {
            buffer.writeString(recipe.group);
            recipe.ingredient.write(buffer);
            buffer.writeVarInt(recipe.results.size());
            for (ItemStack result : recipe.results) {
                buffer.writeItemStack(result);
            }
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeVarInt(recipe.maxOutput);
        }
    }
}
