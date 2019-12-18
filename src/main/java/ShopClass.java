package com.einspaten.bukkit.mcpillage;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ShopClass implements CommandExecutor {

    private final MCPillagePlugin plugin;
    private final HashMap<String, Number> prices = new HashMap<>();

    public ShopClass(MCPillagePlugin mcPillagePlugin) {
        this.plugin = mcPillagePlugin;
        prices.put("villager", 45);

        prices.put("cow", 10);
        prices.put("pig", 10);
        prices.put("chicken", 10);
        prices.put("sheep", 10);
        prices.put("bee", 15);
        prices.put("rabbit", 10);

        prices.put("fox", 15);
        prices.put("cat", 15);
        prices.put("wolf", 15);
        prices.put("parrot", 15);
        prices.put("dolphin", 15);
        prices.put("turtle", 15);
        prices.put("polar_bear", 15);

        prices.put("horse", 15);
        prices.put("lama", 15);
        prices.put("donkey", 15);
        prices.put("skeleton_horse", 20);
        prices.put("zombie_horse", 25);

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {

        Player player = (Player) sender;

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

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (Objects.requireNonNull(itemStack.getData()).getItemType() == Material.LEGACY_DIAMOND) {
                int amount = itemStack.getAmount();
                int price = prices.get(item).intValue();
                if (amount >= price) {
                    ItemStack updated = new ItemStack(Material.LEGACY_DIAMOND, amount - price);
                    player.getInventory().setItemInMainHand(updated);
                    ItemStack villagers = new ItemStack(material, 1);
                    player.getWorld().dropItem(player.getLocation(), villagers);
                } else {
                    player.sendMessage("The price of " + item + " is " + price);
                }

            } else {
                player.sendMessage("You are not holding Diamonds in your hand you want to give for the desired Spawnegg");
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
