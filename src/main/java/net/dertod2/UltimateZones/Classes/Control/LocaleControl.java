package net.dertod2.UltimateZones.Classes.Control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Utils.UltimateUtils;
import net.md_5.bungee.api.ChatColor;

public class LocaleControl {
	private Map<String, Locale> localeList;
	
	public LocaleControl() {
		this.localeList = new HashMap<String, Locale>();
		
		File localeDirectory = new File(UltimateZones.getInstance().getDataFolder(), "locales");		
		if (!localeDirectory.exists()) localeDirectory.mkdirs();
		
		// Before loading or extracting check for outdated locale files
		this.deleteOldVersions(localeDirectory, "locales/de_DE.dynmap.xml", "locales/de_DE.json.xml", "locales/de_DE.plain.xml", "locales/de_DE.help.xml");
		this.deleteOldVersions(localeDirectory, "locales/en_US.dynmap.xml", "locales/en_US.json.xml", "locales/en_US.plain.xml", "locales/en_US.help.xml");

		// Extract embedded locales
		try {
			UltimateUtils.extractRessources(localeDirectory, "locales/de_DE.dynmap.xml", "locales/de_DE.json.xml", "locales/de_DE.plain.xml", "locales/de_DE.help.xml");
		} catch (Exception exc) {
			UltimateUtils.notify("Error while saving embedded locale " + ChatColor.GOLD + "de_DE");
			exc.printStackTrace();
		}
		
		try {
			UltimateUtils.extractRessources(localeDirectory, "locales/en_US.dynmap.xml", "locales/en_US.json.xml", "locales/en_US.plain.xml", "locales/en_US.help.xml");
		} catch (Exception exc) {
			UltimateUtils.notify("Error while saving embedded locale " + ChatColor.GOLD + "en_US");
			exc.printStackTrace();
		}
		
		// Extract simple readme text file for customized phrases
		try {
			UltimateUtils.extractRessources(localeDirectory, "locales/readme.txt");
		} catch (Exception exc) { }
		
		Map<String, List<File>> localeFiles = new HashMap<String, List<File>>();	
		for (File file : localeDirectory.listFiles()) {
			if (!file.isFile() || !file.getName().endsWith(".xml")) continue;
			
			String fileName = file.getName();
			String locale = fileName.substring(0, fileName.indexOf("."));
			
			if (!localeFiles.containsKey(locale)) localeFiles.put(locale, new ArrayList<File>());
			localeFiles.get(locale).add(file);
		}
		
		for (Entry<String, List<File>> entry : localeFiles.entrySet()) {
			try {		
				this.loadLocale(entry.getKey(), entry.getValue());
			} catch (Exception exc) {
				UltimateUtils.error("Error while loading locale " + ChatColor.GOLD + entry.getKey());
			}
		}
	}
	
	private void loadLocale(String localeName, List<File> files) throws Exception {
		List<NodeList> nodes = new ArrayList<NodeList>();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();	
		
		for (File file : files) {
			Document document = documentBuilder.parse(file);
			nodes.add(document.getElementsByTagName("locale"));
		}
		
		Locale locale = new Locale(nodes);
		this.localeList.put(locale.getTag(), locale);
		
		UltimateUtils.notify("Finished loading locale " + ChatColor.GOLD + locale.getTag());
	}
	
	private void deleteOldVersions(File targetFolder, String... ressources) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException exc) {
			exc.printStackTrace();
			return;
		}
		
		for (String ressource : ressources) {
			File file = new File(targetFolder, ressource.substring(ressource.lastIndexOf("/")));
			if (!file.exists()) continue;
			
			try {
				Document resDoc = documentBuilder.parse(UltimateZones.getInstance().getResource(ressource));
				Document fileDoc = documentBuilder.parse(file);
				
				String resVersion = ((Element) resDoc.getElementsByTagName("locale").item(0)).getElementsByTagName("version").item(0).getTextContent();
				String fileVersion = ((Element) fileDoc.getElementsByTagName("locale").item(0)).getElementsByTagName("version").getLength() > 0 ? ((Element) fileDoc.getElementsByTagName("locale").item(0)).getElementsByTagName("version").item(0).getTextContent() : "0";
			
				if (Integer.parseInt(resVersion) <= Integer.parseInt(fileVersion)) continue;
				UltimateUtils.notify("Found outdated locale file " + ChatColor.GOLD + file.getName() + ChatColor.DARK_GREEN + " (v" + ChatColor.GOLD + fileVersion + ChatColor.DARK_GREEN + "). Updating to newest version v" + ChatColor.GOLD + resVersion + ChatColor.DARK_GREEN + "...");
			} catch (Exception exc) {
			}
			
			file.delete();
		}
	}

	public Locale get(CommandSender sender) {
		String forceLocale = UltimateZones.getConfiguration().getString("force-locale", "null");
		Locale locale = null;
		
		if (!forceLocale.equals("null")) locale = this.localeList.get(forceLocale);		
		if (locale == null && sender instanceof Player && UltimateZones.getConfiguration().getBoolean("detect-locale", true)) locale = this.localeList.get(((Player) sender).spigot().getLocale());
		if (locale == null) locale = this.localeList.get(UltimateZones.getConfiguration().get("fallback-locale", "en_US"));

		return locale;
	}
	
	public Locale get(String localeName) {
		Locale locale = this.localeList.get(localeName);		
		
		if (locale == null) locale = this.localeList.get(UltimateZones.getConfiguration().get("fallback-locale", "en_US"));

		return locale;
	}
}