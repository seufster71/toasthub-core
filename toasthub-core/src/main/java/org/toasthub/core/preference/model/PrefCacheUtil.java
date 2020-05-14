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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Language;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.language.LanguageSvc;
import org.toasthub.core.preference.service.PrefSvc;
import org.toasthub.core.system.model.AppCacheClientDomains;

@Component("PrefCacheUtil")
public class PrefCacheUtil {
	
	public static final String RESPONSE = "response";
	public static final String REQUEST = "request";
	public static final String PREFPARAMLOC = "prefParamLoc";
	public static final String PREFFORMKEYS = "prefFormKeys";
	public static final String PREFFORMNAME = "prefFormName";
	public static final String PREFFORMFIELDS = "prefFormFields";
	public static final String PREFLABELNAME = "prefLabelName";
	public static final String PREFLABELKEYS = "prefLabelKeys";
	public static final String PREFLABELS = "prefLabels";
	public static final String PREFTEXTNAME = "prefTextName";
	public static final String PREFTEXTKEYS = "prefTextKeys";
	public static final String PREFTEXTS = "prefTexts";
	public static final String PREFOPTIONNAME = "prefOptionName";
	public static final String PREFOPTIONKEYS = "prefOptionKeys";
	public static final String PREFOPTIONS = "prefOptions";
	public static final String PREFGLOBAL = "prefGlobal";
	public static final String LANGUAGES = "LANGUAGES";
	
	
	@Autowired
	@Qualifier("PrefSvc")
	protected PrefSvc prefSvc;
	
	@Autowired
	@Qualifier("LanguageSvc")
	protected LanguageSvc languageSvc;
	
	@Autowired	
	protected UtilSvc utilSvc;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	@Autowired
	PrefCache prefCache;
	
	public void clearCache(){
		prefCache.setPrefFormFields(new ConcurrentHashMap<String,List<PrefFormFieldValue>>());
		prefCache.setPrefLabels(new ConcurrentHashMap<String,List<PrefLabelValue>>());
		prefCache.setPrefOptions(new ConcurrentHashMap<String,Map<String,PrefOptionValue>>());
		prefCache.setPrefTexts(new ConcurrentHashMap<String,Map<String,PrefTextValue>>());
		prefCache.setLanguages(new ConcurrentHashMap<String,List<Language>>());
	}
	
	public void clearPrefCache() {
		prefCache.setPrefFormFields(new ConcurrentHashMap<String,List<PrefFormFieldValue>>());
		prefCache.setPrefLabels(new ConcurrentHashMap<String,List<PrefLabelValue>>());
		prefCache.setPrefOptions(new ConcurrentHashMap<String,Map<String,PrefOptionValue>>());
		prefCache.setPrefTexts(new ConcurrentHashMap<String,Map<String,PrefTextValue>>());
		
	}
	
	@SuppressWarnings("unchecked")
	public void getPrefInfo(RestRequest request, RestResponse response) {
		if (request.containsParam(PREFFORMKEYS) && !request.getParam(PREFFORMKEYS).equals("") && request.getParam(PREFFORMKEYS) instanceof List){
			for (String item : (List<String>) request.getParam(PREFFORMKEYS)) {
				request.addParam(PREFFORMNAME, item);
				getPrefFormFields(request,response);
			}
		}
		if (request.containsParam(PREFLABELKEYS) && !request.getParam(PREFLABELKEYS).equals("") && request.getParam(PREFLABELKEYS) instanceof List){
			for (String item : (List<String>) request.getParam(PREFLABELKEYS)) {
				request.addParam(PREFLABELNAME, item);
				getPrefLabels(request,response);
			}
		}
		if (request.containsParam(PREFTEXTKEYS) && !request.getParam(PREFTEXTKEYS).equals("") && request.getParam(PREFTEXTKEYS) instanceof List){
			for (String item : (List<String>) request.getParam(PREFTEXTKEYS)) {
				request.addParam(PREFTEXTNAME, item);
				getPrefTexts(request,response);
			}
		}
		if (request.containsParam(PREFOPTIONKEYS) && !request.getParam(PREFOPTIONKEYS).equals("") && request.getParam(PREFOPTIONKEYS) instanceof List){
			for (String item : (List<String>) request.getParam(PREFOPTIONKEYS)) {
				request.addParam(PREFOPTIONNAME, item);
				getPrefOptions(request,response);
			}
		}
		if (request.containsParam(PREFGLOBAL) && !request.getParam(PREFGLOBAL).equals("") && request.getParam(PREFGLOBAL) instanceof List){
			for (String item : (List<String>) request.getParam(PREFGLOBAL)) {
				if ("LANGUAGES".equals(item)) {
					getLanguages(request,response);
				}
			}
		}
		
	}
	
