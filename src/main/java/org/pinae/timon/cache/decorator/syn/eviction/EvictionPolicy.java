package org.pinae.timon.cache.decorator.syn.eviction;

import java.util.Comparator;

import org.pinae.timon.cache.CacheObject;
/**
 * 缓存清理策略接口
 * 
 * @author Huiyugeng
 *
 */
public interface EvictionPolicy extends Comparator<CacheObject> {

}
