package org.toasthub.core.general.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.service.MenuSvc;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.preference.model.AppCachePage;
import org.toasthub.core.system.model.AppCacheClientDomains;


@Component("AppCacheMenu")
@Scope("singleton")
public class AppCacheMenu implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String,Map<Integer,MenuItem>> menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
	private final String[] categories = {"PUBLIC","MEMBER","ADMIN"};
	
	@Autowired 
	@Qualifier("MenuSvc")
	MenuSvc menuSvc;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	@Autowired
	AppCachePage appCachePage;
	
	// Constructor
	public AppCacheMenu(){
	}
	
	//@Schedule(hour="1")
	public void clearCache(){
		this.menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
	}

	// Menus
	public Map<Integer,MenuItem> getMenu(String menuName, String apiVersion, String appVersion, String lang) {
		if (this.menus == null) {
			this.menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
		}
		Map<Integer,MenuItem> menu = null;
		StringBuilder key = new StringBuilder();
		key.append(menuName);
		key.append("_");
		key.append(appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain());
		key.append("_");
		key.append(apiVersion);
		key.append("_");
		key.append(appVersion);
		key.append("_");
		key.append(lang);
		if (this.menus.containsKey(key.toString())){
			// In cache return menu
			menu = this.menus.get(key.toString());
		}
		return menu;
	}

	public void addMenu(String key, Map<Integer,MenuItem> menu) {
		if (this.menus != null){
			this.menus.put(key, menu);
		}
	}

	public void clearMenuCache(){
		this.menus = null;
		this.menus = new ConcurrentHashMap<String,Map<Integer,MenuItem>>();
	}
	
	public void loadMenuCache(String tenant) {
		// get available menus for this tenant
		for (int i = 0; i < categories.length; i++) {
			RestRequest request = new RestRequest();
			request.addParam(BaseEntity.ACTIVE, true);
			request.addParam("category", categories[i]);
			RestResponse response = new RestResponse();
			menuSvc.getMenus(request,response);
			// load each menu
			List<Menu> myMenus = (List<Menu>) response.getParam(BaseEntity.ITEMS);
			for(Menu m : myMenus) {
				// by languages
				List<String> codes = appCachePage.getAvailableLanguageCodes(tenant);
				for(String lang : codes) {
					Map<Integer,MenuItem> menu = menuSvc.getMenu(m.getCode(),m.getApiVersion(),m.getAppVersion(),lang);
					StringBuilder key = new StringBuilder();
					key.append(m.getCode());
					key.append("_");
					key.append(tenant);
					key.append("_");
					key.append(m.getApiVersion());
					key.append("_");
					key.append(m.getAppVersion());
					key.append("_");
					key.append(lang);
					
					this.menus.put(key.toString(),menu);
				}
			}
		}
	}
	
	public void reloadMenuCache() {
		String tenant = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain();
		for(Iterator<Map.Entry<String, Map<Integer,MenuItem>>> it = this.menus.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, Map<Integer,MenuItem>> entry = it.next();
		      if(entry.getKey().contains(tenant)) {
		    	  it.remove();
		      }
		}
		loadMenuCache(tenant);
	}
}
