package tfc.pehkuispawnsizes;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfc.pehkuispawnsizes.checkers.EntityChecker;
import virtuoel.pehkui.api.ScaleData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

@Mod("pehkuispawnsizes")
public class PehkuiSpawnSizes {
	private static final Logger LOGGER = LogManager.getLogger();
	
	protected static Options options;
	
	protected static long lastRead = 0;
	protected static final File fl = new File("config/pehkuispawnsizes.cfg");
	
	public PehkuiSpawnSizes() {
		MinecraftForge.EVENT_BUS.addListener(PehkuiSpawnSizes::onEntityCreated);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		String text = "";
		if (!fl.exists()) {
			try {
				fl.getParentFile().mkdirs();
				fl.createNewFile();
				
				FileOutputStream outputStream = new FileOutputStream(fl);
				outputStream.write((text = (
						"scale_type = pehkuispawnsizes:scale\n" +
								"# if the scale type above should be registered\n" +
								"# this should only be turned on if you're using a scale type that doesn't already exist\n" +
								"register_type = true\n" +
								"\n" +
								"# examples: \n" +
								"# \n" +
								"# zombies can be anywhere from 0.125* to 0.23*, or from 1* to 3* the size\n" +
								"# pigs are still 1.2-3.4* the size\n" +
								"# scales = <\n" +
								"#    minecraft:zombie 5\n" +
								"#    minecraft:pig 1.2-3.4\n" +
								"# >\n" +
								"# \n" +
								"# zombies can be anywhere from 0.125* to 0.23*, or from 1* to 3* the size\n" +
								"# pigs are still 1.2-3.4* the size\n" +
								"# any mob from aoa3 (advent of ascension) can be anywhere from half size, to 1.5* the size\n" +
								"# and any entity with the registry name \"pig\", from any mod, can be half size to 1.5* the size\n" +
								"# if you do not include decimals, it will only pick integer sizes (whole numbers)\n" +
								"# you can list sizes using commas to separate them, and repeat them to weight the odds (though I may add percent chances later)" +
								"#\n" +
								"# scales = <\n" +
								"#    minecraft:zombie 0.125-0.23, 1-3\n" +
								"#    minecraft:pig 1.2-3.4\n" +
								"#    aoa3:* 0.5-1.5\n" +
								"#    *:pig 0.5-1.5\n" +
								"# >\n" +
								"#\n" +
								"# entries without wildcards are prioritized over entries with wildcards\n" +
								"# what this means, is that if you define:\n" +
								"# \tminecraft:player 1\n" +
								"# \t*:* 0.5\n" +
								"# then everything that is not a player will spawn at half size, but the player will spawn at full size\n" +
								"#\n" +
								"# you can also use ! to cause it to interpret the entry as a wildcard entry, thus putting it at a lower priority\n" +
								"# a size of \"&\" indicates to skip scaling, and just use whatever size it spawned with\n" +
								"scales = <\n" +
								"    \n" +
								">"
				)).getBytes(StandardCharsets.UTF_8));
			} catch (Throwable ignored) {
			}
		} else {
			try {
				text = new String(readFast(new FileInputStream(fl)));
			} catch (Throwable ignored) {
			}
		}
		lastRead = fl.lastModified();
		options = new Options(text);
		bus.addListener(this::setup);
	}
	
	public static void checkFile() {
		if (fl.lastModified() > lastRead) {
			String text = null;
			try {
				text = new String(readFast(new FileInputStream(fl)));
			} catch (Throwable ignored) {
			}
			assert text != null;
			try {
				options = new Options(text);
			} catch (Throwable ignored) {
				System.out.println("An error occurred while reading the config file:");
				ignored.printStackTrace();
			}
			lastRead = fl.lastModified();
		}
	}
	
	protected static byte[] readFast(InputStream stream) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int b;
			// so, fun thing about J8 streams
			// reading it byte by byte is slow
			// reading it all at once is unreliable
			// this does both: it reads as many bytes as the stream claims it has, and then reads a single byte to see if the stream is done
			while (true) {
				int availible = stream.available();
				if (availible != 0) {
					byte[] bytes = new byte[availible];
					stream.read(bytes);
					outputStream.write(bytes);
				}
				b = stream.read();
				if (b == -1) break;
				outputStream.write(b);
			}
			stream.close();
			byte[] data = outputStream.toByteArray();
			outputStream.flush();
			outputStream.close();
			return data;
		} catch (Throwable err) {
			throw new RuntimeException(err);
		}
	}
	
	public static void onEntitySpawn(BabyEntitySpawnEvent event) {
		// TODO: maybe?
	}
	
	public static void onEntityCreated(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote) return;
		CompoundNBT persistentData = event.getEntity().getPersistentData();
		if (!persistentData.contains("pehkuispawnsizes")) {
			persistentData.putBoolean("pehkuispawnsizes", true);
			
			ScaleData data = ScaleRegister.SUScaleType.get().getScaleData(event.getEntity());
			
			ArrayList<ScaleInfo> info = options.simpleNames.getOrDefault(event.getEntity().getType().getRegistryName().toString(), null);
			if (info == null) {
				for (Pair<EntityChecker, ArrayList<ScaleInfo>> nameChecker : options.nameCheckers) {
					if (nameChecker.getFirst().check(event.getEntity())) {
						info = nameChecker.getSecond();
						break;
					}
				}
				if (info == null) return;
			}
			Random rng = event.getWorld().getRandom();
			int selected = rng.nextInt(info.size());
			ScaleInfo inf = info.get(selected);
			if (inf.shouldPerform()) data.setScale((float) inf.select(rng));
		}
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		ScaleRegister.setup();
	}
}
