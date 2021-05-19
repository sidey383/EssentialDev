package ru.sidey383.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.java.JavaPlugin;

import ru.sidey383.test.essential.dec.frame.map.ItemFrameMapTest;

public class Plugin extends JavaPlugin{

	@Override
	public void onEnable() {
		System.out.print("plugin start");
		Bukkit.getWorlds().forEach(a -> a.getEntitiesByClass(ItemFrame.class).stream()
				.filter(b -> b.getScoreboardTags().contains("Temp")).forEach(b -> b.remove()));
		Bukkit.getScheduler().runTaskLater(this, ()->{
			try {
				new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -10, 101, 19), BlockFace.NORTH, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
			new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -10, 100, 20), BlockFace.DOWN, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -11, 101, 20), BlockFace.WEST, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
					new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -10, 101, 21), BlockFace.SOUTH, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -9, 101, 20), BlockFace.EAST, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				new ItemFrameMapTest(new Location(Bukkit.getWorld("world"), -10, 102, 20), BlockFace.UP, this);
			}catch (Exception e) {
				e.printStackTrace();
			}
}, 2L);
	}
	
	
}
