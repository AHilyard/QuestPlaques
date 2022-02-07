package com.anthonyhilyard.questplaques;

import javax.annotation.Nonnull;

import com.anthonyhilyard.advancementplaques.AdvancementPlaque;
import com.anthonyhilyard.advancementplaques.AdvancementPlaques;
import com.anthonyhilyard.advancementplaques.AdvancementPlaquesConfig;
import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.Util;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;


public class QuestPlaque extends AdvancementPlaque
{
	private long animationTime = -1L;
	private long visibleTime = -1L;
	private boolean hasPlayedSound = false;
	private Visibility visibility = Visibility.SHOW;
	private final Minecraft minecraft;
	private final CustomItemRenderer itemRenderer;
	private final QuestDisplay display;


	public record QuestDisplay(FormattedText questName, FormattedText title, ItemStack stack, FrameType frame) {}

	public QuestPlaque(@Nonnull QuestDisplay display, Minecraft mcIn, CustomItemRenderer itemRendererIn)
	{
		super(null, mcIn, itemRendererIn);
		minecraft = mcIn;
		itemRenderer = itemRendererIn;
		this.display = display;
	}

	public int width()
	{
		return 256;
	}

	public int height()
	{
		return 32;
	}

	private float getVisibility(long currentTime)
	{
		float f = Mth.clamp((float)(currentTime - animationTime) / 200.0f, 0.0f, 1.0f);
		f = f * f;
		return visibility == Visibility.HIDE ? 1.0f - f : f;
	}

