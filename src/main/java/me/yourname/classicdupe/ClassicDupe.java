package me.yourname.classicdupe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public final class ClassicDupe extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        // Register events and commands natively to the 26.3 engine
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("dupe") != null) {
            getCommand("dupe").setExecutor(this);
        }
        getLogger().info("ClassicDupe v1.0 activated successfully for Paper 26.3!");
    }

    // --- FEATURE 1: CUSTOM ITEM FRAME DUPE LISTENER ---
    @EventHandler
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        // Check if the entity right-clicked is an Item Frame
        if (event.getRightClicked() instanceof ItemFrame itemFrame) {
            Player player = event.getPlayer();
            
            // If the item frame already holds an item, simulate an old desync drop
            if (itemFrame.getItem().getType() != Material.AIR) {
                ItemStack itemToDupe = itemFrame.getItem().clone();
                
                // Spawn a duplicated copy of the item naturally into the world
                itemFrame.getWorld().dropItemNaturally(itemFrame.getLocation(), itemToDupe);
                player.sendMessage(ChatColor.GREEN + "💥 Item frame desync triggered! Item duplicated.");
            }
        }
    }

    // --- FEATURE 2: MANDATORY /DUPE COMMAND HANDLER ---
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!"); //
            return true;
        }

        // Enforce basic permissions
        if (!player.hasPermission("classicdupe.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to dupe items!");
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must be holding an item to duplicate it!");
            return true;
        }

        // Duplicate the exact stack details including NBT tags, enchants, and quantity
        ItemStack duplicatedStack = handItem.clone();
        player.getInventory().addItem(duplicatedStack);
        player.sendMessage(ChatColor.GOLD + "✨ Successfully duplicated your hand item!");
        
        return true;
    }
}
