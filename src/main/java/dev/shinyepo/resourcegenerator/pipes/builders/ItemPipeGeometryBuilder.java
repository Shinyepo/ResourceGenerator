package dev.shinyepo.resourcegenerator.pipes.builders;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.shinyepo.resourcegenerator.ResourceGenerator;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection;
import dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipePatterns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import static dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipeConnection.*;
import static dev.shinyepo.resourcegenerator.pipes.helpers.ItemPipePatterns.SpriteIdx.*;
import static dev.shinyepo.resourcegenerator.properties.CustomProperties.*;

public final class ItemPipeGeometryBuilder {
    private TextureAtlasSprite spriteNormalCable;
    private TextureAtlasSprite spriteNoneCable;
    private TextureAtlasSprite spriteEndCable;
    private TextureAtlasSprite spriteCornerCable;
    private TextureAtlasSprite spriteThreeCable;
    private TextureAtlasSprite spriteCrossCable;
    private TextureAtlasSprite spriteSideInsert;
    private TextureAtlasSprite spriteSideExtract;
    private TextureAtlasSprite spriteInsert;
    private TextureAtlasSprite spriteExtract;


    static {
        // For all possible patterns we define the sprite to use and the rotation. Note that each
        // pattern looks at the existance of a cable section for each of the four directions
        // excluding the one we are looking at.
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, false, false, false), ItemPipePatterns.QuadSetting.of(SPRITE_NONE, 0));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, false, false, false), ItemPipePatterns.QuadSetting.of(SPRITE_END, 3));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, true, false, false), ItemPipePatterns.QuadSetting.of(SPRITE_END, 0));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, false, true, false), ItemPipePatterns.QuadSetting.of(SPRITE_END, 1));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, false, false, true), ItemPipePatterns.QuadSetting.of(SPRITE_END, 2));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, true, false, false), ItemPipePatterns.QuadSetting.of(SPRITE_CORNER, 0));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, true, true, false), ItemPipePatterns.QuadSetting.of(SPRITE_CORNER, 1));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, false, true, true), ItemPipePatterns.QuadSetting.of(SPRITE_CORNER, 2));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, false, false, true), ItemPipePatterns.QuadSetting.of(SPRITE_CORNER, 3));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, true, false, true), ItemPipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 0));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, false, true, false), ItemPipePatterns.QuadSetting.of(SPRITE_STRAIGHT, 1));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, true, true, false), ItemPipePatterns.QuadSetting.of(SPRITE_THREE, 0));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(false, true, true, true), ItemPipePatterns.QuadSetting.of(SPRITE_THREE, 1));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, false, true, true), ItemPipePatterns.QuadSetting.of(SPRITE_THREE, 2));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, true, false, true), ItemPipePatterns.QuadSetting.of(SPRITE_THREE, 3));
        ItemPipePatterns.PATTERNS.put(ItemPipePatterns.Pattern.of(true, true, true, true), ItemPipePatterns.QuadSetting.of(SPRITE_CROSS, 0));
    }

    private void initTextures() {
        if (spriteExtract == null) {
            spriteInsert = getTexture("block/pipe/connector_insert");
            spriteExtract = getTexture("block/pipe/connector_extract");
            spriteSideInsert = getTexture("block/pipe/connector_side_insert");
            spriteSideExtract = getTexture("block/pipe/connector_side_extract");
            spriteNormalCable = getTexture("block/pipe/normal");
            spriteNoneCable = getTexture("block/pipe/none");
            spriteEndCable = getTexture("block/pipe/end");
            spriteCornerCable = getTexture("block/pipe/corner");
            spriteThreeCable = getTexture("block/pipe/three");
            spriteCrossCable = getTexture("block/pipe/cross");
        }
    }

    private static final ConcurrentMap<GeometryKey, QuadCollection> GEOMETRY_CACHE = new ConcurrentHashMap<>();

    public ItemPipeGeometryBuilder() {
    }

    private static TextureAtlasSprite getTexture(String path) {
        return Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(ResourceGenerator.MODID, path)));
    }

    private TextureAtlasSprite getSpriteNormal(ItemPipePatterns.SpriteIdx idx) {
        initTextures();
        return switch (idx) {
            case SPRITE_NONE -> spriteNoneCable;
            case SPRITE_END -> spriteEndCable;
            case SPRITE_STRAIGHT -> spriteNormalCable;
            case SPRITE_CORNER -> spriteCornerCable;
            case SPRITE_THREE -> spriteThreeCable;
            case SPRITE_CROSS -> spriteCrossCable;
        };
    }

    public QuadCollection getOrBuild(BlockState state) {
        GeometryKey key = new GeometryKey(state);
        return GEOMETRY_CACHE.computeIfAbsent(key, this::build);
    }

    private QuadCollection build(GeometryKey key) {
        initTextures();
        ItemPipeConnection north, south, west, east, up, down;
        if (key == null) {
            north = south = west = east = up = down = ItemPipeConnection.NONE;
        } else {
            north = key.north;
            south = key.south;
            west = key.west;
            east = key.east;
            up = key.up;
            down = key.down;
        }

        double o = .34;      // Thickness of the cable. .0 would be full block, .5 is infinitely thin.
        double p = .1;      // Thickness of the connector as it is put on the connecting block
        double q = .2;      // The wideness of the connector

        TextureAtlasSprite spriteCable = spriteNormalCable;
        Function<ItemPipePatterns.SpriteIdx, TextureAtlasSprite> spriteGetter = this::getSpriteNormal;

        QuadCollection.Builder builder = new QuadCollection.Builder();

        if (up == CABLE) {
            builder.addUnculledFace(quad(v(1 - o, 1, o), v(1 - o, 1, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1, 1 - o), v(o, 1, o), v(o, 1 - o, o), v(o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1, o), v(1 - o, 1, o), v(1 - o, 1 - o, o), v(o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1, 1 - o), v(o, 1, 1 - o), spriteCable));
        } else if (up == INSERT || up == EXTRACT) {
            builder.addUnculledFace(quad(v(1 - o, 1 - p, o), v(1 - o, 1 - p, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - p, 1 - o), v(o, 1 - p, o), v(o, 1 - o, o), v(o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - p, o), v(1 - o, 1 - p, o), v(1 - o, 1 - o, o), v(o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - p, 1 - o), v(o, 1 - p, 1 - o), spriteCable));

            TextureAtlasSprite spriteConnector = up == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = up == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(1 - q, 1 - p, q), v(1 - q, 1, q), v(1 - q, 1, 1 - q), v(1 - q, 1 - p, 1 - q), spriteSide));
            builder.addUnculledFace(quad(v(q, 1 - p, 1 - q), v(q, 1, 1 - q), v(q, 1, q), v(q, 1 - p, q), spriteSide));
            builder.addUnculledFace(quad(v(q, 1, q), v(1 - q, 1, q), v(1 - q, 1 - p, q), v(q, 1 - p, q), spriteSide));
            builder.addUnculledFace(quad(v(q, 1 - p, 1 - q), v(1 - q, 1 - p, 1 - q), v(1 - q, 1, 1 - q), v(q, 1, 1 - q), spriteSide));

            builder.addUnculledFace(quad(v(q, 1 - p, q), v(1 - q, 1 - p, q), v(1 - q, 1 - p, 1 - q), v(q, 1 - p, 1 - q), spriteConnector));
            builder.addUnculledFace(quad(v(q, 1, q), v(q, 1, 1 - q), v(1 - q, 1, 1 - q), v(1 - q, 1, q), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(west, south, east, north);
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), v(o, 1 - o, o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        if (down == CABLE) {
            builder.addUnculledFace(quad(v(1 - o, o, o), v(1 - o, o, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, 0, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(o, o, o), v(o, 0, o), v(o, 0, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(1 - o, o, o), v(1 - o, 0, o), v(o, 0, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 0, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, o, 1 - o), v(o, o, 1 - o), spriteCable));
        } else if (down == INSERT || down == EXTRACT) {
            builder.addUnculledFace(quad(v(1 - o, o, o), v(1 - o, o, 1 - o), v(1 - o, p, 1 - o), v(1 - o, p, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(o, o, o), v(o, p, o), v(o, p, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(1 - o, o, o), v(1 - o, p, o), v(o, p, o), spriteCable));
            builder.addUnculledFace(quad(v(o, p, 1 - o), v(1 - o, p, 1 - o), v(1 - o, o, 1 - o), v(o, o, 1 - o), spriteCable));

            TextureAtlasSprite spriteConnector = down == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = down == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(1 - q, 0, q), v(1 - q, p, q), v(1 - q, p, 1 - q), v(1 - q, 0, 1 - q), spriteSide));
            builder.addUnculledFace(quad(v(q, 0, 1 - q), v(q, p, 1 - q), v(q, p, q), v(q, 0, q), spriteSide));
            builder.addUnculledFace(quad(v(q, p, q), v(1 - q, p, q), v(1 - q, 0, q), v(q, 0, q), spriteSide));
            builder.addUnculledFace(quad(v(q, 0, 1 - q), v(1 - q, 0, 1 - q), v(1 - q, p, 1 - q), v(q, p, 1 - q), spriteSide));

            builder.addUnculledFace(quad(v(q, p, 1 - q), v(1 - q, p, 1 - q), v(1 - q, p, q), v(q, p, q), spriteConnector));
            builder.addUnculledFace(quad(v(q, 0, 1 - q), v(q, 0, q), v(1 - q, 0, q), v(1 - q, 0, 1 - q), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(west, north, east, south);
            builder.addUnculledFace(quad(v(o, o, o), v(1 - o, o, o), v(1 - o, o, 1 - o), v(o, o, 1 - o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        if (east == CABLE) {
            builder.addUnculledFace(quad(v(1, 1 - o, 1 - o), v(1, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(1, o, o), v(1, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(1, 1 - o, o), v(1, o, o), v(1 - o, o, o), v(1 - o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(1, o, 1 - o), v(1, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), spriteCable));
        } else if (east == INSERT || east == EXTRACT) {
            builder.addUnculledFace(quad(v(1 - p, 1 - o, 1 - o), v(1 - p, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(1 - p, o, o), v(1 - p, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(1 - p, 1 - o, o), v(1 - p, o, o), v(1 - o, o, o), v(1 - o, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(1 - p, o, 1 - o), v(1 - p, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), spriteCable));

            TextureAtlasSprite spriteConnector = east == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = east == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(1 - p, 1 - q, 1 - q), v(1, 1 - q, 1 - q), v(1, 1 - q, q), v(1 - p, 1 - q, q), spriteSide));
            builder.addUnculledFace(quad(v(1 - p, q, q), v(1, q, q), v(1, q, 1 - q), v(1 - p, q, 1 - q), spriteSide));
            builder.addUnculledFace(quad(v(1 - p, 1 - q, q), v(1, 1 - q, q), v(1, q, q), v(1 - p, q, q), spriteSide));
            builder.addUnculledFace(quad(v(1 - p, q, 1 - q), v(1, q, 1 - q), v(1, 1 - q, 1 - q), v(1 - p, 1 - q, 1 - q), spriteSide));

            builder.addUnculledFace(quad(v(1 - p, q, 1 - q), v(1 - p, 1 - q, 1 - q), v(1 - p, 1 - q, q), v(1 - p, q, q), spriteConnector));
            builder.addUnculledFace(quad(v(1, q, 1 - q), v(1, q, q), v(1, 1 - q, q), v(1, 1 - q, 1 - q), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(down, north, up, south);
            builder.addUnculledFace(quad(v(1 - o, o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        if (west == CABLE) {
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(0, 1 - o, o), v(0, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(o, o, 1 - o), v(0, o, 1 - o), v(0, o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - o, o), v(o, o, o), v(0, o, o), v(0, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(0, 1 - o, 1 - o), v(0, o, 1 - o), spriteCable));
        } else if (west == INSERT || west == EXTRACT) {
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(p, 1 - o, o), v(p, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(o, o, 1 - o), v(p, o, 1 - o), v(p, o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, 1 - o, o), v(o, o, o), v(p, o, o), v(p, 1 - o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(p, 1 - o, 1 - o), v(p, o, 1 - o), spriteCable));

            TextureAtlasSprite spriteConnector = west == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = west == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(0, 1 - q, 1 - q), v(p, 1 - q, 1 - q), v(p, 1 - q, q), v(0, 1 - q, q), spriteSide));
            builder.addUnculledFace(quad(v(0, q, q), v(p, q, q), v(p, q, 1 - q), v(0, q, 1 - q), spriteSide));
            builder.addUnculledFace(quad(v(0, 1 - q, q), v(p, 1 - q, q), v(p, q, q), v(0, q, q), spriteSide));
            builder.addUnculledFace(quad(v(0, q, 1 - q), v(p, q, 1 - q), v(p, 1 - q, 1 - q), v(0, 1 - q, 1 - q), spriteSide));

            builder.addUnculledFace(quad(v(p, q, q), v(p, 1 - q, q), v(p, 1 - q, 1 - q), v(p, q, 1 - q), spriteConnector));
            builder.addUnculledFace(quad(v(0, q, q), v(0, q, 1 - q), v(0, 1 - q, 1 - q), v(0, 1 - q, q), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(down, south, up, north);
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(o, o, o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        if (north == CABLE) {
            builder.addUnculledFace(quad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 0), v(o, 1 - o, 0), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 0), v(1 - o, o, 0), v(1 - o, o, o), v(o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(1 - o, o, 0), v(1 - o, 1 - o, 0), v(1 - o, 1 - o, o), v(1 - o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(o, 1 - o, o), v(o, 1 - o, 0), v(o, o, 0), spriteCable));
        } else if (north == INSERT || north == EXTRACT) {
            builder.addUnculledFace(quad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, p), v(o, 1 - o, p), spriteCable));
            builder.addUnculledFace(quad(v(o, o, p), v(1 - o, o, p), v(1 - o, o, o), v(o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(1 - o, o, p), v(1 - o, 1 - o, p), v(1 - o, 1 - o, o), v(1 - o, o, o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, o), v(o, 1 - o, o), v(o, 1 - o, p), v(o, o, p), spriteCable));

            TextureAtlasSprite spriteConnector = north == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = north == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(q, 1 - q, p), v(1 - q, 1 - q, p), v(1 - q, 1 - q, 0), v(q, 1 - q, 0), spriteSide));
            builder.addUnculledFace(quad(v(q, q, 0), v(1 - q, q, 0), v(1 - q, q, p), v(q, q, p), spriteSide));
            builder.addUnculledFace(quad(v(1 - q, q, 0), v(1 - q, 1 - q, 0), v(1 - q, 1 - q, p), v(1 - q, q, p), spriteSide));
            builder.addUnculledFace(quad(v(q, q, p), v(q, 1 - q, p), v(q, 1 - q, 0), v(q, q, 0), spriteSide));

            builder.addUnculledFace(quad(v(q, q, p), v(1 - q, q, p), v(1 - q, 1 - q, p), v(q, 1 - q, p), spriteConnector));
            builder.addUnculledFace(quad(v(q, q, 0), v(q, 1 - q, 0), v(1 - q, 1 - q, 0), v(1 - q, q, 0), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(west, up, east, down);
            builder.addUnculledFace(quad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, o, o), v(o, o, o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        if (south == CABLE) {
            builder.addUnculledFace(quad(v(o, 1 - o, 1), v(1 - o, 1 - o, 1), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, 1), v(o, o, 1), spriteCable));
            builder.addUnculledFace(quad(v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, 1), v(1 - o, o, 1), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1), v(o, 1 - o, 1), v(o, 1 - o, 1 - o), v(o, o, 1 - o), spriteCable));
        } else if (south == INSERT || south == EXTRACT) {
            builder.addUnculledFace(quad(v(o, 1 - o, 1 - p), v(1 - o, 1 - o, 1 - p), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, 1 - p), v(o, o, 1 - p), spriteCable));
            builder.addUnculledFace(quad(v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - p), v(1 - o, o, 1 - p), spriteCable));
            builder.addUnculledFace(quad(v(o, o, 1 - p), v(o, 1 - o, 1 - p), v(o, 1 - o, 1 - o), v(o, o, 1 - o), spriteCable));

            TextureAtlasSprite spriteConnector = south == INSERT ? spriteInsert : spriteExtract;
            TextureAtlasSprite spriteSide = south == INSERT ? spriteSideInsert : spriteSideExtract;
            builder.addUnculledFace(quad(v(q, 1 - q, 1), v(1 - q, 1 - q, 1), v(1 - q, 1 - q, 1 - p), v(q, 1 - q, 1 - p), spriteSide));
            builder.addUnculledFace(quad(v(q, q, 1 - p), v(1 - q, q, 1 - p), v(1 - q, q, 1), v(q, q, 1), spriteSide));
            builder.addUnculledFace(quad(v(1 - q, q, 1 - p), v(1 - q, 1 - q, 1 - p), v(1 - q, 1 - q, 1), v(1 - q, q, 1), spriteSide));
            builder.addUnculledFace(quad(v(q, q, 1), v(q, 1 - q, 1), v(q, 1 - q, 1 - p), v(q, q, 1 - p), spriteSide));

            builder.addUnculledFace(quad(v(q, 1 - q, 1 - p), v(1 - q, 1 - q, 1 - p), v(1 - q, q, 1 - p), v(q, q, 1 - p), spriteConnector));
            builder.addUnculledFace(quad(v(q, 1 - q, 1), v(q, q, 1), v(1 - q, q, 1), v(1 - q, 1 - q, 1), spriteSide));
        } else {
            ItemPipePatterns.QuadSetting pattern = ItemPipePatterns.findPattern(west, down, east, up);
            builder.addUnculledFace(quad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), spriteGetter.apply(pattern.sprite()), pattern.rotation()));
        }

        return builder.build();
    }

    public static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static BakedQuad quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, int rotation) {
        return switch (rotation) {
            case 0 -> quad(v1, v2, v3, v4, sprite);
            case 1 -> quad(v2, v3, v4, v1, sprite);
            case 2 -> quad(v3, v4, v1, v2, sprite);
            case 3 -> quad(v4, v1, v2, v3, sprite);
            default -> quad(v1, v2, v3, v4, sprite);
        };
    }

    public static BakedQuad quad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();

        BakedQuad[] quad = new BakedQuad[1];
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(sprite, ChunkSectionLayer.SOLID, RenderTypes.solidMovingBlock());
        builder.setDirection(Direction.getNearest((int) normal.x, (int) normal.y, (int) normal.z, Direction.NORTH));
        putVertex(builder, normal, v1.x, v1.y, v1.z, 0, 0, sprite);
        putVertex(builder, normal, v2.x, v2.y, v2.z, 0, 1, sprite);
        putVertex(builder, normal, v3.x, v3.y, v3.z, 1, 1, sprite);
        putVertex(builder, normal, v4.x, v4.y, v4.z, 1, 0, sprite);
        quad[0] = builder.bakeQuad();
        return quad[0];
    }

    private static void putVertex(VertexConsumer builder, Position normal,
                                  double x, double y, double z, float u, float v,
                                  TextureAtlasSprite sprite) {
        float iu = sprite.getU(u);
        float iv = sprite.getV(v);
        builder.addVertex((float) x, (float) y, (float) z)
                .setUv(iu, iv)
                .setUv2(0, 0)
                .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
    }

    public static class GeometryKey {
        private final ItemPipeConnection north;
        private final ItemPipeConnection south;
        private final ItemPipeConnection west;
        private final ItemPipeConnection east;
        private final ItemPipeConnection up;
        private final ItemPipeConnection down;

        public GeometryKey(BlockState state) {
            this.north = state.getValue(NORTH);
            this.south = state.getValue(SOUTH);
            this.west = state.getValue(WEST);
            this.east = state.getValue(EAST);
            this.up = state.getValue(UP);
            this.down = state.getValue(DOWN);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GeometryKey that)) return false;
            return up == that.up && west == that.west && east == that.east && down == that.down && north == that.north && south == that.south;
        }

        @Override
        public int hashCode() {
            return Objects.hash(north, south, west, east, up, down);
        }
    }
}
