package com.anthonyhilyard.questplaques.compat.CustomQuests;

import com.anthonyhilyard.questplaques.network.Protocol;
import com.anthonyhilyard.questplaques.network.QuestCompletedMessage;
import com.anthonyhilyard.questplaques.network.QuestCompletedMessage.QuestMod;
import com.vincentmet.customquests.event.QuestEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerHandler
{
	public static void initServer()
	{
		MinecraftForge.EVENT_BUS.addListener((e) -> {
			if (e instanceof QuestEvent.Completed)
			{
				QuestEvent.Completed completedEvent = (QuestEvent.Completed)e;
				Protocol.CHANNEL.send(PacketDistributor.PLAYER.with(() -> completedEvent.getPlayer()), new QuestCompletedMessage(QuestMod.CUSTOM_QUESTS, completedEvent.getQuestId()));
			}
		});
	}
}
