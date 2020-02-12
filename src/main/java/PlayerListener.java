package com.einspaten.bukkit.mcpillage;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

/**
 * Handle events for all Player related events
 *
 * @author SpartanerSpaten
 */
public class PlayerListener implements Listener {
    private final MCPillagePlugin plugin;

    public PlayerListener(MCPillagePlugin instance) {
        this.plugin = instance;
    }

    private void giveStarterStuff(Inventory player_inv) {
        // Starter Gear
        player_inv.addItem(new ItemStack(Material.IRON_SWORD));
        player_inv.addItem(new ItemStack(Material.STONE_PICKAXE));
        player_inv.addItem(new ItemStack(Material.STONE_AXE));
        player_inv.addItem(new ItemStack(Material.STONE_SHOVEL));
        player_inv.addItem(new ItemStack(Material.BREAD, 32));
        player_inv.addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
        player_inv.addItem(new ItemStack(Material.LEATHER_BOOTS));
        player_inv.addItem(new ItemStack(Material.LEATHER_HELMET));
        player_inv.addItem(new ItemStack(Material.LEATHER_LEGGINGS));
    }

    Block getChest(Sign sign) {

        if (sign.getBlock().getRelative(BlockFace.NORTH).getType() == Material.CHEST) {
            return sign.getBlock().getRelative(BlockFace.NORTH);
        } else if (sign.getBlock().getRelative(BlockFace.EAST).getType() == Material.CHEST) {
            return sign.getBlock().getRelative(BlockFace.EAST);
        } else if (sign.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.CHEST) {
            return sign.getBlock().getRelative(BlockFace.SOUTH);
        } else if (sign.getBlock().getRelative(BlockFace.WEST).getType() == Material.CHEST) {
            return sign.getBlock().getRelative(BlockFace.WEST);
        }
        return null;
    }

    boolean isSign(Material material) {
        return material == Material.OAK_WALL_SIGN || material == Material.SPRUCE_WALL_SIGN || material == Material.DARK_OAK_WALL_SIGN || material == Material.ACACIA_WALL_SIGN || material == Material.BIRCH_WALL_SIGN || material == Material.JUNGLE_WALL_SIGN;
    }


    @EventHandler
    public void onPlayJoin(PlayerJoinEvent playerJoinEvent) {

        String username = playerJoinEvent.getPlayer().getName();
        String user_uuid = playerJoinEvent.getPlayer().getUniqueId().toString();

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("MC-Pillage", "dummy", "§4§oMC-Pillage");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score money = obj.getScore(ChatColor.GRAY + "» Money: ");
        money.setScore(1);

        Team moneyCounter = board.registerNewTeam("moneyCounter");
        moneyCounter.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        moneyCounter.setPrefix(ChatColor.GREEN + "$" + 1);
        obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(0);

        if (!this.plugin.db.existsInDB(user_uuid)) {
            Bukkit.getLogger().info("Adding User: " + username);
            this.plugin.db.addPlayer(username, user_uuid);
            playerJoinEvent.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "xxx" + ChatColor.RESET + ChatColor.GRAY + "Welcome on the MC-Pillage City Server !");
            giveStarterStuff(playerJoinEvent.getPlayer().getInventory());
        }

        this.plugin.addPlayer(user_uuid);
        com.einspaten.bukkit.mcpillage.PluginPlayer player = this.plugin.getPlayer(user_uuid);

        player.setMoney_team(moneyCounter);
        player.increaseMoney(0); //Just updates
        playerJoinEvent.getPlayer().setScoreboard(board);

        String name;
        if (playerJoinEvent.getPlayer().isOp()) { // Admin
            name = ChatColor.RED + username + ChatColor.RESET;
        } else if (player.getRole() == 1) { // Moderator
            name = ChatColor.DARK_GREEN + username + ChatColor.RESET;
        } else {
            name = ChatColor.AQUA + username + ChatColor.RESET;
        }
        playerJoinEvent.getPlayer().setPlayerListName(name);
        playerJoinEvent.getPlayer().setDisplayName(name);
        playerJoinEvent.getPlayer().setCustomName(name);
        playerJoinEvent.getPlayer().setCustomNameVisible(true);

