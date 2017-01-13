package io.shockah.dunlin.terrariadb;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import io.shockah.dunlin.commands.CommandCall;
import io.shockah.dunlin.commands.CommandParseException;
import io.shockah.dunlin.commands.CommandResult;
import io.shockah.dunlin.commands.ErrorCommandResult;
import io.shockah.dunlin.commands.NamedCommand;
import io.shockah.dunlin.commands.ValueMessageEmbedCommandResult;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;

public class ItemCommand extends NamedCommand<ItemCommand.Input, Item> {
	private final TerrariaDBPlugin plugin;
	
	public ItemCommand(TerrariaDBPlugin plugin) {
		super("titem");
		this.plugin = plugin;
	}
	
	@Override
	public Input parseInput(GenericMessageEvent e, String input) throws CommandParseException {
		try {
			return new IDInput(Integer.parseInt(input));
		} catch (NumberFormatException ex) {
			//TODO: by fuzzy name
			throw new IllegalArgumentException();
		}
	}

	@Override
	public CommandResult<Item> call(CommandCall call, Input input) {
		Item item = input.find(plugin.getItems());
		if (item == null)
			return new ErrorCommandResult<>(String.format("No item matching '%s' found.", input));
		
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(String.format("#%d %s", item.id, item.name));
		embed.setThumbnail("http://tapi.axxim.net/req/terrariaid/Item_757.png");
		
		if (item.tooltip.length != 0)
			embed.setDescription(StringUtils.join(item.tooltip, "\n"));
		
		if (item.damage > 0) {
			embed.addField(item.getDamageTypeName(), String.valueOf(item.damage), true);
			embed.addField("Critical chance", String.format("%d%%", item.crit + 4), true);
		}
		
		embed.addField("Value", item.getFormattedValue(), true);
		
		return new ValueMessageEmbedCommandResult<>(item, embed.build());
	}

	public static abstract class Input {
		public abstract Item find(List<Item> items);
	}
	
	public static class IDInput extends Input {
		public final int id;
		
		public IDInput(int id) {
			this.id = id;
		}
		
		@Override
		public String toString() {
			return String.format("ID: %d", id);
		}

		@Override
		public Item find(List<Item> items) {
			return items.stream().filter(item -> item.id == id).limit(1).findFirst().orElse(null);
		}
	}
}