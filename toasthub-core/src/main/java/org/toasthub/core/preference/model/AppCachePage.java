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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
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

@Component("AppCachePage")
@Scope("singleton")
public class AppCachePage implements Serializable {

	private static final long serialVersionUID = 1L;
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
	
	private Map<String,List<AppPageFormFieldValue>> appPageFormFields = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
	private Map<String,List<AppPageLabelValue>> appPageLabels = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
	private Map<String,Map<String,AppPageOptionValue>> appPageOptions = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
	private Map<String,Map<String,AppPageTextValue>> appPageTexts = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
	private Map<String,List<Language>> languages = new ConcurrentHashMap<String,List<Language>>();
	
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
	
	// Constructor
	public AppCachePage(){
	}
	
	public void clearCache(){
		this.setAppPageFormFields(new ConcurrentHashMap<String,List<AppPageFormFieldValue>>());
		this.setAppPageLabels(new ConcurrentHashMap<String,List<AppPageLabelValue>>());
		this.setAppPageOptions(new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>());
		this.setAppPageTexts(new ConcurrentHashMap<String,Map<String,AppPageTextValue>>());
		this.setLanguages(new ArrayList<Language>());
	}
	
	public void clearAppCache() {
		this.setAppPageFormFields(new ConcurrentHashMap<String,List<AppPageFormFieldValue>>());
		this.setAppPageLabels(new ConcurrentHashMap<String,List<AppPageLabelValue>>());
		this.setAppPageOptions(new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>());
		this.setAppPageTexts(new ConcurrentHashMap<String,Map<String,AppPageTextValue>>());
		
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
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGEFORMNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		
		if (this.appPageFormFields != null && this.appPageFormFields.containsKey(key.toString())){
			// Pull from memory cache
			Map<String,List<AppPageFormFieldValue>> f = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGEFORMFIELDS)){
					f = (Map<String, List<AppPageFormFieldValue>>) response.getParam(APPPAGEFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
				}
				f.put((String) request.getParam(APPPAGEFORMNAME), this.appPageFormFields.get(key.toString()));
				response.addParam(APPPAGEFORMFIELDS,f);
			} else {
				if (request.getParams().containsKey(APPPAGEFORMFIELDS)){
					f = (Map<String, List<AppPageFormFieldValue>>) request.getParam(APPPAGEFORMFIELDS);
				} else {
					f = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
				}
				f.put((String) request.getParam(APPPAGEFORMNAME), this.appPageFormFields.get(key.toString()));
				request.addParam(APPPAGEFORMFIELDS,f);
			}
		} else {
			// Get from DB and put in cache
			List<AppPageFormFieldValue> formFields = appPageSvc.getFormFields((String)request.getParam(APPPAGEFORMNAME), (String)request.getParam(BaseEntity.LANG));
			if (formFields != null){
				// add to cache
				this.addAppPageFormField(key.toString(), formFields);
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
	}
	
	
	protected void setAppPageFormFields(Map<String,List<AppPageFormFieldValue>> appPageFormFields) {
		this.appPageFormFields = appPageFormFields;
	}

	public void addAppPageFormField(String key,List<AppPageFormFieldValue> appPageFormFields ){
		if (this.appPageFormFields != null){
			this.appPageFormFields.put(key, appPageFormFields);
		}
	}
	
	public void clearAppPageFormFieldCache(){
		// Clear cache immediately
		this.setAppPageFormFields(new ConcurrentHashMap<String,List<AppPageFormFieldValue>>());
	}
	
	public void clearAppPageFormFieldCache(String key){
		// Clear one item in cache immediately
		this.appPageFormFields.remove(key);
	}
	
	///////////////////////////// App Page Labels
	public void getAppPageLabels(RestRequest request, RestResponse response) {
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGELABELNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		//String key = request.getParam(APPPAGELABELNAME)+"_"+(String)request.getParam(BaseEntity.LANG);
		if (this.appPageLabels != null && this.appPageLabels.containsKey(key.toString())){
			// Pull from memory cache
			Map<String,List<AppPageLabelValue>> l = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGELABELS)){
					l = (Map<String, List<AppPageLabelValue>>) response.getParam(APPPAGELABELS);
				} else {
					l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
				}
				l.put((String) request.getParam(APPPAGELABELNAME), this.appPageLabels.get(key.toString()));
				response.addParam(APPPAGELABELS,l);
			} else {
				if (request.getParams().containsKey(APPPAGELABELS)){
					l = (Map<String, List<AppPageLabelValue>>) request.getParam(APPPAGELABELS);
				} else {
					l = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
				}
				l.put((String) request.getParam(APPPAGELABELNAME), this.appPageLabels.get(key.toString()));
				request.addParam(APPPAGELABELS,l);
			}
		} else {
			// Get from DB and put in cache
			List<AppPageLabelValue> labels = appPageSvc.getLabels((String)request.getParam(APPPAGELABELNAME), (String)request.getParam(BaseEntity.LANG));
			if (labels != null){
				// add to cache
				this.addAppPageLabel(key.toString(), labels);
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
	}
	
	protected void setAppPageLabels(Map<String,List<AppPageLabelValue>> appPageLabels) {
		this.appPageLabels = appPageLabels;
	}
	
	public void addAppPageLabel(String key, List<AppPageLabelValue> appPageLabels){
		if (this.appPageLabels != null){
			this.appPageLabels.put(key, appPageLabels);
		}
	}
	
	public void clearAppPageLabelCache(){
		// Clear cache immediately
		this.setAppPageLabels(new ConcurrentHashMap<String,List<AppPageLabelValue>>());
	}

	public void clearAppPageLabelCache(String key){
		// Clear one item in cache immediately
		this.appPageLabels.remove(key);
	}
	
	//////////////////// App Page Options
	public void getAppPageOptions(RestRequest request, RestResponse response) {
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGEOPTIONNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		//String key = request.getParam(APPPAGEOPTIONNAME)+"_"+(String)request.getParam(BaseEntity.LANG);
		if (this.appPageOptions != null && this.appPageOptions.containsKey(key.toString())){
			// Pull from Memory
			Map<String,Map<String,AppPageOptionValue>> o = null;
			// add to request or response
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGEOPTIONS)){
					o = (Map<String, Map<String,AppPageOptionValue>>) response.getParam(APPPAGEOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
				}
				o.put((String) request.getParam(APPPAGEOPTIONNAME), this.appPageOptions.get(key.toString()));
				response.addParam(APPPAGEOPTIONS, o);
			} else {
				if (request.getParams().containsKey(APPPAGEOPTIONS)){
					o = (Map<String, Map<String,AppPageOptionValue>>) request.getParam(APPPAGEOPTIONS);
				} else {
					o = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
				}
				o.put((String) request.getParam(APPPAGEOPTIONNAME), this.appPageOptions.get(key.toString()));
				request.addParam(APPPAGEOPTIONS,o);
			}
		} else {
			// Pull from DB
			Map<String,AppPageOptionValue> appOptions = appPageSvc.getOptionsMap((String)request.getParam(APPPAGEOPTIONNAME), (String)request.getParam(BaseEntity.LANG));
			if (appOptions != null){
				// add to cache
				this.addAppPageOption(key.toString(), appOptions);
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
	}
	
	protected void setAppPageOptions(Map<String,Map<String,AppPageOptionValue>> appPageOptions) {
		this.appPageOptions = appPageOptions;
	}
	
	public void addAppPageOption(String key, Map<String,AppPageOptionValue> appPageOptions){
		if (this.appPageOptions != null){
			this.appPageOptions.put(key, appPageOptions);
		}
	}

	public void clearAppPageOptionCache(){
		this.setAppPageOptions(new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>());
	}

	public void clearAppPageOptionCache(String key){
		this.appPageOptions.remove(key);
	}
	
	//////////////////// App Page Texts
	public void getAppPageTexts(RestRequest request, RestResponse response) {
		StringBuilder key = new StringBuilder();
		key.append(appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain());
		key.append("_");
		key.append(request.getParam(APPPAGETEXTNAME));
		key.append("_");
		key.append((String)request.getParam(BaseEntity.LANG));
		//String key = request.getParam(APPPAGETEXTNAME)+"_"+(String)request.getParam(BaseEntity.LANG);
		if (this.appPageTexts != null && this.appPageTexts.containsKey(key.toString())){
			// Pull from memory cache
			Map<String,Map<String,AppPageTextValue>> t = null;
			if (request.containsParam(APPPAGEPARAMLOC) && RESPONSE.equals(request.getParam(APPPAGEPARAMLOC)) ) {
				if (response.getParams().containsKey(APPPAGETEXTS)){
					t = (Map<String, Map<String,AppPageTextValue>>) response.getParam(APPPAGETEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
				}
				t.put((String) request.getParam(APPPAGETEXTNAME), this.appPageTexts.get(key.toString()));
				response.addParam(APPPAGETEXTS, t);
			} else {
				if (request.getParams().containsKey(APPPAGETEXTS)){
					t = (Map<String, Map<String,AppPageTextValue>>) request.getParam(APPPAGETEXTS);
				} else {
					t = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
				}
				t.put((String) request.getParam(APPPAGETEXTNAME), this.appPageTexts.get(key.toString()));
				request.addParam(APPPAGETEXTS,t);
			}
		} else {
			// Get from DB and put in cache
			Map<String,AppPageTextValue> appTexts = appPageSvc.getTextsMap((String)request.getParam(APPPAGETEXTNAME), (String)request.getParam(BaseEntity.LANG));
			if (appTexts != null){
				// add to cache
				this.addAppPageText(key.toString(), appTexts);
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
	}
	
	protected void setAppPageTexts(Map<String,Map<String,AppPageTextValue>> appPageTexts) {
		this.appPageTexts = appPageTexts;
	}
	
	protected void addAppPageText(String key, Map<String,AppPageTextValue> appPageTexts){
		if (this.appPageTexts != null){
			this.appPageTexts.put(key, appPageTexts);
		}
	}

	public void clearAppPageTextCache(){
		// Clear cache immediately
		this.setAppPageTexts(new ConcurrentHashMap<String,Map<String,AppPageTextValue>>());
	}

	public void clearAppPageTextCache(String key){
		// Clear one item in cache immediately
		this.appPageTexts.remove(key);
	}

	//////////////////////// Language 
	public void getLanguages(RestRequest request, RestResponse response) {
		String key = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain();
		if (this.languages != null && this.languages.containsKey(key)) {
			// Pull from memory cache
			response.addParam("languages", this.languages.get(key));
		} else {
			// Get from DB and put in cache
			RestRequest LangRequest = new RestRequest();
			LangRequest.addParam(BaseEntity.ACTIVE, true);
			RestResponse LangResponse = new RestResponse();
			languageSvc.getAllLanguages(LangRequest,LangResponse);
			if (LangResponse.containsParam(BaseEntity.ITEMS)){
				this.setLanguages((List<Language>) LangResponse.getParam(BaseEntity.ITEMS));
				response.addParam("languages", this.languages.get(key));
			} else {
				utilSvc.addStatus(RestResponse.INFO, RestResponse.PAGEOPTIONS, "Languages issue", response);
			}
		}
	}

	public void setLanguages(List<Language> languages) {
		String key = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain();
		if (this.languages == null) {
			this.languages = new ConcurrentHashMap<String,List<Language>>();
		}
		this.languages.put(key, languages);
	}
	
	public void clearLanguageCache(){
		// Clear cache immediately
		this.languages = new ConcurrentHashMap<String,List<Language>>();
	}
	
	public String getDefaultLang(){
		String lang = "en";
		String key = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getAPPDomain();
		List<Language> languages = this.languages.get(key);
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
		List<Language> languages = this.languages.get(tenant);
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
			this.languages.put(tenant, (List<Language>) LangResponse.getParam(BaseEntity.ITEMS));
		}
	}
}
