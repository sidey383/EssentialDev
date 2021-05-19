package ru.sidey383.essential.dev.item.frame.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

public abstract class ItemFrameMap implements Listener{

	private Location location;
	private ItemFrame itemFrame;
	private boolean isCanceled = true;
	private double maxDistance = -1;
	
	public ItemFrameMap(Location location, BlockFace face, Plugin plugin) 
	{
		itemFrame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
		itemFrame.setFacingDirection(face);	
		this.location = itemFrame.getLocation();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public void setBlockFace(BlockFace face) 
	{
		if(face == itemFrame.getFacing()) return;
		itemFrame.setFacingDirection(face);
	}
	
	public ItemFrame getItemFrame() 
	{
		return itemFrame;
	}
	
	public abstract void onFrameClick(int x, int y, PlayerInteractEvent e);
	
	public abstract void onFrameClick(int x, int y, PlayerInteractEntityEvent e);
		
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) 
	{
		if(e.getPlayer() == null || e.getPlayer().getLocation() == null) 
			return;
		double[] fLoc = locationToArray(itemFrame.getLocation());
		double[] playerHead = locationToArray(e.getPlayer().getEyeLocation());
		double[] playerLook = new double[] {e.getPlayer().getEyeLocation().getDirection().getX(),e.getPlayer().getEyeLocation().getDirection().getY(),e.getPlayer().getEyeLocation().getDirection().getZ()};
		
		int[] pixel = getMapPixel(fLoc, playerLook, playerHead, itemFrame.getFacing());
		if(pixel == null) return;
		if(pixel[0] > 127 || pixel[0] < 0 || pixel[1] > 127 || pixel[1] < 0)
			return;
		double dist = 100.0;
		if(maxDistance > 0  ) dist = maxDistance;
		if(rayTraycingCheck(e.getPlayer(),dist, itemFrame.getLocation().getBlockX(), itemFrame.getLocation().getBlockY(), itemFrame.getLocation().getBlockZ(), itemFrame.getFacing())) 
		{
			pixel = calculateFrameRotation(pixel, itemFrame.getRotation(), itemFrame.getFacing());
			onFrameClick(pixel[0], pixel[1], e);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) 
	{
		if(e.getPlayer() == null || e.getPlayer().getLocation() == null) 
			return;
		if(itemFrame == null || !itemFrame.equals(e.getRightClicked()))
			return;
		double[] playerHead = locationToArray(e.getPlayer().getEyeLocation());
		double[] playerLook = new double[] {e.getPlayer().getEyeLocation().getDirection().getX(),e.getPlayer().getEyeLocation().getDirection().getY(),e.getPlayer().getEyeLocation().getDirection().getZ()};
		
		double[] fLoc = locationToArray(itemFrame.getLocation());
		int[] pixel = getMapPixel(fLoc, playerLook, playerHead, itemFrame.getFacing());
		if(pixel == null) return;
		if(pixel[0] > 127 || pixel[0] < 0 || pixel[1] > 127 || pixel[1] < 0)
			return;
		
		pixel = calculateFrameRotation(pixel, itemFrame.getRotation(), itemFrame.getFacing());
		onFrameClick(pixel[0], pixel[1], e);
	}
	
	public void cancelEvents(boolean cancel)
	{
		isCanceled = cancel;
	}
	
	public boolean eventsIsCanceled() 
	{
		return isCanceled;
	}
	
	public double getMaxDistance() 
	{
		return maxDistance;
	}
	
	public void setMaxDistance(double distance) 
	{
		maxDistance = distance;
	}
	
	private static int[] calculateFrameRotation(int[] pixel, Rotation rot, BlockFace face) 
	{
		if(face == BlockFace.DOWN)
		{
				pixel[0] = 127 - pixel[0];
		}
		if(face == BlockFace.UP)
		{
				pixel[0] = 127 - pixel[0];
				pixel[1] = 127 - pixel[1];
		}
		if(face == BlockFace.EAST)
		{
				int t = pixel[0];
				pixel[0] = pixel[1];
				pixel[1] = t;
		}
		if(face == BlockFace.WEST)
		{
				int t = pixel[0];
				pixel[0] = 127 - pixel[1];
				pixel[1] = t;
		}
		if(face == BlockFace.NORTH)
		{
		}
		if(face == BlockFace.SOUTH)
		{
			pixel[0] = 127 - pixel[0];
		}
		int t = pixel[0];
		switch(rot) 
		{
		
			case CLOCKWISE:
				pixel[0] = 127 - pixel[0];
				pixel[1] = 127 - pixel[1];
			break;
			case CLOCKWISE_45:
				
				pixel[0] = pixel[1];
				pixel[1] = 127 - t;
			break;
			case CLOCKWISE_135:
				pixel[0] = 127 -pixel[1];
				pixel[1] = t;
			break;
			case COUNTER_CLOCKWISE:
				pixel[0] = 127 - pixel[0];
				pixel[1] = 127 - pixel[1];
			break;
			case COUNTER_CLOCKWISE_45:
				pixel[0] = 127 - pixel[1];
				pixel[1] = t;
			break;
			case FLIPPED:
				
			break;
			case FLIPPED_45:
				pixel[0] = pixel[1];
				pixel[1] = 127 - t;
			break;
			case NONE:
			break;
		}
		
		return pixel;
	}
	
	private static int[] getMapPixel(double usualFrameLocatio[], double playerLook[], double playerHead[], BlockFace face) 
	{
		int[] pixels = new int[2];
		int frameSurface = -1;
		if(face == BlockFace.DOWN || face == BlockFace.UP)
		{
			usualFrameLocatio[0] += 0.5;
			usualFrameLocatio[2] += 0.5;
			if(face == BlockFace.DOWN)
				usualFrameLocatio[1]-= 0.04;
			if(face == BlockFace.UP)
				usualFrameLocatio[1]+= 0.04;
			frameSurface = 1;
		}
		if(face == BlockFace.NORTH || face == BlockFace.SOUTH)
		{
			usualFrameLocatio[0] += 0.5;
			usualFrameLocatio[1] += 0.5;
			if(face == BlockFace.SOUTH)
				usualFrameLocatio[2] += 0.04;
			if(face == BlockFace.NORTH)
				usualFrameLocatio[2] -= 0.04;
			frameSurface = 2;
		}
		if(face == BlockFace.WEST || face == BlockFace.EAST)
		{
			usualFrameLocatio[1] += 0.5;
			usualFrameLocatio[2] += 0.5;
			if(face == BlockFace.EAST)
				usualFrameLocatio[0] += 0.04;
			if(face == BlockFace.WEST)
				usualFrameLocatio[0] -= 0.04;
			frameSurface = 0;
		}
		double[] result = calculateÑoordinates(usualFrameLocatio, playerLook, playerHead, frameSurface);
		if(result == null) return null; 
		if(frameSurface == 0)
		{
			pixels[0] = (int) Math.floor(result[1]*128.0);
			pixels[1] = (int) Math.floor(result[2]*128.0);
		}
		if(frameSurface == 1)
		{
			pixels[0] = (int) Math.floor(result[0]*128.0);
			pixels[1] = (int) Math.floor(result[2]*128.0);
		}
		if(frameSurface == 2)
		{
			pixels[0] = (int) Math.floor(result[0]*128.0);
			pixels[1] = (int) Math.floor(result[1]*128.0);
		}
		return pixels;
	}
	
	private static double[] calculateÑoordinates(double normilizeFrameLocatio[], double playerLook[], double playerHead[], int frameSurafce) 
	{
		double[] pos = new double[] {normilizeFrameLocatio[0] - playerHead[0], normilizeFrameLocatio[1] - playerHead[1], normilizeFrameLocatio[2] - playerHead[2]};;
		if(playerLook[frameSurafce] == 0 ||  pos[frameSurafce] * playerLook[frameSurafce] < 0) return null;
		return new double[]
				{
						(pos[0] -  (playerLook[0] * pos[frameSurafce])/ playerLook[frameSurafce]), 
						(pos[1] -  (playerLook[1] * pos[frameSurafce])/ playerLook[frameSurafce]), 
						(pos[2] -  (playerLook[2] * pos[frameSurafce])/ playerLook[frameSurafce])};
	}
	
	private static boolean rayTraycingCheck(Player p, double dist, int x, int y, int z, BlockFace frameFace) 
	{
		RayTraceResult res = p.getWorld().rayTraceBlocks(p.getEyeLocation(), p.getEyeLocation().getDirection(), dist);
		if(res.getHitBlockFace() == null) 
			return false;
		if(res.getHitBlockFace() != frameFace)
			return false;
		Location loc = moveTo(frameFace, res.getHitBlock().getLocation().clone());
		if(loc.getBlockX() != x || loc.getBlockY() != y|| loc.getBlockZ() != z)
			return false;
		return true;
	}

	private static Location moveTo(BlockFace face, Location loc) 
	{
		switch(face) 
		{
		case DOWN:
			return loc.add(0, -1 ,0);
		case UP:
			return loc.add(0, 1, 0);
		case NORTH:
			return loc.add(0, 0 ,-1);
		case SOUTH:
			return loc.add(0, 0 ,1);
		case WEST:
			return loc.add(-1, 0 ,0);
		case EAST:
			return loc.add(1, 0 ,0);
		default:
			return loc;
		}
	}
	
	public void respawnFrame() 
	{
		itemFrame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
	}
	
	private static double[] locationToArray(Location loc) 
	{
		return new double[] {loc.getX(), loc.getY(), loc.getZ()};
	}
	
}
