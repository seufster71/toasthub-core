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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "menu_item_values")
public class MenuItemValue extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	protected MenuItem menuItem;
	protected String value;
	protected String routeWeb;
	protected String routeNative;
	protected String iconWeb;
	protected String iconNative;
	protected String lang;
	protected boolean rendered;
	
	// Constructors
	public MenuItemValue() {
		super();
	}
	// Setters/Getters
	@JsonIgnore
	@ManyToOne(targetEntity = MenuItem.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "menu_item_id")
	public MenuItem getMenuItem() {
		return menuItem;
	}
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "menu_value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "route_web")
	public String getRouteWeb() {
		return routeWeb;
	}
	public void setRouteWeb(String routeWeb) {
		this.routeWeb = routeWeb;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "route_native")
	public String getRouteNative() {
		return routeNative;
	}
	public void setRouteNative(String routeNative) {
		this.routeNative = routeNative;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "icon_web")
	public String getIconWeb() {
		return iconWeb;
	}
	public void setIconWeb(String iconWeb) {
		this.iconWeb = iconWeb;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "icon_native")
	public String getIconNative() {
		return iconNative;
	}
	public void setIconNative(String iconNative) {
		this.iconNative = iconNative;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "lang")
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "rendered")
	public boolean isRendered() {
		return rendered;
	}
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
	
}
