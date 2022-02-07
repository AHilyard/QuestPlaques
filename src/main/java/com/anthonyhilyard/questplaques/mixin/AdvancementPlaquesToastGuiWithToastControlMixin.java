package com.anthonyhilyard.questplaques.mixin;

import java.util.Deque;

import com.anthonyhilyard.advancementplaques.AdvancementPlaque;
import com.anthonyhilyard.advancementplaques.AdvancementPlaquesToastGuiWithToastControl;
import com.anthonyhilyard.iceberg.renderer.CustomItemRenderer;
import com.anthonyhilyard.questplaques.QuestPlaque;
import com.anthonyhilyard.questplaques.QuestPlaques;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.AdvancementToast;
import shadows.toaster.BetterToastComponent;


@Mixin(AdvancementPlaquesToastGuiWithToastControl.class)
public class AdvancementPlaquesToastGuiWithToastControlMixin extends BetterToastComponent
{
	@Shadow(remap = false)
	@Final
	private AdvancementPlaque[] plaques;

	@Shadow(remap = false)
	@Final
	private Minecraft mc;

	@Shadow(remap = false)
	@Final
	private CustomItemRenderer itemRenderer;

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Deque;isEmpty()Z"))
	boolean redirectIsEmpty(Deque<AdvancementToast> instance)
	{
		// If we have completed quests waiting, show those first (in case completing a quest grants an advancement, this is less confusing).
		if (!QuestPlaques.completedQuests.isEmpty())
		{
			plaques[0] = new QuestPlaque(QuestPlaques.completedQuests.removeFirst(), mc, itemRenderer);
			return true;
		}

		// No pending quests to display, just do normal functionality.
		return instance.isEmpty();
	}
}
