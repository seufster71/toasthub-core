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
import org.toasthub.core.preference.model.PrefFormFieldValue;
import org.toasthub.core.preference.model.PrefLabelValue;
import org.toasthub.core.preference.model.PrefName;
import org.toasthub.core.preference.model.PrefOptionValue;
import org.toasthub.core.preference.model.PrefTextValue;
import org.toasthub.core.preference.repository.PrefFormFieldDao;
import org.toasthub.core.preference.repository.PrefLabelDao;
import org.toasthub.core.preference.repository.PrefOptionDao;
import org.toasthub.core.preference.repository.PrefDao;
import org.toasthub.core.preference.repository.PrefTextDao;


@Service("PrefSvc")
public class PrefSvcImpl implements PrefSvc {

	@Autowired
	@Qualifier("PrefDao")
	PrefDao prefDao;
	
	@Autowired 
	@Qualifier("PrefFormFieldDao")
	PrefFormFieldDao prefFormFieldDao;
	
	@Autowired 
	@Qualifier("PrefLabelDao")
	PrefLabelDao prefLabelDao;
	
	@Autowired
	@Qualifier("PrefTextDao")
	PrefTextDao prefTextDao;
	
	@Autowired
	@Qualifier("PrefOptionDao")
	PrefOptionDao prefOptionDao;
	
	@Autowired 
	UtilSvc utilSvc;
	
	// Pref name
	public PrefName getPrefName(Long id) {
		return prefDao.getPrefName(id);
	}

	public PrefName getPrefName(String name) {
		return prefDao.getPrefName(name);
	}

	// Form Fields
	public List<PrefFormFieldValue> getFormFields(String prefName, String lang) {
		return prefFormFieldDao.getFormFields(prefName, lang);
	}

	public Map<String, PrefFormFieldValue> getFormFieldsMap(String prefName,String lang) {
		List<PrefFormFieldValue> prefFormFields = prefFormFieldDao.getFormFields(prefName, lang);
		Map<String, PrefFormFieldValue> formFieldsMap = new HashMap<String,PrefFormFieldValue>();
		for (PrefFormFieldValue field : prefFormFields){
			formFieldsMap.put(field.getPrefFormFieldName().getName(), field);
		}
		return formFieldsMap;
	}
	
	// Labels
	public List<PrefLabelValue> getLabels(String prefName, String lang) {
		return prefLabelDao.getLabels(prefName, lang);
	}

	// Options
	public List<PrefOptionValue> getOptions(String prefName, String lang) {
		return prefOptionDao.getOptions(prefName, lang);
	}

	public Map<String, PrefOptionValue> getOptionsMap(String prefName, String lang) {
		List<PrefOptionValue> prefOptions = prefOptionDao.getOptions(prefName, lang);
		Map<String, PrefOptionValue> optionsMap = new HashMap<String, PrefOptionValue>();
		for (PrefOptionValue option : prefOptions){
			optionsMap.put(option.getName(), option);
		}
		return optionsMap;
	}

	// Texts
	public List<PrefTextValue> getTexts(String prefName, String lang) {
		return prefTextDao.getTexts(prefName, lang);
	}

	public Map<String, PrefTextValue> getTextsMap(String prefName, String lang) {
		List<PrefTextValue> prefTexts = prefTextDao.getTexts(prefName, lang);
		Map<String, PrefTextValue> textsMap = new HashMap<String,PrefTextValue>();
		for (PrefTextValue text : prefTexts){
			textsMap.put(text.getName(), text);
		}
		return textsMap;
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			//if (request.containsParam("category")) {
				prefDao.itemCount(request, response);
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
				prefDao.items(request, response);
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
			prefDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item Failed", response);
			e.printStackTrace();
		}
		
	}

}
