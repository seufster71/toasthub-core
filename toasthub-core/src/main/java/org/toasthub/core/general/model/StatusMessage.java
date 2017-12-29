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

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

public class StatusMessage {

	private String code;
	private String message;
	
	public StatusMessage(String code, String message){

		this.setCode(code);
		this.setMessage(message);
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
