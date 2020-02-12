package com.einspaten.bukkit.mcpillage;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShopClass implements CommandExecutor {

    private final MCPillagePlugin plugin;
    private final HashMap<String, Number> prices = new HashMap<>();

    public ShopClass(MCPillagePlugin mcPillagePlugin) {
        this.plugin = mcPillagePlugin;
        prices.put("villager", 450);

        prices.put("cow", 150);
        prices.put("pig", 150);
        prices.put("chicken", 100);
        prices.put("sheep", 150);
        prices.put("bee", 250);
        prices.put("rabbit", 130);

        prices.put("fox", 200);
        prices.put("cat", 200);
        prices.put("wolf", 200);
        prices.put("parrot", 200);
        prices.put("dolphin", 200);
        prices.put("turtle", 200);
        prices.put("polar_bear", 200);

        prices.put("horse", 150);
        prices.put("lama", 150);
        prices.put("donkey", 150);
        prices.put("skeleton_horse", 300);
        prices.put("zombie_horse", 350);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {

        Player player = (Player) sender;
        com.einspaten.bukkit.mcpillage.PluginPlayer plugin_player = this.plugin.getPlayer(player.getUniqueId().toString());

        if (split.length > 0) {
            String item = split[0];
            Material material;
            switch (item) {

                // EndGame
                case "villager":
                    material = Material.VILLAGER_SPAWN_EGG;
                    break;

                // Farming Animals
                case "cow":
                    material = Material.COW_SPAWN_EGG;
                    break;
                case "pig":
                    material = Material.PIG_SPAWN_EGG;
                    break;
                case "chicken":
                    material = Material.CHICKEN_SPAWN_EGG;
                    break;
                case "sheep":
                    material = Material.SHEEP_SPAWN_EGG;
                    break;
                case "bee":
                    material = Material.BEE_SPAWN_EGG;
                    break;
                case "rabbit":
                    material = Material.RABBIT_SPAWN_EGG;
                    break;

                // Pets
                case "fox":
                    material = Material.FOX_SPAWN_EGG;
                    break;
                case "cat":
                    material = Material.CAT_SPAWN_EGG;
                    break;
                case "wolf":
                    material = Material.WOLF_SPAWN_EGG;
                    break;
                case "parrot":
                    material = Material.PARROT_SPAWN_EGG;
                    break;
                case "dolphin":
                    material = Material.DOLPHIN_SPAWN_EGG;
                    break;
                case "turtle":
                    material = Material.TURTLE_SPAWN_EGG;
                    break;
                case "polar_bear":
                    material = Material.POLAR_BEAR_SPAWN_EGG;
                    break;

                // Transportation
                case "horse":
                    material = Material.HORSE_SPAWN_EGG;
                    break;
                case "llama":
                    material = Material.LLAMA_SPAWN_EGG;
                    break;
                case "donkey":
                    material = Material.DONKEY_SPAWN_EGG;
                    break;
                case "skeleton_horse":
                    material = Material.SKELETON_HORSE_SPAWN_EGG;
                    break;
                case "zombie_horse":
                    material = Material.ZOMBIE_HORSE_SPAWN_EGG;
                    break;
                default:
                    player.sendMessage("Not a valid spawn egg name");
                    return true;
            }

            int money = plugin_player.getMoney();
            int price = prices.get(item).intValue();

            if (price > money) {
                player.sendMessage(ChatColor.GRAY + "You don't have enough Money it costs " + ChatColor.GREEN + "$" + money);
                return true;
            } else {

                plugin_player.increaseMoney(-1 * price);
                this.plugin.db.setMoney(player.getUniqueId().toString(), -1 * price);

                ItemStack villagers = new ItemStack(material, 1);
                player.getInventory().addItem(villagers);
            }


        } else {
            // Prints Prices for Spawneggs
            Iterator hmIterator = prices.entrySet().iterator();
            player.sendMessage("§3Shop Prices");
            while (hmIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) hmIterator.next();
                int price = ((int) mapElement.getValue());
                player.sendMessage("§o§b[" + mapElement.getKey() + "]§r : " + price);
            }
        }
        return true;
    }

}
