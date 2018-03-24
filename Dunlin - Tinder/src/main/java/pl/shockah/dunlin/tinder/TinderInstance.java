package pl.shockah.dunlin.tinder;

import com.djm.tinder.Tinder;
import com.djm.tinder.match.Match;
import com.djm.tinder.user.Photo;
import com.djm.tinder.user.User;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import pl.shockah.dunlin.settings.GuildSettingScope;
import pl.shockah.dunlin.settings.SettingScope;
import pl.shockah.util.ReadWriteList;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TinderInstance {
	private final TinderPlugin plugin;
	private final Guild guild;
	private final Tinder tinder;

	private final ReadWriteList<User> users = new ReadWriteList<>(new ArrayList<>());

	private Category category;
	private TextChannel voteChannel;
	private TextChannel voteHistoryChannel;

	public TinderInstance(TinderPlugin plugin, Guild guild, String facebookToken) throws Exception {
		this.plugin = plugin;
		this.guild = guild;
		tinder = Tinder.fromAccessToken(facebookToken);
	}

	public User getNextUser() {
		return users.writeOperation(users -> {
			if (users.isEmpty()) {
				try {
					users.addAll(tinder.getRecommendations());
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return users.stream().findFirst().orElse(null);
		});
	}

	public void setup() {
		String categoryName = plugin.categorySetting.get(new GuildSettingScope(guild));
		if (categoryName == null)
			return;

		Category category = guild.getCategoriesByName(categoryName, true).stream().findFirst().orElse(null);
		if (category == null)
			guild.getController().createCategory(categoryName).queue(newCategory -> setupCategory((Category)newCategory));
		else
			setupCategory(category);
	}

	private void setupCategory(Category category) {
		this.category = category;
		String voteChannelName = plugin.voteChannelSetting.get(new GuildSettingScope(guild));
		if (voteChannelName == null)
			return;

		TextChannel voteChannel = guild.getTextChannelsByName(voteChannelName, false).stream().findFirst().orElse(null);
		if (voteChannel == null) {
			guild.getController().createTextChannel(voteChannelName).queue(newChannel -> {
				newChannel.getManager().setParent(category).queue(
						(__) -> setupVoteChannel((TextChannel) newChannel)
				);
			});
		} else {
			setupVoteChannel(voteChannel);
		}
	}

	private void clearChannel(TextChannel channel) {
		MessageHistory history = channel.getHistory();
		while (true) {
			List<Message> messages = history.retrievePast(100).complete();
			if (messages.isEmpty())
				break;
			else if (messages.size() == 1)
				messages.get(0).delete().queue();
			else
				channel.deleteMessages(messages).queue();
		}
	}

	private void setupVoteChannel(TextChannel channel) {
		voteChannel = channel;
		SettingScope scope = new GuildSettingScope(guild);

		Boolean voteHistoryEnabled = plugin.voteHistoryEnabledSetting.get(scope);
		if (voteHistoryEnabled != null && voteHistoryEnabled) {
			String voteHistoryChannelName = plugin.voteChannelSetting.get(scope);
			if (voteHistoryChannelName != null) {
				TextChannel voteHistoryChannel = guild.getTextChannelsByName(voteHistoryChannelName, false).stream().findFirst().orElse(null);
				if (voteHistoryChannel == null)
					guild.getController().createTextChannel(voteHistoryChannelName).queue(newChannel -> setupVoteHistoryChannel((TextChannel)newChannel));
				else
					setupVoteHistoryChannel(voteHistoryChannel);
			}
		}

		clearChannel(channel);
		//setupUser(channel, getNextUser());

		try {
			for (Match match : tinder.getMatches()) {
				setupMatchChannel(match);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupMatchChannel(User user) {
		String userLabel = getUserLabel(user);
		TextChannel channel = guild.getTextChannelsByName(userLabel, true).stream().findFirst().orElse(null);
		if (category == null)
			guild.getController().createTextChannel(userLabel).queue(newChannel -> setupMatchChannel((TextChannel)newChannel, user));
		else
			setupMatchChannel(channel, user);
	}

	private void setupMatchChannel(TextChannel channel, User user) {
		clearChannel(channel);
		setupUser(channel, getNextUser());
	}

	private void setupVoteHistoryChannel(TextChannel channel) {
		voteHistoryChannel = channel;
	}

	private String getUserLabel(User user) {
		return String.format("%s %s", user.getName(), new SimpleDateFormat("yyyy.MM.dd").format(user.getBirthDate()));
	}

	private void setupUser(TextChannel channel, User user) {
		if (user.getPhotos().isEmpty()) {
			channel.sendMessage("No photos.").queue();
		} else {
			for (Photo photo : user.getPhotos()) {
				channel.sendMessage(photo.getUrl()).queue();
			}
		}

		int years = (int)ChronoUnit.YEARS.between(user.getBirthDate().toInstant(), LocalDateTime.now());
		channel.sendMessage(new EmbedBuilder()
				.setAuthor(String.format("%s, %d", user.getName(), years))
				.setDescription(user.getBio())
				.build()).queue();
	}
}