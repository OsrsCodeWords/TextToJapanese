package com.textToJapanese;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TextToJapaneseTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TextToJapanesePlugin.class);
		RuneLite.main(args);
	}
}