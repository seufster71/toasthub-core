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
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "pref_name")
@JsonInclude(Include.NON_NULL)
public class PrefName extends BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String name;
	protected Text title;
	protected String category;
	protected PrefProduct prefProduct;
	protected Set<PrefTextName> texts;
	protected Set<PrefFormFieldName> formFields;
	protected Set<PrefLabelName> labels;
	protected Set<PrefOptionName> options;
	
	// Transient
	protected Long prefProductId;
	
	// Constructor
	public PrefName () {
		super();
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
	}
	
	
	// Methods
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonView({View.Admin.class,View.System.class})
	@ManyToOne(targetEntity = Text.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "text_id")
	public Text getTitle() {
		return title;
	}
	public void setTitle(Text title) {
		this.title = title;
	}
	
	@JsonView({View.Public.class,View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "category")
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "prefName", cascade = CascadeType.ALL)
	public Set<PrefTextName> getTexts() {
		return texts;
	}
	public void setTexts(Set<PrefTextName> texts) {
		this.texts = texts;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "prefName", cascade = CascadeType.ALL)
	public Set<PrefFormFieldName> getFormFields() {
		return formFields;
	}
	public void setFormFields(Set<PrefFormFieldName> formFields) {
		this.formFields = formFields;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "prefName", cascade = CascadeType.ALL)
	public Set<PrefLabelName> getLabels() {
		return labels;
	}
	public void setLabels(Set<PrefLabelName> labels) {
		this.labels = labels;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "prefName", cascade = CascadeType.ALL)
	public Set<PrefOptionName> getOptions() {
		return options;
	}
	public void setOptions(Set<PrefOptionName> options) {
		this.options = options;
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
	
	@JsonIgnore
	@ManyToOne(targetEntity = PrefProduct.class)
	@JoinColumn(name = "pref_product_id")
	public PrefProduct getPrefProduct() {
		return prefProduct;
	}
	public void setPrefProduct(PrefProduct prefProduct) {
		this.prefProduct = prefProduct;
	}

	@JsonView({View.Admin.class})
	@Transient
	public Long getPrefProductId() {
		return prefProductId;
	}
	public void setPrefProductId(Long prefProductId) {
		this.prefProductId = prefProductId;
	}
}
