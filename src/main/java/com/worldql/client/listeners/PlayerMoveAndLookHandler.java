package com.worldql.client.listeners;

import com.google.flatbuffers.FlatBufferBuilder;
import com.worldql.client.WorldQLClient;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import WorldQLFB.StandardEvents.*;
import zmq.ZMQ;

public class PlayerMoveAndLookHandler implements Listener {


    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        if (e.getTo() == null) return;

        FlatBufferBuilder builder = new FlatBufferBuilder(1024);

        int uuid = builder.createString(e.getPlayer().getUniqueId().toString());
        int name = builder.createString(e.getPlayer().getName());
        int worldName = builder.createString(e.getPlayer().getWorld().getName());
        int instruction = builder.createString("MinecraftPlayerMove");

        Update.startUpdate(builder);
        Update.addUuid(builder, uuid);
        Update.addPosition(builder, Vec3.createVec3(builder, (float) e.getTo().getX(), (float) e.getTo().getY(), (float) e.getTo().getZ()));
        Update.addPitch(builder, e.getTo().getPitch());
        Update.addYaw(builder, e.getTo().getYaw());
        Update.addName(builder, name);
        Update.addWorldName(builder, worldName);
        Update.addInstruction(builder, instruction);
        Update.addSenderid(builder, WorldQLClient.getPluginInstance().getZmqPortClientId());

        int player = Update.endUpdate(builder);
        builder.finish(player);

        byte[] buf = builder.sizedByteArray();
        WorldQLClient.getPluginInstance().getPushSocket().send(buf, ZMQ.ZMQ_DONTWAIT);
    }
}
