package ru.sidey383.test.essential.dec.frame.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import ru.sidey383.essential.dev.item.frame.map.FrameClickAction;
import ru.sidey383.essential.dev.item.frame.map.ItemFrameMap;

public class ItemFrameMapTest extends ItemFrameMap{

	MapView mv;

	byte[][] map;
	
	public ItemFrameMapTest(Location location, BlockFace face, Plugin plugin) {
		super(location, face, plugin);
		
		ItemStack item = new ItemStack(Material.FILLED_MAP);
		
		MapMeta mm = (MapMeta) item.getItemMeta();
		
		mv = Bukkit.createMap(location.getWorld());

		map = emptyMap();

		mv.addRenderer(new MapRenderer() {

			@Override
			public void render(MapView arg0, MapCanvas canvas, Player arg2) {
				try {
					for (int i = 0; i < 128; i++)
						for (int k = 0; k < 128; k++)
							canvas.setPixel(i, k, map[i][k]);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});

		mm.setMapView(mv);
		
		item.setItemMeta(mm);
		
		getItemFrame().setItem(item);
		getItemFrame().addScoreboardTag("Temp");
	}
	
	private static byte[][] emptyMap() {
		byte[][] map = new byte[128][128];
		@SuppressWarnings("deprecation")
		byte color = MapPalette.matchColor(225, 255, 255);
		for (int i = 0; i < 128; i++)
			for (int k = 0; k < 128; k++)
				map[i][k] = color;

		return map;
	}


	@Override
	public void onClick(int x, int y, Player p, FrameClickAction action) {
		map[x][y] = 64;
		p.sendMap(mv);
	}
}
