package com.mcsunnyside.craftbooklimiter;

import com.google.common.cache.*;
import com.sk89q.craftbook.AbstractCraftBookMechanic;
import org.bukkit.Chunk;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuotaManager {
    private final Cache<Chunk, Count> caching;
    private final int limitPerChunk;
    private final boolean worksOnly;
    private final List<Class<? extends AbstractCraftBookMechanic>> ignores;
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
    public boolean tickAndCheckNext(Chunk chunk, boolean works, Class<? extends AbstractCraftBookMechanic> clazz){
        if(ignores.contains(clazz)){
            return true;
        }
        if(worksOnly && !works){
            return true;
        }
        if(!worksOnly && works){
            return true; //Prevent count twice
        }
        Count count = caching.getIfPresent(chunk);
        if(count == null) {
            count = new Count();
            caching.put(chunk, count);
        }
        boolean result = count.reach(this.limitPerChunk);
        count.grow();
        return !result;
    }
}
class Count{
    int count = 0;
    public int grow(){
        return count++;
    }
    public int decay(){
        return count--;
    }
    public int get(){
        return count;
    }
    public boolean reach(int limit){
        return count >= limit;
    }
}
