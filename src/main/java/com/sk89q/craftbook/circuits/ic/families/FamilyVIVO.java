// $Id$
/*
 * Copyright (C) 2012 Lymia Aluysia <lymiahugs@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.circuits.ic.families;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.sk89q.craftbook.ChangedSign;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.circuits.ic.AbstractChipState;
import com.sk89q.craftbook.circuits.ic.AbstractICFamily;
import com.sk89q.craftbook.circuits.ic.ChipState;
import com.sk89q.craftbook.util.SignUtil;
import com.sk89q.worldedit.BlockWorldVector;

/**
 * Handles detection for the variable-input variable-output family.
 *
 * @author Lymia
 */
public class FamilyVIVO extends AbstractICFamily {

    @Override
    public ChipState detect(BlockWorldVector source, ChangedSign sign) {

        return new ChipStateVIVO(source, sign);
    }

    @Override
    public ChipState detectSelfTriggered(BlockWorldVector source, ChangedSign sign) {

        return new ChipStateVIVO(source, sign, true);
    }

    public static class ChipStateVIVO extends AbstractChipState {

        public ChipStateVIVO(BlockWorldVector source, ChangedSign sign) {

            super(source, sign, false);
        }

        public ChipStateVIVO(BlockWorldVector source, ChangedSign sign, boolean selfTriggered) {

            super(source, sign, selfTriggered);
        }

        @Override
        protected Block getBlock(int pin) {

            BlockFace fback = SignUtil.getBack(BukkitUtil.toSign(sign).getBlock());
            Block backBlock = BukkitUtil.toSign(sign).getBlock().getRelative(fback);

            switch (pin) {
                case 0:
                    return SignUtil.getFrontBlock(BukkitUtil.toSign(sign).getBlock());
                case 1:
                    return SignUtil.getLeftBlock(BukkitUtil.toSign(sign).getBlock());
                case 2:
                    return SignUtil.getRightBlock(BukkitUtil.toSign(sign).getBlock());
                case 3:
                    return backBlock.getRelative(fback);
                case 4:
                    return backBlock.getRelative(SignUtil.getCounterClockWise(fback));
                case 5:
                    return backBlock.getRelative(SignUtil.getClockWise(fback));
                default:
                    return null;

            }

        }

        @Override
        public boolean getInput(int inputIndex) {

            return get(inputIndex);
        }

        @Override
        public boolean getOutput(int outputIndex) {

            return get(outputIndex + 3);
        }

        @Override
        public void setOutput(int outputIndex, boolean value) {

            set(outputIndex + 3, value);
        }

        @Override
        public int getInputCount() {

            return 3;
        }

        @Override
        public int getOutputCount() {

            return 3;
        }

    }

    @Override
    public String getName () {
        return "VIVO";
    }
}
