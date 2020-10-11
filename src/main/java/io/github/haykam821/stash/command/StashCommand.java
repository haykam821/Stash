package io.github.haykam821.stash.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.haykam821.stash.Main;
import io.github.haykam821.stash.command.argument.StashFilterArgumentType;
import io.github.haykam821.stash.component.StashComponent;
import io.github.haykam821.stash.filter.AlwaysStashFilter;
import io.github.haykam821.stash.filter.HandStashFilter;
import io.github.haykam821.stash.filter.PredicateStashFilter;
import io.github.haykam821.stash.filter.SlotIndexStashFilter;
import io.github.haykam821.stash.filter.SlotRange;
import io.github.haykam821.stash.filter.StashFilter;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StashCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("stash");
				
		// List
		LiteralArgumentBuilder<ServerCommandSource> listBuilder = CommandManager.literal("list");
		LiteralArgumentBuilder<ServerCommandSource> reversedBuilder = CommandManager.literal("reversed");

		for (StashEntrySort stashEntrySort : StashEntrySort.values()) {
			// Ascending
			listBuilder.then(CommandManager.literal(stashEntrySort.asString())
				.executes(context -> {
					return StashCommand.list(context, stashEntrySort.getComparator(), Integer.MAX_VALUE);
				})
					.then(CommandManager.argument("maxItems", IntegerArgumentType.integer(1))
					.executes(context -> {
						return StashCommand.list(context, stashEntrySort.getComparator(), IntegerArgumentType.getInteger(context, "maxItems"));
					})));
	
			// Descending
			reversedBuilder.then(CommandManager.literal(stashEntrySort.asString())
				.executes(context -> {
					return StashCommand.list(context, stashEntrySort.getComparator().reversed(), Integer.MAX_VALUE);
				})
					.then(CommandManager.argument("maxItems", IntegerArgumentType.integer(1))
					.executes(context -> {
						return StashCommand.list(context, stashEntrySort.getComparator().reversed(), IntegerArgumentType.getInteger(context, "maxItems"));
					})));
		}

		listBuilder.then(CommandManager.literal("random")
			.executes(context -> {
				StashComponent stash = Main.STASH.get(context.getSource().getPlayer());

				List<Object2IntMap.Entry<Item>> entries = new ArrayList<>(stash.getEntries());
				Collections.shuffle(entries);
		
				return StashCommand.list(context, entries, Integer.MAX_VALUE);
			})
				.then(CommandManager.argument("maxItems", IntegerArgumentType.integer(1))
				.executes(context -> {
					StashComponent stash = Main.STASH.get(context.getSource().getPlayer());

					List<Object2IntMap.Entry<Item>> entries = new ArrayList<>(stash.getEntries());
					Collections.shuffle(entries);
			
					int maxItems = IntegerArgumentType.getInteger(context, "maxItems");
					return StashCommand.list(context, entries.stream().limit(maxItems).collect(Collectors.toList()), maxItems);
				})));

		listBuilder.then(reversedBuilder);
		builder.then(listBuilder);
				
		// Query
		LiteralArgumentBuilder<ServerCommandSource> queryBuilder = CommandManager.literal("query");
		queryBuilder.then(CommandManager.argument("item", ItemStackArgumentType.itemStack()).executes(context -> {
			return StashCommand.query(context, ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false));
		}));
		builder.then(queryBuilder);
				
		// Retrieve
		LiteralArgumentBuilder<ServerCommandSource> retrieveBuilder = CommandManager.literal("retrieve");
		retrieveBuilder
			.then(CommandManager.argument("item", ItemStackArgumentType.itemStack())
			.suggests((context, suggestionsBuilder) -> {
				StashComponent stash = Main.STASH.get(context.getSource().getPlayer());
				for (Object2IntMap.Entry<Item> entry : stash.getEntries()) {
					Identifier id = Registry.ITEM.getId(entry.getKey());
					suggestionsBuilder.suggest(id.toString());
				}
				return suggestionsBuilder.buildFuture();
			})
			.executes(context -> {
				return StashCommand.retrieve(context, Integer.MAX_VALUE);
			})
				.then(CommandManager.argument("maxCount", IntegerArgumentType.integer(1))
				.executes(context -> {
					return StashCommand.retrieve(context, IntegerArgumentType.getInteger(context, "maxCount"));
				})));
		builder.then(retrieveBuilder);

		// Store
		LiteralArgumentBuilder<ServerCommandSource> storeBuilder = CommandManager.literal("store");

		storeBuilder.then(CommandManager.argument("stashFilter", StashFilterArgumentType.stashFilter()).executes(context -> {
			return StashCommand.store(context, StashFilterArgumentType.getStashFilter(context, "stashFilter"));
		}));
		storeBuilder.then(CommandManager.literal("all").executes(context -> {
			return StashCommand.store(context, new AlwaysStashFilter());
		}));

		storeBuilder.then(CommandManager.literal("item")
			.then(CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate()).executes(context -> {
				return StashCommand.store(context, new PredicateStashFilter(ItemPredicateArgumentType.getItemPredicate(context, "item")));
			})));
		storeBuilder.then(CommandManager.literal("slot")
			.then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot()).executes(context -> {
				int index = ItemSlotArgumentType.getItemSlot(context, "slot");
				return StashCommand.store(context, new SlotIndexStashFilter(index, index));
			})));
		
		LiteralArgumentBuilder<ServerCommandSource> slotsBuilder = CommandManager.literal("slots");
		for (SlotRange slotRange : SlotRange.values()) {
			slotsBuilder.then(CommandManager.literal(slotRange.asString()).executes(context -> {
				return StashCommand.store(context, new SlotIndexStashFilter(slotRange.getMin(), slotRange.getMax()));
			}));
		}

		slotsBuilder.then(CommandManager.literal("hand").executes(context -> {
			return StashCommand.store(context, new HandStashFilter());
		}));

		storeBuilder.then(slotsBuilder);
		builder.then(storeBuilder);
		
		dispatcher.register(builder);
	}

	private static int list(CommandContext<ServerCommandSource> context, Comparator<Object2IntMap.Entry<Item>> comparator, int maxItems) throws CommandSyntaxException {
		StashComponent stash = Main.STASH.get(context.getSource().getPlayer());

		List<Object2IntMap.Entry<Item>> entries = stash.getEntries().stream().sorted(comparator).limit(maxItems).collect(Collectors.toList());
		return StashCommand.list(context, entries, maxItems);
	}

	private static int list(CommandContext<ServerCommandSource> context, List<Object2IntMap.Entry<Item>> entries, int maxItems) throws CommandSyntaxException {
		int items = 0;
		List<Text> texts = new ArrayList<>();
		for (Object2IntMap.Entry<Item> entry : entries) {
			items += entry.getIntValue();

			Text stackText = entry.getKey().getDefaultStack().toHoverableText();
			texts.add(new TranslatableText("commands.stash.stash.list.entry", stackText, entry.getIntValue()));
		}

		context.getSource().sendFeedback(new TranslatableText("commands.stash.stash.list.header" + (maxItems == Integer.MAX_VALUE ? "" : ".partial"), items, entries.size()), false);
		for (Text text : texts) {
			context.getSource().sendFeedback(text, false);
		}

		return 1;
	}

	private static int query(CommandContext<ServerCommandSource> context, ItemStack stack) throws CommandSyntaxException {
		StashComponent stash = Main.STASH.get(context.getSource().getPlayer());
		int count = stash.getCount(stack.getItem());

		context.getSource().sendFeedback(new TranslatableText("commands.stash.stash.query", count, stack.toHoverableText()), false);
		return 1;
	}

	private static int retrieve(CommandContext<ServerCommandSource> context, int maxCount) throws CommandSyntaxException {
		ItemStackArgument itemArgument = ItemStackArgumentType.getItemStackArgument(context, "item");
		Item item = itemArgument.getItem();

		PlayerEntity player = context.getSource().getPlayer();
		StashComponent stash = Main.STASH.get(player);

		int totalRetrievableCount = Math.min(maxCount, stash.getCount(item));
		int remainingCount = totalRetrievableCount;

		// Initial pass for non-completely filled stacks of the same type
		for (int slot = 0; slot < 36; slot++) {
			if (remainingCount == 0) {
				break;
			}

			ItemStack stack = player.inventory.getStack(slot);
			if (item == stack.getItem() && StashComponent.isInsertable(stack)) {
				int difference = Math.min(remainingCount, item.getMaxCount() - stack.getCount());

				stack.increment(difference);
				remainingCount -= difference;
			}
		}

		// Second pass to create new stacks
		for (int slot = 0; slot < 36; slot++) {
			if (remainingCount == 0) {
				break;
			}
			
			ItemStack stack = player.inventory.getStack(slot);
			if (stack.isEmpty()) {
				int count = Math.min(remainingCount, item.getMaxCount());
				player.inventory.setStack(slot, new ItemStack(item, count));
				remainingCount -= count;
			}
		}

		int retrievedCount = totalRetrievableCount - remainingCount;
		stash.decreaseCount(item, retrievedCount);

		Text stackText = itemArgument.createStack(1, false).toHoverableText();
		context.getSource().sendFeedback(new TranslatableText("commands.stash.stash.retrieve", retrievedCount, stackText), false);

		return 1;
	}

	private static int store(CommandContext<ServerCommandSource> context, StashFilter stashFilter) throws CommandSyntaxException {
		PlayerEntity player = context.getSource().getPlayer();
		StashComponent stash = Main.STASH.get(player);
	
		List<ItemStack> matchedStacks = new ArrayList<>();
		int matchedCount = 0;
		for (int slot = 0; slot < player.inventory.size(); slot++) {
			ItemStack stack = player.inventory.getStack(slot);
			if (StashComponent.isInsertable(stack) && stashFilter.matches(matchedStacks, stack, player, slot)) {
				matchedStacks.add(stack.copy());
				matchedCount += stack.getCount();

				stash.insertStack(stack);
				player.inventory.removeStack(slot);
			}
		}

		if (matchedStacks.size() == 1) {
			context.getSource().sendFeedback(new TranslatableText("commands.stash.stash.store.success.single", matchedStacks.get(0).getCount(), matchedStacks.get(0).toHoverableText()), false);
		} else {
			context.getSource().sendFeedback(new TranslatableText("commands.stash.stash.store.success.multiple", matchedStacks.size(), matchedCount), false);
		}
		return 1;
	}
}