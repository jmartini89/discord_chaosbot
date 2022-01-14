package me.name.bot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class Player_Tx {
	public static void audio(
			@NotNull Server server, @NotNull String cmd, AudioChannel audio_channel, TextChannel text_channel) {
		BotManager.playerManager.loadItemOrdered(server.player, cmd, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				if (text_channel != null) text_channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
				play(server, audio_channel, track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				AudioTrack firstTrack = playlist.getSelectedTrack();
				if (firstTrack == null) firstTrack = playlist.getTracks().get(0);
				if (text_channel != null) {
					text_channel.sendMessage(
							"Adding to queue " +
									firstTrack.getInfo().title +
									" (first track of playlist " +
									playlist.getName() + ")")
							.queue();
				}
				play(server, audio_channel, firstTrack);
			}

			@Override
			public void noMatches() {
				if (!cmd.startsWith("ytsearch:")) {
					Player_Tx.audio(server, "ytsearch:".concat(cmd), audio_channel, text_channel);
					return;
				}
				if (text_channel != null) text_channel.sendMessage("Nothing found by " + cmd).queue();
			}

			@Override
			public void loadFailed(FriendlyException exception) {
				if (text_channel != null) text_channel.sendMessage("Could not play: " + exception.getMessage()).queue();
			}
		});
	}

	private static void play(@NotNull Server server, AudioChannel audio_channel, AudioTrack track) {
		server.guild.getAudioManager().openAudioConnection(audio_channel);
		try { server.track_scheduler.queue(track); }
		catch (Exception e) { e.printStackTrace(); }
	}
}
