package com.anthonyhilyard.questplaques.compat.CustomQuests;

import com.anthonyhilyard.questplaques.event.QuestCompletedEvent;
import com.anthonyhilyard.questplaques.event.QuestCompletedEvent.QuestType;
import com.anthonyhilyard.questplaques.QuestDisplay;
import com.vincentmet.customquests.api.QuestHelper;
import com.vincentmet.customquests.event.QuestEvent;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.hierarchy.quest.Quest;

import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class Handler
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.addListener((e) -> {
			if (e instanceof QuestEvent.Completed)
			{
				QuestEvent.Completed completedEvent = (QuestEvent.Completed)e;
				finishQuest(completedEvent.getQuestId());
			}
		});
	}

	public static QuestDisplay getQuestInfo(Quest quest)
	{
		FrameType frame = FrameType.GOAL;
		ITextProperties title = new TranslationTextComponent("customquests.general.quest_completed");
		ITextProperties questName = new StringTextComponent(quest.getTitle().getText());
		ItemStack itemStack = ItemStack.EMPTY;

		if (quest.getButton().getIcon() instanceof ItemSlideshowTexture)
		{
			ItemSlideshowTexture itemIcon = (ItemSlideshowTexture)quest.getButton().getIcon();
			itemStack = itemIcon.getCurrentItemStack();
		}
		/// TODO: Add support for non-item quest icons.
		/// TODO: Add support for task completion?
		// else if (e instanceof QuestEvent.Task.Completed completedEvent)
		// {
		// 	Task task = QuestHelper.getTaskFromId(completedEvent.getQuestId(), completedEvent.getTaskId());

		// 	if (task != null)
		// 	{
		// 		frame = FrameType.TASK;
		// 		title = new TextComponent("Task Completed!");
		// 		questName = new TextComponent(task.)
		// 	}
			
		// }

		QuestDisplay result = new QuestDisplay(questName, title, itemStack, frame);
		return result;
	}

	public static void finishQuest(long questID)
	{
		final Minecraft minecraft = Minecraft.getInstance();
		Quest quest = QuestHelper.getQuestFromId((int)questID);
		if (quest == null)
		{
			return;
		}

		MinecraftForge.EVENT_BUS.post(new QuestCompletedEvent(minecraft.player, getQuestInfo(quest), questID, QuestType.QUEST));
	}
}
