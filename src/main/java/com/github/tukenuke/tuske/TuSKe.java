package com.github.tukenuke.tuske;

import java.util.logging.Level;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.tukenuke.tuske.gui.GUIManager;
import com.github.tukenuke.tuske.gui.SkriptGUIEvent;
import com.github.tukenuke.tuske.util.ReflectionUtils;

public class TuSKe extends JavaPlugin {
	private static TuSKe plugin;
	private static GUIManager gui;

	public TuSKe() {
		if (plugin != null) //Unnecessary, just to look cool.
			throw new IllegalStateException("TuSKe can't have two instances.");
		plugin = this;
	}

	@Override
	public void onEnable() {
		// --------- Safe check if everything is ok to load ---------
		Boolean hasSkript = hasPlugin("Skript");
		if (!hasSkript || !Skript.isAcceptRegistrations()) {
			if (!hasSkript)
				log("Error 404 - Skript not found.", Level.SEVERE);
			else
				log("TuSKe can't be loaded when the server is already loaded.", Level.SEVERE);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		SkriptAddon tuske = Skript.registerAddon(this).setLanguageFileDirectory("lang");
		try {
			//                 It will return as "me.tuske.sktuke"
			tuske.loadClasses(getClass().getPackage().getName(), "register", "conditions", "effects", "expressions");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// ----------------------------------------------------------
	}

	@Override
	public void onDisable() {
		SkriptGUIEvent.getInstance().unregisterAll();
		if (gui != null)
			gui.clearAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
	public static TuSKe getInstance(){
		return plugin;
	}
	public boolean hasPlugin(String str) {
		return plugin.getServer().getPluginManager().isPluginEnabled(str);
	}

	public void info(String msg, Object... values) {
		log(String.format(msg, values), Level.INFO);
	}
	
	public static GUIManager getGUIManager(){
		if (gui == null)
			 gui = new GUIManager(getInstance());
	    return gui;
	}
	public static void log(String msg){
	    log(msg, Level.INFO);
	}
	public static void log(String msg, Level lvl){
	    plugin.getLogger().log(lvl, msg);
	}
	public static void log(Level lvl, String... msgs){
		for (String msg : msgs)
			log(msg, lvl);
	}

	public static boolean isSpigot(){
		return ReflectionUtils.hasMethod(Player.class, "spigot");
	}

}