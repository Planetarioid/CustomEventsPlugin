package com.untitled.eventplugin.kits;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.*;

public final class KitStore {
    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration yaml;
    public KitStore(JavaPlugin plugin) {
        this.plugin=plugin; this.file=new File(plugin.getDataFolder(),"kits.yml");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.yaml=YamlConfiguration.loadConfiguration(file);
    }
    private String path(String name) { return "kits."+name.toLowerCase(Locale.ROOT); }
    public boolean has(String name) { return yaml.isConfigurationSection(path(name)); }
    public Set<String> names() {
        var section=yaml.getConfigurationSection("kits");
        if(section==null) return new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        TreeSet<String> out=new TreeSet<>(String.CASE_INSENSITIVE_ORDER); out.addAll(section.getKeys(false)); return out;
    }
    public void put(String name, ItemStack[] storage, ItemStack[] armor, ItemStack offhand, ItemStack[] ender) throws IOException {
        String p=path(name);
        yaml.set(p+".displayName",name);
        yaml.set(p+".storage",Arrays.asList(storage));
        yaml.set(p+".armor",Arrays.asList(armor));
        yaml.set(p+".offhand",offhand);
        yaml.set(p+".enderChest",Arrays.asList(ender));
        yaml.save(file);
    }
    public Kit get(String name) {
        String p=path(name); if(!yaml.isConfigurationSection(p)) return null;
        return new Kit(readItems(yaml.getList(p+".storage")),readItems(yaml.getList(p+".armor")),
                yaml.getItemStack(p+".offhand"),readItems(yaml.getList(p+".enderChest")));
    }
    private ItemStack[] readItems(List<?> list) {
        if(list==null) return new ItemStack[0];
        ItemStack[] items=new ItemStack[list.size()];
        for(int i=0;i<list.size();i++) items[i]=list.get(i) instanceof ItemStack item ? item : null;
        return items;
    }
    public void save() { try { yaml.save(file); } catch(IOException e) { plugin.getLogger().severe("Could not save kits.yml: "+e.getMessage()); } }
    public record Kit(ItemStack[] storage, ItemStack[] armor, ItemStack offhand, ItemStack[] enderChest) {}
}
