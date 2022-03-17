package io.lumine.cosmetics.nms;

import io.lumine.cosmetics.MCCosmeticsPlugin;
import io.lumine.cosmetics.nms.v1_18_R2.cosmetic.VolatileHatImpl;
import io.lumine.cosmetics.nms.v1_18_R2.network.VolatileChannelHandler;
import io.lumine.utils.reflection.Reflector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.Getter;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class VolatileCodeEnabled_v1_18_R1 implements VolatileCodeHandler {

    @Getter private final MCCosmeticsPlugin plugin;
    @Getter private final VolatileHatHelper hatHelper;

    private Reflector<ServerLevel> refServerLevel = new Reflector<>(ServerLevel.class, "O");
    
    public VolatileCodeEnabled_v1_18_R1(MCCosmeticsPlugin plugin) {
        this.plugin = plugin;
        this.hatHelper = new VolatileHatImpl(plugin, this);
    }

    @Override
    public void injectPlayer(Player player) {
        ServerPlayer ply = ((CraftPlayer) player).getHandle();
        VolatileChannelHandler cdh = new VolatileChannelHandler(this, player);

        ChannelPipeline pipeline = ply.connection.getConnection().channel.pipeline();
        for (String name : pipeline.toMap().keySet()) {
            if (pipeline.get(name) instanceof Connection) {
                pipeline.addBefore(name, "mc_cosmetics_packet_handler", cdh);
                break;
            }
        }
    }

    @Override
    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().connection.getConnection().channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove("mc_cosmetics_packet_handler");
            return null;
        });
    }

    public void broadcast(Packet<?>... packets) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            var connection = ((CraftPlayer) player).getHandle().connection;
            for(Packet<?> packet : packets) {
                connection.send(packet);
            }
        }
    }

    public void broadcast(World world, Packet<?>... packets) {
        for(Player player : world.getPlayers()) {
            var connection = ((CraftPlayer) player).getHandle().connection;
            for(Packet<?> packet : packets) {
                connection.send(packet);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Entity getEntity(World world, int id) {
        ServerLevel level = ((CraftWorld) world).getHandle();
        PersistentEntitySectionManager<net.minecraft.world.entity.Entity> entityManager = (PersistentEntitySectionManager<net.minecraft.world.entity.Entity>) refServerLevel.get(level, "O");
        net.minecraft.world.entity.Entity entity = entityManager.getEntityGetter().get(id);
        return entity == null ? null : entity.getBukkitEntity();
    }

}