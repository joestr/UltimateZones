package net.dertod2.UltimateZones.CreationWizard;

import java.util.ArrayList;
import java.util.List;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Flag;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.RightResult.RightEnum;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Classes.Zones.MainZone;
import net.dertod2.UltimateZones.Classes.Zones.SubZone;
import net.dertod2.UltimateZones.Classes.Zones.UltimateZoneType;
import net.dertod2.UltimateZones.Utils.NMSHelper;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.dertod2.ZonesLib.Classes.CuboidZone;
import net.dertod2.ZonesLib.Classes.HeightZone;
import net.dertod2.ZonesLib.Classes.OriginZone;
import net.dertod2.ZonesLib.Classes.PolygonZone;
import net.dertod2.ZonesLib.Classes.RoundedZone;
import net.dertod2.ZonesLib.Classes.Zone;
import net.dertod2.ZonesLib.Classes.ZoneType;
import net.dertod2.ZonesLib.DisplayAPI.DisplayControl;
import net.dertod2.ZonesLib.DisplayAPI.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class NormalCreation extends AbstractCreation {
	private List<Location> positions = new ArrayList<Location>();
	
	private AbstractZone parentZone;
	
	public NormalCreation(Player zoneCreator, UltimateZoneType ultimateZoneType, ZoneType zoneType, boolean force3d) {
		super(zoneCreator, zoneCreator, ultimateZoneType, zoneType, force3d);				
		if (zoneType.getMaximum() != -1) for (int i = 0; i < zoneType.getMaximum(); i++) this.positions.add(null);
	}
	
	public NormalCreation(Player zoneCreator, OfflinePlayer zoneOwner, UltimateZoneType ultimateZoneType, ZoneType zoneType, boolean force3d) {
		super(zoneCreator, zoneOwner, ultimateZoneType, zoneType, force3d);
		if (zoneType.getMaximum() != -1) for (int i = 0; i < zoneType.getMaximum(); i++) this.positions.add(null);
	}

	public void setLocation(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) return;
		if (UltimateZones.zoneItem != null && UltimateZones.zoneItem.getType() != Material.AIR) {
			if (!event.getItem().getType().equals(UltimateZones.zoneItem.getType())) {
				return;
			}
		}
		
		event.setCancelled(true);
		
		boolean rightClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;
		Location location = event.getClickedBlock().getLocation();

		if (this.ultimateZoneType != UltimateZoneType.MAIN) {
			AbstractZone parentZone = AbstractZone.getZone(location);
			
			if (parentZone == null) {
				Locale.sendPlain(this.zoneCreator, "creation.error.subzone.mainzone");
				return;
			}
			
			if (this.parentZone == null) this.parentZone = parentZone;
			
			if (this.parentZone.getId() != parentZone.getId()) {
				Locale.sendPlain(this.zoneCreator, "creation.error.subzone.sameparent");
				return;
			}		
		}
		
		this.creationState = CreationState.PositionsSet;
		
		switch (this.zoneType) {
			case CUBOID:
				if (rightClick) {
					if (this.positions.get(0) == null || !this.positions.get(0).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.cuboid.right", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(0, location);
					}
				} else {
					if (this.positions.get(1) == null || !this.positions.get(1).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.cuboid.left", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(1, location);
					}
				}
				
				break;
			case CYLINDER:
				if (rightClick) {
					if (this.positions.get(0) == null || !this.positions.get(0).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.cylinder.right", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(0, location);
					}
				} else {
					if (this.positions.get(1) == null || !this.positions.get(1).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.cylinder.left", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(1, location);
					}
				}
				
				break;
			case SPHERE:
				if (rightClick) {
					if (this.positions.get(0) == null || !this.positions.get(0).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.sphere.right", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(0, location);
					}
				} else {
					if (this.positions.get(1) == null || !this.positions.get(1).equals(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.sphere.left", location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.set(1, location);
					}
				}
				
				break;
			case POLYGON:
				if (rightClick) {
					if (!this.positions.contains(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.polygon.right.success", this.getSize(this.positions) + 1, location.getBlockX(), location.getBlockY(), location.getBlockZ());
						this.positions.add(location);
					} else {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.polygon.right.failed");
					}
				} else {
					if (this.positions.remove(location)) {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.polygon.left.success", location.getBlockX(), location.getBlockY(), location.getBlockZ());
					} else {
						Locale.sendPlain(this.zoneCreator, "creation.normal.positions.polygon.left.failed");
					}
				}
				
				break;
		}
		
		DisplayControl.stopViewing(this.zoneCreator, this.displayId);
		
		if (this.getSize(this.positions) < this.zoneType.getMinimum()) return;
		this.creationState = this.zoneType == ZoneType.POLYGON ? CreationState.CheckPossible : CreationState.WaitForCheck;
		
		Zone previewZone = Zone.test(this.zoneCreator, this.cleanup(this.positions), !this.force3D, this.zoneType);

		this.displayId = DisplayControl.startViewing(previewZone, ParticleEffect.REDSTONE, -1, this.zoneCreator);
		
		if (previewZone instanceof OriginZone) {
			Vector origin = ((OriginZone) previewZone).getCenter();
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.origin", origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
		}
		
		Locale.sendPlain(this.zoneCreator, "creation.normal.info.area", UltimateUtils.formattedArea(previewZone.getArea(), Locale.get(this.zoneCreator).getJavaLocale()));
		Locale.sendPlain(this.zoneCreator, "creation.normal.info.volume", UltimateUtils.formattedVolume(previewZone.getVolume(), Locale.get(this.zoneCreator).getJavaLocale()));
			
		Locale.json(this.zoneCreator, "creation.normal.info.clickhere");
	}
	
	public boolean check(boolean silent) {
		if (this.creationState != CreationState.WaitForCheck && this.creationState != CreationState.CheckPossible && this.creationState != CreationState.CheckSuccesfully) return false;
		
		Zone checkZone = Zone.test(this.zoneCreator, this.cleanup(this.positions), !this.force3D, this.zoneType);
		
		YamlConfiguration yamlConfiguration = UltimateZones.getConfiguration(checkZone.getWorld());
		boolean ignoreRestrictions = this.zoneCreator.hasPermission("ultimatezones.ignorerestrictions");
		
		if (this.ultimateZoneType == UltimateZoneType.MAIN) {
			if (!ignoreRestrictions) {
				int maxZones = yamlConfiguration.getInt("max-zones");
				
				if (maxZones != -1 && AbstractZone.getZones(this.zoneOwner).size() + 1 > maxZones) {
					if (!silent) Locale.sendPlain(this.zoneCreator, "", maxZones);
					return false;
				}
				
				String restrictions = "restrictions.geometry." + this.zoneType.getName().toLowerCase() + ".";			
				double minWidth, maxWidth, minRadius, maxRadius, minHeight, maxHeight, aspectRatio, maxDots;
				
				if (checkZone instanceof HeightZone && this.force3D) {
					minHeight = yamlConfiguration.getDouble(restrictions + "min-height");
					maxHeight = yamlConfiguration.getDouble(restrictions + "max-height");
					
					double height = ((HeightZone) checkZone).getHeight();
					
					if (minHeight != -1 && height < minHeight) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.heightzone.minheight", minHeight);
						return false;
					}
					
					if (maxHeight != -1 && height > maxHeight) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.heightzone.maxheight", maxHeight);
						return false;
					}
				}
				
				if (checkZone instanceof RoundedZone) {
					minRadius = yamlConfiguration.getDouble(restrictions + "min-radius");
					maxRadius = yamlConfiguration.getDouble(restrictions + "max-radius");
					
					double radius = ((RoundedZone) checkZone).getRadius();
					
					if (minRadius != -1 && radius < minRadius) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.roundedzone.minradius", minRadius);							
						return false;
					}
					
					if (maxRadius != -1 && radius > maxRadius) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.roundedzone.maxradius", maxRadius);							
						return false;
					}			
				}
				
				switch (this.zoneType) {
					case CUBOID:
						minWidth = yamlConfiguration.getDouble(restrictions + "min-width");
						maxWidth = yamlConfiguration.getDouble(restrictions + "max-width");
						aspectRatio = yamlConfiguration.getDouble(restrictions + "sts-multiplier");
						
						double zoneWidth = ((CuboidZone) checkZone).getWidth();
						double zoneLength = ((CuboidZone) checkZone).getLength();
						
						if (minWidth != -1 && zoneWidth < minWidth || zoneLength < minWidth) {
							if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.cuboid.minlength", minWidth);
							return false;
						}
						
						if (maxWidth != -1 && (zoneWidth > maxWidth || zoneLength > maxWidth)) {
							if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.cuboid.maxlength", maxWidth);							
							return false;
							
						}

						if (aspectRatio != -1 && (zoneLength > (aspectRatio * zoneWidth) || zoneWidth > (aspectRatio * zoneLength))) {
							if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.cuboid.aspect", aspectRatio);							
							return false;
						}
						
						break;
					case CYLINDER:
						break;
					case SPHERE:
						break;
					case POLYGON:
						maxDots = yamlConfiguration.getDouble(restrictions + "max-dots");
						
						int dots = ((PolygonZone) checkZone).getPolygonCount();
						
						if (maxDots != -1 && dots > maxDots) {
							if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.polygon.maxdots", maxDots);
							return false;
						}
						
						break;
				}
				
				int maxArea = yamlConfiguration.getInt("restrictions.main.max-area");
				if (maxArea != -1) {		
					double completeArea = checkZone.getArea(); 
					for (AbstractZone abstractZone : AbstractZone.getZones(this.zoneOwner)) {
						completeArea += abstractZone.getZone().getArea();
					}
					
					if (completeArea > maxArea) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.main.maxarea", maxArea);
						return false;
					}
				}
				
				int maxVolume = yamlConfiguration.getInt("restrictions.main.max-volume");
				if (maxVolume != -1) {		
					double completeVolume = checkZone.getVolume();
					for (AbstractZone abstractZone : AbstractZone.getZones(this.zoneOwner)) {
						completeVolume += abstractZone.getZone().getVolume();
					}
					
					if (completeVolume > maxVolume) {
						if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.main.maxvolume", maxVolume);
						return false;
					}
				}
			}
			
			boolean disableOverlapping = UltimateZones.getConfiguration().getBoolean("deactivate-overlapping");
			List<AbstractZone> collisionList = AbstractZone.getZones(checkZone);
			for (AbstractZone abstractZone : collisionList) {
				if (abstractZone instanceof SubZone) continue;
				MainZone mainZone = (MainZone) abstractZone;
				
				if (disableOverlapping || this.zonePriority == mainZone.getPriority()) { 
					if (!silent) Locale.json(this.zoneCreator, "creation.normal.check.collision.main", mainZone.hasSynonym() ? mainZone.getSynonym() : mainZone.getId(), mainZone.getId(), mainZone.getPlayer().getName());
					if (!ignoreRestrictions) return false;
				}
			}
		} else {
			if (!ignoreRestrictions) {
				int maxChilds = yamlConfiguration.getInt("max-children-zone");
				int maxDepth = yamlConfiguration.getInt("max-zone-depth");
				
				if (maxChilds != -1 && this.parentZone.getZones(true).size() + 1 > maxChilds) {
					if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.child.maxchilds", maxChilds);
					return false;
				}
				
				if (maxDepth != -1 && UltimateUtils.getRecursiveZoneDepth(this.parentZone) + 1 > maxDepth) {
					if (!silent) Locale.sendPlain(this.zoneCreator, "creation.normal.check.child.maxdepth", maxDepth);
					return false;
				}
				
				if (!this.parentZone.isOwner((CommandSender) this.zoneCreator) && this.parentZone.hasRight(this.zoneCreator, Flag.AdministrateZones).rightEnum != RightEnum.Success) {
					if (!silent) // TODO Output
					return false;
				}
			}
			
			List<SubZone> zoneList = this.parentZone.getZones(false);
			for (SubZone subZone : zoneList) {
				if (subZone.getZone().intersect(checkZone)) {
					if (!silent) Locale.json(this.zoneCreator, "creation.normal.check.collision.child", subZone.hasSynonym() ? subZone.getSynonym() : subZone.getId(), subZone.getId(), subZone.getPlayer().getName());
					return false;
				}
			}
		}
		
		if (!silent) Locale.json(this.zoneCreator, "creation.normal.check.success");
		
		this.creationState = CreationState.CheckSuccesfully;
		return true;
	}
	
	public boolean create() {
		if (this.creationState != CreationState.CheckSuccesfully) return false;
		if (!this.check(true)) return false; // To be safe <- Check if other zones has been created meanwhile
		
		Zone zone = Zone.create(this.zoneCreator, this.cleanup(this.positions), !this.force3D, this.zoneType);
		if (zone != null) {
			AbstractZone abstractZone = UltimateZones.zoneControl.addZone(zone, this.ultimateZoneType, this.zoneOwner, this.parentZone != null ? this.parentZone.getId() : -1, this.zonePriority);
			if (abstractZone != null) {
				Locale.sendPlain(this.zoneCreator, "creation.normal.create.success");
			} else {
				Locale.sendPlain(this.zoneCreator, "creation.normal.create.error");
			}
		} else {
			Locale.sendPlain(this.zoneCreator, "creation.normal.create.error");
		}
		
		this.abort(true);
		return true;
	}
	
	public void info() {
		if (UltimateZones.zoneItem != null && UltimateZones.zoneItem.getType() != Material.AIR) {
			Locale.json(this.zoneCreator, "creation.normal.info.withitem", NMSHelper.getMojangIdentifierName(UltimateZones.zoneItem), NMSHelper.getMojangTranslatableName(UltimateZones.zoneItem));
		} else {
			Locale.json(this.zoneCreator, "creation.normal.info.withoutitem");
		}
		
		switch (this.zoneType) {
		case CUBOID:
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.cuboid");
			break;
		case CYLINDER:
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.cylinder.right");
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.cylinder.left");
			break;
		case SPHERE:
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.sphere.right");
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.sphere.left");
			break;
		case POLYGON:
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.polygon.right");
			Locale.sendPlain(this.zoneCreator, "creation.normal.info.polygon.left");
			break;
		}
	}
	
	private int getSize(List<?> list) {
		int size = 0;
		
		for (Object object : list) {
			if (object != null) size++;
		}
		
		return size;
	}
	
	private <T> List<T> cleanup(List<T> list) {
		List<T> newList = new ArrayList<T>();
		
		for (T t : list) {
			if (t != null) newList.add(t);
		}
		
		return newList;
	}
}