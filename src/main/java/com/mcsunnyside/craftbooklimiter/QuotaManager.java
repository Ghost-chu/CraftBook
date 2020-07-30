package com.mcsunnyside.craftbooklimiter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuotaManager {
    private final Cache<Chunk, Count> caching;
    private final int limitPerChunk;
    private final boolean worksOnly;
    private final List<Class<? extends AbstractCraftBookMechanic>> ignores;
    private final Map<Class<? extends AbstractCraftBookMechanic>, Count> map = new HashMap<>();
    public QuotaManager(int maxSize, int expireAfterWriteSec, int limitPerChunk, boolean worksOnly, List<Class<? extends AbstractCraftBookMechanic>> ignores){
        this.ignores = ignores;
        this.worksOnly = worksOnly;
        this.limitPerChunk = limitPerChunk;
        caching = CacheBuilder.newBuilder()
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .initialCapacity(300)
                .recordStats()
                .maximumSize(maxSize)
                .expireAfterWrite(expireAfterWriteSec, TimeUnit.SECONDS)
                .build();
    }

    public Map<Class<? extends AbstractCraftBookMechanic>, Count> getStatsMap() {
        return map;
    }

    public Cache<Chunk, Count> getCaching() {
        return caching;
    }

    /**
     * Check this chunk machines can be tick
     * @param works Does machine already effect the world?
     * @param chunk The chunk
     * @return Can tick
     */
    public boolean machineTickable(Chunk chunk, boolean works, Class<? extends AbstractCraftBookMechanic> clazz){
        if(ignores.contains(clazz)){
            return true;
        }
        Count count = caching.getIfPresent(chunk);
        if(count == null) { return true;}
        return count.reach(this.limitPerChunk);
    }
    /**
     * Tick machine in this chunk and counter will +1
     * @param works Does machine already effect the world?
     * @param chunk The chunk
     */
    public void machineTick(Chunk chunk, boolean works, Class<? extends AbstractCraftBookMechanic> clazz){
        if(ignores.contains(clazz)){
            return;
        }
        if(worksOnly && !works){
            return;
        }
        if(!worksOnly && works){
            return; //Prevent count twice
        }
        map.getOrDefault(clazz, new Count()).grow();
        Count count = caching.getIfPresent(chunk);
        if(count == null) {
            count = new Count();
            caching.put(chunk, count);
        }
        count.grow();
    }
    /**
     * Tick machine in this chunk and counter will +1
     * @param chunk The chunk
     * @param works Does machine already effect the world?
     * @return This time tick is allowed
     */
    public boolean tickAndCheckNext(Chunk chunk, boolean works, Class<? extends AbstractCraftBookMechanic> clazz) {
        machineTick(chunk, works, clazz);
        return machineTickable(chunk, works, clazz);
    }

}
