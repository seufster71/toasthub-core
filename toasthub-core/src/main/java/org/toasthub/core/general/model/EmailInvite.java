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
import javax.persistence.Entity;
import javax.persistence.Table;

import org.toasthub.core.general.api.View;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "email_invites")
public class EmailInvite extends BaseEntity implements SendMailIntf,Serializable {

	private static final long serialVersionUID = 1L;
	public static final String INIT = "INIT";
	public static final String FAIL = "FAIL";
	public static final String PEND = "PEND";
	public static final String DUPLICATE = "DUP";
	
	private Long senderRefId;
	private String receiverEmail;
	private String subject;
	private String message;
	private String status;
	
	// constructors
	public EmailInvite() {
		super();
		this.setStatus(EmailInvite.INIT);
	}
	public EmailInvite(Long senderRefId,String receiverEmail,String message){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		this.setSenderRefId(senderRefId);
		this.setReceiverEmail(receiverEmail);
		this.setMessage(message);
		this.setStatus(EmailInvite.INIT);
	}
	
	// Constructor for ajax
	public EmailInvite(RestRequest request, RestResponse response, String formName){
		this.setActive(true);
		this.setArchive(false);
		this.setLocked(false);
		this.setCreated(Instant.now());
		//userInputHelper(request, response, formName);
	}
		
	// setter/getters
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "message")
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "sender_id")
	public Long getSenderRefId() {
		return senderRefId;
	}
	public void setSenderRefId(Long senderRefId) {
		this.senderRefId = senderRefId;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "receiver_email")
	public String getReceiverEmail() {
		return receiverEmail;
	}
	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}
	
	@JsonView({View.Member.class,View.Admin.class})
	@Column(name = "subject")
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
}
