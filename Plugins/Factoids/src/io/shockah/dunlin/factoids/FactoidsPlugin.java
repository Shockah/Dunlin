package io.shockah.dunlin.factoids;

import java.util.HashMap;
import io.shockah.dunlin.MessageMedium;
import io.shockah.dunlin.commands.CommandsPlugin;
import io.shockah.dunlin.db.DbObject;
import io.shockah.dunlin.factoids.db.Factoid;
import io.shockah.dunlin.permissions.PermissionsPlugin;
import io.shockah.dunlin.plugin.Plugin;
import io.shockah.dunlin.plugin.PluginManager;
import io.shockah.dunlin.timedmessages.TimedMessagesPlugin;
import io.shockah.dunlin.util.ReadWriteMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;

public class FactoidsPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency("io.shockah.dunlin.permissions")
	protected Plugin permissionsPlugin;
	
	@Dependency("io.shockah.dunlin.timedmessages")
	protected Plugin timedMessagesPlugin;
	
	protected FactoidCommandProvider commandProvider;
	
	protected ReadWriteMap<String, FactoidType> types = new ReadWriteMap<>(new HashMap<>());
	
	private RememberCommand rememberCommand;
	private ForgetCommand forgetCommand;
	private FactoidInfoCommand factoidInfoCommand;
	
	public FactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		getConfig().putDefault("defaultContext", Factoid.Context.Server.name());
		commandsPlugin.addProvider(commandProvider = new FactoidCommandProvider(this));
		addType(new SimpleFactoidType());
		commandsPlugin.addNamedCommands(
			rememberCommand = new RememberCommand(this),
			forgetCommand = new ForgetCommand(this),
			factoidInfoCommand = new FactoidInfoCommand(this)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeProvider(commandProvider);
		commandsPlugin.removeNamedCommands(
			rememberCommand,
			forgetCommand,
			factoidInfoCommand
		);
	}
	
	public void addType(FactoidType type) {
		types.put(type.type, type);
	}
	
	public void removeType(FactoidType type) {
		types.remove(type.type);
	}
	
	public FactoidType getType(String type) {
		return types.get(type);
	}
	
	public Factoid.Context getDefaultContext() {
		return Factoid.Context.valueOf(getConfig().getString("defaultContext"));
	}
	
	public Factoid findActiveFactoid(GenericMessageEvent e, String name) {
		Factoid factoid;
		
		if (e instanceof GenericGuildMessageEvent) {
			factoid = findActiveFactoid(e, name, Factoid.Context.Channel);
			if (factoid != null)
				return factoid;
			
			factoid = findActiveFactoid(e, name, Factoid.Context.Server);
			if (factoid != null)
				return factoid;
		}
		
		return findActiveFactoid(e, name, Factoid.Context.Global);
	}
	
	public Factoid findActiveFactoid(GenericMessageEvent e, String name, Factoid.Context context) {
		if (context == null)
			return findActiveFactoid(e, name);
		
		Guild guild = null;
		TextChannel channel = null;
		if (e instanceof GenericGuildMessageEvent) {
			GenericGuildMessageEvent guildMessageEvent = (GenericGuildMessageEvent)e;
			guild = guildMessageEvent.getGuild();
			channel = guildMessageEvent.getChannel();
		}
		
		final Guild fguild = guild;
		final TextChannel fchannel = channel;
		
		return manager.app.getDatabaseManager().queryFirst(Factoid.class, (builder, where) -> {
			where.equals(Factoid.ACTIVE_COLUMN, true);
			where.equals(Factoid.NAME_COLUMN, name);
			if (context != null)
				where.equals(Factoid.CONTEXT_COLUMN, context);
			switch (context) {
				case Channel:
					where.equals(Factoid.CHANNEL_COLUMN, fchannel.getId());
					where.equals(Factoid.SERVER_COLUMN, fguild.getId());
					break;
				case Server:
					where.equals(Factoid.SERVER_COLUMN, fguild.getId());
					break;
				default:
					break;
			}
			builder.orderBy(DbObject.ID_COLUMN, false);
		});
	}
	
	protected boolean hasGlobalFactoidPermission(User user) {
		if (this.permissionsPlugin == null)
			return true;
		
		PermissionsPlugin permissionsPlugin = (PermissionsPlugin)this.permissionsPlugin;
		return permissionsPlugin.permissionGranted(user, this, "global");
	}
	
	protected MessageMedium getMessageMediumForConfirmationMessage(MessageMedium baseMedium, String message) {
		if (this.timedMessagesPlugin == null)
			return baseMedium;
		
		TimedMessagesPlugin timedMessagesPlugin = (TimedMessagesPlugin)this.timedMessagesPlugin;
		return timedMessagesPlugin.createTimedMessageMedium(baseMedium, message.length() * 500l);
	}
}