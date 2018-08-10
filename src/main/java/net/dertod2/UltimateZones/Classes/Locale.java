package net.dertod2.UltimateZones.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.dertod2.UltimateZones.Binary.UltimateZones;
import net.dertod2.UltimateZones.Utils.ChatComponent;
import net.dertod2.UltimateZones.Utils.UltimateUtils;

public class Locale {
	private Map<String, Map<String, String>> phrases;
	
	private String name, tag, javaLocale;
	
	public Locale(List<NodeList> nodes) throws Exception {
		this.phrases = new HashMap<String, Map<String, String>>();
		
		Element localeElement = (Element) nodes.get(0).item(0);
		
		this.name = localeElement.getElementsByTagName("name").item(0).getTextContent();
		this.tag = localeElement.getElementsByTagName("tag").item(0).getTextContent();		
		this.javaLocale = localeElement.getElementsByTagName("java").item(0).getTextContent();
		
		List<NodeList> overrides = new ArrayList<NodeList>();
		
		for (NodeList nodeList : nodes) {
			localeElement = (Element) nodeList.item(0);
			
			if (localeElement.getElementsByTagName("override").item(0).getTextContent().equalsIgnoreCase("true")) {
				overrides.add(nodeList);
				continue;
			}
			
			String module = localeElement.getElementsByTagName("module").item(0).getTextContent();
			boolean parseColors = localeElement.getElementsByTagName("colors").item(0).getTextContent().equalsIgnoreCase("true");
			
			Element phrasesElement = (Element) localeElement.getElementsByTagName("phrases").item(0);
			this.lookupCategory(phrasesElement, parseColors, module, "");
		}
		
		for (NodeList nodeList : overrides) {
			localeElement = (Element) nodeList.item(0);

			String module = localeElement.getElementsByTagName("module").item(0).getTextContent();
			boolean parseColors = localeElement.getElementsByTagName("colors").item(0).getTextContent().equalsIgnoreCase("true");
			
			Element phrasesElement = (Element) localeElement.getElementsByTagName("phrases").item(0);
			this.lookupCategory(phrasesElement, parseColors, module, "");
		}
	}
	
	private void lookupCategory(Element category, boolean parseColors, String module, String currentPath) {
		NodeList categories = category.getElementsByTagName("category");
		for (int i = 0; i < categories.getLength(); i++) {
			Element categoryElement = (Element) categories.item(i);	
			String nextPath = currentPath + categoryElement.getAttribute("key") + ".";
			
			this.lookupCategory(categoryElement, parseColors, module, nextPath);
		}
		
		if (category.getElementsByTagName("entry").getLength() >= 1) {
			NodeList entries = category.getElementsByTagName("entry");
			
			for (int i = 0; i < entries.getLength(); i++) {
				Element entry = (Element) entries.item(i);
				
				String path = currentPath + entry.getAttribute("key");
				String phrase = entry.getTextContent();
				if (parseColors && phrase.contains("§")) {
					for (ChatColor chatColor : ChatColor.values()) {
						phrase = phrase.replace("§" + chatColor.name().toLowerCase().replace("_", ""), "§" + chatColor.getChar());
					}
				}
				
				this.addPhrase(module, path, phrase);
			}
		}			
	}
	
	private void addPhrase(String module, String path, String phrase) {
		if (!this.phrases.containsKey(module)) this.phrases.put(module, new HashMap<String, String>());
		this.phrases.get(module).put(path, phrase);
	}
	
	public static Locale get(CommandSender sender) {
		return UltimateZones.localeControl.get(sender);
	}
	
	public static Locale get(String localeName) {
		return UltimateZones.localeControl.get(localeName);
	}
	
	public static String dynmap(String localeName, String phrase, Object... args) {
		Locale locale = UltimateZones.localeControl.get(localeName);
		return locale.parse("dynmap", phrase, args);
	}
	
	public static String plain(String localeName, String phrase, Object... args) {
		Locale locale = UltimateZones.localeControl.get(localeName);
		return locale.parse("plain", phrase, args);
	}
	
	public static String plain(CommandSender sender, String phrase, Object... args) {
		Locale locale = UltimateZones.localeControl.get(sender);
		return locale.parse("plain", phrase, args);
	}
	
	public static void sendPlain(CommandSender sender, String phrase, Object... args) {
		Locale locale = UltimateZones.localeControl.get(sender);
		sender.sendMessage(locale.parse("plain", phrase, args));
	}
	
	public static void json(CommandSender sender, String phrase, Object... args) {
		Locale locale = UltimateZones.localeControl.get(sender);
		ChatComponent.sendParsed(sender, locale.parse("json", phrase, args));
	}
	
	public String getTag() {
		return this.tag;
	}
	
	public String getName() {
		return this.name;
	}
	
	public java.util.Locale getJavaLocale() {
		return java.util.Locale.forLanguageTag(this.javaLocale);
	}
	
	public String parse(String category, String phrase, Object... args) {		
		String translated = this.phrases.get(category.toLowerCase()).get(phrase);
		if (translated == null) {
			UltimateUtils.error("Missing phrase " + phrase);
			return null;
		}
		
		for (int i = 0; i < args.length; i++) translated = translated.replace("{" + i + "}", args[i].toString());
		return translated;
	}
}