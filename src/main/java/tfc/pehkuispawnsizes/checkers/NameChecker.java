package tfc.pehkuispawnsizes.checkers;

import net.minecraft.entity.Entity;

public abstract class NameChecker extends EntityChecker {
	public abstract boolean execute(String name);
	
	@Override
	public final boolean check(Entity entity) {
		return false;
	}
}
