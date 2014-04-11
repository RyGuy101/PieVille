package com.blogspot.mathjoy.pieville;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PieVille extends JavaPlugin implements Listener
{
	public final Logger log = Logger.getLogger("Minecraft");
	public static Economy economy = null;

	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
		{
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	@Override
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(this, this);
		boolean ecoSuccess = setupEconomy();
		if (!ecoSuccess)
		{
			Bukkit.getPluginManager().disablePlugin(this);
			log.severe("The Pieville plugin can't find vault! Plugin is disabled!");
		} else
		{
			log.info("The (unofficial) PieVille plugin is enabled!");
		}
	}

	@Override
	public void onDisable()
	{
		log.info("The (unofficial) PieVille plugin is disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("sellpie"))
		{
			if (!(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether")))
			{
				player.sendMessage("You have to be in the survival world in order to sell pies!");
				return false;
			}
			try
			{
				Integer num = Integer.parseInt(args[0]);
				ItemStack pie = new ItemStack(Material.PUMPKIN_PIE, num);
				PlayerInventory pi = player.getInventory();
				for (int i = num; i <= 64; i++)
				{
					ItemStack checkPie = new ItemStack(Material.PUMPKIN_PIE, i);
					if (pi.contains(checkPie))
					{
						EconomyResponse er = economy.depositPlayer(player.getName(), 3.14 * num);
						if (er.transactionSuccess())
						{
							pi.removeItem(pie);
						} else
						{
							player.sendMessage("Uh-oh! Something went wrong whith you transaction");
							return false;
						}
						if (num == 1)
						{
							player.sendMessage("You sold 1 pie for 3.14 Dollars");
						} else
						{
							player.sendMessage("You sold " + num + " pies for " + (num * 3.14) + " Dollars");
						}
						return true;
					}
					if (i == 64)
					{
						player.sendMessage("You don't have enough pie!");
						return false;
					}
				}
			} catch (Exception e)
			{
				player.sendMessage("You have to enter a number: /sellpie <Number>");
				return false;
			}
		}
		if (label.equalsIgnoreCase("buypie"))
		{
			if (!(player.getWorld().getName().equals("world") || player.getWorld().getName().equals("world_nether")))
			{
				player.sendMessage("You have to be in the survival world in order to buy pies!");
				return false;
			}
			try
			{
				Integer num = Integer.parseInt(args[0]);
				ItemStack pie = new ItemStack(Material.PUMPKIN_PIE, num);
				PlayerInventory pi = player.getInventory();
				if (economy.getBalance(player.getName()) >= num * 3.14)
				{
					EconomyResponse er = economy.withdrawPlayer(player.getName(), 3.14 * num);
					if (er.transactionSuccess())
					{
						pi.addItem(pie);
					} else
					{
						player.sendMessage("Uh-oh! Something went wrong whith you transaction");
						return false;
					}
				} else
				{
					player.sendMessage("You don't have enough money!");
					return false;
				}
				if (num == 1)
				{
					player.sendMessage("You bought 1 pie for 3.14 Dollars");
				} else
				{
					player.sendMessage("You bought " + num + " pies for " + (num * 3.14) + " Dollars");
				}
				return true;
			} catch (Exception e)
			{
				player.sendMessage("You have to enter a number: /buypie <Number>");
				return false;
			}
		}
		return false;
	}
}
