package tech.secretgarden.stash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventListener implements Listener {

    private final Database database = new Database();
    private final MapConversion mapConversion = new MapConversion();
    private final GiveMethods giveMethods = new GiveMethods();
    private final GetMethods getMethods = new GetMethods();

    LocalDateTime date = LocalDateTime.now();
    Timestamp timestamp = Timestamp.valueOf(date);

    @EventHandler
    //Stash inventory creation
    public void join(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        if (!MapConversion.map.containsKey(e.getPlayer().getUniqueId().toString())) {

            System.out.println(MapConversion.map.get(uuid));

            Inventory inv = Bukkit.createInventory(null, 18, ChatColor.DARK_PURPLE + "Stash");

            String player = e.getPlayer().getName();
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String invName = player + "'s Stash";

            MapConversion.map.put(uuid, inv);
            System.out.println("created inv");
            String stash = mapConversion.inventoryToString(inv);

            try (Connection connection = database.getPool().getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO player (uuid, name, inv, timestamp) VALUES (?,?,?,?);")) {
                statement.setString(1, uuid);
                statement.setString(2, player);
                statement.setString(3, stash);
                statement.setTimestamp(4, timestamp);
                statement.executeUpdate();

            } catch (Exception x) {
                x.printStackTrace();
            }

        }

        if (!MapConversion.map.get(e.getPlayer().getUniqueId().toString()).isEmpty()) {
            e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You have Items in your Stash, use '/stash' to get your items.");
        }
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {

        String title = e.getView().getTitle();

        if (title.contains(ChatColor.DARK_PURPLE + "Stash")) {
            //top inv title includes "stash"
            ItemStack cursor = e.getCursor();
            ItemStack slot = e.getCurrentItem();

            if (!cursor.getType().isAir() && slot != null) {
                e.setCancelled(true);
            }
            if (e.getClick().isRightClick()) {
                e.setCancelled(true);
                //if user tries to swap item or right click, cancel event regardless of their permissions!
            }
            if (e.getClickedInventory() == null) {
                e.setCancelled(true);
            } else {
                Inventory stash = e.getView().getTopInventory();
                Inventory playerInventory = e.getWhoClicked().getInventory();

                String key = getMethods.getMapKey(stash);
                UUID uuid = UUID.fromString(key);
                int playerId = getMethods.getPlayerId(key);
                String owner = getMethods.getName(uuid);

                String name = e.getWhoClicked().getName();
                String stashStr = mapConversion.inventoryToString(stash);

                String cursorBukkitItemName = cursor.toString();

                if (e.getClickedInventory().equals(stash)) {
                    //clicked stash inventory
                    if (cursor.getType().isAir() && slot != null) {
                        String slotBukkitItemName = slot.toString();
                        //removing item from stash
                        int integer = slot.getAmount();
                        String number = Integer.toString(integer);
                        if (e.getClick().isShiftClick()) {
                            if (slot.hasItemMeta()) {
                                String slotCustomItemName = slot.getItemMeta().getDisplayName();
                                //custom item
                                giveMethods.recordItem(name, slotCustomItemName, number, "removed ", owner, playerId);
                            } else {
                                giveMethods.recordItem(name, slotBukkitItemName, number, "removed ", owner, playerId);
                            }
                        }
                        giveMethods.updatePlayers(stashStr, key);

                    } else if (!cursor.getType().isAir() && (slot == null || slot.getType().isAir())) {
                        //adding item to stash
                        if (e.getWhoClicked().hasPermission("stash.a")) {
                            int integer = cursor.getAmount();
                            String number = Integer.toString(integer);
                            if (cursor.hasItemMeta()) {
                                String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
                                //custom item
                                giveMethods.recordItem(name, cursorCustomItemName, number, "added ", owner, playerId);
                            } else {
                                giveMethods.recordItem(name, cursorBukkitItemName, number, "added ", owner, playerId);
                            }
                            giveMethods.updatePlayers(stashStr, key);
                        } else {
                            e.setCancelled(true);
                        }

                    }
                } else if (e.getClickedInventory().equals(playerInventory)) {
                    //clicked player inventory
                    if (cursor.getType().isAir() && (slot != null)) {
                        //removing item from player inv
                        if (e.getWhoClicked().hasPermission("stash.a")) {
                            if (e.getClick().isShiftClick()) {
                                int integer = slot.getAmount();
                                String number = Integer.toString(integer);
                                if (slot.hasItemMeta()) {
                                    String slotCustomItemName = slot.getItemMeta().getDisplayName();
                                    giveMethods.recordItem(name, slotCustomItemName, number, "added ", owner, playerId);
                                } else {
                                    String slotBukkitItemName = slot.toString();
                                    giveMethods.recordItem(name, slotBukkitItemName, number, "added ", owner, playerId);
                                }

                            }

                            giveMethods.updatePlayers(stashStr, key);
                        } else {
                            e.setCancelled(true);
                        }

                    } else if (!cursor.getType().isAir() && (slot == null || slot.getType().isAir())) {
                        int integer = cursor.getAmount();
                        String number = Integer.toString(integer);
                        if (cursor.hasItemMeta()) {
                            String cursorCustomItemName = cursor.getItemMeta().getDisplayName();
                            //custom item
                            giveMethods.recordItem(name, cursorCustomItemName, number, "removed ", owner, playerId);
                        } else {
                            giveMethods.recordItem(name, cursorBukkitItemName, number, "removed ", owner, playerId);
                        }
                        giveMethods.updatePlayers(stashStr, key);
                        //adding item to player inv

                        giveMethods.updatePlayers(stashStr, key);
                    } else {
                        e.setCancelled(true);
                    }

                }
            }
        }
    }

    @EventHandler
    public void drag(InventoryDragEvent e) {
        Inventory stashInv = MapConversion.map.get(e.getWhoClicked().getUniqueId().toString());
        if (stashInv.getViewers().contains(e.getWhoClicked())) {
            e.setCancelled(true);
        }
    }
}


