package org.toasthub.core.general.model;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component("AppCacheMenu")
@Scope("singleton")
public class AppCacheMenu implements Serializable {

	private static final long serialVersionUID = 1L;
	// menuName-tenant-apiVerison-appVersion-lang -> order -> menuItem
	private Map<String,Map<Integer,MenuItem>> menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
	public static final String[] categories = {"PUBLIC","MEMBER","ADMIN"};
	
	
	// Constructor
	public AppCacheMenu(){
	}
	
	public void clearCache(){
		this.menus = null;
		this.menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
	}

	public void setMenus(Map<String,Map<Integer,MenuItem>> menus){ 
		this.menus = menus;
	}
	
	public Map<String,Map<Integer,MenuItem>> getMenus(){
		return menus;
	}

	public void addMenu(String key, Map<Integer,MenuItem> menu) {
		if (this.menus != null){
			this.menus.put(key, menu);
		}
	}
	
}
