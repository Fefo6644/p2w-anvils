package com.github.fefo.p2wanvils;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ReloadCommand implements TabExecutor {

  private static final TextComponent SPACE = text(" ");
  private static final TextComponent PREFIX;
  private static final TextComponent CONFIG_RELOADED;

  static {
    final TextComponent openBracket = text("[");
    final TextComponent prefixContent = text("P2W-Anvils", ChatColor.AQUA);
    final TextComponent closeBracket = text("]");

    PREFIX = new TextComponent(openBracket, prefixContent, closeBracket);
    PREFIX.setColor(ChatColor.GRAY);

    final TextComponent configReloadedContent = text("Configuration file reloaded", ChatColor.GREEN);
    CONFIG_RELOADED = new TextComponent(PREFIX, SPACE, configReloadedContent);
  }

  private static TextComponent text(final String text) {
    return new TextComponent(text);
  }

  private static TextComponent text(final String text, final ChatColor color) {
    final TextComponent component = new TextComponent(text);
    component.setColor(color);
    return component;
  }

  private final P2WAnvils plugin;

  public ReloadCommand(final P2WAnvils plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender,
                           final @NotNull Command cmd,
                           final @NotNull String alias,
                           final @NotNull String @NotNull [] args) {
    if (args.length == 0) {
      this.plugin.reloadConfig();
      sender.spigot().sendMessage(CONFIG_RELOADED);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public List<String> onTabComplete(final @NotNull CommandSender sender,
                                    final @NotNull Command command,
                                    final @NotNull String alias,
                                    final @NotNull String @NotNull [] args) {
    return ImmutableList.of();
  }
}
