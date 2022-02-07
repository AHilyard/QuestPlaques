package com.anthonyhilyard.questplaques.compat.CustomQuests;

import com.anthonyhilyard.questplaques.QuestPlaques;
import com.anthonyhilyard.questplaques.QuestPlaquesConfig;
import com.anthonyhilyard.questplaques.QuestPlaque.QuestDisplay;
import com.vincentmet.customquests.api.QuestHelper;
import com.vincentmet.customquests.event.QuestEvent;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.hierarchy.quest.Quest;

import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class Handler
{

	public static void init()
	{
		MinecraftForge.EVENT_BUS.addListener(Handler::onQuestComplete);
	}

	private static void onQuestComplete(QuestEvent e)
	{
		FrameType frame = null;
		FormattedText title = null;
		FormattedText questName = null;
		ItemStack itemStack = ItemStack.EMPTY;

		// If this is a quest completed or task completed event, continue.
		if (e instanceof QuestEvent.Completed completedEvent)
		{
			Quest quest = QuestHelper.getQuestFromId(completedEvent.getQuestId());
			if (quest != null)
			{
				frame = FrameType.GOAL;
				title = new TextComponent("Quest complete!");
				questName = new TextComponent(quest.getTitle().getText());
				if (quest.getButton().getIcon() instanceof ItemSlideshowTexture itemIcon)
				{
					itemStack = itemIcon.getCurrentItemStack();
				}
				/// TODO: Add support for non-item quest icons.

				if (QuestPlaquesConfig.INSTANCE.shouldShowPlaque(frame, quest.getQuestId()))
				{
					QuestPlaques.completedQuests.push(new QuestDisplay(questName, title, itemStack, frame));
				}
			}
		}
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
	}
}
