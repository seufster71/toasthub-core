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

import jakarta.persistence.CascadeType;
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
@Table(name = "pref_form_field_value")
@JsonInclude(Include.NON_NULL)
public class PrefFormFieldValue extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	protected PrefFormFieldName prefFormFieldName;
	protected String value;
	protected String label;
	protected String lang;
	protected Boolean rendered;
	protected Boolean required;
	protected String validation;
	protected String image;
	protected PrefFormFieldValue subElement;
	// make output simple for preference object
	protected String name;
	protected String fieldType;
	protected String htmlType;
	protected String className;
	protected String group;
	protected String subGroup;
	protected Integer tabIndex;
	protected String optionalParams;
	protected String classModel;
	protected int sortOrder;
	
	// Constructor
	public PrefFormFieldValue() {
		super();
	}
	
	public PrefFormFieldValue(String lang) {
		super();
		setLang(lang);
		setRendered(false);
		setRequired(false);
		setActive(true);
		setArchive(false);
		setLocked(false);
	}
	
	// Contructor for fields 
	public PrefFormFieldValue(Long id,String value, String label, String lang, Boolean rendered, 
			Boolean required, String validation, String image, String name, String fieldType,
			String htmlType, String className, String group, String subGroup, Integer tabIndex, 
			String optionalParams, String classModel, int sortOrder) {
		this.setId(id);
		this.setValue(value);
		this.setLabel(label);
		this.setLang(lang);
		this.setRendered(rendered);
		this.setRequired(required);
		this.setValidation(validation);
		this.setImage(image);
		// 
		this.setName(name);
		this.setFieldType(fieldType);
		this.setHtmlType(htmlType);
		this.setClassName(className);
		this.setGroup(group);
		this.setSubGroup(subGroup);
		this.setTabIndex(tabIndex);
		this.setOptionalParams(optionalParams);
		this.setClassModel(classModel);
		this.setSortOrder(sortOrder);
	}
	
	
	
	// Setters/Getters
	@JsonIgnore
	@ManyToOne(targetEntity = PrefFormFieldName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_form_field_name_id")
	public PrefFormFieldName getPrefFormFieldName() {
		return prefFormFieldName;
	}
	public void setPrefFormFieldName(PrefFormFieldName prefFormFieldName) {
		this.prefFormFieldName = prefFormFieldName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "field_value")
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
	@Column(name = "required")
	public Boolean getRequired() {
		return required;
	}
	public void setRequired(Boolean required) {
		this.required = required;
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
	@Column(name = "field_label")
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	@JsonIgnore
	@ManyToOne(targetEntity = PrefFormFieldValue.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "sub_element_id")
	public PrefFormFieldValue getSubElement() {
		return subElement;
	}
	public void setSubElement(PrefFormFieldValue subElement) {
		this.subElement = subElement;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "image_path")
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getHtmlType() {
		return htmlType;
	}
	public void setHtmlType(String htmlType) {
		this.htmlType = htmlType;
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
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
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
	public String getOptionalParams() {
		return optionalParams;
	}
	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Transient
	public String getClassModel() {
		return classModel;
	}
	public void setClassModel(String classModel) {
		this.classModel = classModel;
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
