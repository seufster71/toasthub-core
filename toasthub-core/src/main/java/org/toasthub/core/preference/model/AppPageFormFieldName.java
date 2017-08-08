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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "page_form_field_name")
public class AppPageFormFieldName extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	public static final String SELECT = "SELECT";
	public static final String INPUT = "INPUT";
	public static final String CHECKBOX = "CHECKBOX";
	public static final String RADIO = "RADIO";
	
	private AppPageName pageName;
	private String name;
	private Text title;
	private String fieldType;
	private String htmlType;
	private String tabIndex;
	private Integer rows;
	private Integer cols;
	private String className;
	private String group;
	private String subGroup;
	private String optionalParams;
	private String classModel;
	private Set<AppPageFormFieldValue> values;
	
	// Constructors
	public AppPageFormFieldName () {
		super();
	}
	public AppPageFormFieldName (AppPageName pageName,String name,Text title,String fieldType) {
		super();
		this.setPageName(pageName);
		this.setName(name);
		this.setTitle(title);
		this.setFieldType(fieldType);
	}
	
	public AppPageFormFieldName (String name, String fieldType, String htmlType, String className, String group, String subGroup,
			 String tabIndex, String optionalParams, String classModel) {
		this.setName(name);
		this.setFieldType(fieldType);
		this.setHtmlType(htmlType);
		this.setClassName(className);
		this.setGroup(group);
		this.setSubGroup(subGroup);
		this.setTabIndex(tabIndex);
		this.setOptionalParams(optionalParams);
		this.setClassModel(classModel);
	}
	
	// Getters and Setters
	@JsonIgnore
	@ManyToOne(targetEntity = AppPageName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "page_name_id")
	public AppPageName getPageName() {
		return pageName;
	}
	public void setPageName(AppPageName pageName) {
		this.pageName = pageName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Admin.class})
	@ManyToOne(targetEntity = Text.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "text_id")
	public Text getTitle() {
		return title;
	}
	public void setTitle(Text title) {
		this.title = title;
	}
	
	@JsonView({View.Admin.class})
	@OneToMany(mappedBy = "pageFormFieldName", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<AppPageFormFieldValue> getValues() {
		return values;
	}
	public void setValues(Set<AppPageFormFieldValue> values) {
		this.values = values;
	}

	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "field_type")
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "html_type")
	public String getHtmlType() {
		return htmlType;
	}
	public void setHtmlType(String htmlType) {
		this.htmlType = htmlType;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "row_count")
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "column_count")
	public Integer getCols() {
		return cols;
	}
	public void setCols(Integer cols) {
		this.cols = cols;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "class_name")
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "group_name")
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "sub_group_name")
	public String getSubGroup() {
		return subGroup;
	}
	public void setSubGroup(String subGroup) {
		this.subGroup = subGroup;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "tab_index")
	public String getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(String tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class})
	@Column(name = "optional_params")
	public String getOptionalParams() {
		return optionalParams;
	}
	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}
	
	@JsonView({View.Admin.class})
	@Column(name = "class_model")
	public String getClassModel() {
		return classModel;
	}
	public void setClassModel(String classModel) {
		this.classModel = classModel;
	}

	@Transient
	public void setTitleDefaultText(String defaultText){
		if (this.title != null) {
			this.title.setDefaultText(defaultText);
		} else {
			Text text = new Text();
			text.setDefaultText(defaultText);
			this.setTitle(text);
		}
	}
	
	@Transient
	public void setTitleMtext(Map<String,String> langMap){
		if (this.title != null) {
			this.title.setLangTexts(langMap);
		} else {
			Text text = new Text();
			text.setLangTexts(langMap);
			this.setTitle(text);
		}
	}
	
	@Transient
	public void setMValues(Map<String,String> langMap) {
		String field = langMap.get(BaseEntity.FIELD);
		langMap.remove(BaseEntity.FIELD);
		if (this.values == null) {
			this.values = new HashSet<AppPageFormFieldValue>();
		}
		// loop through langMap
		for (String key : langMap.keySet()) {
			// loop through existing values to find match
			boolean added = false;
			for (AppPageFormFieldValue v : values){
				if (v.getLang().equals(key)){
					switch (field) {
					case "value":
						v.setValue(langMap.get(key));
						break;
					case "label":
						v.setLabel(langMap.get(key));
						break;
					case "rendered":
						v.setRendered(Boolean.parseBoolean(langMap.get(key)));
						break;
					case "required":
						v.setRequired(Boolean.parseBoolean(langMap.get(key)));
						break;
					}
					added = true;
					break;
				} 
			}
			if (!added) {
				// lang does not exist create a new one
				AppPageFormFieldValue val = new AppPageFormFieldValue();
				val.setLang(key);
				val.setOrder(0l);
				val.setPageFormFieldName(this);
				val.setActive(true);
				val.setArchive(false);
				val.setLocked(false);
				val.setValidation("");
				switch (field) {
				case "value":
					val.setValue(langMap.get(key));
					break;
				case "label":
					val.setLabel(langMap.get(key));
					break;
				case "rendered":
					val.setRendered(Boolean.parseBoolean(langMap.get(key)));
					break;
				case "required":
					val.setRequired(Boolean.parseBoolean(langMap.get(key)));
					break;
				}
				values.add(val);
			}
		}
	}
}
