/*package io.shockah.dunlin.terrariadb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import io.shockah.dunlin.argparser.ArgumentSchema;
import io.shockah.dunlin.argparser.ArgumentType;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.commands.ValueCommandResult;
import io.shockah.skylark.func.Func1;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ItemsCommand extends NamedCommand<List<Func1<Item, Boolean>>, List<Item>> {
	private final TerrariaDBPlugin plugin;
	private final ArgumentSchema schema;
	
	public ItemsCommand(TerrariaDBPlugin plugin) {
		super("titems");
		this.plugin = plugin;
		
		schema = new ArgumentSchema.Builder()
			.with("accessory", ArgumentType.Bool)
			.with("weapon", ArgumentType.Bool)
			.with("melee", ArgumentType.Bool)
			.with("ranged", ArgumentType.Bool)
			.with("magic", ArgumentType.Bool)
			.with("summon", ArgumentType.Bool)
			.with("thrown", ArgumentType.Bool)

			.with("id", ArgumentType.IntRange)
			.with("rare", ArgumentType.IntRange)
			.with("damage", ArgumentType.IntRange)
			.with("crit", ArgumentType.IntRange)

			.with("exactname", ArgumentType.String)
			.with("regexname", ArgumentType.String)
			.with("name", ArgumentType.String)
			
			.build();
	}

	@Override
	public List<Func1<Item, Boolean>> parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		ComplexParserResult result = parser.parse(input);
		
		boolean b;
		IntRange ir;
		DoubleRange dr;
		
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
		
		while ((ir = result.consumeRange("id")) != null) {
			final IntRange fr = ir;
			otherFilters.add(item -> fr.contains(item.id));
		}
		while ((ir = result.consumeRange("value")) != null) {
			final IntRange fr = ir;
			otherFilters.add(item -> fr.contains(item.value));
		}
		while ((ir = result.consumeRange("rare")) != null) {
			final IntRange fr = ir;
			otherFilters.add(item -> fr.contains(item.rare));
		}
		while ((ir = result.consumeRange("damage")) != null) {
			final IntRange fr = ir;
			otherFilters.add(item -> fr.contains(item.damage));
		}
		while ((ir = result.consumeRange("crit")) != null) {
			final IntRange fr = ir;
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
		return new ValueCommandResult<>(filtered, formatMessage(filtered));
	}
	
	private String formatMessage(List<Item> items) {
		return items.toString(); //TODO: proper implementation
	}
}*/