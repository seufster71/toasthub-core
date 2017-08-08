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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.handler.ServiceProcessor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;


@Entity
@Table(name = "service_class")
public class ServiceClass extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private String serviceName;
	private String apiVersion;
	private String appVersion;
	private ServiceProcessor serviceProcessor;
	private String className;
	private String category;
	private String location;
	// need to add stuff to identify if this is a micro service
	
	// Constructor
	public ServiceClass(){	
	}

	@JsonView({View.Admin.class})
	@Column(name = "service_name")
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@JsonView({View.Admin.class})
	@Column(name = "api_version")
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	@JsonView({View.Admin.class})
	@Column(name = "app_version")
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	@JsonIgnore
	@Transient
	public String getServiceKey(){
		return this.serviceName + "-" + this.apiVersion + "-" + this.appVersion;
	}
	
	@JsonIgnore
	@Transient
	public ServiceProcessor getServiceProcessor() {
		return serviceProcessor;
	}
	public void setServiceProcessor(ServiceProcessor x) {
		this.serviceProcessor = x;
	}

	@JsonView({View.Admin.class})
	@Column(name = "class_name")
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	@JsonView({View.Admin.class})
	@Column(name = "category")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	@JsonView({View.Admin.class})
	@Column(name = "location")
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
