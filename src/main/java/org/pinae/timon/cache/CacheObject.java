package org.pinae.timon.cache;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pinae.timon.util.ObjectUtils;

/**
 * 缓存对象
 * 用于包装缓存键和值，放入缓存中
 * 
 * @author Huiyugeng
 *
 */
public class CacheObject implements Serializable {

	private static final long serialVersionUID = 1492487230367266468L;

	private long id; //缓存ID

	private String key; //缓存对象的键

	private Object value; //缓存对象的值
	
	private int expire; //缓存对象过期时间(s), -1: 使用配置过期时间

	private long createTime; //缓存对象建立时间

	private long lastAccessTime; //缓存对象最后访问时间

	private int hitCount; //缓存对象访问计数

	private int size; //缓存对象长度
	
	/**
	 * 构造函数
	 * 
	 * @param key 缓存对象键
	 * @param value 缓存对象值
	 */
	public CacheObject(String key, Object value) {
		this(key, value, -1);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param key 缓存对象键
	 * @param value 缓存对象值
	 * @param expire 缓存过期时间
	 */
	public CacheObject(String key, Object value, int expire) {
		long now = System.currentTimeMillis();
		
		this.key = key;
		this.value = value;
		this.expire = expire;
		
		this.createTime = now;
		this.lastAccessTime = now;
		this.hitCount = 1;
		this.id = nextId();
		this.size = ObjectUtils.size(this);
	}
	
	/**
	 * 返回缓存对象被访问次数
	 * 
	 * @return 缓存对象被访问次数
	 */
	public int getHitCount() {
		return hitCount;
	}
	/**
	 * 返回缓存对象建立时间
	 * 
	 * @return 缓存对象建立时间
	 */
	public Date getCreateTime() {
		return new Date(createTime);
	}
	/**
	 * 返回缓存对象编号
	 * 
	 * @return 缓存对象编号
	 */
	public long getId() {
		return id;
	}
	/**
	 * 返回缓存对象键
	 * 
	 * @return 缓存对象键
	 */
	public String getKey() {
		return key;
	}
	/**
	 * 设置缓存对象键
	 * 
	 * @param key 缓存对象键
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * 返回缓存对象最后访问时间
	 * 
	 * @return 缓存对象最后访问时间
	 */
	public Date getLastAccessTime() {
		return new Date(lastAccessTime);
	}
	/**
	 * 返回缓存对象长度
	 * 
	 * @return 缓存对象长度
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * 设置缓存对象长度
	 * 
	 * @param size 缓存对象长度
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * 返回缓存对象过期时间
	 * 
	 * @return 缓存对象过期时间, -1: 使用全局过期时间
	 */
	public int getExpire() {
		return expire;
	}

	/**
	 * 设置缓存对象过期时间
	 * 
	 * @param expire 缓存对象过期时间
	 */
	public void setExpire(int expire) {
		this.expire = expire;
	}

	/**
	 * 返回缓存对象值
	 * 
	 * @return 缓存对象值
	 */
	public Object getValue() {
		if (value != null) {
			hit();
		}
		return value;
	}
	/**
	 * 设置缓存对象值
	 * 
	 * @param value 缓存对象值
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	protected void hit() {
		this.lastAccessTime = System.currentTimeMillis();
		this.hitCount++;
	}

	private static long ID = 0;

	private static synchronized long nextId() {
		return ID++;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
				.append(this.id)
				.append(this.createTime)
				.append(this.lastAccessTime)
				.append(this.hitCount)
				.append(key)
				.build();
	}

	public boolean equals(Object object) {
		if (object instanceof CacheObject) {
			CacheObject target = (CacheObject) object;
			return new EqualsBuilder()
					.append(key, target.getKey())
					.append(value, target.getValue())
					.append(size, target.getSize())
					.isEquals();
		} else {
			return false;
		}
	}

	public int hashCode() {
		return (key.hashCode() + value.hashCode()) * 19;
	}

}
