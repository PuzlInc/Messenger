/*
* Messenger
* Copyright (C) 2014 Puzl Inc.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.puzlinc.messenger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Messenger {

    // Begin example message keys - edit these if you modify the class to suit your needs
    public static final String EXAMPLE = "example-string";
    public static final String EXAMPLE_FORMAT = "example-string-format";
    public static final String EXAMPLE_COLOR = "example-string-color";
    public static final String EXAMPLE_LIST = "example-string-list";
    // End keys

    private Map<String, Object> defaults = new HashMap<String, Object>(){{
        put(EXAMPLE, "This is a string");
        put(EXAMPLE_FORMAT, "This is a string with some data in it: %s");
        put(EXAMPLE_COLOR, "This is a string with some &ccolor in it");
        put(EXAMPLE_LIST, Arrays.asList(new String[]{"This is the first message", "This is the second message"}));
    }};

    private String prefix = ChatColor.DARK_GRAY + "[PLUGIN] " + ChatColor.WHITE;

    private String fileName = "messages.yml"; // File to store as inside data folder

    private static final String SPLIT_TOKEN = "\n";

    private File parentFile;
    private File configFile;
    private YamlConfiguration config;

    /**
     * Create an instance of the Messenger for a given plugin.
     * Note: {@link Messenger#load()} must be called before values can be retrieved.
     * @param plugin Your JavaPlugin.
     */
    public Messenger(JavaPlugin plugin) {
        parentFile = plugin.getDataFolder();
        configFile = new File(parentFile, fileName);

        // Run unique test on keys
        testDuplicates();
    }

    /**
     * Set the default message values.
     * @param defaults Map where key is String for YAML storage, value is either String or List<String>.
     */
    public void setDefaults(Map<String, Object> defaults) {
        this.defaults = defaults;
    }

    /**
     * Set the prefix sent before any message.
     * Set to null for no prefix.
     * @param prefix Prefix, or null for no prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * The file name inside of the plugin's data folder to save values in.
     * @param fileName Messages file name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        configFile = new File(parentFile, fileName);
    }

    /**
     * Get all default values.
     * @return Defaults.
     */
    public Map<String, Object> getDefaults() {
        return defaults;
    }

    /**
     * Get message prefix.
     * @return Prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get file name.
     * @return File name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Load the configuration and create any default files / values as needed.
     * This function must be called before values can be retrieved, and after defaults or file name changes are made.
     */
    public void load() {
        if (!parentFile.exists()) {
            parentFile.mkdir();
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                config = YamlConfiguration.loadConfiguration(configFile);
                for (String key : defaults.keySet()) {
                    config.set(key, defaults.get(key));
                }
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    /**
     * Send a message to a given CommandSender.
     * @param key The key that the message is stored as.
     * @param sender CommandSender to send message to.
     */
    public void send(String key, CommandSender sender) {
        Object value = config.get(key);
        if (value instanceof String) {
            String string  = (String) value;
            sender.sendMessage(prefix != null ? prefix + format(string) : format(string));
        } else {
            sender.sendMessage(format(arrayToString(value)).split(SPLIT_TOKEN));
        }
    }

    /**
     * Send a message to a given CommandSender with formatting.
     * @param key The key that the message is stored as.
     * @param sender CommandSender to send message to.
     * @param format The format to use with {@link java.lang.String#format(String, Object...)}.
     */
    public void send(String key, CommandSender sender, Object... format) {
        Object value = config.get(key);
        if (value instanceof String) {
            String string  = (String) value;
            sender.sendMessage(prefix != null ? prefix + format(string) : format(string));
        } else {
            sender.sendMessage(format(arrayToString(value), format).split(SPLIT_TOKEN));
        }
    }

    private void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testDuplicates() {
        // Run unique test on keys
        Field[] declaredFields = this.getClass().getDeclaredFields();
        List<String> keys = new ArrayList<String>();
        for (Field field : declaredFields) {
            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                if (field.getType().getName().equals("java.lang.String")) {
                    try {
                        String value = (String) field.get(field);
                        if (keys.contains(value)) {
                            System.out.println("!!WARNING: Messaging class " + field.getName() + " has duplicate key.");
                        }
                        keys.add(value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String format(String string, Object... format) {
        return format(String.format(string, format));
    }

    private String format(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private String arrayToString(Object object) {
        return arrayToString((List<String>) object);
    }

    private String arrayToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String string : list) {
            if (prefix != null) {
                builder.append(prefix);
            }
            builder.append(string);
            builder.append(SPLIT_TOKEN);
        }
        return builder.toString();
    }
}
