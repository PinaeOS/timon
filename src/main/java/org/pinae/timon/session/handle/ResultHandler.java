package org.pinae.timon.session.handle;

public interface ResultHandler {
	public <T> void handle(T t);
}
