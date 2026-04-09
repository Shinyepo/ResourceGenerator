package dev.shinyepo.resourcegenerator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.controllers.DeviceNetworkController;
import dev.shinyepo.resourcegenerator.pipes.CustomBlockStateModel;
import dev.shinyepo.resourcegenerator.pipes.builders.CustomBlockDefinition;
import dev.shinyepo.resourcegenerator.registries.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry.ENTITIES;
import static dev.shinyepo.resourcegenerator.registries.BlockRegistry.BLOCKS;
import static dev.shinyepo.resourcegenerator.registries.BlockTypeRegistry.BLOCK_TYPE;
import static dev.shinyepo.resourcegenerator.registries.CreativeTabRegistry.CREATIVE_TABS;
import static dev.shinyepo.resourcegenerator.registries.DataComponentRegistry.DATA_COMPONENTS;
import static dev.shinyepo.resourcegenerator.registries.ItemRegistry.ITEMS;
import static dev.shinyepo.resourcegenerator.registries.MenuRegistry.MENUS;
import static dev.shinyepo.resourcegenerator.registries.PriceDefinitionRegistry.PRICES;
import static dev.shinyepo.resourcegenerator.registries.UpgradeRegistry.UPGRADES;

@Mod(ResourceGenerator.MODID)
public class ResourceGenerator {
    public static final String MODID = "resourcegenerator";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ResourceGenerator(IEventBus modEventBus, ModContainer modContainer) {
        DATA_COMPONENTS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        UPGRADES.register(modEventBus);
        PRICES.register(modEventBus);
        BLOCK_TYPE.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ResourceGenerator::registerRegistries);
        modEventBus.addListener(ResourceGenerator::registerDefinitions);
        modEventBus.addListener(CapabilityRegistry::registerCapabilities);
        modEventBus.addListener(PacketRegistry::registerPayloadHandler);
        modEventBus.addListener(DataPackRegistry::registerDatapackRegistries);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ResourceGenerator::registerCommands);
        NeoForge.EVENT_BUS.addListener(DeviceNetworkController::onServerTick);
        NeoForge.EVENT_BUS.addListener(AccountController::onServerTick);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    public void onServerStopping(ServerStoppingEvent event) {
        event.getServer().getAllLevels()
                .forEach(level -> {
                    DeviceNetworkController.unloadData(level);
                    AccountController.unloadData(level);
                });
    }

    public static void registerDefinitions(RegisterBlockStateModels event) {
        event.registerDefinition(CustomBlockDefinition.ID, CustomBlockDefinition.CODEC);
        event.registerModel(CustomBlockStateModel.Unbaked.ID, CustomBlockStateModel.Unbaked.CODEC);
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(UpgradeRegistry.UPGRADE_REGISTRY);
        event.register(PriceDefinitionRegistry.PRICE_REGISTRY);
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // ./acc <add||remove> int
        // Adds or removes balance from users account
        dispatcher.register(
                Commands.literal("acc")
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", integer())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            long newBalance = changeAccountAmount(source.getLevel(), Objects.requireNonNull(source.getPlayer()), getInteger(context, "amount"));
                                            source.sendSuccess(() -> Component.literal("New account balance: " + newBalance), true);
                                            return 1;
                                        })

                                ))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", integer())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            long newBalance = changeAccountAmount(source.getLevel(), Objects.requireNonNull(source.getPlayer()), -getInteger(context, "amount"));
                                            source.sendSuccess(() -> Component.literal("New account balance: " + newBalance), true);
                                            return 1;
                                        })

                                )
                        )
        );

        // ./upgrade buy ResourceLocation tier
        // Buys upgrade if balance allows
        dispatcher.register(
                Commands.literal("upgrade")
                        .then(Commands.literal("buy")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .then(Commands.argument("tier", integer())
                                                .executes(ctx -> {
                                                    CommandSourceStack source = ctx.getSource();
                                                    AccountController controller = AccountController.getInstance(source.getLevel());
                                                    UUID accId = controller.getOrCreateAccount(Objects.requireNonNull(source.getPlayer()).nameAndId().id());

                                                    Identifier id = IdentifierArgument.getId(ctx, "id");
                                                    Integer tier = getInteger(ctx, "tier");
                                                    controller.buyUpgrade(source.getLevel(), accId, source.getPlayer().nameAndId().id(), id, tier);

                                                    MutableComponent text = Component.literal("Bought: ").append(Component.translatable("gui." + id.toLanguageKey()));
                                                    source.sendSuccess(() -> text, true);
                                                    return 1;
                                                })))
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", IdentifierArgument.id())
                                        .executes(ctx -> {
                                            CommandSourceStack source = ctx.getSource();
                                            AccountController controller = AccountController.getInstance(source.getLevel());
                                            UUID accId = controller.getOrCreateAccount(Objects.requireNonNull(source.getPlayer()).nameAndId().id());

                                            Identifier id = IdentifierArgument.getId(ctx, "id");
                                            controller.removeUpgrade(accId, id);
                                            source.sendSuccess(() -> Component.literal("Removed upgrade: ").append(Component.translatable("gui." + id.toLanguageKey())), true);
                                            return 1;
                                        })))
        );
    }

    private static long changeAccountAmount(ServerLevel level, ServerPlayer player, long amount) {
        AccountController controller = AccountController.getInstance(level);
        UUID accid = controller.getOrCreateAccount(player.nameAndId().id());
        if (accid != null) {
            controller.changeAccountBalance(level, accid, amount);
        }
        return controller.getAccountBalance(accid);
    }
}
