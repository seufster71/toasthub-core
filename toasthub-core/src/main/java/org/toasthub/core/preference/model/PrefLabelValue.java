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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "pref_label_value")
@JsonInclude(Include.NON_NULL)
public class PrefLabelValue extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	protected PrefLabelName prefLabelName;
	protected String value;
	protected String lang;
	protected Boolean rendered;
	// make output simple on preference object
	protected String name;
	protected String className;
	protected Integer tabIndex;
	protected String group;
	protected String optionalParams;
	protected int sortOrder;
		
	// Constructor
	public PrefLabelValue() {
		super();
	}
	
	public PrefLabelValue(String lang) {
		super();
		setLang(lang);
		setRendered(false);
		setActive(true);
		setArchive(false);
		setLocked(false);
	}
	
	public PrefLabelValue(Long id, String value, String lang, Boolean rendered, String name, String className, Integer tabIndex, String group, String optionalParams, int sortOrder){
		this.setId(id);
		this.setValue(value);
		this.setLang(lang);
		this.setRendered(rendered);
		this.setName(name);
		this.setClassName(className);
		this.setTabIndex(tabIndex);
		this.setGroup(group);
		this.setOptionalParams(optionalParams);
		this.setSortOrder(sortOrder);
	}
	
	// Setters/Getters
	@JsonIgnore
	@ManyToOne(targetEntity = PrefLabelName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_label_name_id")
	public PrefLabelName getPrefLabelName() {
		return prefLabelName;
	}
	public void setPrefLabelName(PrefLabelName prefLabelName) {
		this.prefLabelName = prefLabelName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "label_value")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public Boolean getRendered() {
		return rendered;
	}
	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public Integer getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getOptionalParams() {
		return optionalParams;
	}
	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}
