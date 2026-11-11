package com.untitled.eventplugin;

import com.untitled.eventplugin.kits.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class CustomEventsPlugin extends JavaPlugin {
    private KitStore kits;
    @Override public void onEnable() {
        saveDefaultConfig();
        kits = new KitStore(this);
        Objects.requireNonNull(getCommand("create")).setExecutor(new CreateKitCommand(kits));
        KitCommand kitCommand = new KitCommand(kits);
        Objects.requireNonNull(getCommand("kit")).setExecutor(kitCommand);
        getServer().getPluginManager().registerEvents(new KitListListener(kitCommand), this);
        getLogger().warning("Early development build: only saved-kit commands and paginated kit browsing are implemented.");
    }
    @Override public void onDisable() { if (kits != null) kits.save(); }
}
