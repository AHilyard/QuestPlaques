package com.anthonyhilyard.questplaques;

import net.minecraft.advancements.FrameType;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;

/** Yes, this entire file is for one record. */
public record QuestDisplay(FormattedText questName, FormattedText title, ItemStack stack, FrameType frame)
{

}