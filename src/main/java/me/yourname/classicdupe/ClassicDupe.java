package me.yourname.classicdupe;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class ClassicDupe extends JavaPlugin implements Listener, CommandExecutor {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("dupe") != null) {
            getCommand("dupe").setExecutor(this);
        }
        getLogger().info("ClassicDupe v1.3 (Guaranteed Stealth) activated!");
    }

    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame itemFrame) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();
            
            if (itemFrame.getItem().getType() == Material.AIR) return;

            // 1. Silent Cooldown Check (3 seconds to prevent absolute spam)
            long currentTime = System.currentTimeMillis();
            if (cooldowns.containsKey(playerUUID)) {
                long timePassed = currentTime - cooldowns.get(playerUUID);
                if (timePassed < 3000) {
                    event.setCancelled(true); // Silently block interactions if clicking too fast
                    return;
                }
            }
            cooldowns.put(playerUUID, currentTime);

            // 2. Guaranteed Success - Silently slide item into their inventory
            if (player.getInventory().firstEmpty() != -1) {
                ItemStack itemToDupe = itemFrame.getItem().clone();
                player.getInventory().addItem(itemToDupe);
                // No chat messages, no sounds, no drops on the ground
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!player.hasPermission("classicdupe.use")) return true;
        
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() != Material.AIR) {
            player.getInventory().addItem(handItem.clone());
        }
        return true;
    }
}
