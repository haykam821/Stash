package io.github.haykam821.stash.command.argument;

import java.util.Arrays;
import java.util.Collection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import io.github.haykam821.stash.filter.StashFilter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class StashFilterArgumentType implements ArgumentType<StashFilter> {
	private static final Collection<String> EXAMPLES = Arrays.asList("{\"type\": \"stash:tag\", \"tag\": \"minecraft:leaves\"}");

	public static final DynamicCommandExceptionType MALFORMED_EXCEPTION = new DynamicCommandExceptionType(object -> {
		return Text.translatable("arguments.stash.stash_filter.malformed", object);
	});
	public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> {
		return Text.translatable("arguments.stash.stash_filter.invalid", object);
	});

	private final DynamicOps<JsonElement> ops;

	public StashFilterArgumentType(CommandRegistryAccess registryAccess) {
		this.ops = RegistryOps.of(JsonOps.INSTANCE, registryAccess);
	}

	public static StashFilterArgumentType stashFilter(CommandRegistryAccess registryAccess) {
		return new StashFilterArgumentType(registryAccess);
	}

	public static StashFilter getStashFilter(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, StashFilter.class);
	}

	@Override
	public StashFilter parse(StringReader reader) throws CommandSyntaxException {
		String string = StringArgumentType.greedyString().parse(reader);
		try {
			JsonElement element = JsonParser.parseString(string);
			if (!element.isJsonObject()) {
				throw INVALID_EXCEPTION.create(string);
			}
			JsonObject object = element.getAsJsonObject();

			return StashFilter.TYPE_CODEC
				.parse(this.ops, object)
				.getOrThrow(error -> INVALID_EXCEPTION.createWithContext(reader, error));
		} catch (JsonParseException exception) {
			throw MALFORMED_EXCEPTION.createWithContext(reader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}