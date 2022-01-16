package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class StashCommand implements CommandExecutor {

    private final Main plugin;
    public StashCommand(Main instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        Set<String> configKeys = plugin.getConfig().getKeys(false);

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Inventory senderStash = MapConversion.map.get(player.getUniqueId().toString());
            //opens your own stash
            if (args.length == 0) {
                player.openInventory(senderStash);

                //*********************ADD COMMAND TO OPEN OTHER'S STASH HERE ***************************


            } else if (args.length == 1 && player.hasPermission("stash.a")) {
                String p = args[0];

                if (Bukkit.getPlayer(p) != null) {
                    String id = Bukkit.getPlayer(p).getUniqueId().toString();
                    Inventory otherStash = MapConversion.map.get(id);
                    player.openInventory(otherStash);
                } else if (Bukkit.getOfflinePlayer(p).hasPlayedBefore()) {
                    String id = Bukkit.getOfflinePlayer(p).getUniqueId().toString();
                    Inventory otherStash = MapConversion.map.get(id);
                    player.openInventory(otherStash);
                } else {
                    player.sendMessage(ChatColor.RED + "This is not a valid player!");
                }
                //GIVE ARG
            } else if (args[0].equals("give") && player.hasPermission("stash.a")) {
                Material mat = Material.getMaterial(args[2].toUpperCase());
                ItemStack item = new ItemStack(mat);
                String itemName = item.toString();

                //ALL ARG
                if (args[1].equals("all")) {
                    if (args.length == 3) {
                        Integer integer = 1;
                        for (Map.Entry entry : MapConversion.map.entrySet()) {
                            Inventory stashInv = (Inventory) entry.getValue();
                            stashInv.addItem(item);
                        }
                        configAddAll(configKeys, itemName, integer);
                    } else if (args.length == 4) {
                        parseIntegersAll(args, item, configKeys, player);
                    }
                /*
                    BELOW IS FOR GIVING ITEMS TO A SINGLE PLAYER
                     */
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        Inventory singleStash = MapConversion.map.get(target.getUniqueId().toString());
                        if (args.length == 3) {
                            Integer integer = 1;
                            singleStash.addItem(item);
                            configAdd(args, itemName, integer);
                        } else if (args.length == 4) {
                            parseIntegersSingle(args, singleStash, item, player);
                        }
                    } else {
                        //target == null
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                        String offlinePlayerId = offlinePlayer.getUniqueId().toString();
                        Inventory singleStash = MapConversion.map.get(offlinePlayer.getUniqueId().toString());
                        if (args.length == 3) {
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                Integer integer = 1;
                                //Now see if offlinePlayer String matches map key.
                                if (MapConversion.map.containsKey(offlinePlayerId)) {
                                    singleStash.addItem(item);
                                    configAdd(args, itemName, integer);
                                } else {
                                    player.sendMessage("This player has not logged in before");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "This is not a valid player.");
                            }
                        } else if (args.length == 4) {
                            if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                                parseIntegersSingle(args, singleStash, item, player);
                            } else {
                                player.sendMessage(ChatColor.RED + "This is not a valid player.");
                            }
                        }
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
            }
        }
        return false;
    }

    public void configAddAll(Set<String> configKeys, String itemName, Integer integer) {
        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh,mm,ss a"));
        for (String p : configKeys) {
            plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer + " " + itemName, dateStr);
            plugin.saveConfig();
        }
    }
    public void configAdd(String[] args, String itemName, Integer integer) {
        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMMM dd yyyy hh,mm,ss a"));
        String p = args[1];
        plugin.getConfig().set(p + ".Added Items." + "- " + "x" + integer + " " + itemName, dateStr);
        plugin.saveConfig();
    }
    public void parseIntegersAll(String[] args, ItemStack item, Set<String> keys, Player player) {
        try {
            Integer.parseInt(args[3]);
            int integer = Integer.parseInt(args[3]);
            String itemName = item.getItemMeta().getDisplayName();
            if (integer <= item.getMaxStackSize()) {
                for (Map.Entry entry : MapConversion.map.entrySet()) {
                    Inventory stashInv = (Inventory) entry.getValue();
                    for (int i = 0; i < integer; i++) {
                        stashInv.addItem(item);
                    }
                }
                configAddAll(keys, itemName, integer);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
    public void parseIntegersSingle(String[] args, Inventory singleStash, ItemStack item, Player player) {
        try {
            Integer.parseInt(args[3]);
            String itemName = item.getItemMeta().getDisplayName();
            int integer = Integer.parseInt(args[3]);
            if (integer <= item.getMaxStackSize()) {
                for (int i = 0; i < integer; i++) {
                    singleStash.addItem(item);
                }
                configAdd(args, itemName, integer);
            }
        } catch (NumberFormatException nfe) {
            player.sendMessage("This argument must be an integer");
        }
    }
}