package dev.shinyepo.resourcegenerator.properties;

import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CustomProperties {
    public static final BooleanProperty OPERATIONAL = BooleanProperty.create("operational");
    public static final EnumProperty<ItemPipeConnection> NORTH = EnumProperty.create("north", ItemPipeConnection.class);
    public static final EnumProperty<ItemPipeConnection> SOUTH = EnumProperty.create("south", ItemPipeConnection.class);
    public static final EnumProperty<ItemPipeConnection> WEST = EnumProperty.create("west", ItemPipeConnection.class);
    public static final EnumProperty<ItemPipeConnection> EAST = EnumProperty.create("east", ItemPipeConnection.class);
    public static final EnumProperty<ItemPipeConnection> UP = EnumProperty.create("up", ItemPipeConnection.class);
    public static final EnumProperty<ItemPipeConnection> DOWN = EnumProperty.create("down", ItemPipeConnection.class);
}
