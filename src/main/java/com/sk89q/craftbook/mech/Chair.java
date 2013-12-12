package com.sk89q.craftbook.mech;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.util.BlockUtil;
import com.sk89q.craftbook.util.ItemInfo;
import com.sk89q.craftbook.util.LocationUtil;
import com.sk89q.craftbook.util.SignUtil;
import com.sk89q.craftbook.util.Tuple2;
import com.sk89q.worldedit.blocks.BlockType;


/**
 * @author Me4502
 */
public class Chair extends AbstractCraftBookMechanic {

    public Map<String, Tuple2<Entity, Block>> chairs = new ConcurrentHashMap<String, Tuple2<Entity, Block>>();

    public void addChair(final Player player, Block block) {

        Entity ar = null;
        if(chairs.containsKey(player.getName()))
            ar = chairs.get(player.getName()).a;
        boolean isNew = false;

        if(ar == null || !ar.isValid() || ar.isDead() || !ar.getLocation().getBlock().equals(block)) {
            if(ar != null && !ar.getLocation().getBlock().equals(block))
                ar.remove();
            ar = block.getWorld().spawnArrow(BlockUtil.getBlockCentre(block).subtract(0, 0.5, 0), new Vector(0,-0.1,0), 0.01f, 0);
            isNew = true;
        }
        if (!chairs.containsKey(player.getName()))
            CraftBookPlugin.inst().wrapPlayer(player).print("mech.chairs.sit");
        // Attach the player to said arrow.
        final Entity far = ar;
        if(ar.isEmpty() && isNew) {
            Bukkit.getScheduler().runTask(CraftBookPlugin.inst(), new Runnable() {
                @Override
                public void run () {
                    far.setPassenger(player);
                }
            });
        } else if (ar.isEmpty()) {
            removeChair(player);
            return;
        }

        ar.setTicksLived(1);

        chairs.put(player.getName(), new Tuple2<Entity, Block>(ar, block));
    }

    public void removeChair(final Player player) {

        CraftBookPlugin.inst().wrapPlayer(player).print("mech.chairs.stand");
        final Entity ent = chairs.get(player.getName()).a;
        final Block block = chairs.get(player.getName()).b;
        if(ent != null) {
            player.eject();
            player.teleport(block.getLocation().add(0, 1, 0));
            ent.remove();
            Bukkit.getScheduler().runTaskLater(CraftBookPlugin.inst(), new Runnable() {
                @Override
                public void run () {
                    player.teleport(block.getLocation().add(0, 1, 0));
                    player.setSneaking(false);
                }
            }, 1L);
        }
        chairs.remove(player.getName());
    }

    public boolean hasSign(Block block, List<Location> searched) {

        boolean found = false;

        for (BlockFace face : LocationUtil.getDirectFaces()) {
            Block otherBlock = block.getRelative(face);

            if(searched.contains(otherBlock.getLocation())) continue;
            searched.add(otherBlock.getLocation());

            if (found) break;

            if (SignUtil.isSign(otherBlock) && SignUtil.getFront(otherBlock) == face) {
                found = true;
                break;
            }

            if (BlockUtil.areBlocksIdentical(block, otherBlock))
                found = hasSign(otherBlock, searched);
        }

        return found;
    }

    public Tuple2<Entity, Block> getChair(Player player) {

        return chairs.get(player.getName());
    }

    public boolean hasChair(Player player) {

        return chairs.containsKey(player.getName());
    }

