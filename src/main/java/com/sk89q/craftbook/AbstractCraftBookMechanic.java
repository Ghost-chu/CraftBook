package com.sk89q.craftbook;

import com.mcsunnyside.craftbooklimiter.QuotaManager;
import com.sk89q.craftbook.util.LoadPriority;
import com.sun.org.apache.xpath.internal.operations.Quo;

public abstract class AbstractCraftBookMechanic implements CraftBookMechanic, Comparable<LoadPriority> {
    public QuotaManager quotaManager;
    public AbstractCraftBookMechanic(QuotaManager quotaManager){
        this.quotaManager = quotaManager;
    }
    @Override
    public boolean enable() {
        return true;
    }

    @Override
    public void disable() {

    }

    @Override
    public LoadPriority getLoadPriority() {

        return LoadPriority.STANDARD;
    }

    @Override
    public int compareTo(LoadPriority compare) {

        return compare.index < getLoadPriority().index ? -1 : 1;
    }
}