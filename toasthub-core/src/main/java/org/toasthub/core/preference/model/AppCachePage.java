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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.model.Language;

@Component("AppCachePage")
@Scope("singleton")
public class AppCachePage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String,List<AppPageFormFieldValue>> appPageFormFields = new ConcurrentHashMap<String,List<AppPageFormFieldValue>>();
	private Map<String,List<AppPageLabelValue>> appPageLabels = new ConcurrentHashMap<String,List<AppPageLabelValue>>();
	private Map<String,Map<String,AppPageOptionValue>> appPageOptions = new ConcurrentHashMap<String,Map<String,AppPageOptionValue>>();
	private Map<String,Map<String,AppPageTextValue>> appPageTexts = new ConcurrentHashMap<String,Map<String,AppPageTextValue>>();
	private Map<String,List<Language>> languages = new ConcurrentHashMap<String,List<Language>>();
	
	// Constructor
	public AppCachePage(){
	}
	
	// formField
	public void setAppPageFormFields(Map<String,List<AppPageFormFieldValue>> appPageFormFields) {
		this.appPageFormFields = appPageFormFields;
	}
	
	public Map<String,List<AppPageFormFieldValue>> getAppPageFormFields() {
		return this.appPageFormFields;
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
	
	// label
	public void setAppPageLabels(Map<String,List<AppPageLabelValue>> appPageLabels) {
		this.appPageLabels = appPageLabels;
	}
	
	public Map<String,List<AppPageLabelValue>> getAppPageLabels() {
		return this.appPageLabels;
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
	
	// appPageOptions
	public void setAppPageOptions(Map<String,Map<String,AppPageOptionValue>> appPageOptions) {
		this.appPageOptions = appPageOptions;
	}
	
	public Map<String,Map<String,AppPageOptionValue>> getAppPageOptions() {
		return this.appPageOptions;
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
	
	// Page Text
	public void setAppPageTexts(Map<String,Map<String,AppPageTextValue>> appPageTexts) {
		this.appPageTexts = appPageTexts;
	}
	
	public Map<String,Map<String,AppPageTextValue>> getAppPageTexts() {
		return this.appPageTexts;
	}
	
	public void addAppPageText(String key, Map<String,AppPageTextValue> appPageTexts){
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
	
	// language
	public void setLanguages(Map<String,List<Language>> languages) {
		this.languages = languages;
	}
	
	public Map<String,List<Language>> getLanguages() {
		return this.languages;
	}
	
	public void clearLanguageCache(){
		// Clear cache immediately
		this.languages = new ConcurrentHashMap<String,List<Language>>();
	}
}
