package org.pinae.timon.cache;

/**
 * 缓存异常处理
 * 
 * @author Huiyugeng
 *
 */
public class CacheException extends Exception {

	private static final long serialVersionUID = 8423714358000052785L;
	/**
	 * 构造函数
	 *
	 */
	public CacheException() {
		super();
	}
	/**
	 * 构造函数
	 * 
	 * @param message 异常提示
	 * @param cause 异常引发原因
	 */
	public CacheException(String msg, Throwable cause) {
		super(msg, cause);
	}
	/**
	 * 构造函数
	 * 
	 * @param message 异常提示
	 */
	public CacheException(String msg) {
		super(msg);
	}
	/**
	 * 构造函数
	 * 
	 * @param cause 异常引发原因
	 */
	public CacheException(Throwable cause) {
		super(cause);
	}

}
