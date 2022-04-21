package io.lumine.cosmetics.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.lumine.cosmetics.MCCosmeticsPlugin;
import io.lumine.cosmetics.api.MCCosmetics;
import io.lumine.cosmetics.commands.CommandHelper;
import io.lumine.cosmetics.constants.Permissions;
import io.lumine.cosmetics.managers.sprays.Spray;
import io.lumine.utils.commands.Command;

public class SprayCommand extends Command<MCCosmeticsPlugin> {

    public SprayCommand(MCCosmeticsPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var profile = getPlugin().getProfiles().getProfile(player);
        var inv = profile.getCosmeticInventory();
        
        if(args.length > 0) {
            var sprayName = args[1];
            var maybeSpray = getPlugin().getSprayManager().getCosmetic(sprayName);
            
            if(maybeSpray.isEmpty()) {
                return true;
            }
            
            var spray = maybeSpray.get();
            
            if(inv.hasUnlocked(spray)) {
                getPlugin().getSprayManager().useSpray(player, spray);
            }
        } else {
            var maybeSpray = inv.getEquipped(Spray.class);
            
            if(maybeSpray.isEmpty()) {
                return true;
            }
            
            var spray = (Spray) maybeSpray.get();
            
            if(inv.hasUnlocked(spray)) {
                getPlugin().getSprayManager().useSpray(player, spray);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return null;
    }

    @Override
    public boolean isConsoleFriendly() {
        return false;
    }

    @Override
    public String getName() {
        return "";
    }
}