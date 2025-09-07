package dev.shinyepo.resourcegenerator.registries;

import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.datacomponents.IdCardData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DataComponentRegistry {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ResourceGenerator.MODID);

    public static final Supplier<DataComponentType<IdCardData>> ID_CARD = DATA_COMPONENTS.registerComponentType(
            "id_card_data",
            builder -> builder
                    .persistent(IdCardData.CODEC)
                    .networkSynchronized(IdCardData.STREAM_CODEC)
    );
}
