package com.anthonyhilyard.questplaques.compat.FTBQuests;

import java.util.HashMap;
import java.util.Map;

import com.anthonyhilyard.questplaques.QuestDisplay;
import com.anthonyhilyard.questplaques.QuestPlaquesConfig;
import com.anthonyhilyard.questplaques.event.QuestCompletedEvent;
import com.anthonyhilyard.questplaques.event.QuestCompletedEvent.QuestType;

import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectType;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.architectury.event.EventResult;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class Handler
{
	private static final Map<QuestObjectType, FrameType> frameMap = new HashMap<>() {{
		put(QuestObjectType.TASK, FrameType.TASK);
		put(QuestObjectType.QUEST, FrameType.GOAL);
		put(QuestObjectType.CHAPTER, FrameType.CHALLENGE);
		put(QuestObjectType.FILE, FrameType.CHALLENGE);
	}};

	public static void init()
	{
		ObjectCompletedEvent.GENERIC.register(event -> 
		{
			finishQuest(event.getObject().id);
			return EventResult.pass();
		});
	}

	public static QuestDisplay getQuestInfo(QuestObject quest)
	{
		FrameType frame = frameMap.getOrDefault(quest.getObjectType(), FrameType.TASK);
		FormattedText title = new TranslatableComponent(quest.getObjectType().translationKey + ".completed").withStyle(quest.getObjectType().getColor());
		FormattedText questName = quest.getTitle();

		ItemStack itemStack = ItemStack.EMPTY;
		Icon icon = quest.getIcon();

		if (icon instanceof ItemIcon)
		{
			ItemIcon itemIcon = (ItemIcon)icon;
			itemStack = itemIcon.getStack();
		}

		if (itemStack.isEmpty())
		{
			Icon altIcon = quest.getAltIcon();
			if (altIcon instanceof ItemIcon)
			{
				ItemIcon itemIcon = (ItemIcon)altIcon;
				itemStack = itemIcon.getStack();
			}
		}

		QuestDisplay result = new QuestDisplay(questName, title, itemStack, frame);
		return result;
	}

	public static void finishQuest(long questID)
	{
		final Minecraft minecraft = Minecraft.getInstance();
		QuestObject quest = ClientQuestFile.INSTANCE.get(questID);

		// Toast is disabled for this quest object, so bail.
		if (quest.disableToast)
		{
			return;
		}

		QuestType questType;
		switch (quest.getObjectType())
		{
			case FILE:
			case CHAPTER:
				questType = QuestType.CHAPTER;
				break;
			case TASK:
				questType = QuestType.TASK;
				break;
			default:
			case QUEST:
				questType = QuestType.QUEST;
				break;
		}

		// Check if this is a task from a simple quest and skip it if configured so.
		if (QuestPlaquesConfig.INSTANCE.consolidateSimpleQuests.get() && quest instanceof Task)
		{
			Task task = (Task)quest;
			if (task.quest.tasks.size() == 1)
			{
				return;
			}
		}

		MinecraftForge.EVENT_BUS.post(new QuestCompletedEvent(minecraft.player, getQuestInfo(quest), questID, questType));
	}
}
