package ga.poglej.recycler;

import ga.poglej.recycler.recipe.BlastingRecyclingRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(RecyclerMod.MOD_ID)
public class RecyclerMod {
    static final String MOD_ID = "recycler";

    private static final Logger LOGGER = LogManager.getLogger();

    public RecyclerMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new BlastingRecyclingRecipe.Serializer().setRegistryName(new ResourceLocation(RecyclerMod.MOD_ID, "blasting_recycling")));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName(RecyclerMod.MOD_ID, "diamond_nugget"));
    }

    @ObjectHolder(RecyclerMod.MOD_ID)
    public static class Objects {
        public static final IRecipeSerializer<BlastingRecyclingRecipe> BLASTING_RECYCLING = Null();

        public static final Item DIAMOND_NUGGET = Null();

        /**
         * Returns null, but annotated @Nonnull
         * Used for @ObjectHolder initialization to prevent IntelliJ from complaining
         */
        @SuppressWarnings({"ConstantConditions", "SameReturnValue"})
        @Nonnull
        private static <T> T Null() {
            return null;
        }
    }
}
