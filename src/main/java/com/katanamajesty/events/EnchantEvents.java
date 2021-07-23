package com.katanamajesty.events;

import com.katanamajesty.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EnchantEvents implements Listener {

    private final Plugin plugin;

    public EnchantEvents(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTableEnchant(EnchantItemEvent event) {
        System.out.println(event.getEnchantsToAdd());
        System.out.println(event.getItem());
    }

    @EventHandler
    public void onAnvilEnchant(InventoryClickEvent event) {
        // Инвентарь - наковальня?
        if (!(event.getInventory() instanceof AnvilInventory)) return;

        Player player = (Player) event.getWhoClicked();
        boolean permBased = plugin.getConfig().getBoolean("permission_based");
        if (permBased) {
            if (player.hasPermission("tarucaenchants.elytra")) {
                return;
            }
        }

        // Если слот, по которому кликнули не результат наковальни - игнорируем
        int slot = event.getSlot();
        if (slot != 2) return;

        // Проверка на соответствие результата
        ItemStack anvilResult = event.getInventory().getItem(2);
        if (anvilResult != null
                && anvilResult.containsEnchantment(Enchantment.MENDING)
                && anvilResult.getType() == Material.ELYTRA) {
            // Отмена ивента
            event.setCancelled(true);
            player.sendMessage(Main.colorize(plugin.getConfig().getString("elytra_forbidden")));
        }
    }

}
