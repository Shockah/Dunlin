package io.shockah.dunlin.terrariadb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.complexcommandparser.ComplexParser;
import io.shockah.dunlin.complexcommandparser.ComplexParserArgument;
import io.shockah.dunlin.complexcommandparser.ComplexParserResult;
import io.shockah.dunlin.complexcommandparser.Range;
import io.shockah.skylark.func.Func1;
import net.dv8tion.jda.events.message.GenericMessageEvent;

public class ItemCommand extends NamedCommand<List<Func1<Item, Boolean>>, List<Item>> {
	private final TerrariaDBPlugin plugin;
	private final ComplexParser parser = new ComplexParser();
	
	public ItemCommand(TerrariaDBPlugin plugin) {
		super("items", "item");
		this.plugin = plugin;
		
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "accessory"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "weapon"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "melee"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "ranged"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "magic"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "summon"));
		parser.arguments.add(ComplexParserArgument.Type.Flag.make(false, "thrown"));
		
		parser.arguments.add(ComplexParserArgument.Type.Range.make(true, "id"));
		parser.arguments.add(ComplexParserArgument.Type.Range.make(true, "rare"));
		parser.arguments.add(ComplexParserArgument.Type.Range.make(true, "damage"));
		parser.arguments.add(ComplexParserArgument.Type.Range.make(true, "crit"));
		
		parser.arguments.add(ComplexParserArgument.Type.String.make(true, "exactname"));
		parser.arguments.add(ComplexParserArgument.Type.String.make(true, "regexname"));
		parser.arguments.add(ComplexParserArgument.Type.String.make(true, "name", "fuzzyname"));
	}

	@Override
	public List<Func1<Item, Boolean>> parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		ComplexParserResult result = parser.parse(input);
		
		boolean b;
		Range r;
		
		List<Func1<Item, Boolean>> damageTypeFilters = new ArrayList<>();
		List<Func1<Item, Boolean>> otherFilters = new ArrayList<>();
		
		while (b = result.consumeBoolean("accessory")) {
			final boolean fb = b;
			otherFilters.add(item -> item.accessory == fb);
		}
		while (b = result.consumeBoolean("weapon")) {
			final boolean fb = b;
			otherFilters.add(item -> (item.damage > 0) == fb);
		}
		
		while (b = result.consumeBoolean("melee")) {
			final boolean fb = b;
			damageTypeFilters.add(item -> item.melee == fb);
		}
		while (b = result.consumeBoolean("ranged")) {
			final boolean fb = b;
			damageTypeFilters.add(item -> item.ranged == fb);
		}
		while (b = result.consumeBoolean("magic")) {
			final boolean fb = b;
			damageTypeFilters.add(item -> item.magic == fb);
		}
		while (b = result.consumeBoolean("summon")) {
			final boolean fb = b;
			damageTypeFilters.add(item -> item.summon == fb);
		}
		while (b = result.consumeBoolean("thrown")) {
			final boolean fb = b;
			damageTypeFilters.add(item -> item.thrown == fb);
		}
		
		while ((r = result.consumeRange("id")) != null) {
			final Range fr = r;
			otherFilters.add(item -> fr.contains(item.id));
		}
		while ((r = result.consumeRange("value")) != null) {
			final Range fr = r;
			otherFilters.add(item -> fr.contains(item.value));
		}
		while ((r = result.consumeRange("rare")) != null) {
			final Range fr = r;
			otherFilters.add(item -> fr.contains(item.rare));
		}
		while ((r = result.consumeRange("damage")) != null) {
			final Range fr = r;
			otherFilters.add(item -> fr.contains(item.damage));
		}
		while ((r = result.consumeRange("crit")) != null) {
			final Range fr = r;
			otherFilters.add(item -> fr.contains(item.crit));
		}
		
		List<Func1<Item, Boolean>> filters = new ArrayList<>();
		if (!damageTypeFilters.isEmpty())
			filters.add(buildORFilter(damageTypeFilters));
		filters.addAll(otherFilters);
		
		return filters;
	}
	
	private Func1<Item, Boolean> buildORFilter(List<Func1<Item, Boolean>> subfilters) {
		return item -> {
			for (Func1<Item, Boolean> subfilter : subfilters) {
				if (subfilter.call(item))
					return true;
			}
			return false;
		};
	}

	@Override
	public CommandResult<List<Item>> call(CommandCall call, List<Func1<Item, Boolean>> input) {
		Stream<Item> items = plugin.getItems().stream();
		for (Func1<Item, Boolean> f : input) {
			items = items.filter(item -> f.call(item));
		}
		
		List<Item> filtered = items.collect(Collectors.toList());
		return CommandResult.of(filtered, formatMessage(filtered));
	}
	
	private String formatMessage(List<Item> items) {
		return items.toString(); //TODO: proper implementation
	}
}