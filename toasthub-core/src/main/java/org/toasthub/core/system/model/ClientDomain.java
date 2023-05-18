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

package org.toasthub.core.system.model;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "clientdomains")
public class ClientDomain extends ToastEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String URLDomain;
	private String APPDomain;
	private String CustDomain;
	private String APPName;
	private String HTMLPrefix;
	private String publicLayout;
	private String adminLayout;
	private String memberLayout;
	private String sysAdminLayout;
	
	// Constructor
	public ClientDomain () {
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}

	// Setter/Getters
	
	@JsonView({View.System.class})
	@Column(name = "url_domain")
	public String getURLDomain() {
		return URLDomain;
	}
	public void setURLDomain(String URLDomain) {
		this.URLDomain = URLDomain;
	}

	@JsonView({View.System.class})
	@Column(name = "app_domain")
	public String getAPPDomain() {
		return APPDomain;
	}
	public void setAPPDomain(String APPDomain) {
		this.APPDomain = APPDomain;
	}

	@JsonView({View.System.class})
	@Column(name = "cust_domain")
	public String getCustDomain() {
		return CustDomain;
	}
	public void setCustDomain(String CustDomain) {
		this.CustDomain = CustDomain;
	}
	
	@JsonView({View.System.class})
	@Column(name = "app_name")
	public String getAPPName() {
		return APPName;
	}
	public void setAPPName(String aPPName) {
		APPName = aPPName;
	}
	
	@JsonView({View.System.class})
	@Column(name = "html_prefix")
	public String getHTMLPrefix() {
		return HTMLPrefix;
	}
	public void setHTMLPrefix(String hTMLPrefix) {
		HTMLPrefix = hTMLPrefix;
	}
	
	@JsonView({View.System.class})
	@Column(name = "public_layout")
	public String getPublicLayout() {
		return publicLayout;
	}
	public void setPublicLayout(String publicLayout) {
		this.publicLayout = publicLayout;
	}
	
	@JsonView({View.System.class})
	@Column(name = "admin_layout")
	public String getAdminLayout() {
		return adminLayout;
	}
	public void setAdminLayout(String adminLayout) {
		this.adminLayout = adminLayout;
	}
	
	@JsonView({View.System.class})
	@Column(name = "member_layout")
	public String getMemberLayout() {
		return memberLayout;
	}
	public void setMemberLayout(String memberLayout) {
		this.memberLayout = memberLayout;
	}
	
	@JsonView({View.System.class})
	@Column(name = "sysadmin_layout")
	public String getSysAdminLayout() {
		return sysAdminLayout;
	}
	public void setSysAdminLayout(String sysAdminLayout) {
		this.sysAdminLayout = sysAdminLayout;
	}
	
}