    public boolean hasChair(Block player) {

        for(Tuple2<Entity, Block> tup : chairs.values())
            if(player.equals(tup.b))
                return true;

        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        if (!CraftBookPlugin.inst().getConfiguration().chairEnabled) return;
        if (hasChair(event.getBlock())) {
            event.setCancelled(true);
            CraftBookPlugin.inst().wrapPlayer(event.getPlayer()).printError("mech.chairs.in-use");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {

        if (!CraftBookPlugin.inst().getConfiguration().chairEnabled) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null || !CraftBookPlugin.inst().getConfiguration().chairBlocks.contains(new ItemInfo(event.getClickedBlock())))
            return;

        LocalPlayer lplayer = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());
        if (lplayer.isSneaking()) return;
        Player player = event.getPlayer();

        // Now everything looks good, continue;
        if (CraftBookPlugin.inst().getConfiguration().chairAllowHeldBlock || !lplayer.isHoldingBlock() || lplayer.getHeldItemType() == 0) {
            if (CraftBookPlugin.inst().getConfiguration().chairRequireSign && !hasSign(event.getClickedBlock(), new ArrayList<Location>()))
                return;
            if (!lplayer.hasPermission("craftbook.mech.chair.use")) {
                if(CraftBookPlugin.inst().getConfiguration().showPermissionMessages)
                    lplayer.printError("mech.use-permission");
                return;
            }
            if (hasChair(player.getPlayer())) { // Stand
                removeChair(player.getPlayer());
            } else { // Sit
                if (hasChair(event.getClickedBlock())) {
                    lplayer.print("mech.chairs.in-use");
                    return;
                }
                if (BlockType.canPassThrough(event.getClickedBlock().getRelative(0, -1, 0).getTypeId())) {

                    lplayer.printError("mech.chairs.floating");
                    return;
                }

                Location chairLoc = event.getClickedBlock().getLocation().add(0.5,0,0.5);

                if(CraftBookPlugin.inst().getConfiguration().chairFacing && event.getClickedBlock().getState().getData() instanceof Directional) {

                    BlockFace direction = ((Directional) event.getClickedBlock().getState().getData()).getFacing();

                    double dx = direction.getModX();
                    double dy = direction.getModY();
                    double dz = direction.getModZ();

                    if (dx != 0) {
                        if (dx < 0)
                            chairLoc.setYaw((float) (1.5 * Math.PI));
                        else
                            chairLoc.setYaw((float) (0.5 * Math.PI));
                        chairLoc.setYaw((float)(chairLoc.getYaw() - Math.atan(dz / dx)));
                    } else if (dz < 0)
                        chairLoc.setYaw((float) Math.PI);

                    double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

                    chairLoc.setPitch((float) -Math.atan(dy / dxz));

                    chairLoc.setYaw(-chairLoc.getYaw() * 180f / (float) Math.PI);
                    chairLoc.setPitch(chairLoc.getPitch() * 180f / (float) Math.PI);
                } else {
                    chairLoc.setPitch(player.getPlayer().getLocation().getPitch());
                    chairLoc.setYaw(player.getPlayer().getLocation().getYaw());
                }
                player.getPlayer().teleport(chairLoc);
                addChair(player.getPlayer(), event.getClickedBlock());
                event.setCancelled(true);
            }
        }
    }

    public class ChairChecker implements Runnable {

        @Override
        public void run() {

            for (String pl : chairs.keySet()) {
                Player p = Bukkit.getPlayerExact(pl);
                if (p == null  || p.isDead()) {
                    chairs.remove(pl);
                    continue;
                }

                if (!CraftBookPlugin.inst().getConfiguration().chairBlocks.contains(new ItemInfo(getChair(p).b)) || !p.getWorld().equals(getChair(p).b.getWorld()) || LocationUtil.getDistanceSquared(p.getLocation(), getChair(p).b.getLocation()) > 1.5)
                    removeChair(p);
                else {
                    addChair(p, getChair(p).b); // For any new players.

                    if (CraftBookPlugin.inst().getConfiguration().chairHealth && p.getHealth() < p.getMaxHealth())
                        p.setHealth(Math.min(p.getHealth() + 1, p.getMaxHealth()));
                    if (p.getExhaustion() > -20d) p.setExhaustion((float)(p.getExhaustion() - 0.1d));
                }
            }
        }
    }

    @Override
    public boolean enable () {

        Bukkit.getScheduler().runTaskTimer(CraftBookPlugin.inst(), new ChairChecker(), 20L, 20L);

        try {
            Class.forName("com.comphenix.protocol.events.PacketListener");
            ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(PacketAdapter.params(CraftBookPlugin.inst(), Packets.Client.PLAYER_INPUT).clientSide().listenerPriority(ListenerPriority.HIGHEST).options(ListenerOptions.INTERCEPT_INPUT_BUFFER)) {
                @Override
                public void onPacketReceiving(PacketEvent e) {
                    if (!e.isCancelled()) {
                        Player player = e.getPlayer();
                        if (e.getPacket().getBooleans().getValues().get(1).booleanValue())
                            if(hasChair(player))
                                removeChair(player);
                    }
                }
            }).syncStart();

            ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(new PacketAdapter(PacketAdapter.params(CraftBookPlugin.inst(), Packets.Client.ENTITY_ACTION).clientSide().listenerPriority(ListenerPriority.HIGHEST).options(ListenerOptions.INTERCEPT_INPUT_BUFFER)) {
                @Override
                public void onPacketReceiving(PacketEvent e) {
                    if (!e.isCancelled()) {
                        Player player = e.getPlayer();
                        if(hasChair(player))
                            removeChair(player);
                    }
                }
            }).syncStart();
        } catch(Throwable e) {
            CraftBookPlugin.inst().getLogger().warning("ProtocolLib is required for chairs! Disabling chairs!");
            return false;
        }

        return true;
    }

    @Override
    public void disable () {
        chairs.clear();
        ProtocolLibrary.getProtocolManager().getAsynchronousManager().unregisterAsyncHandlers(CraftBookPlugin.inst());
    }
}