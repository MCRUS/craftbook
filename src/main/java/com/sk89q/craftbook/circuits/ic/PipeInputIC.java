package com.sk89q.craftbook.circuits.ic;

import com.sk89q.craftbook.circuits.pipe.PipePutEvent;

public interface PipeInputIC {

    /**
     * Called when a pipe transfers items into an {@link IC}.
     *
     * @param event The event that the pipe is sending.
     */
    public void onPipeTransfer(PipePutEvent event);
}