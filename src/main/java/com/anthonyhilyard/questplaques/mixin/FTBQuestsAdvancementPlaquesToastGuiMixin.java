package com.anthonyhilyard.questplaques.mixin;

import com.anthonyhilyard.advancementplaques.AdvancementPlaquesToastGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftbquests.gui.ToastQuestObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;


@Mixin(AdvancementPlaquesToastGui.class)
public class FTBQuestsAdvancementPlaquesToastGuiMixin extends ToastGui
{
	public FTBQuestsAdvancementPlaquesToastGuiMixin(Minecraft p_94918_) { super(p_94918_); }

	@Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
	public void cancelQuestToasts(IToast toastIn, CallbackInfo info)
	{
		if (toastIn instanceof ToastQuestObject)
		{
			info.cancel();
		}
	}
}
