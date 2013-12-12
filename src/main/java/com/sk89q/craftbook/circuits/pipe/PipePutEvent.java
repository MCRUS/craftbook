package com.sk89q.craftbook.circuits.pipe;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PipePutEvent extends PipeEvent implements Cancellable {

    private Block put;

    public PipePutEvent (Block theBlock, List<ItemStack> items, Block put) {
        super(theBlock, items);
        this.put = put;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static final HandlerList handlers = new HandlerList();

    public Block getPuttingBlock() {

        return put;
    }

    @Override
    public boolean isCancelled () {
        return isCancelled;
    }

    @Override
    public void setCancelled (boolean arg0) {
        isCancelled = arg0;
    }

    private boolean isCancelled = false;

    public boolean isValid() {
        return !isCancelled() && !getItems().isEmpty();
    }
}
