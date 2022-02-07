package com.anthonyhilyard.questplaques;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Deque;

import com.anthonyhilyard.questplaques.QuestPlaque.QuestDisplay;
import com.google.common.collect.Queues;

import org.codehaus.plexus.util.ExceptionUtils;

@Mod.EventBusSubscriber(modid = Loader.MODID, bus = Bus.MOD)
public class QuestPlaques
{
	public static final Deque<QuestDisplay> completedQuests = Queues.newArrayDeque();

	public void onClientSetup(FMLClientSetupEvent event)
	{
		event.enqueueWork(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					// Set up FTB Quests compatibility.
					if (ModList.get().isLoaded("ftbquests"))
					{
						Class.forName("com.anthonyhilyard.questplaques.compat.FTBQuests.Handler").getMethod("init").invoke(null);
					}

					// Set up Custom Quests compatibility.
					if (ModList.get().isLoaded("customquests"))
					{
						Class.forName("com.anthonyhilyard.questplaques.compat.CustomQuests.Handler").getMethod("init").invoke(null);
					}
				}
				catch (Exception e)
				{
					Loader.LOGGER.warn(ExceptionUtils.getStackTrace(e));
				}
			}
		});
	}
}
