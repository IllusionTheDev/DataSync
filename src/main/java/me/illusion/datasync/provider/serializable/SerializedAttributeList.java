package me.illusion.datasync.provider.serializable;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializedAttributeList implements Serializable {

    private final Map<Attribute, Double> data = new HashMap<>();

    public void apply(Player player) {

        for(Map.Entry<Attribute, Double> entry : data.entrySet())
            player.getAttribute(entry.getKey()).setBaseValue(entry.getValue());
    }

    public void save(Player player) {
        for(Attribute attribute : Attribute.values()) {
            if(player.getAttribute(attribute) == null)
                continue;

            data.put(attribute, player.getAttribute(attribute).getBaseValue());

        }
    }

}