	////////////////////////////// Pref form fields
	public void getPrefFormFields(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
		key.append("_");
		key.append(request.getParam(PREFFORMNAME));
		key.append("_");
		key.append((String)request.getParam(GlobalConstant.LANG));
		
		if (prefCache.getPrefFormFields() != null && prefCache.getPrefFormFields().containsKey(key.toString())){
			// Pull from memory cache
			prefFormFieldLoadFromMem(request,response,key.toString());
		} else {
			// Get from DB and put in cache
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (prefCache.getPrefFormFields() != null && prefCache.getPrefFormFields().containsKey(key.toString())){
					prefFormFieldLoadFromMem(request,response,key.toString());
				} else {
					prefFormFieldLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefFormFieldLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,List<PrefFormFieldValue>> f = null;
		// add to request or response
		if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
			if (response.getParams().containsKey(PREFFORMFIELDS)){
				f = (Map<String, List<PrefFormFieldValue>>) response.getParam(PREFFORMFIELDS);
			} else {
				f = new ConcurrentHashMap<String,List<PrefFormFieldValue>>();
			}
			f.put((String) request.getParam(PREFFORMNAME), prefCache.getPrefFormFields().get(key));
			response.addParam(PREFFORMFIELDS,f);
		} else {
			if (request.getParams().containsKey(PREFFORMFIELDS)){
				f = (Map<String, List<PrefFormFieldValue>>) request.getParam(PREFFORMFIELDS);
			} else {
				f = new ConcurrentHashMap<String,List<PrefFormFieldValue>>();
			}
			f.put((String) request.getParam(PREFFORMNAME), prefCache.getPrefFormFields().get(key));
			request.addParam(PREFFORMFIELDS,f);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefFormFieldLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		List<PrefFormFieldValue> formFields = prefSvc.getFormFields((String)request.getParam(PREFFORMNAME), (String)request.getParam(GlobalConstant.LANG));
		if (formFields != null){
			// add to cache
			prefCache.addPrefFormField(key, formFields);
			Map<String,List<PrefFormFieldValue>> f = null;
			// add to request or response
			if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
				if (response.getParams().containsKey(PREFFORMFIELDS)){
					f = (Map<String, List<PrefFormFieldValue>>) response.getParam(PREFFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<PrefFormFieldValue>>();
				}
				f.put((String) request.getParam(PREFFORMNAME), formFields);
				response.addParam(PREFFORMFIELDS,f);
			} else {
				if (request.getParams().containsKey(PREFFORMFIELDS)){
					f = (Map<String, List<PrefFormFieldValue>>) request.getParam(PREFFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<PrefFormFieldValue>>();
				}
				f.put((String) request.getParam(PREFFORMNAME), formFields);
				request.addParam(PREFFORMFIELDS,f);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Pref FormField issue", response);
		}
	}
	
	public void clearPrefFormFieldCache(){
		// Clear cache immediately
		prefCache.clearPrefFormFieldCache();
	}
	
	public void clearPrefFormFieldCache(String key){
		// Clear one item in cache immediately
		prefCache.clearPrefFormFieldCache(key);
	}
	
	///////////////////////////// Pref Labels
	public void getPrefLabels(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
		key.append("_");
		key.append(request.getParam(PREFLABELNAME));
		key.append("_");
		key.append((String)request.getParam(GlobalConstant.LANG));
		if (prefCache.getPrefLabels() != null && prefCache.getPrefLabels().containsKey(key.toString())){
			// Pull from memory cache
			prefLabelLoadFromMem(request,response,key.toString());
		} else {
			// Get from DB and put in cache
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (prefCache.getPrefLabels() != null && prefCache.getPrefLabels().containsKey(key.toString())){
					prefLabelLoadFromMem(request,response,key.toString());
				} else {
					prefLabelLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefLabelLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,List<PrefLabelValue>> l = null;
		// add to request or response
		if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
			if (response.getParams().containsKey(PREFLABELS)){
				l = (Map<String, List<PrefLabelValue>>) response.getParam(PREFLABELS);
			} else {
				l = new ConcurrentHashMap<String,List<PrefLabelValue>>();
			}
			l.put((String) request.getParam(PREFLABELNAME), prefCache.getPrefLabels().get(key));
			response.addParam(PREFLABELS,l);
		} else {
			if (request.getParams().containsKey(PREFLABELS)){
				l = (Map<String, List<PrefLabelValue>>) request.getParam(PREFLABELS);
			} else {
				l = new ConcurrentHashMap<String,List<PrefLabelValue>>();
			}
			l.put((String) request.getParam(PREFLABELNAME), prefCache.getPrefLabels().get(key));
			request.addParam(PREFLABELS,l);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefLabelLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		List<PrefLabelValue> labels = prefSvc.getLabels((String)request.getParam(PREFLABELNAME), (String)request.getParam(GlobalConstant.LANG));
		if (labels != null){
			// add to cache
			prefCache.addPrefLabel(key, labels);
			Map<String,List<PrefLabelValue>> l = null;
			// add to request or response
			if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
				if (response.getParams().containsKey(PREFLABELS)){
					l = (Map<String, List<PrefLabelValue>>) response.getParam(PREFLABELS);
				} else {
					l = new ConcurrentHashMap<String,List<PrefLabelValue>>();
				}
				l.put((String) request.getParam(PREFLABELNAME), labels);
				response.addParam(PREFLABELS, l);
			} else {
				if (request.getParams().containsKey(PREFLABELS)){
					l = (Map<String, List<PrefLabelValue>>) request.getParam(PREFLABELS);
				} else {
					l = new ConcurrentHashMap<String,List<PrefLabelValue>>();
				}
				l.put((String) request.getParam(PREFLABELNAME), labels);
				request.addParam(PREFLABELS, l);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Pref Label issue", response);
		}
	}
	
	public void clearPrefLabelCache(){
		// Clear cache immediately
		prefCache.clearPrefFormFieldCache();
	}

	public void clearPrefLabelCache(String key){
		// Clear one item in cache immediately
		prefCache.clearPrefFormFieldCache(key);
	}
	
	//////////////////// Pref Options
	public void getPrefOptions(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
		key.append("_");
		key.append(request.getParam(PREFOPTIONNAME));
		key.append("_");
		key.append((String)request.getParam(GlobalConstant.LANG));
		if (prefCache.getPrefOptions() != null && prefCache.getPrefOptions().containsKey(key.toString())){
			prefOptionLoadFromMem(request,response,key.toString());
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (prefCache.getPrefOptions() != null && prefCache.getPrefOptions().containsKey(key.toString())){
					prefOptionLoadFromMem(request,response,key.toString());
				} else {
					prefOptionLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefOptionLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from Memory
		Map<String,Map<String,PrefOptionValue>> o = null;
		// add to request or response
		if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
			if (response.getParams().containsKey(PREFOPTIONS)){
				o = (Map<String, Map<String,PrefOptionValue>>) response.getParam(PREFOPTIONS);
			} else {
				o = new ConcurrentHashMap<String,Map<String,PrefOptionValue>>();
			}
			o.put((String) request.getParam(PREFOPTIONNAME), prefCache.getPrefOptions().get(key));
			response.addParam(PREFOPTIONS, o);
		} else {
			if (request.getParams().containsKey(PREFOPTIONS)){
				o = (Map<String, Map<String,PrefOptionValue>>) request.getParam(PREFOPTIONS);
			} else {
				o = new ConcurrentHashMap<String,Map<String,PrefOptionValue>>();
			}
			o.put((String) request.getParam(PREFOPTIONNAME), prefCache.getPrefOptions().get(key));
			request.addParam(PREFOPTIONS,o);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefOptionLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Pull from DB
		Map<String,PrefOptionValue> prefOptions = prefSvc.getOptionsMap((String)request.getParam(PREFOPTIONNAME), (String)request.getParam(GlobalConstant.LANG));
		if (prefOptions != null){
			// add to cache
			prefCache.addPrefOption(key, prefOptions);
			Map<String,Map<String,PrefOptionValue>> o = null;
			// add to request or response
			if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
				if (response.getParams().containsKey(PREFOPTIONS)){
					o = (Map<String, Map<String,PrefOptionValue>>) response.getParam(PREFOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,PrefOptionValue>>();
				}
				o.put((String) request.getParam(PREFOPTIONNAME), prefOptions);
				response.addParam(PREFOPTIONS, o);
			} else {	
				if (request.getParams().containsKey(PREFOPTIONS)){
					o = (Map<String, Map<String,PrefOptionValue>>) request.getParam(PREFOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,PrefOptionValue>>();
				}
				o.put((String) request.getParam(PREFOPTIONNAME), prefOptions);
				request.addParam(PREFOPTIONS, o);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Pref Option issue", response);
		}
	}
	
	public void clearPrefOptionCache(){
		prefCache.clearPrefOptionCache();
	}

	public void clearPrefOptionCache(String key){
		prefCache.clearPrefOptionCache(key);
	}
	
	//////////////////// Pref Texts
	public void getPrefTexts(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
		key.append("_");
		key.append(request.getParam(PREFTEXTNAME));
		key.append("_");
		key.append((String)request.getParam(GlobalConstant.LANG));
		if (prefCache.getPrefTexts() != null && prefCache.getPrefTexts().containsKey(key.toString())){
			// Pull from memory cache
			prefTextLoadFromMem(request,response,key.toString());
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (prefCache.getPrefTexts() != null && prefCache.getPrefTexts().containsKey(key.toString())){
					// Pull from memory cache
					prefTextLoadFromMem(request,response,key.toString());
				} else {
					prefTextLoadFromDB(request,response,key.toString());
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefTextLoadFromMem(RestRequest request, RestResponse response, String key) {
		// Pull from memory cache
		Map<String,Map<String,PrefTextValue>> t = null;
		if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
			if (response.getParams().containsKey(PREFTEXTS)){
				t = (Map<String, Map<String,PrefTextValue>>) response.getParam(PREFTEXTS);
			} else {
				t = new ConcurrentHashMap<String,Map<String,PrefTextValue>>();
			}
			t.put((String) request.getParam(PREFTEXTNAME), prefCache.getPrefTexts().get(key));
			response.addParam(PREFTEXTS, t);
		} else {
			if (request.getParams().containsKey(PREFTEXTS)){
				t = (Map<String, Map<String,PrefTextValue>>) request.getParam(PREFTEXTS);
			} else {
				t = new ConcurrentHashMap<String,Map<String,PrefTextValue>>();
			}
			t.put((String) request.getParam(PREFTEXTNAME), prefCache.getPrefTexts().get(key));
			request.addParam(PREFTEXTS,t);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void prefTextLoadFromDB(RestRequest request, RestResponse response, String key) {
		// Get from DB and put in cache
		Map<String,PrefTextValue> prefTexts = prefSvc.getTextsMap((String)request.getParam(PREFTEXTNAME), (String)request.getParam(GlobalConstant.LANG));
		if (prefTexts != null){
			// add to cache
			prefCache.addPrefText(key, prefTexts);
			// add to request or response
			Map<String,Map<String,PrefTextValue>> t = null;
			if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
				if (response.getParams().containsKey(PREFTEXTS)){
					t = (Map<String, Map<String,PrefTextValue>>) response.getParam(PREFTEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,PrefTextValue>>();
				}
				t.put((String) request.getParam(PREFTEXTNAME), prefTexts);
				response.addParam(PREFTEXTS, t);
			} else {
				if (request.getParams().containsKey(PREFTEXTS)){
					t = (Map<String, Map<String,PrefTextValue>>) request.getParam(PREFTEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,PrefTextValue>>();
				}
				t.put((String) request.getParam(PREFTEXTNAME), prefTexts);
				request.addParam(PREFTEXTS, t);
			}
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Pref Text issue", response);
		}
	}
	
	public void clearPrefTextCache(){
		// Clear cache immediately
		prefCache.clearPrefTextCache();
	}

	public void clearPrefTextCache(String key){
		// Clear one item in cache immediately
		prefCache.clearPrefTextCache(key);
	}

	//////////////////////// Language 
	@SuppressWarnings("unchecked")
	public void getLanguages(RestRequest request, RestResponse response) {
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getCustDomain();
		
		if (prefCache.getLanguages() != null && prefCache.getLanguages().containsKey(key)) {
			if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
				// Pull from memory cache
				response.addParam(LANGUAGES, prefCache.getLanguages().get(key));
			} else {
				// Pull from memory cache
				request.addParam(LANGUAGES, prefCache.getLanguages().get(key));
			}
			
		} else {
			synchronized (this) {
				// this is done to catch all concurrent users during a cache reload to prevent then from all trying to reloading the cache
				// only the first shall do the reload.
				if (prefCache.getLanguages() != null && prefCache.getLanguages().containsKey(key)) {
					if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
						// Pull from memory cache
						response.addParam(LANGUAGES, prefCache.getLanguages().get(key));
					} else {
						// Pull from memory cache
						request.addParam(LANGUAGES, prefCache.getLanguages().get(key));
					}
				} else {
					// Get from DB and put in cache
					RestRequest LangRequest = new RestRequest();
					LangRequest.addParam(GlobalConstant.ACTIVE, true);
					RestResponse LangResponse = new RestResponse();
					languageSvc.getAllLanguages(LangRequest,LangResponse);
					if (LangResponse.containsParam(GlobalConstant.ITEMS)){
						this.setLanguages((List<Language>) LangResponse.getParam(GlobalConstant.ITEMS));
						if (request.containsParam(PREFPARAMLOC) && RESPONSE.equals(request.getParam(PREFPARAMLOC)) ) {
							response.addParam(LANGUAGES, prefCache.getLanguages().get(key));
						} else {
							request.addParam(LANGUAGES, prefCache.getLanguages().get(key));
						}
					} else {
						utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Languages issue", response);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadGlobalCache(String tenant) {
		String lang = "en";
		List<Language> languages = prefCache.getLanguages().get(tenant);
		if (languages != null && !languages.isEmpty()) {
			for (Language language : languages) {
				if (language.isDefaultLang()){
					lang = language.getCode();
				}
			}
		}
		List<String> texts =  new ArrayList<String>(Arrays.asList("GLOBAL_PAGE"));
		
		for (String item : texts) {
			StringBuilder key = new StringBuilder();
			key.append(tenant);
			key.append("_");
			key.append(item);
			key.append("_");
			key.append(lang);
			
			Map<String,PrefTextValue> prefTexts = prefSvc.getTextsMap(item, lang);
			prefCache.addPrefText(key.toString(), prefTexts);	
		}
		
	}
	
	public void setLanguages(List<Language> languages) {
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getCustDomain();
		if (prefCache.getLanguages() == null) {
			prefCache.setLanguages(new ConcurrentHashMap<String,List<Language>>());
		}
		prefCache.getLanguages().put(key, languages);
	}
	
	public String getDefaultLang(){
		String lang = "en";
		String tenant = TenantContext.getURLDomain();
		String key = appCacheClientDomains.getClientDomain(tenant).getCustDomain();
		List<Language> languages = prefCache.getLanguages().get(key);
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
		List<Language> languages = prefCache.getLanguages().get(tenant);
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
		LangRequest.addParam(GlobalConstant.ACTIVE, true);
		RestResponse LangResponse = new RestResponse();
		languageSvc.getAllLanguages(LangRequest,LangResponse);
		if (LangResponse.containsParam(GlobalConstant.ITEMS)){
			prefCache.getLanguages().put(tenant, (List<Language>) LangResponse.getParam(GlobalConstant.ITEMS));
		}
	}
	
	public void clearLanguageCache(){
		// Clear cache immediately
		prefCache.clearLanguageCache();
	}
	
	public String getLang(RestRequest request) {
		if (request.containsParam(GlobalConstant.LANG)) {
			return (String) request.getParam(GlobalConstant.LANG);
		} else {
			return getDefaultLang();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static PrefOptionValue getPrefOption(RestRequest request, String pageName, String valueName) {
		PrefOptionValue optionValue = null;
		if (request.containsParam(PREFOPTIONS)) {
			Map<String,Object> items = (Map<String,Object>)request.getParam(PREFOPTIONS);
			if (items != null) {
				Map<String,Object> item = (Map<String,Object>)(items).get(pageName);
				if (item != null){
					optionValue = (PrefOptionValue) (item).get(valueName);
				}
			}
		}
		return optionValue;
	}
	
	@SuppressWarnings("unchecked")
	public static PrefTextValue getPrefText(RestRequest request, String pageName, String valueName) {
		PrefTextValue textValue = null;
		if (request.containsParam(PREFTEXTS)) {
			Map<String,Object> items = (Map<String,Object>)request.getParam(PREFTEXTS);
			if (items != null) {
				Map<String,Object> item = (Map<String,Object>)(items).get(pageName);
				if (item != null){
					textValue = (PrefTextValue) (item).get(valueName);
				}
			}
		}
		return textValue;
	}
	
	@SuppressWarnings("unchecked")
	public String getPrefText(String pageName, String valueName, String lang) {
		String result = "";
		try {
			String tenant = TenantContext.getURLDomain();
			StringBuilder key = new StringBuilder();
			key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
			key.append("_");
			key.append(pageName);
			key.append("_");
			key.append(lang);
			if (prefCache.getPrefTexts() != null && prefCache.getPrefTexts().containsKey(key.toString())){
				Map<String,PrefTextValue> prefTextValues = prefCache.getPrefTexts().get(key.toString());
				result = prefTextValues.get(valueName).getValue();
			} else {
				synchronized (this) {
					if (prefCache.getPrefTexts() != null && prefCache.getPrefTexts().containsKey(key.toString())){
						Map<String,PrefTextValue> prefTextValues = prefCache.getPrefTexts().get(key.toString());
						result = prefTextValues.get(valueName).getValue();
					} else {
						Map<String,PrefTextValue> prefTextValues = prefSvc.getTextsMap(pageName, lang);
						prefCache.addPrefText(key.toString(), prefTextValues);
						result = prefTextValues.get(valueName).getValue();
					}
				}
			}
			
		} catch (Exception e) {
			// eat error 
			StringBuilder r = new StringBuilder();
			r.append("Global text option for ").append(pageName).append(" ").append(valueName).append(" missing contact admin");
			result = r.toString();
		}
		return result;
	}
	
	public PrefOptionValue getPrefOption(String pageName, String valueName, String lang) {
		PrefOptionValue result = null;
		try {
			String tenant = TenantContext.getURLDomain();
			StringBuilder key = new StringBuilder();
			key.append(appCacheClientDomains.getClientDomain(tenant).getCustDomain());
			key.append("_");
			key.append(pageName);
			key.append("_");
			key.append(lang);
			if (prefCache.getPrefOptions() != null && prefCache.getPrefOptions().containsKey(key.toString())){
				Map<String,PrefOptionValue> prefOptionValues = prefCache.getPrefOptions().get(key.toString());
				
				result = prefOptionValues.get(valueName);
			}
		} catch (Exception e) {
			// eat error
			result = null;
		}
		return result;
	}
	
	// Add 
	@SuppressWarnings("unchecked")
	public static void addPrefForm(RestRequest request, String... pageName) {
		if (!request.containsParam(PREFFORMKEYS)) {
			List<String> prefForms = new ArrayList<String>();
			request.addParam(PREFFORMKEYS, prefForms);
		}
		for (String item : pageName) {
			((List<String>) request.getParam(PREFFORMKEYS)).add(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addPrefText(RestRequest request, String... pageName) {
		if (!request.containsParam(PREFTEXTKEYS)) {
			List<String> prefTexts = new ArrayList<String>();
			request.addParam(PREFTEXTKEYS, prefTexts);
		}
		for (String item : pageName) {
			((List<String>) request.getParam(PREFTEXTKEYS)).add(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void addPrefOption(RestRequest request, String... pageName) {
		if (!request.containsParam(PREFOPTIONKEYS)) {
			List<String> prefOptions = new ArrayList<String>();
			request.addParam(PREFOPTIONKEYS, prefOptions);
		}
		for (String item : pageName) {
			((List<String>) request.getParam(PREFOPTIONKEYS)).add(item);
		}
	}
	
	
}
