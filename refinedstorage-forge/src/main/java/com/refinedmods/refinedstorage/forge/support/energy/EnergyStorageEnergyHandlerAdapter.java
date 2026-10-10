package com.refinedmods.refinedstorage.forge.support.energy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.energy.EnergyStorage;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageEnergyHandlerAdapter implements IEnergyStorage {
    private final EnergyStorage energyStorage;

    public EnergyStorageEnergyHandlerAdapter(final EnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return (int) energyStorage.receive(maxReceive, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(energyStorage.getStored(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(energyStorage.getCapacity(), Integer.MAX_VALUE);
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
