package com.anthonyhilyard.questplaques.compat.FTBQuests;

import java.util.HashMap;
import java.util.Map;

import com.anthonyhilyard.questplaques.QuestPlaques;
import com.anthonyhilyard.questplaques.QuestPlaquesConfig;
import com.anthonyhilyard.questplaques.QuestPlaque.QuestDisplay;

import dev.architectury.event.EventResult;
import dev.ftb.mods.ftblibrary.icon.Icon;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftbquests.events.ObjectCompletedEvent;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.QuestObject;
import dev.ftb.mods.ftbquests.quest.QuestObjectType;
import dev.ftb.mods.ftbquests.quest.task.Task;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

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
			final QuestObject questObject = event.getObject();
			if (!questObject.disableToast)
			{
				FrameType frame = frameMap.getOrDefault(questObject.getObjectType(), FrameType.TASK);
				FormattedText title = new TranslatableComponent(questObject.getObjectType().translationKey + ".completed").withStyle(questObject.getObjectType().getColor());
				FormattedText questName = questObject.getTitle();
				ItemStack itemStack = ItemStack.EMPTY;
				Icon icon = questObject.getIcon();
				long questID = questObject.id;
				if (icon instanceof ItemIcon itemIcon)
				{
					itemStack = itemIcon.getStack();
				}
				
				if (itemStack.isEmpty())
				{
					Icon altIcon = questObject.getAltIcon();
					if (altIcon instanceof ItemIcon itemIcon)
					{
						itemStack = itemIcon.getStack();
					}
				}

				if (questObject instanceof Task taskObject)
				{
					Quest quest = taskObject.quest;
					if (quest.isCompletedRaw(event.getData()))
					{
						frame = frameMap.getOrDefault(quest.getObjectType(), FrameType.TASK);
						title = new TranslatableComponent(quest.getObjectType().translationKey + ".completed").withStyle(quest.getObjectType().getColor());
						questName = quest.getTitle();
						icon = quest.getIcon();
						questID = quest.id;
						if (icon instanceof ItemIcon itemIcon)
						{
							itemStack = itemIcon.getStack();
						}
						
						if (itemStack.isEmpty())
						{
							Icon altIcon = quest.getAltIcon();
							if (altIcon instanceof ItemIcon itemIcon)
							{
								itemStack = itemIcon.getStack();
							}
						}
					}
				}

				if (QuestPlaquesConfig.INSTANCE.shouldShowPlaque(frame, questID))
				{
					QuestPlaques.completedQuests.push(new QuestDisplay(questName, title, itemStack, frame));
				}
			}
			return EventResult.pass();
		});
	}
}
