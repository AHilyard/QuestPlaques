package com.anthonyhilyard.questplaques.network;

import com.anthonyhilyard.questplaques.Loader;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Protocol
{
	private static final String NETWORK_PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Loader.MODID, "main"), () -> NETWORK_PROTOCOL_VERSION,
			NETWORK_PROTOCOL_VERSION::equals, NETWORK_PROTOCOL_VERSION::equals
	);

	public static final void register()
	{
		int messageID = 0;

		CHANNEL.registerMessage(
			messageID++,
			QuestCompletedMessage.class,
			QuestCompletedMessage::encode,
			QuestCompletedMessage::decode,
			QuestCompletedMessage::handle
		);
	}
}
