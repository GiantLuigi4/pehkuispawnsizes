package tfc.pehkuispawnsizes.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.pehkuispawnsizes.PehkuiSpawnSizes;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class ServerMixin {
	@Shadow private int tickCounter;
	
	@Inject(at = @At("HEAD"), method = "tick")
	private void onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
		if ((tickCounter % 20) == 0) {
			PehkuiSpawnSizes.checkFile();
		}
	}
}
