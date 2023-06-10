package me.msicraft.modpackintegrated.CraftingEquip.Doppelganger;

import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.MathUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Husk;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

public class DoppelgangerUtil {

    public static double getPlayerMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    public static double getPlayerExtraHealth(Player player) {
        double d = 0;
        if (CraftingEquipStatUtil.containStatMap(player)) {
            d = CraftingEquipStatUtil.getStatMap(player).get("Health");
        }
        return d;
    }

    public static void setMaxHealth(LivingEntity livingEntity, double amount) {
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            double cal = Math.round(amount*100.0)/100.0;
            instance.setBaseValue(cal);
            livingEntity.setHealth(cal);
        }
    }

    public static double getPlayerDamage(Player player) {
        double d = 4;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            d = instance.getValue();
        }
        return d;
    }

    public static double getPlayerExtraDamage(Player player) {
        double d = 0;
        if (CraftingEquipStatUtil.containStatMap(player)) {
            d = CraftingEquipStatUtil.getStatMap(player).get("MeleeDamage");
        }
        return d;
    }

    public static void setBaseDamage(LivingEntity livingEntity, double amount) {
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            instance.setBaseValue(amount);
        }
    }

    public static boolean isDoppelganger(LivingEntity livingEntity) {
        boolean check = false;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Doppelganger"), PersistentDataType.STRING)) {
            check = true;
        }
        return check;
    }

    public static void spawnDoppelganger(Location location, Player player) {
        World world = location.getWorld();
        if (world != null) {
            ItemStack helmet = player.getInventory().getHelmet();
            ItemStack chest = player.getInventory().getChestplate();
            ItemStack leggings = player.getInventory().getLeggings();
            ItemStack boots = player.getInventory().getBoots();
            ItemStack hand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> world.spawn(location, Husk.class, husk -> {
                husk.setCustomName(player.getName() + " 의 장비를 복제한 좀비");
                husk.setCustomNameVisible(false);
                husk.setCanPickupItems(false);
                husk.setBaby(false);
                EntityEquipment entityEquipment = husk.getEquipment();
                if (entityEquipment != null) {
                    if (helmet != null && helmet.getType() != Material.AIR) {
                        entityEquipment.setHelmet(new ItemStack(helmet));
                    }
                    if (chest != null && chest.getType() != Material.AIR) {
                        entityEquipment.setChestplate(new ItemStack(chest));
                    }
                    if (leggings != null && leggings.getType() != Material.AIR) {
                        entityEquipment.setLeggings(new ItemStack(leggings));
                    }
                    if (boots != null && boots.getType() != Material.AIR) {
                        entityEquipment.setBoots(new ItemStack(boots));
                    }
                    if (hand != null && hand.getType() != Material.AIR) {
                        entityEquipment.setItemInMainHand(new ItemStack(hand));
                    }
                    if (offHand != null && offHand.getType() != Material.AIR) {
                        entityEquipment.setItemInOffHand(new ItemStack(offHand));
                    }
                }
                double baseMaxHealth = getPlayerMaxHealth(player);
                double baseDamage = getPlayerExtraDamage(player);
                double randomHealthMultiple = MathUtil.getRandomValueDouble(10.1, 3.1);
                double randomDamageMultiple = MathUtil.getRandomValueDouble(7.1, 2.1);
                if (baseDamage < 0) {
                    baseDamage = 1;
                }
                double calHealth = baseMaxHealth + (baseMaxHealth * randomHealthMultiple);
                double calDamage = baseDamage + (baseDamage * randomDamageMultiple);
                setMaxHealth(husk, calHealth);
                setBaseDamage(husk, calDamage);
                PersistentDataContainer data = husk.getPersistentDataContainer();
                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Doppelganger"), PersistentDataType.STRING, husk.getUniqueId().toString());
                BukkitTask doppelTask = new DoppelgangerTask(husk).runTaskTimer(ModPackIntegrated.getPlugin(), 0, 40L);
                husk.setTarget(player);
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "도플갱어 소환");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "복제한 플레이어: " + ChatColor.GRAY + player);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "체력 배수: " + ChatColor.GRAY + randomHealthMultiple);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "계산된 체력: " + ChatColor.GRAY + calHealth);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "데미지 배수: " + ChatColor.GRAY + randomDamageMultiple);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "계산된 데미지: " + ChatColor.GRAY + calDamage);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                }
            }));
        }
    }

    public static void replaceEquipment(LivingEntity livingEntity, Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chest = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
            livingEntity.setCustomName(player.getName() + " 의 장비를 복제한 습격자");
            livingEntity.setCustomNameVisible(false);
            livingEntity.setCanPickupItems(false);
            EntityEquipment entityEquipment = livingEntity.getEquipment();
            if (entityEquipment != null) {
                if (helmet != null && helmet.getType() != Material.AIR) {
                    entityEquipment.setHelmet(new ItemStack(helmet));
                }
                if (chest != null && chest.getType() != Material.AIR) {
                    entityEquipment.setChestplate(new ItemStack(chest));
                }
                if (leggings != null && leggings.getType() != Material.AIR) {
                    entityEquipment.setLeggings(new ItemStack(leggings));
                }
                if (boots != null && boots.getType() != Material.AIR) {
                    entityEquipment.setBoots(new ItemStack(boots));
                }
                if (hand != null && hand.getType() != Material.AIR) {
                    entityEquipment.setItemInMainHand(new ItemStack(hand));
                }
                if (offHand != null && offHand.getType() != Material.AIR) {
                    entityEquipment.setItemInOffHand(new ItemStack(offHand));
                }
            }
            double baseMaxHealth = getPlayerMaxHealth(player);
            double baseDamage = getPlayerExtraDamage(player);
            double randomHealthMultiple = MathUtil.getRandomValueDouble(10.1, 3.1);
            double randomDamageMultiple = MathUtil.getRandomValueDouble(7.1, 2.1);
            double calHealth = baseMaxHealth + (baseMaxHealth * randomHealthMultiple);
            double calDamage = baseDamage + (baseDamage * randomDamageMultiple);
            setMaxHealth(livingEntity, calHealth);
            setBaseDamage(livingEntity, calDamage);
            PersistentDataContainer data = livingEntity.getPersistentDataContainer();
            data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Doppelganger"), PersistentDataType.STRING, livingEntity.getUniqueId().toString());
            BukkitTask doppelTask = new DoppelgangerTask(livingEntity).runTaskTimer(ModPackIntegrated.getPlugin(), 0, 40L);
        });
    }

}
