package ga.poglej.recycler;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RecyclerMod.MOD_ID)
public class RecyclerMod {
    public static final String MOD_ID = "recycler";

    private static final Logger LOGGER = LogManager.getLogger();

    @ObjectHolder(RecyclerMod.MOD_ID)
    public static final IRecipeSerializer<BlastingRecyclingRecipe> BLASTING_RECYCLING = null;

    public RecyclerMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new BlastingRecyclingRecipe.Serializer().setRegistryName(new ResourceLocation(RecyclerMod.MOD_ID, "blasting_recycling")));
    }
}
