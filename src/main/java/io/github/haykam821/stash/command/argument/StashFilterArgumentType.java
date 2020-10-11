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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.JsonOps;

import io.github.haykam821.stash.filter.StashFilter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class StashFilterArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("{\"type\": \"stash:tag\", \"tag\": \"minecraft:leaves\"}");

	public static final DynamicCommandExceptionType MALFORMED_EXCEPTION = new DynamicCommandExceptionType(object -> {
		return new TranslatableText("arguments.stash.stash_filter.malformed", object);
	});
	public static final DynamicCommandExceptionType INVALID_EXCEPTION = new DynamicCommandExceptionType(object -> {
		return new TranslatableText("arguments.stash.stash_filter.invalid", object);
	});

	public static StashFilterArgumentType stashFilter() {
		return new StashFilterArgumentType();
	}

	public static StashFilter getStashFilter(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		String string = StringArgumentType.getString(context, name);
		try {
			JsonElement element = new JsonParser().parse(string);
			if (!element.isJsonObject()) {
				throw INVALID_EXCEPTION.create(string);
			}
			JsonObject object = element.getAsJsonObject();
			
			DataResult<StashFilter> dataResult = StashFilter.TYPE_CODEC.parse(JsonOps.INSTANCE, object);
			if (dataResult.error().isPresent()) {
				PartialResult<StashFilter> partial = dataResult.error().get();
				throw INVALID_EXCEPTION.create(partial.message());
			} else if (!dataResult.result().isPresent()) {
				throw INVALID_EXCEPTION.create(string);
			}

			return dataResult.result().get();
		} catch (JsonParseException exception) {
			throw MALFORMED_EXCEPTION.create(string);
		}
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		return StringArgumentType.greedyString().parse(reader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}