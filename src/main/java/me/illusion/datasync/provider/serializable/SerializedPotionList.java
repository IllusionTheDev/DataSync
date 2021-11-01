package me.illusion.datasync.provider.serializable;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serializable class used to store potion effects
 *
 * This is just a fancy wrapper for ConfigurationSerializable,
 * which uses Serializable methods, but in fact isn't serializable
 * according to java's standards.
 */
public class SerializedPotionList implements Serializable {

    /**
     * List of serialized potions
     */
    private final List<Map<String, Object>> data = new ArrayList<>();

    /**
     * Method to apply serialized effects to a player
     *
     * @param player - The player to apply to
     */
    public void apply(Player player) {
        for (Map<String, Object> map : data) {
            PotionEffect effect = new PotionEffect(map);
            player.addPotionEffect(effect);
        }
    }

    /**
     * Method to fetch a player's effects and store them internally
     *
     * @param player - The player to fetch from
     */
    public void serialize(Player player) {
        data.clear();

        for (PotionEffect effect : player.getActivePotionEffects())
            data.add(effect.serialize());

    }
}