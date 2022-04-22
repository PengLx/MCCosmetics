package io.lumine.cosmetics.managers.hats;

import io.lumine.cosmetics.MCCosmeticsPlugin;
import io.lumine.cosmetics.api.cosmetics.Cosmetic;
import io.lumine.cosmetics.api.cosmetics.manager.HideableCosmetic;
import io.lumine.cosmetics.api.players.CosmeticProfile;
import io.lumine.cosmetics.managers.MCCosmeticsManager;
import io.lumine.cosmetics.managers.gestures.Gesture;
import io.lumine.cosmetics.nms.cosmetic.VolatileEquipmentHelper;
import io.lumine.cosmetics.players.Profile;
import io.lumine.utils.Events;
import io.lumine.utils.events.extra.ArmorEquipEvent; 
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;

public class HatManager extends MCCosmeticsManager<Hat> implements HideableCosmetic {
    
    public HatManager(MCCosmeticsPlugin plugin) {
        super(plugin, Hat.class);   
        
        load(plugin);
    }

    @Override
    public void load(MCCosmeticsPlugin plugin) {
        super.load(plugin);

        Events.subscribe(InventoryCloseEvent.class)
                .handler(event -> {
                    final Player player = (Player) event.getPlayer();
                    getProfiles().awaitProfile(player).thenAcceptAsync(maybeProfile -> {
                        if(maybeProfile.isEmpty())
                            return;
                        final Profile profile = maybeProfile.get();
                        equip(profile);
                    });
                }).bindWith(this);

        Events.subscribe(ArmorEquipEvent.class)
                .handler(event -> {
                    final Player player = event.getPlayer();
                    getProfiles().awaitProfile(player).thenAcceptAsync(maybeProfile -> {
                        if(maybeProfile.isEmpty()) {
                            return;
                        }
                        final Profile profile = maybeProfile.get();
                        equip(profile);
                    });
                }).bindWith(this);
    }

    @Override
    public Hat build(File file, String node) {
        return new Hat(this, file, node);
    }

    @Override
    public void equip(CosmeticProfile profile) {
        getHelper().apply(profile);
    }

    @Override
    public void unequip(CosmeticProfile profile) {
        getHelper().unapply(profile);
    }

    @Override
    public void hide(CosmeticProfile profile, Cosmetic request) {
        if(!(request instanceof Gesture))
            return;
        getHelper().unapply(profile);
    }

    @Override
    public void show(CosmeticProfile profile) {
        getHelper().apply(profile);
    }

    protected VolatileEquipmentHelper getHelper() {
        return (VolatileEquipmentHelper) getNMSHelper();
    }
}
