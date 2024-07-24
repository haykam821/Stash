package io.github.haykam821.stash.network.packet;

import io.github.haykam821.stash.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record InsertStashC2SPayload(boolean all) implements CustomPayload {
	public static final PacketCodec<ByteBuf, InsertStashC2SPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.BOOL, InsertStashC2SPayload::all,
		InsertStashC2SPayload::new
	);

	public static final CustomPayload.Id<InsertStashC2SPayload> ID = new CustomPayload.Id<>(Main.id("insert_stash"));

	@Override
	public CustomPayload.Id<? extends InsertStashC2SPayload> getId() {
		return ID;
	}
}
