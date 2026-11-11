package com.untitled.eventplugin.kits;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.Arrays;

public final class CreateKitCommand implements CommandExecutor {
    private final KitStore kits;
    public CreateKitCommand(KitStore kits) { this.kits=kits; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        if(!p.hasPermission("customevents.kit.create")) { p.sendMessage(ChatColor.RED+"No permission."); return true; }
        if(args.length<2 || !args[0].equalsIgnoreCase("kit")) { p.sendMessage(ChatColor.YELLOW+"Usage: /create kit <kitname>"); return true; }
        String name=String.join(" ",Arrays.copyOfRange(args,1,args.length)).trim();
        if(name.isBlank()) { p.sendMessage(ChatColor.RED+"Enter a kit name."); return true; }
        if(kits.has(name)) { p.sendMessage(ChatColor.RED+"Kit already exists. Use /kit update <kitname>."); return true; }
        try {
            kits.put(name,p.getInventory().getStorageContents(),p.getInventory().getArmorContents(),
                    p.getInventory().getItemInOffHand(),p.getEnderChest().getContents());
            p.sendMessage(ChatColor.GREEN+"Saved kit '"+name+"' including inventory, armor, offhand, and ender chest. Your items were not consumed.");
        } catch(Exception ex) { p.sendMessage(ChatColor.RED+"Could not save kit. Check console."); }
        return true;
    }
}
