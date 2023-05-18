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
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_option_value")
@JsonInclude(Include.NON_NULL)
public class PrefOptionValue extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private PrefOptionName prefOptionName;
	private String value;
	private String lang;
	private Boolean rendered;
	private String validation;
	// make output simple for preference object
	private String name;
	private String valueType;
	private String defaultValue;
	private Boolean useDefault;
	
	// Constructors
	public PrefOptionValue() {
		super();
	}
	
	public PrefOptionValue(String lang) {
		super();
		setLang(lang);
		setRendered(false);
		setActive(true);
		setArchive(false);
		setLocked(false);
	}
	
	public PrefOptionValue(Long id, String value, String lang, Boolean rendered, String validation, String name,
			String valueType, String defaultValue, Boolean useDefault) {
		this.setId(id);
		this.setValue(value);
		this.setLang(lang);
		this.setRendered(rendered);
		this.setValidation(validation);
		//
		this.setName(name);
		this.setValueType(valueType);
		this.setDefaultValue(defaultValue);
		this.setUseDefault(useDefault);
	}
	
	// Setters/Getter
	@JsonIgnore
	@ManyToOne(targetEntity = PrefOptionName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_option_name_id")
	public PrefOptionName getPrefOptionName() {
		return prefOptionName;
	}
	public void setPrefOptionName(PrefOptionName prefOptionName) {
		this.prefOptionName = prefOptionName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "option_value")
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
	@Column(name = "validation")
	public String getValidation() {
		return validation;
	}
	public void setValidation(String validation) {
		this.validation = validation;
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
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public Boolean isUseDefault() {
		return useDefault;
	}
	public void setUseDefault(Boolean useDefault) {
		this.useDefault = useDefault;
	}
}
