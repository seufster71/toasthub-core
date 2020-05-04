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
import javax.validation.constraints.NotNull;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_label_name")
@JsonInclude(Include.NON_NULL)
public class PrefLabelName extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private PrefName prefName;
	private String name;
	private Text title;
	private String className;
	private String group;
	private Integer tabIndex;
	private String optionalParams;
	private Set<PrefLabelValue> values;
	
	// Constructor
	public PrefLabelName() {
		super();
	}
	
	public PrefLabelName (PrefName prefName, String name, Text Title) {
		super();
		this.setPrefName(prefName);
		this.setName(name);
		this.setTitle(title);
	}
	
	public PrefLabelName (String name, String className, Integer tabIndex, String optionalParams) {
		this.setName(name);
		this.setClassName(className);
		this.setTabIndex(tabIndex);
		this.setOptionalParams(optionalParams);
	}
	
	// Setters/Getters
	@JsonIgnore
	@NotNull
	@ManyToOne(targetEntity = PrefName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_name_id", nullable = false)
	public PrefName getPrefName() {
		return prefName;
	}
	public void setPrefName(PrefName prefName) {
		this.prefName = prefName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@NotNull
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

	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "class_name")
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "group_name")
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "tab_index")
	public Integer getTabIndex() {
		return tabIndex;
	}
	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "optional_params")
	public String getOptionalParams() {
		return optionalParams;
	}
	public void setOptionalParams(String optionalParams) {
		this.optionalParams = optionalParams;
	}

	@JsonView({View.Admin.class,View.System.class})
	@OneToMany(mappedBy = "prefLabelName", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public Set<PrefLabelValue> getValues() {
		return values;
	}
	public void setValues(Set<PrefLabelValue> values) {
		this.values = values;
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
		String field = langMap.get(GlobalConstant.FIELD);
		langMap.remove(GlobalConstant.FIELD);
		if (this.values == null) {
			values = new HashSet<PrefLabelValue>();
		}
		// loop through langMap
		for (String key : langMap.keySet()) {
			// loop through existing values to find match
			boolean added = false;
			for (PrefLabelValue v : values){
				if (v.getLang().equals(key)){
					switch (field) {
					case "value":
						v.setValue(langMap.get(key));
						break;
					case "rendered":
						v.setRendered(Boolean.parseBoolean(langMap.get(key)));
						break;
					}
					added = true;
					break;
				} 
			}
			if (!added) {
				// lang does not exist create a new one
				PrefLabelValue val = new PrefLabelValue();
				val.setLang(key);
				val.setOrder(0l);
				val.setPrefLabelName(this);
				val.setActive(true);
				val.setArchive(false);
				val.setLocked(false);
				switch (field) {
				case "value":
					val.setValue(langMap.get(key));
					break;
				case "rendered":
					val.setRendered(Boolean.parseBoolean(langMap.get(key)));
					break;
				}
				values.add(val);
			}
		}
	}
	
	@Transient
	public void addToValues(Object value) {
		PrefLabelValue val = (PrefLabelValue) value;
		val.setPrefLabelName(this);
		if (values == null) {
			values = new HashSet<PrefLabelValue>();
			values.add(val);
		} else {
			boolean exists = false;
			for (PrefLabelValue v : values) {
				if (v.getLang().equals(val.getLang())) {
					v.setValue(val.getValue());
					v.setRendered(val.getRendered());
					v.setOrder(val.getOrder());
					exists = true;
					break;
				}
			}
			if (exists == false) {
				values.add(val);
			}
		}
	}
}
