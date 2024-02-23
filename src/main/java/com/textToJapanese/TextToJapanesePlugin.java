
package com.textToJapanese;

import com.google.common.base.Joiner;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.IndexedSprite;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;

@Slf4j
@PluginDescriptor(
		name = "Text to Japanese Plugin",
		description = "This will convert any text in the chatbox into Hiragana if you type jp before your message."
)
public class TextToJapanesePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;


	private int modIconsStart = -1;

	@Override
	protected void startUp()
	{
		loadEmojiIcons();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			loadEmojiIcons();
		}
	}

	private void loadEmojiIcons()
	{
		final IndexedSprite[] modIcons = client.getModIcons();
		if (modIconsStart != -1 || modIcons == null)
		{
			return;
		}

		final Hiragana[] emojis = Hiragana.values();
		final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + emojis.length);
		modIconsStart = modIcons.length;

		for (int i = 0; i < emojis.length; i++)
		{
			final Hiragana hiragana = emojis[i];

			try
			{
				System.out.println(hiragana);
				final BufferedImage image = hiragana.loadImage();
				final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
				newModIcons[modIconsStart + i] = sprite;
			}
			catch (Exception ex)
			{
				System.out.println("error for " + hiragana);
			}
		}

		client.setModIcons(newModIcons);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (client.getGameState() != GameState.LOGGED_IN || modIconsStart == -1)
		{
			return;
		}

		switch (chatMessage.getType())
		{
			case PUBLICCHAT:
			case MODCHAT:
			case FRIENDSCHAT:
			case PRIVATECHAT:
			case PRIVATECHATOUT:
			case MODPRIVATECHAT:
				break;
			default:
				return;
		}

		final MessageNode messageNode = chatMessage.getMessageNode();
		final String message = messageNode.getValue();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		messageNode.setRuneLiteFormatMessage(updatedMessage);
		chatMessageManager.update(messageNode);
		client.refreshChat();
	}

	@Subscribe
	public void onOverheadTextChanged(final OverheadTextChanged event)
	{
		if (!(event.getActor() instanceof Player))
		{
			return;
		}

		final String message = event.getOverheadText();
		final String updatedMessage = updateMessage(message);

		if (updatedMessage == null)
		{
			return;
		}

		event.getActor().setOverheadText(updatedMessage);
	}

	@Nullable
	String updateMessage(final String message)
	{
		if(!message.toLowerCase().startsWith("jp"))
			return null;

		boolean editedMessage = false;
		Hiragana[] triggers = Hiragana.values();

		List<Hiragana> triggerList = Arrays.asList(triggers);
		List<String> newMessageWords = new ArrayList<>();
		int index = 0;

		while (index < message.length()) {
			String longestTrigger = "";
			int longestLength = 0;

			for (Hiragana trigger : triggerList) {
				if (message.startsWith(trigger.getTriggerPhrase(), index) && trigger.getTriggerPhrase().length() > longestLength) {
					longestTrigger = trigger.getTriggerPhrase();
					longestLength = trigger.getTriggerPhrase().length();
				}
			}

			if (longestLength > 0) {
				final Hiragana emoji = Hiragana.getHiragana(longestTrigger);
				final int emojiId = modIconsStart + emoji.ordinal();
				newMessageWords.add("<img=" + emojiId + ">");
				index += longestLength;
			}
			else {
				index++;
			}
			editedMessage = true;
		}

		if (!editedMessage)
		{
			return null;
		}

		return String.join("", newMessageWords);
	}

}
