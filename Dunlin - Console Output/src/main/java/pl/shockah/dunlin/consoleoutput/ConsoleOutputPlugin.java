package pl.shockah.dunlin.consoleoutput;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import org.apache.commons.lang3.StringUtils;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;

public class ConsoleOutputPlugin extends ListenerPlugin {
	public ConsoleOutputPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		logMessage(event.getMessage(), false);
	}
	
	@Override
	protected void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		logMessage(event.getMessage(), true);
	}

	protected void logMessage(Message message, boolean edit) {
		Guild guild = message.getGuild();
		TextChannel channel = message.getTextChannel();

		String rawContent = message.getRawContent();
		if (!StringUtils.isEmpty(rawContent)) {
			for (String line : rawContent.split("\\n")) {
				System.out.println(String.format("%s->#%s |%s<%s#%s> %s",
						guild.getName(), channel.getName(), edit ? " * " : " ", message.getAuthor().getName(), message.getAuthor().getDiscriminator(), line));
			}
		}

		for (Attachment attachment : message.getAttachments()) {
			System.out.println(String.format("%s->#%s |%s<%s#%s> Attachment: %s",
					guild.getName(), channel.getName(), edit ? " * " : " ", message.getAuthor().getName(), message.getAuthor().getDiscriminator(), attachment.getUrl()));
		}

		for (MessageEmbed embed : message.getEmbeds()) {
			System.out.println(String.format("%s->#%s |%s<%s#%s> Embed:",
					guild.getName(), channel.getName(), edit ? " * " : " ", message.getAuthor().getName(), message.getAuthor().getDiscriminator()));

			if (embed.getAuthor() != null)
				System.out.println(String.format("\tAuthor: %s", embed.getAuthor().getName()));
			if (!StringUtils.isEmpty(embed.getTitle()))
				System.out.println(String.format("\tTitle: %s", embed.getTitle()));

			for (MessageEmbed.Field field : embed.getFields()) {
				System.out.println(String.format("\t%s: %s", field.getName(), field.getValue()));
			}

			if (!StringUtils.isEmpty(embed.getDescription()))
				System.out.println(String.format("\t%s", embed.getDescription()));
			if (embed.getFooter() != null)
				System.out.println(String.format("\tFooter: %s", embed.getFooter().getText()));
		}
	}
}