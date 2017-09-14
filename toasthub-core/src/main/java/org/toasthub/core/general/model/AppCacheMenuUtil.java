package org.toasthub.core.general.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.menu.MenuSvc;
import org.toasthub.core.preference.model.AppCachePageUtil;
import org.toasthub.core.system.model.AppCacheClientDomains;

@Component("AppCacheMenuUtil")
public class AppCacheMenuUtil {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired 
	@Qualifier("MenuSvc")
	MenuSvc menuSvc;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	@Autowired
	AppCachePageUtil appCachePageUtil;
	
	@Autowired
	AppCacheMenu appCacheMenu;
	
	// Menus
	public List<MenuItem> getMenu(String menuName, String apiVersion, String appVersion, String lang) {
		List<MenuItem> menu = null;
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
		if (appCacheMenu.getMenus() != null && appCacheMenu.getMenus().containsKey(key.toString())){
			// Pull from memory
			menu = appCacheMenu.getMenus().get(key.toString());
		} else {
			// Get from DB and put in cache
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCacheMenu.getMenus() != null && appCacheMenu.getMenus().containsKey(key.toString())){
					menu = appCacheMenu.getMenus().get(key.toString());
				} else {
					menu = menuSvc.getMenu(menuName,apiVersion,appVersion,lang);
					appCacheMenu.getMenus().put(key.toString(),menu);
				}
			}
		}
		return menu;
	}
	
	public void loadMenuCache(String tenant) {
		// get available menus for this tenant
		String[] categories = AppCacheMenu.categories;
		for (int i = 0; i < categories.length; i++) {
			RestRequest request = new RestRequest();
			request.addParam(GlobalConstant.ACTIVE, true);
			request.addParam("category", categories[i]);
			RestResponse response = new RestResponse();
			menuSvc.getMenus(request,response);
			// load each menu
			List<Menu> myMenus = (List<Menu>) response.getParam(GlobalConstant.ITEMS);
			for(Menu m : myMenus) {
				// by languages
				List<String> codes = appCachePageUtil.getAvailableLanguageCodes(tenant);
				for(String lang : codes) {
					logger.info("Code: "+m.getCode()+" lang "+lang);
					List<MenuItem> menu = menuSvc.getMenu(m.getCode(),m.getApiVersion(),m.getAppVersion(),lang);
						if (menu != null) {
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
						
						appCacheMenu.getMenus().put(key.toString(),menu);
					}
				}
			}
		}
	}
	
	public void reloadMenuCache() {
		String tenant = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain();
		for(Iterator<Map.Entry<String, List<MenuItem>>> it = appCacheMenu.getMenus().entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, List<MenuItem>> entry = it.next();
		      if(entry.getKey().contains(tenant)) {
		    	  it.remove();
		      }
		}
		loadMenuCache(tenant);
	}
		
	public void clearCache(){
		appCacheMenu.clearCache();
	}
}
