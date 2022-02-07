package com.anthonyhilyard.questplaques.mixin;

import com.anthonyhilyard.advancementplaques.AdvancementPlaquesToastGuiWithToastControl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.ftb.mods.ftbquests.gui.ToastQuestObject;
import net.minecraft.client.gui.components.toasts.Toast;
import shadows.toaster.BetterToastComponent;


@Mixin(AdvancementPlaquesToastGuiWithToastControl.class)
public class FTBQuestsAdvancementPlaquesToastGuiWithToastControlMixin extends BetterToastComponent
{
	@Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
	public void cancelQuestToasts(Toast toastIn, CallbackInfo info)
	{
		if (toastIn instanceof ToastQuestObject)
		{
			info.cancel();
		}
	}
}
