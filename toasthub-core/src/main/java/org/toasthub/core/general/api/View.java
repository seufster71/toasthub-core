package org.toasthub.core.general.api;

public class View {
	public interface Public {}
	public interface Member {}
	public interface Admin {}
	public interface System {}
	public interface PublicMember extends Public, Member {}
}
