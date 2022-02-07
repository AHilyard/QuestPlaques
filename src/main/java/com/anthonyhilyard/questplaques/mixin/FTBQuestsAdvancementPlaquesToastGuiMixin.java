package com.anthonyhilyard.questplaques.mixin;

import com.anthonyhilyard.advancementplaques.AdvancementPlaquesToastGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftbquests.gui.ToastQuestObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;


@Mixin(AdvancementPlaquesToastGui.class)
public class FTBQuestsAdvancementPlaquesToastGuiMixin extends ToastComponent
{
	public FTBQuestsAdvancementPlaquesToastGuiMixin(Minecraft p_94918_) { super(p_94918_); }

	@Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
	public void cancelQuestToasts(Toast toastIn, CallbackInfo info)
	{
		if (toastIn instanceof ToastQuestObject)
		{
			info.cancel();
		}
	}
}
