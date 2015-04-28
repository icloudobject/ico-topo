/**
 * 
 */
package com.icloudobject.icosvr.core;

/**
 * @author ralph
 * 
 */
public enum Cloud {

	ALIYUN("aliyun"), AWS("aws"), AZURE("azure"), GOOGLE("google"), OPENSTACK("openstack"), UNKNOWN("unknown");

	public final String name;

	private Cloud(final String name) {
		this.name = name;
	}

	public static Cloud fromString(final String name) {
		for (Cloud type : Cloud.values()) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return UNKNOWN;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public String getCloudName() {
		return name;
	}

}
