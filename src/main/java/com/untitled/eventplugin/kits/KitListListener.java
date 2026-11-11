package com.untitled.eventplugin.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public final class KitListListener implements Listener {
    private final KitCommand command;
    public KitListListener(KitCommand command) { this.command=command; }
    @EventHandler public void click(InventoryClickEvent e) {
        if(!(e.getView().getTopInventory().getHolder() instanceof KitCommand.KitPageHolder holder)) return;
        e.setCancelled(true);
        if(e.getClickedInventory()!=e.getView().getTopInventory()) return;
        ItemStack item=e.getCurrentItem(); if(item==null) return;
        if(item.getType()==Material.BARRIER) { e.getWhoClicked().closeInventory(); return; }
        if(item.getType()==Material.ARROW && e.getWhoClicked() instanceof Player p) {
            command.openPage(p,holder.page()+(e.getSlot()==45?-1:1));
        }
    }
    @EventHandler public void drag(InventoryDragEvent e) {
        if(e.getView().getTopInventory().getHolder() instanceof KitCommand.KitPageHolder) e.setCancelled(true);
    }
}
