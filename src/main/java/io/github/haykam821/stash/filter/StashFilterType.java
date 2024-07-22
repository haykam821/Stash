package io.github.haykam821.stash.filter;

import com.mojang.serialization.MapCodec;

public class StashFilterType<T extends StashFilter> {
	private final MapCodec<T> codec;

	public StashFilterType(MapCodec<T> codec) {
		this.codec = codec;
	}

	public MapCodec<T> getCodec() {
		return this.codec;
	}
}