package tfc.pehkuispawnsizes;

import net.minecraft.util.ResourceLocation;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleModifier;
import virtuoel.pehkui.api.ScaleRegistries;
import virtuoel.pehkui.api.ScaleType;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ScaleRegister {
	public static final AtomicReference<ScaleModifier> SUScaleModifier = new AtomicReference<>();
	public static final AtomicReference<ScaleType> SUScaleType = new AtomicReference<>();
	
	public static void setup() {
		if (PehkuiSpawnSizes.options.registerScaleType) {
			ScaleModifier modifier = new ScaleModifier() {
				@Override
				public float modifyScale(ScaleData scaleData, float modifiedScale, float delta) {
					return SUScaleType.get().getScaleData(scaleData.getEntity()).getScale(delta) * modifiedScale;
				}
			};
			ScaleRegistries.SCALE_MODIFIERS.put(new ResourceLocation(PehkuiSpawnSizes.options.typeName), modifier);
			SUScaleModifier.set(modifier);
			ScaleType suType = ScaleType.Builder.create()
					.affectsDimensions()
					.addDependentModifier(SUScaleModifier.get())
					.build();
			ScaleRegistries.SCALE_TYPES.put(new ResourceLocation(PehkuiSpawnSizes.options.typeName), suType);
			Optional<ScaleType> baseType = getType("base");
			// suppress warning because I don't want to risk accidental class loading, nor do I want intelliJ constantly warning me about the fact that I do this
			//noinspection OptionalIsPresent
			if (baseType.isPresent())
				baseType.get().getDefaultBaseValueModifiers().add(modifier);
			SUScaleType.set(suType);
		} else {
			SUScaleType.set(getType(PehkuiSpawnSizes.options.typeName).get());
		}
	}
	
	// using optional to prevent accidental class loading
	public static Optional<ScaleType> getType(String name) {
		if (name.contains(":"))
			return Optional.of(ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, new ResourceLocation(name)));
		return Optional.of(ScaleRegistries.getEntry(ScaleRegistries.SCALE_TYPES, new ResourceLocation("pehkui", name)));
	}
}
