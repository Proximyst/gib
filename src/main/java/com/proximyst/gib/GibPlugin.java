//
// gib - A simple duplication plugin for Bukkit servers.
// Copyright (C) Mariell Hoversholm 2020
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package com.proximyst.gib;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.BukkitCommandManager.BrigadierFailureException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.proximyst.gib.utils.ReflectionUtils;
import java.util.function.Function;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class GibPlugin extends JavaPlugin {
  private static final boolean HAS_OFFHAND = ReflectionUtils.isMethodOnClass(PlayerInventory.class, "getItemInMainHand");

  @Override
  public void onEnable() {
    final PaperCommandManager<CommandSender> commandManager;
    try {
      commandManager = new PaperCommandManager<>(
          this,
          CommandExecutionCoordinator.simpleCoordinator(),
          Function.identity(),
          Function.identity()
      );
    } catch (final Exception ex) {
      this.getLogger().severe("Cannot instantiate the command manager.");
      ex.printStackTrace();
      this.setEnabled(false);
      return;
    }

    try {
      commandManager.registerAsynchronousCompletions();
    } catch (final IllegalStateException ex) {
      this.getLogger().warning("Cannot register asynchronous completions; are you on Paper? "
          + "If not, please see https://papermc.io for information on how to improve your server!");
    }

    try {
      commandManager.registerBrigadier();
    } catch (final BrigadierFailureException ex) {
      this.getLogger().warning("Could not register Brigadier support; are you on Paper? "
          + "If not, please see https://papermc.io for information on how to improve your server!");
    }

    commandManager.command(commandManager.commandBuilder("gib")
        .senderType(Player.class)
        .permission("gib.gib")
        .argument(IntegerArgument.optional("amount", 1))
        .handler(ctx -> {
          final int amount = ctx.get("amount");
          final Player player = (Player) ctx.getSender();
          @SuppressWarnings("deprecation") // We don't always have the method we want.
          final ItemStack itemStack = HAS_OFFHAND ? player.getInventory().getItemInMainHand().clone() : player.getInventory().getItemInHand().clone();
          if (itemStack.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand.");
            return;
          }

          if (itemStack.getAmount() < amount) {
            player.sendMessage(ChatColor.RED + "You can only get as much as you already have. "
                + "You have " + itemStack.getAmount() + " items, but you asked for " + amount + "!");
            return;
          }
          itemStack.setAmount(amount);

          player.sendMessage(ChatColor.GREEN + "You've been given " + amount + " item"
              + (amount == 1 ? "" : "s") + ".");
          player.getInventory().addItem(itemStack);
        }));
  }
}
