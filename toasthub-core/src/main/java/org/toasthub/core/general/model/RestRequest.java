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

package org.toasthub.core.general.model;

import java.util.HashMap;
import java.util.Map;

public class RestRequest {

	protected String action;
	protected String service;
	protected String lang;

	
	protected Map<String,Object> params;
	
	// Methods
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public Map<String,Object> getParams() {
		return params;
	}
	public void setParams(Map<String,Object> params) {
		this.params = params;
	}
	
	public void addParam(String key, Object value) {
		if (params == null) {
			params = new HashMap<String,Object>();
		}
		params.put(key, value);
	}
	
	public Object getParam(String key){
		if (params != null && params.containsKey(key)){
			return params.get(key);
		}
		return null;
	}
	
	public boolean containsParam(String key){
		if (params != null && params.containsKey(key)){
			return true;
		}
		return false;
	}
	
	public Long getParamLong(String key) {
		if (params != null && params.containsKey(key)){
			// try to cast to long
			try {
				return (Long) params.get(key);
			} catch (Exception e) {
				// ignore error
			}
			// Try to cast to integer then long
			try {
				return Long.valueOf((Integer) params.get(key));
			} catch (Exception e) {
				// ignore error
			}
			// Try to cast to string then long
			try {
				return Long.valueOf((String) params.get(key));
			} catch (Exception e) {
				// ignore error
			}
		}
		return null;
	}
	
	public String getParamString(String key) {
		if (params != null && params.containsKey(key)){
			// Try to cast to string then long
			try {
				return (String) params.get(key);
			} catch (Exception e) {
				// ignore error
			}
		}
		return null;
	}
	
	public Integer getParamInteger(String key) {
		if (params != null && params.containsKey(key)){
			// Try to cast to string then long
			try {
				return (Integer) params.get(key);
			} catch (Exception e) {
				// ignore error
			}
		}
		return null;
	}
}
