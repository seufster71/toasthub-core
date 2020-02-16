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
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import org.toasthub.core.general.api.View;
import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass()
public class BaseEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Boolean active;
	private Boolean archive;
	private Boolean locked;
	private Long lockOwnerRefId;
	private Instant lockTime;
	private Instant modified;
	private Instant created;
	private Long version;
	
	// Constructor
	public BaseEntity() {
	}
	
	// Helper for user input
	/*public void userInputHelper(RestRequest request, RestResponse response, String formName) {
		List<SysPageFormFieldValue> formFields = ((Map<String, List<SysPageFormFieldValue>>) request.getParams().get("sysPageFormFields")).get(formName);
		Map<String,String> json = (Map<String,String>) request.getParams().get("userInput");
			
		for(SysPageFormFieldValue field : formFields){
			try {
				if ("TXT".equals(field.getPageFormFieldName().getFieldType())){
					String v = json.get(field.getPageFormFieldName().getName());
					
					if (v != null && !v.contains("-")){
						String name = field.getPageFormFieldName().getFieldName();
						if (name != null){
							Field f = this.getClass().getDeclaredField(name);
							f.setAccessible(true);
							f.set(this, v);
						}
					}
				} else if ("TXTDOUBLE".equals(field.getPageFormFieldName().getFieldType())){
					double v = Double.parseDouble(json.get(field.getPageFormFieldName().getName()));
						String name = field.getPageFormFieldName().getFieldName();
						if (name != null){
							Field f = this.getClass().getDeclaredField(name);
							f.setAccessible(true);
							f.set(this, v);
						}
					
				} else if ("TXTFLOAT".equals(field.getPageFormFieldName().getFieldType())){
					float v = Float.parseFloat(json.get(field.getPageFormFieldName().getName()));
					String name = field.getPageFormFieldName().getFieldName();
					if (name != null){
						Field f = this.getClass().getDeclaredField(name);
						f.setAccessible(true);
						f.set(this, v);
					}
				}
			} catch (NoSuchFieldException e) {
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	*/
	// Setter/Getter
	@JsonView({View.Admin.class,View.Member.class,View.System.class})
	@Id	
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@JsonView({View.Admin.class})
	@Column(name = "modified",updatable = false, insertable = false)
	public Instant getModified() {
		return modified;
	}
	public void setModified(Instant modified) {
		this.modified = modified;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "created", updatable = false, insertable = false)
	public Instant getCreated() {
		return created;
	}
	public void setCreated(Instant created) {
		this.created = created;
	}
	
	@JsonView({View.Admin.class})
	@Version 
	@Column(name = "version")
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}

	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "is_active")
	public Boolean isActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	@JsonView({View.Member.class,View.Admin.class,View.System.class})
	@Column(name = "is_archive")
	public Boolean isArchive() {
		return archive;
	}
	public void setArchive(Boolean archive) {
		this.archive = archive;
	}
	
	@JsonView({View.Admin.class})
	@Column(name = "is_locked")
	public Boolean isLocked() {
		return locked;
	}
	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
	@JsonView({View.Admin.class})
	@Column(name = "lockowner_id")
	public Long getLockOwnerRefId() {
		return lockOwnerRefId;
	}
	public void setLockOwnerRefId(Long lockOwnerRefId) {
		this.lockOwnerRefId = lockOwnerRefId;
	}
	
	@JsonView({View.Admin.class})
	@Column(name = "lock_time")
	public Instant getLockTime() {
		return lockTime;
	}
	public void setLockTime(Instant lockTime) {
		this.lockTime = lockTime;
	}

}
