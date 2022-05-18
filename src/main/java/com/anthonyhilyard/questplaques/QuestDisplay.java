package com.anthonyhilyard.questplaques;

import java.util.Objects;

import net.minecraft.advancements.FrameType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;

public class QuestDisplay
{
	public final ITextProperties questName;
	public final ITextProperties title;
	public final ItemStack stack;
	public final FrameType frame;
	public QuestDisplay(ITextProperties questName, ITextProperties title, ItemStack stack, FrameType frame)
	{
		this.questName = questName;
		this.title = title;
		this.stack = stack;
		this.frame = frame;
	}

	@Override
	public int hashCode() { return Objects.hash(questName, title, stack, frame); }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (!(obj instanceof QuestDisplay))
		{
			return false;
		}
		else
		{
			QuestDisplay other = (QuestDisplay) obj;
			return Objects.equals(questName, other.questName) &&
					Objects.equals(title, other.title) &&
					Objects.equals(stack, other.stack) &&
					Objects.equals(frame, other.frame);
		}
	}
}