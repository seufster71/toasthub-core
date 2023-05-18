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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_option_name")
@JsonInclude(Include.NON_NULL)
public class PrefOptionName extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	private PrefName prefName;
	private String name;
	private Text title;
	private String valueType;
	private String defaultValue;
	private Boolean useDefault;
	private String optionalParams;
	private Set<PrefOptionValue> values;
	
	// Constructor 
	public PrefOptionName() {
		super();
	}
	
	
	// Setters/Getter
	@JsonIgnore
	@ManyToOne(targetEntity = PrefName.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "pref_name_id")
	public PrefName getPrefName() {
		return prefName;
	}
	public void setPrefName(PrefName prefName) {
		this.prefName = prefName;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Text.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "text_id")
	public Text getTitle() {
		return title;
	}
	public void setTitle(Text title) {
		this.title = title;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "value_type")
	public String getValueType() {
		return valueType;
	}
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "default_value")
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "use_default")
	public Boolean getUseDefault() {
		return useDefault;
	}
	public void setUseDefault(Boolean useDefault) {
		this.useDefault = useDefault;
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
	@OneToMany(mappedBy = "prefOptionName", cascade = CascadeType.ALL)
	public Set<PrefOptionValue> getValues() {
		return values;
	}
	public void setValues(Set<PrefOptionValue> values) {
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
	public void addToValues(Object value) {
		PrefOptionValue val = (PrefOptionValue) value;
		val.setPrefOptionName(this);
		if (values == null) {
			values = new HashSet<PrefOptionValue>();
			values.add(val);
		} else {
			boolean exists = false;
			for (PrefOptionValue v : values) {
				if (v.getLang().equals(val.getLang())) {
					v.setValue(val.getValue());
					v.setRendered(val.getRendered());
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
