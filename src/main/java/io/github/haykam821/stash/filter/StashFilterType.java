package io.github.haykam821.stash.filter;

import com.mojang.serialization.Codec;

public class StashFilterType<T extends StashFilter> {
	private final Codec<T> codec;

	public StashFilterType(Codec<T> codec) {
		this.codec = codec;
	}

	public Codec<T> getCodec() {
		return this.codec;
	}
}