package io.github.haykam821.stash.network.packet;

import io.github.haykam821.stash.Main;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;

public record ExtractStashC2SPayload(Item item, boolean all, boolean drop) implements CustomPayload {
	public static final PacketCodec<RegistryByteBuf, ExtractStashC2SPayload> CODEC = PacketCodec.tuple(
		PacketCodecs.registryValue(RegistryKeys.ITEM), ExtractStashC2SPayload::item,
		PacketCodecs.BOOL, ExtractStashC2SPayload::all,
		PacketCodecs.BOOL, ExtractStashC2SPayload::drop,
		ExtractStashC2SPayload::new
	);

	public static final CustomPayload.Id<ExtractStashC2SPayload> ID = new CustomPayload.Id<>(Main.id("extract_stash"));

	@Override
	public CustomPayload.Id<? extends ExtractStashC2SPayload> getId() {
		return ID;
	}
}
