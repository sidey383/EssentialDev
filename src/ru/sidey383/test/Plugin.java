package ru.sidey383.test;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;



public class Plugin extends JavaPlugin implements Listener{

	public JavaPlugin pl;
	
	@Override
	public void onEnable() {
		System.out.print("plugin start");
		//Bukkit.getPluginManager().registerEvents(this, this);
		pl = this;
	}
	
}
