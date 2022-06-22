package ru.vidtu;

import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Modifier;

/**
 * Mod config.
 * @author VidTu
 */
public class Config {
	public static final transient Logger LOG = LogManager.getLogger("GoProne Config");
	
	public static boolean isJumpingAllowed = true;
	public static boolean flying = true;
	public static boolean riding = false;
	public static boolean climbing = false;
	
	/**
	 * Load config for current server instance.
	 * @param srv Server instance
	 */
	public static void load(MinecraftServer srv) {
		File config = srv.getFile("config/goprone.json");
		if (!config.exists()) {
			config.getParentFile().mkdirs();
			try (FileWriter fw = new FileWriter(config)) {
				new GsonBuilder().setPrettyPrinting()
					.excludeFieldsWithModifiers(Modifier.TRANSIENT)
					.create()
					.toJson(new Config(), fw);
			} catch (Throwable t) {
				LOG.error("Unable to save config", t);
			}
		} else {
			try (FileReader fr = new FileReader(config)) {
				new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create().fromJson(fr, Config.class);
			} catch (Throwable t) {
				LOG.error("Unable to save config", t);
			}
		}
	}
}