        playerJoinEvent.setJoinMessage(ChatColor.GRAY + "Player " + playerJoinEvent.getPlayer().getDisplayName() + ChatColor.GRAY + " Joined the Server");

        if (this.plugin.db.daily_reward(user_uuid)) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.MAGIC + "xxx" + ChatColor.GRAY + "You received your daily transaction of " + ChatColor.GREEN + "200 $");
            player.increaseMoney(200);
            this.plugin.db.setMoney(user_uuid, 200);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.removePlayer(event.getPlayer().getUniqueId().toString());
        event.setQuitMessage(ChatColor.GRAY + "Player " + event.getPlayer().getDisplayName() + ChatColor.GRAY + " Left the Server");
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        event.setFormat(event.getPlayer().getPlayerListName() + "§7 | " + event.getMessage());
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if (event.hasBlock()) {
            Material material = event.getClickedBlock().getType();
            if (isSign(material) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Sign sign = (Sign) event.getClickedBlock().getState();

                String[] lines = sign.getLines();
                Material desired_material;
                int price, amount;
                String target_player_uuid;
                try {
                    desired_material = Material.getMaterial(lines[0]);
                    price = Integer.parseInt(lines[1]);
                    amount = Integer.parseInt(lines[2]);
                    target_player_uuid = this.plugin.db.resolveUsername(lines[3]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (target_player_uuid == null || desired_material == null || price < 0 || amount < 0) { // Not found in database or invalid material id
                    return;
                }

                com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(event.getPlayer().getUniqueId().toString());
                if (plugin_player.getMoney() <= price) {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "You don't have enough money too perform this transaction.");
                    return;
                }
                Block expected_chest = getChest(sign);

                ItemStack stack = new ItemStack(desired_material, amount);
                com.einspaten.bukkit.mcpillage.PluginPlayer other_plugin_player = this.plugin.getPlayer(target_player_uuid);
                Inventory chest_inv;

                if (expected_chest == null) {
                    return;
                }

                if (expected_chest.getType() == Material.CHEST) {
                    Chest chest = (Chest) expected_chest.getState();
                    chest_inv = chest.getBlockInventory();
                } else {
                    return;
                }

                if (chest_inv.containsAtLeast(stack, 1)) { // Does contain desired item stack
                    chest_inv.removeItem(stack);
                    event.getPlayer().getInventory().addItem(stack);
                } else {
                    event.getPlayer().sendMessage(ChatColor.GRAY + "This shop is out of stock");
                    return;
                }
                if (other_plugin_player != null) {
                    // Is only called when other player is online
                    other_plugin_player.increaseMoney(price);
                    other_plugin_player.sendMessage(ChatColor.GRAY + "You received a transaction " + ChatColor.GREEN + "$" + price + ChatColor.GRAY + " worth from " + ChatColor.DARK_RED + event.getPlayer().getName());
                }

                plugin_player.increaseMoney(-1 * price); // Updates scoreboard

                // Writes Database
                this.plugin.db.setMoney(target_player_uuid, price);
                this.plugin.db.setMoney(event.getPlayer().getUniqueId().toString(), -1 * price);

                event.getPlayer().sendMessage(ChatColor.GRAY + "Transaction successful paid " + ChatColor.GREEN + price + "$" + ChatColor.GRAY + " for " + amount + " " + desired_material);

            } else {

                Location location = event.getClickedBlock().getLocation();

                if (location.getWorld().getName().equalsIgnoreCase("world")) {
                    if (!this.plugin.getPlayer(event.getPlayer().getUniqueId().toString()).onMyPlot((int) location.getX(), (int) location.getZ()) && !event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                } else {
                    if (location.getX() < 10 && location.getX() > -10 && location.getZ() < 10 && location.getZ() > -10 && !event.getPlayer().isOp()) {
                        event.setCancelled(true);
                    }
                }
            }

        }
    }


    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        World world = Bukkit.getServer().getWorld("world");
        Location location = new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
        event.setRespawnLocation(location);
    }


    @EventHandler
    public void onTeleportation(PlayerTeleportEvent event) {
        PlayerTeleportEvent.TeleportCause cause = event.getCause();

        if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL || cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            event.setCancelled(true);
        }
    }

}