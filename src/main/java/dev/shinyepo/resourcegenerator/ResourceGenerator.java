package dev.shinyepo.resourcegenerator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import dev.shinyepo.resourcegenerator.controllers.AccountController;
import dev.shinyepo.resourcegenerator.registries.CapabilityRegistry;
import dev.shinyepo.resourcegenerator.registries.PacketRegistry;
import dev.shinyepo.resourcegenerator.registries.UpgradeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;

import java.util.UUID;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static dev.shinyepo.resourcegenerator.registries.BlockEntityRegistry.ENTITIES;
import static dev.shinyepo.resourcegenerator.registries.BlockRegistry.BLOCKS;
import static dev.shinyepo.resourcegenerator.registries.CreativeTabRegistry.CREATIVE_TABS;
import static dev.shinyepo.resourcegenerator.registries.DataComponentRegistry.DATA_COMPONENTS;
import static dev.shinyepo.resourcegenerator.registries.ItemRegistry.ITEMS;
import static dev.shinyepo.resourcegenerator.registries.MenuRegistry.MENUS;
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

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ResourceGenerator::registerRegistries);
        modEventBus.addListener(CapabilityRegistry::registerCapabilities);
        modEventBus.addListener(PacketRegistry::registerPayloadHandler);
        NeoForge.EVENT_BUS.addListener(this::onServerStopping);
        NeoForge.EVENT_BUS.addListener(ResourceGenerator::registerCommands);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    public void onServerStopping(ServerStoppingEvent event) {
    }

    public static void registerRegistries(NewRegistryEvent event) {
        System.out.println("registering");
        event.register(UpgradeRegistry.UPGRADE_REGISTRY);
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
                                            long newBalance = changeAccountAmount(source.getLevel(), source.getPlayer(), getInteger(context, "amount"));
                                            source.sendSuccess(() -> Component.literal("New account balance: " + newBalance), true);
                                            return 1;
                                        })

                                ))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("amount", integer())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            long newBalance = changeAccountAmount(source.getLevel(), source.getPlayer(), -getInteger(context, "amount"));
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
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .then(Commands.argument("tier", integer())
                                                .executes(ctx -> {
                                                    CommandSourceStack source = ctx.getSource();
                                                    AccountController controller = AccountController.getInstance(source.getLevel());
                                                    UUID accId = controller.getOrCreateAccount(source.getPlayer().getGameProfile().getId());

                                                    ResourceLocation id = ResourceLocationArgument.getId(ctx, "id");
                                                    Integer tier = getInteger(ctx, "tier");
                                                    boolean result = controller.buyUpgrade(accId, id, tier);
                                                    if (result) {
                                                        MutableComponent text = Component.literal("Bought: ").append(Component.translatable("gui." + id.toLanguageKey()));
                                                        source.sendSuccess(() -> text, true);
                                                    } else {
                                                        source.sendFailure(Component.literal("Not enough balance to buy ").append(Component.translatable("gui." + id.toLanguageKey())));
                                                    }
                                                    return 1;
                                                })))
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("id", ResourceLocationArgument.id())
                                        .executes(ctx -> {
                                            CommandSourceStack source = ctx.getSource();
                                            AccountController controller = AccountController.getInstance(source.getLevel());
                                            UUID accId = controller.getOrCreateAccount(source.getPlayer().getGameProfile().getId());

                                            ResourceLocation id = ResourceLocationArgument.getId(ctx, "id");
                                            controller.removeUpgrade(accId, id);
                                            source.sendSuccess(() -> Component.literal("Removed upgrade: ").append(Component.translatable("gui." + id.toLanguageKey())), true);
                                            return 1;
                                        })))
        );
    }

    private static long changeAccountAmount(ServerLevel level, ServerPlayer player, long amount) {
        AccountController controller = AccountController.getInstance(level);
        UUID accid = controller.getOrCreateAccount(player.getGameProfile().getId());
        if (accid != null) {
            controller.changeAccountBalance(accid, amount);
        }
        return controller.getAccountBalance(accid);
    }
}
