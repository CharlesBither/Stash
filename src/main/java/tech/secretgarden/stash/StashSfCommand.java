package tech.secretgarden.stash;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StashSfCommand implements CommandExecutor {
    private final GiveMethods giveMethods = new GiveMethods();
    private final GetMethods getMethods = new GetMethods();

    private final Stash plugin;
    public StashSfCommand(Stash instance) { this.plugin = instance; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            String world = player.getWorld().getName();

            if (getMethods.getWorld(world)) {
                if (args[0].equals("give") && player.hasPermission("stash.a")) {
                    if (plugin.getSfAPI() != null) {
                        SlimefunItem sfItem = SlimefunItem.getById(args[2].toUpperCase());
                        ItemStack item = sfItem.getItem();
                        String itemName = item.getItemMeta().getDisplayName();
                        if (args[1].equals("all")) {
                            //ALL SF ARGS
                            giveMethods.giveAllPlayers(args, item, player, itemName);
                        }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                        else {
                            Player target = Bukkit.getPlayer(args[1]);
                            if (target != null) {
                                String uuid = target.getUniqueId().toString();
                                Inventory singleStash = MapConversion.map.get(target.getUniqueId().toString());
                                giveMethods.giveSinglePlayer(args, singleStash, item, player, uuid, itemName);
                            } else {
                                //target == null
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                                if (offlinePlayer.hasPlayedBefore()) {
                                    String uuid = offlinePlayer.getUniqueId().toString();
                                    Inventory singleStash = MapConversion.map.get(offlinePlayer.getUniqueId().toString());
                                    giveMethods.giveSinglePlayer(args, singleStash, item, player, uuid, itemName);
                                } else {
                                    player.sendMessage("This player has not logged in before.");
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Cannot get Slimefun instance!");
                    }

                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot do that in this world!");
            }
        }
        return false;
    }
}
