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

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

public class RestResponse {

	public static final String ERROR = "ERROR";
	public static final String SUCCESS = "SUCCESS";
	public static final String INFO = "INFO";
	public static final String WARN = "WARN";
	public static final String REGISTER = "REGISTER";
	
	public static final String AUTHFAIL = "AUTHFAIL";
	public static final String DOESNOTEXIST = "DOESNOTEXIST";
	public static final String ACTIONNOTEXIST = "ACTIONNOTEXIST";
	public static final String PAGEOPTIONS = "PAGEOPTIONS";
	public static final String UNKNOWNTYPE = "UNKNOWNTYPE";
	public static final String MISSINGPARAM = "MISSINGPARAM";
	public static final String SERVERERROR = "SERVERERROR";
	public static final String EXECUTIONFAILED = "EXECUTIONFAILED";
	public static final String EMPTY = "EMPTY";
	public static final String ACTIONFAILED = "ACTIONFAILED";
	public static final String REGISTRATIONFAILED = "REGISTRATIONFAILED";
	public static final String MENUS = "MENUS";

	
	private Map<String,Object> params;
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public Map<String,Object> getParams() {
		if (params == null){
			params = new HashMap<String,Object>();
		}
		return params;
	}
	public void setParams(Map<String,Object> params) {
		this.params = params;
	}
	public void addParam(String key, Object value){
		if (params == null){
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
}
