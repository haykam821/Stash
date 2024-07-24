package io.github.haykam821.stash.network.packet;

import io.github.haykam821.stash.Main;
import io.github.haykam821.stash.command.StashEntrySort;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SetStashSortC2SPayload(StashEntrySort sort) implements CustomPayload {
	public static final PacketCodec<ByteBuf, SetStashSortC2SPayload> CODEC = PacketCodec.tuple(
		StashEntrySort.PACKET_CODEC, SetStashSortC2SPayload::sort,
		SetStashSortC2SPayload::new
	);

	public static final CustomPayload.Id<SetStashSortC2SPayload> ID = new CustomPayload.Id<>(Main.id("set_stash_sort"));

	@Override
	public CustomPayload.Id<? extends SetStashSortC2SPayload> getId() {
		return ID;
	}
}
