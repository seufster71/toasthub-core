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

import org.toasthub.core.general.model.RestRequest;

public class AppCachePageUtil {
	
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
