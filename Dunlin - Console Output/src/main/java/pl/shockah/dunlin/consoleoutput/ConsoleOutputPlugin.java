package pl.shockah.dunlin.consoleoutput;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import pl.shockah.dunlin.plugin.ListenerPlugin;
import pl.shockah.dunlin.plugin.PluginManager;

public class ConsoleOutputPlugin extends ListenerPlugin {
	public ConsoleOutputPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onMessageReceived(MessageReceivedEvent event) {
		Message message = event.getMessage();
		
		String rawContent = message.getRawContent();
		if (!rawContent.isEmpty()) {
			for (String line : rawContent.split("\\n")) {
				System.out.println(String.format("%s->#%s | <%s#%s> %s",
					event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName(), event.getAuthor().getDiscriminator(), line));
			}
		}
		
		for (Attachment attachment : message.getAttachments()) {
			System.out.println(String.format("%s->#%s | <%s#%s> Attachment: ",
				event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName(), event.getAuthor().getDiscriminator(), attachment.getUrl()));
		}
	}
	
	@Override
	protected void onMessageUpdate(MessageUpdateEvent event) {
		Message message = event.getMessage();
		
		String rawContent = message.getRawContent();
		if (!rawContent.isEmpty()) {
			for (String line : rawContent.split("\\n")) {
				System.out.println(String.format("%s->#%s | * <%s#%s> %s",
					event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName(), event.getAuthor().getDiscriminator(), line));
			}
		}
		
		for (Attachment attachment : message.getAttachments()) {
			System.out.println(String.format("%s->#%s | * <%s#%s> Attachment: %s",
				event.getGuild().getName(), event.getChannel().getName(), event.getAuthor().getName(), event.getAuthor().getDiscriminator(), attachment.getUrl()));
		}
	}
}