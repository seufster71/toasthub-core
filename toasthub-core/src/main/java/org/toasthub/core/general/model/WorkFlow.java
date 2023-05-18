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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.Text;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "workflow")
public class WorkFlow extends BaseEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
	public static final String WORKFLOW = "workFlow";
	public static final String WORKFLOWS = "workFlows";
	public static final String ID = "workFlowId";
	
	private Text name;
	private String code;
	
	// Constructor
	public WorkFlow(){
		super();
	}
	
	public WorkFlow(String code, Text name){
		super();
		setCode(code);
		setName(name);
	}
	
	// Setter/Getter
	@JsonView({View.Member.class,View.Admin.class})
	@ManyToOne(targetEntity = Text.class)
	@JoinColumn(name = "name_id", nullable = false)
	public Text getName() {
		return name;
	}
	public void setName(Text name) {
		this.name = name;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
