/*
 * Copyright (C) 2016 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.toasthub.core.preference.model;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Language;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.service.LanguageSvc;
import org.toasthub.core.general.service.UtilSvc;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.preference.service.AppPageSvc;
import org.toasthub.core.system.model.AppCacheClientDomains;

@Component("AppCachePageUtil")
public class AppCachePageUtil {
	
	public static final String RESPONSE = "response";
	public static final String REQUEST = "request";
	public static final String APPPAGEPARAMLOC = "appPageParamLoc";
	public static final String APPFORMS = "appForms";
	public static final String APPPAGEFORMNAME = "appPageFormName";
	public static final String APPPAGEFORMFIELDS = "appPageFormFields";
	public static final String APPLABELS = "appLabels";
	public static final String APPPAGELABELNAME = "appPageLabelName";
	public static final String APPPAGELABELS = "appPageLabels";
	public static final String APPTEXTS = "appTexts";
	public static final String APPPAGETEXTNAME = "appPageTextName";
	public static final String APPPAGETEXTS = "appPageTexts";
	public static final String APPOPTIONS = "appOptions";
	public static final String APPPAGEOPTIONNAME = "appPageOptionName";
	public static final String APPPAGEOPTIONS = "appPageOptions";
	
	@Autowired
	@Qualifier("AppPageSvc")
	protected AppPageSvc appPageSvc;
	
	@Autowired
	@Qualifier("LanguageSvc")
	protected LanguageSvc languageSvc;
	
	@Autowired	
	protected UtilSvc utilSvc;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	@Autowired
	AppCachePage appCachePage;
	
	public void clearCache(){
		appCachePage.setAppPageFormFields(new ConcurrentHashMap<String,List<AppPageFormFieldValue>>());
		appCachePage.setAppPageLabels(new ConcurrentHashMap<String,List<AppPageLabelValue>>());
		appCachePage.setAppPageOptions(new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>());
		appCachePage.setAppPageTexts(new ConcurrentHashMap<String,Map<String,AppPageTextValue>>());
		appCachePage.setLanguages(new ConcurrentHashMap<String,List<Language>>());
	}
	
	public void clearAppCache() {
		appCachePage.setAppPageFormFields(new ConcurrentHashMap<String,List<AppPageFormFieldValue>>());
		appCachePage.setAppPageLabels(new ConcurrentHashMap<String,List<AppPageLabelValue>>());
		appCachePage.setAppPageOptions(new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>());
		appCachePage.setAppPageTexts(new ConcurrentHashMap<String,Map<String,AppPageTextValue>>());
		
	}
	
	public void getPageInfo(RestRequest request, RestResponse response) {
		if (request.containsParam(APPFORMS) && !request.getParam(APPFORMS).equals("") && request.getParam(APPFORMS) instanceof List){
			for (String item : (List<String>) request.getParam(APPFORMS)) {
				request.addParam(APPPAGEFORMNAME, item);
				getAppPageFormFields(request,response);
			}
		}
		if (request.containsParam(APPLABELS) && !request.getParam(APPLABELS).equals("") && request.getParam(APPLABELS) instanceof List){
			for (String item : (List<String>) request.getParam(APPLABELS)) {
				request.addParam(APPPAGELABELNAME, item);
				getAppPageLabels(request,response);
			}
		}
		if (request.containsParam(APPTEXTS) && !request.getParam(APPTEXTS).equals("") && request.getParam(APPTEXTS) instanceof List){
			for (String item : (List<String>) request.getParam(APPTEXTS)) {
				request.addParam(APPPAGETEXTNAME, item);
				getAppPageTexts(request,response);
			}
		}
		if (request.containsParam(APPOPTIONS) && !request.getParam(APPOPTIONS).equals("") && request.getParam(APPOPTIONS) instanceof List){
			for (String item : (List<String>) request.getParam(APPOPTIONS)) {
				request.addParam(APPPAGEOPTIONNAME, item);
				getAppPageOptions(request,response);
			}
		}
		if (request.containsParam("appLanguages") && !request.getParam("appLanguages").equals("") && (Boolean) request.getParam("appLanguages")){
			getLanguages(request,response);
		}
		
	}
	
	////////////////////////////// App Page form fields
	public void getAppPageFormFields(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGEFORMNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		
		if (appCachePage.getAppPageFormFields() != null && appCachePage.getAppPageFormFields().containsKey(key.toString())){
			// Pull from memory cache
			appPageFormFieldLoadFromMem(request,response,key.toString());
		} else {
			// Get from DB and put in cache
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCachePage.getAppPageFormFields() != null && appCachePage.getAppPageFormFields().containsKey(key.toString())){
					appPageFormFieldLoadFromMem(request,response,key.toString());
				} else {
					appPageFormFieldLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	private void appPageFormFieldLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,List<AppPageFormFieldValue>> f = null;
		// add to request or response
		if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
			if (response.getParams().containsKey(APPPAGEFORMFIELDS)){
				f = (Map<String, List<AppPageFormFieldValue>>) response.getParam(APPPAGEFORMFIELDS);
			} else {
				f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
			}
			f.put((String) request.getParam(APPPAGEFORMNAME), appCachePage.getAppPageFormFields().get(key));
			response.addParam(APPPAGEFORMFIELDS,f);
		} else {
			if (request.getParams().containsKey(APPPAGEFORMFIELDS)){
				f = (Map<String, List<AppPageFormFieldValue>>) request.getParam(APPPAGEFORMFIELDS);
			} else {
				f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
			}
			f.put((String) request.getParam(APPPAGEFORMNAME), appCachePage.getAppPageFormFields().get(key));
			request.addParam(APPPAGEFORMFIELDS,f);
		}
	}
	
	private void appPageFormFieldLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		List<AppPageFormFieldValue> formFields = appPageSvc.getFormFields((String)request.getParam(APPPAGEFORMNAME), (String)request.getParam(BaseEntity.LANG));
		if (formFields != null){
			// add to cache
			appCachePage.addAppPageFormField(key, formFields);
			Map<String,List<AppPageFormFieldValue>> f = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGEFORMFIELDS)){
					f = (Map<String, List<AppPageFormFieldValue>>) response.getParam(APPPAGEFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
				}
				f.put((String) request.getParam(APPPAGEFORMNAME), formFields);
				response.addParam(APPPAGEFORMFIELDS,f);
			} else {
				if (request.getParams().containsKey(APPPAGEFORMFIELDS)){
					f = (Map<String, List<AppPageFormFieldValue>>) request.getParam(APPPAGEFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
				}
				f.put((String) request.getParam(APPPAGEFORMNAME), formFields);
				request.addParam(APPPAGEFORMFIELDS,f);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "App Page FormField issue", response);
		}
	}
	
	public void clearAppPageFormFieldCache(){
		// Clear cache immediately
		appCachePage.clearAppPageFormFieldCache();
	}
	
	public void clearAppPageFormFieldCache(String key){
		// Clear one item in cache immediately
		appCachePage.clearAppPageFormFieldCache(key);
	}
	
	///////////////////////////// App Page Labels
	public void getAppPageLabels(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGELABELNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		if (appCachePage.getAppPageLabels() != null && appCachePage.getAppPageLabels().containsKey(key.toString())){
			// Pull from memory cache
			appPageLabelLoadFromMem(request,response,key.toString());
		} else {
			// Get from DB and put in cache
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCachePage.getAppPageLabels() != null && appCachePage.getAppPageLabels().containsKey(key.toString())){
					appPageLabelLoadFromMem(request,response,key.toString());
				} else {
					appPageLabelLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	private void appPageLabelLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,List<AppPageLabelValue>> l = null;
		// add to request or response
		if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
			if (response.getParams().containsKey(APPPAGELABELS)){
				l = (Map<String, List<AppPageLabelValue>>) response.getParam(APPPAGELABELS);
			} else {
				l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
			}
			l.put((String) request.getParam(APPPAGELABELNAME), appCachePage.getAppPageLabels().get(key));
			response.addParam(APPPAGELABELS,l);
		} else {
			if (request.getParams().containsKey(APPPAGELABELS)){
				l = (Map<String, List<AppPageLabelValue>>) request.getParam(APPPAGELABELS);
			} else {
				l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
			}
			l.put((String) request.getParam(APPPAGELABELNAME), appCachePage.getAppPageLabels().get(key));
			request.addParam(APPPAGELABELS,l);
		}
	}
	
	private void appPageLabelLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		List<AppPageLabelValue> labels = appPageSvc.getLabels((String)request.getParam(APPPAGELABELNAME), (String)request.getParam(BaseEntity.LANG));
		if (labels != null){
			// add to cache
			appCachePage.addAppPageLabel(key, labels);
			Map<String,List<AppPageLabelValue>> l = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGELABELS)){
					l = (Map<String, List<AppPageLabelValue>>) response.getParam(APPPAGELABELS);
				} else {
					l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
				}
				l.put((String) request.getParam(APPPAGELABELNAME), labels);
				response.addParam(APPPAGELABELS, l);
			} else {
				if (request.getParams().containsKey(APPPAGELABELS)){
					l = (Map<String, List<AppPageLabelValue>>) request.getParam(APPPAGELABELS);
				} else {
					l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
				}
				l.put((String) request.getParam(APPPAGELABELNAME), labels);
				request.addParam(APPPAGELABELS, l);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "App Page Label issue", response);
		}
	}
	
	public void clearAppPageLabelCache(){
		// Clear cache immediately
		appCachePage.clearAppPageFormFieldCache();
	}

	public void clearAppPageLabelCache(String key){
		// Clear one item in cache immediately
		appCachePage.clearAppPageFormFieldCache(key);
	}
	
	//////////////////// App Page Options
	public void getAppPageOptions(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGEOPTIONNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		if (appCachePage.getAppPageOptions() != null && appCachePage.getAppPageOptions().containsKey(key.toString())){
			appPageOptionLoadFromMem(request,response,key.toString());
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCachePage.getAppPageOptions() != null && appCachePage.getAppPageOptions().containsKey(key.toString())){
					appPageOptionLoadFromMem(request,response,key.toString());
				} else {
					appPageOptionLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	private void appPageOptionLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,Map<String,AppPageOptionValue>> o = null;
		// add to request or response
		if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
			if (response.getParams().containsKey(APPPAGEOPTIONS)){
				o = (Map<String, Map<String,AppPageOptionValue>>) response.getParam(APPPAGEOPTIONS);
			} else {
				o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
			}
			o.put((String) request.getParam(APPPAGEOPTIONNAME), appCachePage.getAppPageOptions().get(key));
			response.addParam(APPPAGEOPTIONS, o);
		} else {
			if (request.getParams().containsKey(APPPAGEOPTIONS)){
				o = (Map<String, Map<String,AppPageOptionValue>>) request.getParam(APPPAGEOPTIONS);
			} else {
				o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
			}
			o.put((String) request.getParam(APPPAGEOPTIONNAME), appCachePage.getAppPageOptions().get(key));
			request.addParam(APPPAGEOPTIONS,o);
		}
	}
	
	private void appPageOptionLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		Map<String,AppPageOptionValue> appOptions = appPageSvc.getOptionsMap((String)request.getParam(APPPAGEOPTIONNAME), (String)request.getParam(BaseEntity.LANG));
		if (appOptions != null){
			// add to cache
			appCachePage.addAppPageOption(key, appOptions);
			Map<String,Map<String,AppPageOptionValue>> o = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGEOPTIONS)){
					o = (Map<String, Map<String,AppPageOptionValue>>) response.getParam(APPPAGEOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
				}
				o.put((String) request.getParam(APPPAGEOPTIONNAME), appOptions);
				response.addParam(APPPAGEOPTIONS, o);
			} else {	
				if (request.getParams().containsKey(APPPAGEOPTIONS)){
					o = (Map<String, Map<String,AppPageOptionValue>>) request.getParam(APPPAGEOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
				}
				o.put((String) request.getParam(APPPAGEOPTIONNAME), appOptions);
				request.addParam(APPPAGEOPTIONS, o);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Page Option issue", response);
		}
	}
	
	public void clearAppPageOptionCache(){
		appCachePage.clearAppPageOptionCache();
	}

	public void clearAppPageOptionCache(String key){
		appCachePage.clearAppPageOptionCache(key);
	}
	
	//////////////////// App Page Texts
	public void getAppPageTexts(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGETEXTNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		if (appCachePage.getAppPageTexts() != null && appCachePage.getAppPageTexts().containsKey(key.toString())){
			// Pull from memory cache
			appPageTextLoadFromMem(request,response,key.toString());
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCachePage.getAppPageTexts() != null && appCachePage.getAppPageTexts().containsKey(key.toString())){
					// Pull from memory cache
					appPageTextLoadFromMem(request,response,key.toString());
				} else {
					appPageTextLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	private void appPageTextLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from memory cache
		Map<String,Map<String,AppPageTextValue>> t = null;
		if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
			if (response.getParams().containsKey(APPPAGETEXTS)){
				t = (Map<String, Map<String,AppPageTextValue>>) response.getParam(APPPAGETEXTS);
			} else {
				t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
			}
			t.put((String) request.getParam(APPPAGETEXTNAME), appCachePage.getAppPageTexts().get(key));
			response.addParam(APPPAGETEXTS, t);
		} else {
			if (request.getParams().containsKey(APPPAGETEXTS)){
				t = (Map<String, Map<String,AppPageTextValue>>) request.getParam(APPPAGETEXTS);
			} else {
				t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
			}
			t.put((String) request.getParam(APPPAGETEXTNAME), appCachePage.getAppPageTexts().get(key));
			request.addParam(APPPAGETEXTS,t);
		}
	}
	
	private void appPageTextLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Get from DB and put in cache
		Map<String,AppPageTextValue> appTexts = appPageSvc.getTextsMap((String)request.getParam(APPPAGETEXTNAME), (String)request.getParam(BaseEntity.LANG));
		if (appTexts != null){
			// add to cache
			appCachePage.addAppPageText(key, appTexts);
			// add to request or response
			Map<String,Map<String,AppPageTextValue>> t = null;
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGETEXTS)){
					t = (Map<String, Map<String,AppPageTextValue>>) response.getParam(APPPAGETEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
				}
				t.put((String) request.getParam(APPPAGETEXTNAME), appTexts);
				response.addParam(APPPAGETEXTS, t);
			} else {
				if (request.getParams().containsKey(APPPAGETEXTS)){
					t = (Map<String, Map<String,AppPageTextValue>>) request.getParam(APPPAGETEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
				}
				t.put((String) request.getParam(APPPAGETEXTNAME), appTexts);
				request.addParam(APPPAGETEXTS, t);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Page Text issue", response);
		}
	}
	
	public void clearAppPageTextCache(){
		// Clear cache immediately
		appCachePage.clearAppPageTextCache();
	}

	public void clearAppPageTextCache(String key){
		// Clear one item in cache immediately
		appCachePage.clearAppPageTextCache(key);
	}

	//////////////////////// Language 
	public void getLanguages(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getAPPDomain();
		if (appCachePage.getLanguages() != null && appCachePage.getLanguages().containsKey(key)) {
			// Pull from memory cache
			response.addParam("languages", appCachePage.getLanguages().get(key));
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (appCachePage.getLanguages() != null && appCachePage.getLanguages().containsKey(key)) {
					// Pull from memory cache
					response.addParam("languages", appCachePage.getLanguages().get(key));
				} else {
					// Get from DB and put in cache
					RestRequest LangRequest = new RestRequest();
					LangRequest.addParam(BaseEntity.ACTIVE, true);
					RestResponse LangResponse = new RestResponse();
					languageSvc.getAllLanguages(LangRequest,LangResponse);
					if (LangResponse.containsParam(BaseEntity.ITEMS)){
						this.setLanguages((List<Language>) LangResponse.getParam(BaseEntity.ITEMS));
						response.addParam("languages", appCachePage.getLanguages().get(key));
					} else {
						utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Languages issue", response);
					}
				}
			}
		}
	}

	public void setLanguages(List<Language> languages) {
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getAPPDomain();
		if (appCachePage.getLanguages() == null) {
			appCachePage.setLanguages(new ConcurrentHashMap<String,List<Language>>());
		}
		appCachePage.getLanguages().put(key, languages);
	}
	
	public String getDefaultLang(){
		String lang = "en";
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getAPPDomain();
		List<Language> languages = appCachePage.getLanguages().get(key);
		if (languages != null && !languages.isEmpty()) {
			for (Language language : languages) {
				if (language.isDefaultLang()){
					lang = language.getCode();
				}
			}
		}
		return lang;
	}
	
	public List<String> getAvailableLanguageCodes(String tenant) {
		List<String> codes = new ArrayList<String>();
		List<Language> languages = appCachePage.getLanguages().get(tenant);
		if (languages != null && !languages.isEmpty()) {
			for (Language language : languages) {
				codes.add(language.getCode());
			}
		} else {
			codes.add("en");
		}
		
		return codes;
	}
	
	@SuppressWarnings("unchecked")
	public void loadLanguageCache(String tenant) {
		RestRequest LangRequest = new RestRequest();
		LangRequest.addParam(BaseEntity.ACTIVE, true);
		RestResponse LangResponse = new RestResponse();
		languageSvc.getAllLanguages(LangRequest,LangResponse);
		if (LangResponse.containsParam(BaseEntity.ITEMS)){
			appCachePage.getLanguages().put(tenant, (List<Language>) LangResponse.getParam(BaseEntity.ITEMS));
		}
	}
	
	public void clearLanguageCache(){
		// Clear cache immediately
		appCachePage.clearLanguageCache();
	}
	
	
	public static AppPageOptionValue getAppOption(RestRequest request, String pageName, String valueName) {
		AppPageOptionValue optionValue = null;
		if (request.containsParam("appPageOptions")) {
			Map<String,Object> items = (Map<String,Object>)request.getParam("appPageOptions");
			if (items != null) {
				Map<String,Object> item = (Map<String,Object>)(items).get(pageName);
				if (item != null){
					optionValue = (AppPageOptionValue) (item).get(valueName);
				}
			}
		}
		return optionValue;
	}
	
	public static AppPageTextValue getAppText(RestRequest request, String pageName, String valueName) {
		AppPageTextValue textValue = null;
		if (request.containsParam("appPageTexts")) {
			Map<String,Object> items = (Map<String,Object>)request.getParam("appPageTexts");
			if (items != null) {
				Map<String,Object> item = (Map<String,Object>)(items).get(pageName);
				if (item != null){
					textValue = (AppPageTextValue) (item).get(valueName);
				}
			}
		}
		return textValue;
	}
	
	// Add 
	public static void addAppForm(RestRequest request, String... pageName) {
		if (!request.containsParam("appForms")) {
			List<String> appForms = new ArrayList<String>();
			request.addParam("appForms", appForms);
		}
		for (String item : pageName) {
			((List<String>) request.getParam("appForms")).add(item);
		}
	}
	
	public static void addAppText(RestRequest request, String... pageName) {
		if (!request.containsParam("appTexts")) {
			List<String> appTexts = new ArrayList<String>();
			request.addParam("appTexts", appTexts);
		}
		for (String item : pageName) {
			((List<String>) request.getParam("appTexts")).add(item);
		}
	}
	
	public static void addAppOption(RestRequest request, String... pageName) {
		if (!request.containsParam("appOptions")) {
			List<String> appOptions = new ArrayList<String>();
			request.addParam("appOptions", appOptions);
		}
		for (String item : pageName) {
			((List<String>) request.getParam("appOptions")).add(item);
		}
	}
	
}
