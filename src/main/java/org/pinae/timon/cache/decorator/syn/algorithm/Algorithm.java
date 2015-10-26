package org.pinae.timon.cache.decorator.syn.algorithm;

import java.util.Comparator;

import org.pinae.timon.cache.CacheObject;
/**
 * 缓存清理策略接口
 * 
 * @author Huiyugeng
 *
 */
public interface Algorithm extends Comparator<CacheObject> {

}
