package io.github.haykam821.stash.network.packet;

import io.github.haykam821.stash.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class OpenStashS2CPayload implements CustomPayload {
	public static final OpenStashS2CPayload INSTANCE = new OpenStashS2CPayload();

	public static final PacketCodec<ByteBuf, OpenStashS2CPayload> CODEC = PacketCodec.unit(INSTANCE);
	public static final CustomPayload.Id<OpenStashS2CPayload> ID = new CustomPayload.Id<>(Main.id("open_stash"));

	private OpenStashS2CPayload() {
		return;
	}

	@Override
	public CustomPayload.Id<? extends OpenStashS2CPayload> getId() {
		return ID;
	}
}
