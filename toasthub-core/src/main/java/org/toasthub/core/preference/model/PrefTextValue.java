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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_text_value")
@JsonInclude(Include.NON_NULL)
public class PrefTextValue extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private PrefTextName prefTextName;
	private String value;
	private String lang;
	private Boolean rendered;
	// make output simple for preference object
	private String name;
	
	// Constructor
	public PrefTextValue() {
		super();
	}
	
	public PrefTextValue(Long id, String value, String lang, Boolean rendered, String name){
		this.setId(id);
		this.setValue(value);
		this.setLang(lang);
		this.setRendered(rendered);
		//
		this.setName(name);
	}
	
	// Setters/Getters
	@JsonIgnore
	@ManyToOne(targetEntity = PrefTextName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_text_name_id")
	public PrefTextName getPrefTextName() {
		return prefTextName;
	}
	public void setPrefTextName(PrefTextName prefTextName) {
		this.prefTextName = prefTextName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "text_value")
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
}
