//
// This file is part of P2W-Anvils, licensed under the MIT License.
//
// Copyright (c) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

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

    PREFIX = text("", ChatColor.GRAY);
    PREFIX.setExtra(ImmutableList.of(openBracket, prefixContent, closeBracket));

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
