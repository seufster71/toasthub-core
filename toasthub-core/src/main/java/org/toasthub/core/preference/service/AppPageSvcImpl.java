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

package org.toasthub.core.preference.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppPageFormFieldValue;
import org.toasthub.core.preference.model.AppPageLabelValue;
import org.toasthub.core.preference.model.AppPageName;
import org.toasthub.core.preference.model.AppPageOptionValue;
import org.toasthub.core.preference.model.AppPageTextValue;
import org.toasthub.core.preference.repository.AppFormFieldDao;
import org.toasthub.core.preference.repository.AppLabelDao;
import org.toasthub.core.preference.repository.AppOptionDao;
import org.toasthub.core.preference.repository.AppPageDao;
import org.toasthub.core.preference.repository.AppTextDao;


@Service("AppPageSvc")
public class AppPageSvcImpl implements AppPageSvc {

	@Autowired
	@Qualifier("AppPageDao")
	AppPageDao appPageDao;
	
	@Autowired 
	@Qualifier("AppFormFieldDao")
	AppFormFieldDao appFormFieldDao;
	
	@Autowired 
	@Qualifier("AppLabelDao")
	AppLabelDao appLabelDao;
	
	@Autowired
	@Qualifier("AppTextDao")
	AppTextDao appTextDao;
	
	@Autowired
	@Qualifier("AppOptionDao")
	AppOptionDao appOptionDao;
	
	@Autowired 
	UtilSvc utilSvc;
	
	// Page name
	public AppPageName getPageName(Long id) {
		return appPageDao.getPageName(id);
	}

	public AppPageName getPageName(String name) {
		return appPageDao.getPageName(name);
	}

	// Form Fields
	public List<AppPageFormFieldValue> getFormFields(String pageName, String lang) {
		return appFormFieldDao.getFormFields(pageName, lang);
	}

	public Map<String, AppPageFormFieldValue> getFormFieldsMap(String pageName,String lang) {
		List<AppPageFormFieldValue> pageFormFields = appFormFieldDao.getFormFields(pageName, lang);
		Map<String, AppPageFormFieldValue> formFieldsMap = new HashMap<String,AppPageFormFieldValue>();
		for (AppPageFormFieldValue field : pageFormFields){
			formFieldsMap.put(field.getPageFormFieldName().getName(), field);
		}
		return formFieldsMap;
	}
	
	// Labels
	public List<AppPageLabelValue> getLabels(String pageName, String lang) {
		return appLabelDao.getLabels(pageName, lang);
	}

	// Options
	public List<AppPageOptionValue> getOptions(String pageName, String lang) {
		return appOptionDao.getOptions(pageName, lang);
	}

	public Map<String, AppPageOptionValue> getOptionsMap(String pageName, String lang) {
		List<AppPageOptionValue> pageOptions = appOptionDao.getOptions(pageName, lang);
		Map<String, AppPageOptionValue> optionsMap = new HashMap<String, AppPageOptionValue>();
		for (AppPageOptionValue option : pageOptions){
			optionsMap.put(option.getName(), option);
		}
		return optionsMap;
	}

	// Texts
	public List<AppPageTextValue> getTexts(String pageName, String lang) {
		return appTextDao.getTexts(pageName, lang);
	}

	public Map<String, AppPageTextValue> getTextsMap(String pageName, String lang) {
		List<AppPageTextValue> pageTexts = appTextDao.getTexts(pageName, lang);
		Map<String, AppPageTextValue> textsMap = new HashMap<String,AppPageTextValue>();
		for (AppPageTextValue text : pageTexts){
			textsMap.put(text.getName(), text);
		}
		return textsMap;
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			//if (request.containsParam("category")) {
				appPageDao.itemCount(request, response);
			//} else {
			//	utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing category param", response);
			//}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
		
	}

	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			//if (request.containsParam("category")) {
				appPageDao.items(request, response);
			//} else {
			//	utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing category param", response);
			//}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			appPageDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item Failed", response);
			e.printStackTrace();
		}
		
	}

}
