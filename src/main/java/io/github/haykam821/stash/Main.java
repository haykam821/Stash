package io.github.haykam821.stash;

import com.mojang.serialization.Lifecycle;

import io.github.haykam821.stash.command.StashCommand;
import io.github.haykam821.stash.command.argument.StashFilterArgumentType;
import io.github.haykam821.stash.filter.StashFilterType;
import io.github.haykam821.stash.filter.StashFilterTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class Main implements ModInitializer {
	public static final String MOD_ID = "stash";

	// Arguments
	private static final Identifier STASH_FILTER_ID = new Identifier(MOD_ID, "stash_filter");
	private static final ArgumentSerializer<StashFilterArgumentType> STASH_FILTER_ARGUMENT_SERIALIZER = new ConstantArgumentSerializer<>(StashFilterArgumentType::stashFilter);

	// Stash filter types
	private static final Identifier STASH_FILTER_TYPE_ID = new Identifier(MOD_ID, "stash_filter_type");
	private static final RegistryKey<Registry<StashFilterType<?>>> STASH_FILTER_TYPE_KEY = RegistryKey.ofRegistry(STASH_FILTER_TYPE_ID);
	public static final Registry<StashFilterType<?>> STASH_FILTER_TYPE_REGISTRY = new SimpleRegistry<>(STASH_FILTER_TYPE_KEY, Lifecycle.stable());

	@Override
	public void onInitialize() {
		// Arguments
		ArgumentTypes.register(STASH_FILTER_ID.toString(), StashFilterArgumentType.class, STASH_FILTER_ARGUMENT_SERIALIZER);

		// Commands
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			StashCommand.register(dispatcher);
		});

		StashFilterTypes.register();
	}
}