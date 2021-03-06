package com.github.fefo.p2wanvils;

import com.google.common.base.Preconditions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class P2WAnvils extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();
    getCommand("p2wanvilsreload").setExecutor(new ReloadCommand(this));
    final Economy economy = Bukkit.getServicesManager().load(Economy.class);
    Preconditions.checkNotNull(economy, "No registered economy system is loaded");
    Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this, economy), this);
  }
}
