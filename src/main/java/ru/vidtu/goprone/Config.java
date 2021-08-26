package ru.vidtu.goprone;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

/**
 * Mod config.
 * @author VidTu
 */
public class Config {
	public static final transient Logger LOG = LogManager.getLogger("GoProne Config");
	
	public static boolean isJumpingAllowed = true;
	public static boolean flying = true;
	public static boolean riding = false;
	public static List<String> ridingException = new ArrayList<>();
	
	/**
	 * Load config for current server instance.
	 * @param srv Server instance
	 */
	public static void load(MinecraftServer srv) {
		File config = new File(srv.getRunDirectory(), "config/goprone.json");
		if (!config.exists()) {
			config.getParentFile().mkdirs();
			try (FileWriter fw = new FileWriter(config)) {
				new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT).create().toJson(new Config(), fw);
			} catch (Throwable t) {
				LOG.error("Unable to save config", t);
			}
			return; //No need to read default settings.
		}
		try (FileReader fr = new FileReader(config)) {
			new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create().fromJson(fr, Config.class);
		} catch (Throwable t) {
			LOG.error("Unable to save config", t);
		}
	}
	
	/**
	 * Test if the player can go prone.
	 * @param pe Player that wants to go prone
	 * @return <code>true</code> if player can go prone, <code>false</code> otherwise
	 */
	public static boolean test(PlayerEntity pe) {
		if (!flying && !pe.isOnGround()) return false;
		if (pe.hasVehicle()) {
			if (riding) {
				return !ridingException.contains(EntityType.getId(pe.getVehicle().getType()).toString());
			} else {
				return ridingException.contains(EntityType.getId(pe.getVehicle().getType()).toString());
			}
		}
		return true;
	}
}
