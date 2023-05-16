package me.msicraft.modpackintegrated.MainMenu.KillPointShop.Skill;

import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.Event.PlayerRelated;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KillPointShopSkill {

    private static Location spawnLocation = null;

    public static void reloadVariables() {
        if (getSpawnLocation() != null) {
            spawnLocation = getSpawnLocation();
        }
    }

    private static Location getSpawnLocation() {
        Location location = null;
        if (ModPackIntegrated.getPlugin().getConfig().contains("SpawnLocation")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("SpawnLocation");
            if (s != null) {
                String[] a = s.split(":");
                World world = Bukkit.getWorld(a[0]);
                if (world != null) {
                    double x = Double.parseDouble(a[1]);
                    double y = Double.parseDouble(a[2]);
                    double z = Double.parseDouble(a[3]);
                    float yaw = Float.parseFloat(a[4]);
                    float pitch = Float.parseFloat(a[5]);
                    location = new Location(world, x, y, z, yaw, pitch);
                }
            }
        }
        return location;
    }

    public static boolean moveSpawn(Player player) {
        boolean isSuccess = false;
        if (spawnLocation != null) {
            player.teleport(spawnLocation);
            player.sendMessage(ChatColor.GREEN + "스폰 지점으로 이동 되었습니다.");
            isSuccess = true;
        }
        return isSuccess;
    }

    private final static PotionEffect resistanceEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255, false, false);

    public static boolean teleportLastDeathLocation(Player player) {
        boolean isSuccess = false;
        if (PlayerRelated.hasLastDeathLocation(player)) {
            Location location = PlayerRelated.getDeathLocation(player);
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "죽은 위치로 이동 되었습니다.");
            player.addPotionEffect(resistanceEffect);
            isSuccess = true;
        }
        return isSuccess;
    }

    public static boolean reRollSpecialAbility(Player player) {
        boolean check = false;
        ItemStack craftingItemStack = player.getInventory().getItemInMainHand();
        if (craftingItemStack != null && craftingItemStack.getType() != Material.AIR) {
            if (CraftingEquipUtil.isCraftingEquipment(craftingItemStack)) {
                if (CraftingEquipStatUtil.hasItemSpecialAbility(craftingItemStack)) {
                    CraftingEquipStatUtil.rerollSpecialAbility(craftingItemStack);
                    player.sendMessage(ChatColor.GREEN + "특수 능력이 재설정 되었습니다.");
                    check = true;
                } else {
                    player.sendMessage(ChatColor.RED + "아이템에 특수 능력이 없습니다.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "제작 장비가 아닙니다.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "장비가 없습니다.");
        }
        return check;
    }

    public static boolean applySpecialAbility(Player player) {
        boolean check = false;
        ItemStack craftingItemStack = player.getInventory().getItemInMainHand();
        if (craftingItemStack != null && craftingItemStack.getType() != Material.AIR) {
            if (CraftingEquipUtil.isCraftingEquipment(craftingItemStack)) {
                if (!CraftingEquipStatUtil.hasItemSpecialAbility(craftingItemStack)) {
                    if (craftingItemStack.getItemMeta() != null) {
                        List<String> lore = craftingItemStack.getItemMeta().getLore();
                        if (lore != null) {
                            boolean a = false;
                            for (String s : lore) {
                                if (s.contains("특수 능력:")) {
                                    a = true;
                                    break;
                                }
                            }
                            if (a) {
                                CraftingEquipStatUtil.applySpecialAbility(craftingItemStack);
                                player.sendMessage(ChatColor.GREEN + "특수 능력이 부여되었습니다.");
                                check = true;
                            } else {
                                player.sendMessage(ChatColor.RED + "특수 능력을 부여할수 없습니다.");
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "이미 특수 능력이 존재합니다.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "제작 장비가 아닙니다.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "장비가 없습니다.");
        }
        return check;
    }

    public static boolean resetCraftingInfo(Player player) {
        boolean check = false;
        ItemStack craftingItemStack = player.getInventory().getItemInMainHand();
        if (craftingItemStack != null && craftingItemStack.getType() != Material.AIR) {
            if (CraftingEquipUtil.isCraftingEquipment(craftingItemStack)) {
                CraftingEquipUtil.resetCraftingEquipment(craftingItemStack);
                check = true;
            } else {
                player.sendMessage(ChatColor.RED + "제작 장비가 아닙니다.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "장비가 없습니다.");
        }
        return check;
    }

}
