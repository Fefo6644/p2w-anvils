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

import com.google.common.base.Preconditions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class P2WAnvils extends JavaPlugin {

  @Override
  public void onEnable() {
    saveDefaultConfig();

    final PluginCommand command = getCommand("p2wanvilsreload");
    final ReloadCommand reloadCommand = new ReloadCommand(this);
    command.setExecutor(reloadCommand);
    command.setTabCompleter(reloadCommand);

    final Economy economy = Bukkit.getServicesManager().load(Economy.class);
    Preconditions.checkNotNull(economy, "No registered economy system is loaded");
    Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this, economy), this);
  }
}
