package me.flail.SlashPlayer.Executables;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.flail.SlashPlayer.SlashPlayer;
import me.flail.SlashPlayer.GUI.PlayerInfoInventory;
import me.flail.SlashPlayer.Utilities.Tools;

@SuppressWarnings("deprecation")
public class SetGamemode implements Listener {

	private SlashPlayer plugin = SlashPlayer.getPlugin(SlashPlayer.class);

	private Tools chat = new Tools();

	FileConfiguration messages = plugin.getMessages();

	private ConsoleCommandSender console = plugin.console;

	@EventHandler
	public void setGamemode(InventoryClickEvent event) {

		FileConfiguration guiConfig = plugin.getGuiConfig();

		Inventory inv = event.getInventory();

		InventoryHolder holder = event.getInventory().getHolder();

		if ((holder instanceof Player) && inv.getType().equals(InventoryType.CHEST)) {

			Player subject = (Player) holder;

			int headerSlot = guiConfig.getInt("PlayerInfo.Header.Slot");

			ItemStack pInfo = inv.getItem(headerSlot - 1);

			String loreUid = "";

			Player pInfoPlayer = null;

			if ((pInfo != null) && pInfo.hasItemMeta() && pInfo.getItemMeta().hasLore()) {

				List<String> lore = pInfo.getItemMeta().getLore();

				String uid = lore.get(0);

				if (!Tools.hasCode(uid)) {
					loreUid = ChatColor.stripColor(lore.get(0));

					pInfoPlayer = plugin.players.get(UUID.fromString(loreUid.trim()));

				}

			}

			if ((subject != null) && (pInfoPlayer != null) && pInfoPlayer.equals(subject)) {

				String invTitle = chat
						.m(guiConfig.getString("GamemodeInventory.Title").replace("%player%", subject.getName()));

				if (inv.getName().equals(invTitle)) {

					Player operator = (Player) event.getWhoClicked();

					Player player = subject;

					if (event.getSlotType().equals(SlotType.OUTSIDE)) {
						operator.closeInventory();

						operator.openInventory(new PlayerInfoInventory().playerInfo(player));
					}

					ItemStack item = event.getCurrentItem();

					ItemMeta iM;

					if ((item != null) && item.hasItemMeta()) {

						if (item.hasItemMeta()) {

							iM = item.getItemMeta();

							if (iM.hasEnchant(Enchantment.MENDING)) {

								int slot = iM.getEnchantLevel(Enchantment.MENDING);
								ConfigurationSection cs = guiConfig
										.getConfigurationSection("GamemodeInventory." + slot);

								if (cs != null) {

									String mode = cs.getString("Mode");

									if ((mode != null) && (mode != "")) {

										if (mode.equalsIgnoreCase("backbutton")) {

											operator.closeInventory();

											operator.openInventory(new PlayerInfoInventory().playerInfo(player));

										} else {

											boolean setMode = this.setMode(operator, player, mode);

											if (!setMode) {

												operator.closeInventory();
												operator.sendMessage(chat.m(
														"%prefix% &cError with setting gamemode, check console for details."));
												console.sendMessage(
														chat.m("&cInvalid Gamemode Specified in &o&nGuiConfig.yml"));
												console.sendMessage(chat.m("&cError @ section &7" + slot));
												console.sendMessage(chat.m("&c" + mode + " is not a valid Mode!"));

												plugin.logAction(operator.getName() + " tried to change "
														+ player.getName() + "'s gamemode but " + mode
														+ " is not a valid gamemode!");

											}

										}

									} else {
										return;
									}

								}

							}

						}

					}

					event.setCancelled(true);
				}

			}

		}

	}

	private boolean setMode(Player operator, Player player, String mode) {
		try {

			boolean equalsCanExecute = plugin.getConfig().getBoolean("EqualsCanExecute");

			int playerRank = Tools.playerRank(player);
			int operatorRank = Tools.playerRank(operator);

			String rankTooLow = chat.msg(messages.getString("RankTooLow"), player, operator, "SetGamemode",
					"slashplayer");

			String accessDenied = chat.msg(messages.getString("GamemodeAccessDenied"), player, operator, "SetGamemode",
					"slashplayer");

			if (operator.hasPermission("slashplayer.gamemode." + mode.toLowerCase())
					|| operator.hasPermission("slashplayer.gamemode.all")) {

				if (((operatorRank >= playerRank) && equalsCanExecute) || (operatorRank > playerRank)) {

					plugin.logAction(operator.getName() + " Changed gamemode for " + player.getName() + " to "
							+ mode.toUpperCase());

					player.setGameMode(GameMode.valueOf(mode.toUpperCase()));

					String gamemodeUpdated = chat.msg(messages.getString("GamemodeChanged"), player, operator,
							"SetGamemode", "slashplayer");

					String changedGamemode = chat.msg(messages.getString("PlayerGamemodeChanged"), player, operator,
							"SetGamemode", "slashplayer");

					player.sendMessage(gamemodeUpdated);

					operator.sendMessage(changedGamemode);

					operator.closeInventory();

					operator.openInventory(new PlayerInfoInventory().playerInfo(player));

				} else {
					operator.closeInventory();
					operator.sendMessage(rankTooLow);
				}

			} else {
				player.sendMessage(accessDenied.replaceAll("%mode%", mode));
				player.closeInventory();
			}

			return true;
		} catch (Throwable t) {
			return false;
		}

	}

}