package net.js.random;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SkinPayload(String username, byte[] image) implements CustomPacketPayload {
    public static final int MAX_IMAGE_SIZE = 1_048_576;
    public static final Type<SkinPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(RandomnessMod.MOD_ID, "skin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SkinPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.PLAYER_NAME,
        SkinPayload::username,
        ByteBufCodecs.byteArray(MAX_IMAGE_SIZE),
        SkinPayload::image,
        SkinPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