	private Visibility drawPlaque(PoseStack poseStack, long displayTime)
	{
		float fadeInTime = 500f, fadeOutTime = 1500f, duration = 7000f;
		
		switch (display.frame())
		{
			default:
			case TASK:
				fadeInTime = (float)(AdvancementPlaquesConfig.INSTANCE.taskEffectFadeInTime.get() * 1000.0);
				fadeOutTime = (float)(AdvancementPlaquesConfig.INSTANCE.taskEffectFadeOutTime.get() * 1000.0);
				duration = (float)(AdvancementPlaquesConfig.INSTANCE.taskDuration.get() * 1000.0);
				break;
			case GOAL:
				fadeInTime = (float)(AdvancementPlaquesConfig.INSTANCE.goalEffectFadeInTime.get() * 1000.0);
				fadeOutTime = (float)(AdvancementPlaquesConfig.INSTANCE.goalEffectFadeOutTime.get() * 1000.0);
				duration = (float)(AdvancementPlaquesConfig.INSTANCE.goalDuration.get() * 1000.0);
				break;
			case CHALLENGE:
				fadeInTime = (float)(AdvancementPlaquesConfig.INSTANCE.challengeEffectFadeInTime.get() * 1000.0);
				fadeOutTime = (float)(AdvancementPlaquesConfig.INSTANCE.challengeEffectFadeOutTime.get() * 1000.0);
				duration = (float)(AdvancementPlaquesConfig.INSTANCE.challengeDuration.get() * 1000.0);
				break;
		}

		if (displayTime >= fadeInTime)
		{
			float alpha = 1.0f;
			if (displayTime > duration)
			{
				alpha = Math.max(0.0f, Math.min(1.0f, 1.0f - ((float)displayTime - duration) / 1000.0f));
			}

			// Grab the title color and apply the current alpha to it.
			int tempColor = (int)QuestPlaquesConfig.INSTANCE.titleColor.get().longValue();
			int tempAlpha = (int)(((tempColor >> 24) & 0xFF) * alpha);
			int titleColor = (tempColor & 0xFFFFFF) | (tempAlpha << 24);

			// Grab the name color and apply the current alpha to it.
			tempColor = (int)QuestPlaquesConfig.INSTANCE.nameColor.get().longValue();
			tempAlpha = (int)(((tempColor >> 24) & 0xFF) * alpha);
			int nameColor = (tempColor & 0xFFFFFF) | (tempAlpha << 24);

			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
			RenderSystem.setShaderTexture(0, AdvancementPlaques.TEXTURE_PLAQUES);
			int frameOffset = 0;
			if (display.frame() == FrameType.GOAL)
			{
				frameOffset = 1;
			}
			else if (display.frame() == FrameType.CHALLENGE)
			{
				frameOffset = 2;
			}
			GuiComponent.blit(poseStack, -1, -1, 0, height() * frameOffset, width(), height(), 256, 256);

			// Only bother drawing text if alpha is greater than 0.1.
			if (alpha > 0.1f)
			{
				// Text like "Challenge Complete!" at the top of the plaque.
				int typeWidth = minecraft.font.width(display.title());
				minecraft.font.draw(poseStack, Language.getInstance().getVisualOrder(display.title()), (width() - typeWidth) / 2.0f + 15.0f, 5.0f, titleColor);

				int titleWidth = minecraft.font.width(display.questName());

				// If the width of the advancement title is less than the full available width, display it normally.
				if (titleWidth <= (220 / 1.5f))
				{
					PoseStack modelViewStack = RenderSystem.getModelViewStack();
					modelViewStack.pushPose();
					modelViewStack.scale(1.5f, 1.5f, 1.0f);
					RenderSystem.applyModelViewMatrix();
					minecraft.font.draw(poseStack, Language.getInstance().getVisualOrder(display.questName()), ((width() / 1.5f) - titleWidth) / 2.0f + (15.0f / 1.5f), 9.0f, nameColor);
					modelViewStack.popPose();
					RenderSystem.applyModelViewMatrix();
				}
				// Otherwise, display it with a smaller (default) font.
				else
				{
					minecraft.font.draw(poseStack, Language.getInstance().getVisualOrder(display.questName()), (width() - titleWidth) / 2.0f + 15.0f, 15.0f, nameColor);
				}
			}

			PoseStack modelViewStack = RenderSystem.getModelViewStack();
			modelViewStack.pushPose();
			modelViewStack.translate(1.0f, 1.0f, 0.0f);
			modelViewStack.scale(1.5f, 1.5f, 1.0f);

			RenderSystem.applyModelViewMatrix();
			itemRenderer.renderItemModelIntoGUIWithAlpha(display.stack(), 1, 1, alpha);
			
			modelViewStack.popPose();
			RenderSystem.applyModelViewMatrix();

			if (!hasPlayedSound)
			{
				hasPlayedSound = true;

				try
				{
					switch (display.frame())
					{
						case TASK:
							if (!AdvancementPlaquesConfig.INSTANCE.muteTasks.get())
							{
								minecraft.getSoundManager().play(SimpleSoundInstance.forUI(AdvancementPlaques.TASK_COMPLETE.get(), 1.0f, 1.0f));
							}
							break;
						case GOAL:
							if (!AdvancementPlaquesConfig.INSTANCE.muteGoals.get())
							{
								minecraft.getSoundManager().play(SimpleSoundInstance.forUI(AdvancementPlaques.GOAL_COMPLETE.get(), 1.0f, 1.0f));
							}
							break;
						default:
						case CHALLENGE:
							if (!AdvancementPlaquesConfig.INSTANCE.muteChallenges.get())
							{
								minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f));
							}
							break;
					}
				}
				catch (NullPointerException e)
				{
					Loader.LOGGER.warn("Tried to play a custom sound for an advancement, but that sound was not registered! Install Quest Plaques on the server or mute tasks and goals in the config file.");
				}
			}
		}

		if (displayTime < fadeInTime + fadeOutTime)
		{
			float alpha = 1.0f - ((float)(displayTime - fadeInTime) / fadeOutTime);
			if (displayTime < fadeInTime)
			{
				alpha = (float)displayTime / fadeInTime;
			}

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
			poseStack.pushPose();
			poseStack.translate(0.0f, 0.0f, 95.0f);
			RenderSystem.setShaderTexture(0, AdvancementPlaques.TEXTURE_PLAQUE_EFFECTS);

			if (display.frame() == FrameType.CHALLENGE)
			{
				GuiComponent.blit(poseStack, -16, -16, 0, height() + 32, width() + 32, height() + 32, 512, 512);
			}
			else
			{
				GuiComponent.blit(poseStack, -16, -16, 0, 0, width() + 32, height() + 32, 512, 512);
			}
			poseStack.popPose();
		}

		return displayTime >= fadeInTime + fadeOutTime + duration ? Visibility.HIDE : Visibility.SHOW;

	}

	@Override
	public boolean render(int screenWidth, int index, PoseStack poseStack)
	{
		long currentTime = Util.getMillis();
		if (animationTime == -1L)
		{
			animationTime = currentTime;
			visibility.playSound(minecraft.getSoundManager());
		}

		if (visibility == Visibility.SHOW && currentTime - animationTime <= 200L)
		{
			visibleTime = currentTime;
		}
		
		RenderSystem.disableDepthTest();
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();

		if (AdvancementPlaquesConfig.INSTANCE.onTop.get())
		{
			modelViewStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() - width()) / 2.0f,
			AdvancementPlaquesConfig.INSTANCE.distance.get(),
									 900.0f + index);
		}
		else
		{
			modelViewStack.translate((float)(minecraft.getWindow().getGuiScaledWidth() - width()) / 2.0f,
									 (float)(minecraft.getWindow().getGuiScaledHeight() - (height() + AdvancementPlaquesConfig.INSTANCE.distance.get())),
									 900.0f + index);
		}
		RenderSystem.applyModelViewMatrix();
		Visibility newVisibility = drawPlaque(poseStack, currentTime - visibleTime);

		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();

		if (newVisibility != visibility)
		{
			animationTime = currentTime - (long)((int)((1.0f - getVisibility(currentTime)) * 200.0f));
			visibility = newVisibility;
		}

		return visibility == Visibility.HIDE && currentTime - animationTime > 200L;
	}
}
