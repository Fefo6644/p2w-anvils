package com.github.fefo.p2wanvils;

import net.md_5.bungee.api.ChatMessageType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static net.md_5.bungee.api.ChatColor.translateAlternateColorCodes;
import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public final class PlayerInteractListener implements Listener {

  private static final SplittableRandom RANDOM = new SplittableRandom();

  private final P2WAnvils plugin;
  private final Economy economy;
  private final Set<Location> anvilsInUse = new HashSet<>();

  public PlayerInteractListener(final P2WAnvils plugin, final Economy economy) {
    this.plugin = plugin;
    this.economy = economy;
  }

  @EventHandler(ignoreCancelled = true)
  public void onAnvilClick(final @NotNull PlayerInteractEvent event) {
    final Player player = event.getPlayer();

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK
        || event.getClickedBlock() == null
        || event.getClickedBlock().getType() != Material.ANVIL) {
      return;
    }

    boolean isWorldEnabled = false;
    final World world = player.getWorld();
    for (String worldName : this.plugin.getConfig().getStringList("enabledInWorlds")) {
      if (world.getName().equalsIgnoreCase(worldName)) {

        if (player.isSneaking()) {
          return;
        }

        isWorldEnabled = true;
        event.setCancelled(true);
        break;
      }
    }

    if (!isWorldEnabled) {
      return;
    }

    final ItemStack damagedItem = event.getItem();
    if (damagedItem == null || damagedItem.getType().getMaxDurability() == 0) {
      return;
    }

    if (damagedItem.getDurability() <= 0) {
      player.playSound(player.getEyeLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.MASTER, 2.0f, 0.0f);

      String itemNotDamagedMessage = this.plugin.getConfig().getString("itemNotDamagedMessage");
      itemNotDamagedMessage = translateAlternateColorCodes('&', itemNotDamagedMessage);
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, fromLegacyText(itemNotDamagedMessage));
      return;
    }

    final boolean canUseForFree = player.hasPermission("p2wanvils.freeuse");
    final double repairCost = this.plugin.getConfig().getDouble("coinsRequired");

    if (!canUseForFree && !this.economy.has(player, repairCost)) {
      player.playSound(player.getEyeLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.MASTER, 2.0f, 0.0f);

      String notEnoughCoinsMessage = this.plugin.getConfig().getString("notEnoughCoinsMessage");
      notEnoughCoinsMessage = translateAlternateColorCodes('&', notEnoughCoinsMessage);
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, fromLegacyText(notEnoughCoinsMessage));
      return;
    }

    final Location anvilLocation = event.getClickedBlock().getLocation().clone().add(0.5, 1.0, 0.5);
    if (this.anvilsInUse.contains(anvilLocation)) {
      player.playSound(player.getEyeLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.MASTER, 2.0f, 0.0f);

      String anvilInUseMessage = this.plugin.getConfig().getString("anvilCurrentlyInUseMessage");
      anvilInUseMessage = translateAlternateColorCodes('&', anvilInUseMessage);
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, fromLegacyText(anvilInUseMessage));
      return;
    }

    this.anvilsInUse.add(anvilLocation);
    final ItemStack repairedItem = damagedItem.clone();
    player.getInventory().setItemInMainHand(null);

    final Item itemEntity = world.dropItem(anvilLocation, repairedItem);
    itemEntity.setPickupDelay(Integer.MAX_VALUE);
    itemEntity.setVelocity(new Vector(0.0, 0.015625, 0.0));
    itemEntity.setGravity(false);
    itemEntity.setGlowing(true);

    final AtomicInteger ticks = new AtomicInteger(0);
    final BukkitTask effectsTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      world.spawnParticle(Particle.LAVA, anvilLocation, 4);
      world.spawnParticle(Particle.END_ROD, anvilLocation, 3, 0, 0, 0, 0.066);
      world.playSound(anvilLocation, Sound.ENTITY_FIREWORK_TWINKLE, SoundCategory.MASTER, (float) RANDOM.nextDouble() * 0.25f, (float) RANDOM.nextDouble() * 2.0f);
      world.playSound(anvilLocation, Sound.ENTITY_FIREWORK_TWINKLE_FAR, SoundCategory.MASTER, (float) RANDOM.nextDouble() * 2.0f, (float) RANDOM.nextDouble() * 2.0f);
      world.playSound(anvilLocation, Sound.BLOCK_GRAVEL_BREAK, SoundCategory.MASTER, 0.25f, (float) RANDOM.nextDouble() * 2.0f);

      if (ticks.getAndIncrement() % 6 == 0) {
        world.playSound(anvilLocation, Sound.BLOCK_ANVIL_PLACE, SoundCategory.MASTER, 0.75f, (float) RANDOM.nextDouble() * 0.33f + 0.33f);
        world.spawnParticle(Particle.BLOCK_CRACK, anvilLocation, 300, 0, 0.125, 0, 1, new MaterialData(Material.ANVIL));
        world.spawnParticle(Particle.BLOCK_CRACK, anvilLocation, 300, 0, 0.125, 0, 1, new MaterialData(Material.IRON_FENCE));
      }
    }, 0L, 3L);

    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
      effectsTask.cancel();
      itemEntity.remove();
      this.anvilsInUse.remove(anvilLocation);
      if (!player.isOnline()) {
        return;
      }

      repairedItem.setDurability((short) 0);
      if (!canUseForFree) {
        this.economy.withdrawPlayer(player, repairCost);
      }

      player.getInventory().addItem(repairedItem);

      String itemRepairedMessage = this.plugin.getConfig().getString("itemRepairedMessage");
      itemRepairedMessage = translateAlternateColorCodes('&', itemRepairedMessage);
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, fromLegacyText(itemRepairedMessage));
      player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 2.0f, 1.0f);
    }, 20L * this.plugin.getConfig().getLong("secondsToRepair", 4L));
  }
}
