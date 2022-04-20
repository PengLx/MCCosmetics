package io.lumine.cosmetics.nms;

import io.lumine.cosmetics.api.cosmetics.Cosmetic;
import io.lumine.cosmetics.nms.cosmetic.VolatileCosmeticHelper;
import org.bukkit.entity.Player;

import java.util.Collection;

public class VolatileCodeDisabled implements VolatileCodeHandler {

    @Override
    public VolatileCosmeticHelper getCosmeticHelper(Class<? extends Cosmetic> tClass) {
        return null;
    }

    @Override
    public Collection<VolatileCosmeticHelper> getCosmeticHelpers() {
        return null;
    }

    @Override
    public void injectPlayer(Player player) {

    }

    @Override
    public void removePlayer(Player player) {

    }

    @Override
    public void removeFakeEntity(int id) {

    }

}
