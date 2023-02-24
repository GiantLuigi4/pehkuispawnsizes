package tfc.pehkuispawnsizes.checkers;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TagChecker extends EntityChecker {
	String tag;
	
	public TagChecker(String tag) {
		this.tag = tag;
	}
	
	@Override
	public boolean check(Entity entity) {
		for (ResourceLocation resourceLocation : entity.getType().getTags())
			if (resourceLocation.toString().equals(tag))
				return true;
		return false;
	}
}
