package com.anthonyhilyard.questplaques;

import com.anthonyhilyard.questplaques.network.Protocol;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Loader.MODID)
public class Loader
{
	public static final String MODID = "questplaques";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public Loader()
	{

		try
		{
			if (FMLEnvironment.dist == Dist.CLIENT)
			{
				Class.forName("com.anthonyhilyard.questplaques.QuestPlaques").getMethod("init").invoke(null);
			}
			else
			{
				Class.forName("com.anthonyhilyard.questplaques.QuestPlaquesServer").getMethod("init").invoke(null);
			}

		}
		catch (Exception e)
		{
			Loader.LOGGER.warn(ExceptionUtils.getStackTrace(e));
		}

		Protocol.register();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, QuestPlaquesConfig.SPEC);
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
	}
}
