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

@Component("PrefCache")
@Scope("singleton")
public class PrefCache implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String,List<PrefFormFieldValue>> prefFormFields = new ConcurrentHashMap<String,List<PrefFormFieldValue>>();
	private Map<String,List<PrefLabelValue>> prefLabels = new ConcurrentHashMap<String,List<PrefLabelValue>>();
	private Map<String,Map<String,PrefOptionValue>> prefOptions = new ConcurrentHashMap<String,Map<String,PrefOptionValue>>();
	private Map<String,Map<String,PrefTextValue>> prefTexts = new ConcurrentHashMap<String,Map<String,PrefTextValue>>();
	private Map<String,List<Language>> languages = new ConcurrentHashMap<String,List<Language>>();
	
	// Constructor
	public PrefCache(){
	}
	
	// formField
	public void setPrefFormFields(Map<String,List<PrefFormFieldValue>> prefFormFields) {
		this.prefFormFields = prefFormFields;
	}
	
	public Map<String,List<PrefFormFieldValue>> getPrefFormFields() {
		return this.prefFormFields;
	}

	public void addPrefFormField(String key,List<PrefFormFieldValue> prefFormFields ){
		if (this.prefFormFields != null){
			this.prefFormFields.put(key, prefFormFields);
		}
	}
	
	public void clearPrefFormFieldCache(){
		// Clear cache immediately
		this.setPrefFormFields(new ConcurrentHashMap<String,List<PrefFormFieldValue>>());
	}
	
	public void clearPrefFormFieldCache(String key){
		// Clear one item in cache immediately
		this.prefFormFields.remove(key);
	}
	
	// label
	public void setPrefLabels(Map<String,List<PrefLabelValue>> prefLabels) {
		this.prefLabels = prefLabels;
	}
	
	public Map<String,List<PrefLabelValue>> getPrefLabels() {
		return this.prefLabels;
	}
	
	public void addPrefLabel(String key, List<PrefLabelValue> prefLabels){
		if (this.prefLabels != null){
			this.prefLabels.put(key, prefLabels);
		}
	}
	
	public void clearPrefLabelCache(){
		// Clear cache immediately
		this.setPrefLabels(new ConcurrentHashMap<String,List<PrefLabelValue>>());
	}

	public void clearPrefLabelCache(String key){
		// Clear one item in cache immediately
		this.prefLabels.remove(key);
	}
	
	// prefOptions
	public void setPrefOptions(Map<String,Map<String,PrefOptionValue>> prefOptions) {
		this.prefOptions = prefOptions;
	}
	
	public Map<String,Map<String,PrefOptionValue>> getPrefOptions() {
		return this.prefOptions;
	}
	
	public void addPrefOption(String key, Map<String,PrefOptionValue> prefOptions){
		if (this.prefOptions != null){
			this.prefOptions.put(key, prefOptions);
		}
	}

	public void clearPrefOptionCache(){
		this.setPrefOptions(new ConcurrentHashMap<String,Map<String,PrefOptionValue>>());
	}

	public void clearPrefOptionCache(String key){
		this.prefOptions.remove(key);
	}
	
	// Pref Text
	public void setPrefTexts(Map<String,Map<String,PrefTextValue>> prefTexts) {
		this.prefTexts = prefTexts;
	}
	
	public Map<String,Map<String,PrefTextValue>> getPrefTexts() {
		return this.prefTexts;
	}
	
	public void addPrefText(String key, Map<String,PrefTextValue> prefTexts){
		if (this.prefTexts != null){
			this.prefTexts.put(key, prefTexts);
		}
	}

	public void clearPrefTextCache(){
		// Clear cache immediately
		this.setPrefTexts(new ConcurrentHashMap<String,Map<String,PrefTextValue>>());
	}

	public void clearPrefTextCache(String key){
		// Clear one item in cache immediately
		this.prefTexts.remove(key);
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
