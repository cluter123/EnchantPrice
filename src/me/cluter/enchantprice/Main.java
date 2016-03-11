package me.cluter.enchantprice;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements Listener {
	private static Economy economy = null;

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		setupEconomy();
		saveDefaultConfig();
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
	
	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));

	@EventHandler
	public void onEnchant(EnchantItemEvent e) {
		Player p = e.getEnchanter();
		if (p.hasPermission("enchant.bypass")) {
			return;
		}
		List<String> worlds =  getConfig().getStringList("worlds");
		String worldname = p.getWorld().getName();
		for (String world : worlds) {
		  if (world.equalsIgnoreCase(worldname)) {
				EconomyResponse r = economy.withdrawPlayer(p, getConfig().getInt("cost"));
				if (r.transactionSuccess()) {
					p.sendMessage(prefix + " " + ChatColor.YELLOW + "$"
							+ getConfig().getInt("cost") + ChatColor.GREEN + " has been taken from your account.");
					break;
				} else {
					p.sendMessage(prefix + ChatColor.RED + " You do not have enough money to enchant this item!");
					p.sendMessage(prefix+ ChatColor.RED + " You need " + ChatColor.GOLD + "$" + getConfig().getInt("cost")
							+ ChatColor.RED + " to enchant this item.");
					e.setCancelled(true);
					break;
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("priceenchant")) {
			if(args.length == 0) {
				sender.sendMessage("Made by cluter123. Plugin download: http://dev.bukkit.org/bukkit-plugins/enchantprice/");
				return true;
			}
			if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("enchant.reload")) {
					reloadConfig();
					sender.sendMessage(ChatColor.GOLD + "EnchantPrice has been reloaded.");
					return true;
				}
				else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to do this");
				}
			}
		}
		return false;
	}
}