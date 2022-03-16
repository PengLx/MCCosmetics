package io.lumine.cosmetics.managers.hats;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.lumine.utils.Events;
import io.lumine.utils.Schedulers;
import io.lumine.utils.config.properties.Property;
import io.lumine.utils.config.properties.types.NodeListProp;
import io.lumine.utils.files.Files;
import io.lumine.utils.logging.Log;
import io.lumine.utils.protocol.Protocol;
import io.lumine.cosmetics.MCCosmeticsPlugin;
import io.lumine.cosmetics.api.events.CosmeticPlayerLoadedEvent;
import io.lumine.cosmetics.api.players.CosmeticProfile;
import io.lumine.cosmetics.config.Scope;
import io.lumine.cosmetics.constants.PackFolders;
import io.lumine.cosmetics.managers.MCCosmeticsManager;
import io.lumine.cosmetics.players.Profile;

public class HatManager extends MCCosmeticsManager {

    private final NodeListProp KEYS = Property.NodeList(Scope.NONE, "");
    
    private final Map<String,Hat> hats = Maps.newConcurrentMap();
    
    public HatManager(MCCosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load(MCCosmeticsPlugin plugin) {
        final Collection<File> files = Lists.newArrayList();
        for(var packFolder : plugin.getConfiguration().getPackFolders()) {
            final File confFolder = new File(packFolder.getAbsolutePath() + System.getProperty("file.separator") + PackFolders.HATS);
            if(confFolder.exists() && confFolder.isDirectory()) {
                files.addAll(Files.getAllYaml(confFolder.getAbsolutePath()));
            }
        }
        
        for(var file : files) {
            for(var node : KEYS.fget(file)) {
                var cosmetic = new Hat(file,node);
                hats.put(node.toUpperCase(), cosmetic);
            }
        }
        
        Log.info("Loaded " + hats.size() + " hats");
        
        Events.subscribe(CosmeticPlayerLoadedEvent.class)
            .handler(event -> {
                final var profile = event.getProfile();
    
                //if(profile.getHat().isPresent()) {
                //    equipHat(profile);
                //}
            }).bindWith(this);
        
        Events.subscribe(InventoryCloseEvent.class)
            .handler(event -> {
                final Player player = (Player) event.getPlayer();
                getProfiles().awaitProfile(player).thenAcceptAsync(maybeProfile -> {
                    if(!maybeProfile.isPresent()) {
                       return; 
                    }
                    final Profile profile = maybeProfile.get();
                    //if(profile.getHat().isPresent()) {
                    //    profile.setHatIsActive(true);
                    //    Schedulers.async().runLater(() -> this.equipHat(profile), 1);
                   // }
                });
            }).bindWith(this);
        
        Protocol.subscribe(PacketType.Play.Server.ENTITY_EQUIPMENT)
            .handler(event -> {
                final PacketContainer packet = event.getPacket();
                final Entity entity = packet.getEntityModifier(event.getPlayer().getWorld()).read(0);
                
                if(!(entity instanceof Player)) {
                    return;
                }
                final Player player = (Player) entity;
                final Profile profile = getProfiles().getProfile(player);
                
                if(profile == null) {
                    return;
                }
                //if(profile.getHat().isPresent()) {
                //    writeHeadItem(packet, profile.getEquippedHat());
                //}
            }).bindWith(this);
        
        Events.subscribe(InventoryClickEvent.class)
            .handler(event -> {
                if(event.getRawSlot() == 5) {
                    final Player player = (Player) event.getWhoClicked();
                    final Profile profile = getProfiles().getProfile(player);
                    
                    //if(profile.getHat().isPresent() && profile.getHatIsActive()) {
                    //    player.updateInventory();
                    //    profile.setHatIsActive(false);
                    //}
                }
            }).bindWith(this);
    }
    
    @Override
    public void unload() {
        hats.clear();
    }
    
    public Collection<Hat> getHats() {
        return hats.values();
    }

    public Optional<Hat> getHat(String key) {
        return Optional.ofNullable(hats.getOrDefault(key, null));
    }
    
    public void equipHat(Player player) {
        equipHat(getProfiles().getProfile(player));
    }
    
    public void equipHat(Profile profile) {
        getPlugin().getVolatileCodeHandler().getHatHelper().applyHatPacket(profile);
    }
}