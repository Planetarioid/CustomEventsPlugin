package com.untitled.eventplugin.kits;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public final class KitCommand implements CommandExecutor {
    static final int PAGE_SIZE=45;
    private final KitStore kits;
    public KitCommand(KitStore kits) { this.kits=kits; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0 || args[0].equalsIgnoreCase("list")) {
            if(sender instanceof Player p) openPage(p,0); else sender.sendMessage("Use /kit list in-game.");
            return true;
        }
        if(args[0].equalsIgnoreCase("give")) {
            if(!sender.hasPermission("customevents.kit.give")) { sender.sendMessage(ChatColor.RED+"No permission."); return true; }
            if(args.length<3) { sender.sendMessage(ChatColor.YELLOW+"Usage: /kit give <player> <kitname>"); return true; }
            Player target=Bukkit.getPlayerExact(args[1]);
            if(target==null) { sender.sendMessage(ChatColor.RED+"Player must be online."); return true; }
            String name=String.join(" ",Arrays.copyOfRange(args,2,args.length));
            KitStore.Kit kit=kits.get(name);
            if(kit==null) { sender.sendMessage(ChatColor.RED+"Kit not found."); return true; }
            target.getInventory().setStorageContents(cloneItems(kit.storage()));
            target.getInventory().setArmorContents(cloneItems(kit.armor()));
            target.getInventory().setItemInOffHand(kit.offhand()==null?null:kit.offhand().clone());
            target.getEnderChest().setContents(cloneItems(kit.enderChest()));
            sender.sendMessage(ChatColor.GREEN+"Gave '"+name+"' to "+target.getName()+".");
            target.sendMessage(ChatColor.GREEN+"You received kit '"+name+"'.");
            return true;
        }
        if(args[0].equalsIgnoreCase("update")) {
            if(!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
            if(!p.hasPermission("customevents.kit.update")) { p.sendMessage(ChatColor.RED+"No permission."); return true; }
            if(args.length<2) { p.sendMessage(ChatColor.YELLOW+"Usage: /kit update <kitname>"); return true; }
            String name=String.join(" ",Arrays.copyOfRange(args,1,args.length));
            if(!kits.has(name)) { p.sendMessage(ChatColor.RED+"Kit not found."); return true; }
            try {
                kits.put(name,p.getInventory().getStorageContents(),p.getInventory().getArmorContents(),
                        p.getInventory().getItemInOffHand(),p.getEnderChest().getContents());
                p.sendMessage(ChatColor.GREEN+"Updated kit '"+name+"'.");
            } catch(Exception ex) { p.sendMessage(ChatColor.RED+"Could not update kit. Check console."); }
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW+"/kit list | /kit give <player> <kitname> | /kit update <kitname>");
        return true;
    }
    void openPage(Player p,int page) {
        List<String> names=new ArrayList<>(kits.names());
        int pages=Math.max(1,(int)Math.ceil(names.size()/(double)PAGE_SIZE));
        int current=Math.max(0,Math.min(page,pages-1));
        Inventory inv=Bukkit.createInventory(new KitPageHolder(current),54,
                ChatColor.DARK_GREEN+"Saved Kits "+ChatColor.GRAY+"("+ (current+1)+"/"+pages+")");
        int start=current*PAGE_SIZE;
        for(int i=0;i<PAGE_SIZE && start+i<names.size();i++) {
            ItemStack icon=new ItemStack(Material.CHEST);
            ItemMeta meta=icon.getItemMeta(); meta.setDisplayName(ChatColor.GOLD+names.get(start+i));
            meta.setLore(List.of(ChatColor.GRAY+"Saved kit",ChatColor.YELLOW+"Use /kit give <player> <kitname>"));
            icon.setItemMeta(meta); inv.setItem(i,icon);
        }
        if(current>0) inv.setItem(45,nav(Material.ARROW,ChatColor.YELLOW+"Previous Page"));
        inv.setItem(49,nav(Material.BARRIER,ChatColor.RED+"Close"));
        if(current+1<pages) inv.setItem(53,nav(Material.ARROW,ChatColor.YELLOW+"Next Page"));
        p.openInventory(inv);
    }
    private ItemStack nav(Material type,String name) {
        ItemStack item=new ItemStack(type); ItemMeta meta=item.getItemMeta(); meta.setDisplayName(name); item.setItemMeta(meta); return item;
    }
    private ItemStack[] cloneItems(ItemStack[] source) {
        if(source==null) return new ItemStack[0];
        return Arrays.stream(source).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new);
    }
    public static final class KitPageHolder implements InventoryHolder {
        private final int page;
        KitPageHolder(int page) { this.page=page; }
        public int page() { return page; }
        @Override public Inventory getInventory() { return null; }
    }
}
