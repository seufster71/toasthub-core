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

package org.toasthub.core.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Language;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.model.StatusMessage;
import org.toasthub.core.preference.model.PrefCacheUtil;
import org.toasthub.core.preference.model.PrefFormFieldValue;
import org.toasthub.core.preference.model.PrefOptionValue;

import com.google.gson.Gson;

@Service("UtilSvc")
public class UtilSvc {
	
	@Autowired 
	PrefCacheUtil prefCacheUtil;
	
	@Autowired
	protected ApplicationContext context;
/*
	public String writeResponsePublic(RestResponse response){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		ObjectWriter w = objectMapper.writerWithView(JsonViews.JsonPublic.class);
		try {
			return w.writeValueAsString(response);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"status\":\"ERROR\",\"statusMessage\":\"JSON write error check Stack Trace\"}";
	}


	public String writeResponseMember(RestResponse response){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		ObjectWriter w = objectMapper.writerWithView(JsonViews.JsonMember.class);
		try {
			return w.writeValueAsString(response);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"status\":\"ERROR\",\"statusMessage\":\"JSON write error check Stack Trace\"}";
	}
	
	public String writeResponseAdmin(RestResponse response){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		objectMapper.disable(Feature.FAIL_ON_EMPTY_BEANS);
		ObjectWriter w = objectMapper.writerWithView(JsonViews.JsonAdmin.class);
		try {
			return w.writeValueAsString(response);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{\"status\":\"ERROR\",\"statusMessage\":\"JSON write error check Stack Trace\"}";
	}
	*/
	public void setupDefaults(RestRequest request){
		
		if (request.getParam(GlobalConstant.LANG) == null){
			request.addParam(GlobalConstant.LANG,"en");
		}
		
		if (request.getParam(GlobalConstant.LISTSTART) == null){
			request.addParam(GlobalConstant.LISTSTART, 0);
		}
		
		PrefOptionValue globalListLimit = prefCacheUtil.getPrefOption("GLOBAL_PAGE", "GLOBAL_PAGE_PAGELIMIT",(String)request.getParam(GlobalConstant.LANG));
		PrefOptionValue globalListLimitMax = prefCacheUtil.getPrefOption("GLOBAL_PAGE", "GLOBAL_PAGE_PAGELIMIT_MAX",(String)request.getParam(GlobalConstant.LANG));
		if (request.getParam(GlobalConstant.LISTLIMIT) == null){
			if (globalListLimit != null) {
				if (!"".equals(globalListLimit.getValue())) {
					request.addParam(GlobalConstant.LISTLIMIT, Integer.parseInt(globalListLimit.getValue()));
				} else {
					request.addParam(GlobalConstant.LISTLIMIT, Integer.parseInt(globalListLimit.getDefaultValue()));
				}
			} else {
				request.addParam(GlobalConstant.LISTLIMIT, 20);
			}
		} else {
			Integer max = 200;
			if (globalListLimitMax != null) {
				if (!"".equals(globalListLimitMax.getValue())) {
					max = Integer.parseInt(globalListLimitMax.getValue());
				} else {
					max = Integer.parseInt(globalListLimitMax.getDefaultValue());
				}
			}
			if ((Integer) request.getParam(GlobalConstant.LISTLIMIT) > max ) {
				if (!"".equals(globalListLimit.getValue())) {
					request.addParam(GlobalConstant.LISTLIMIT, Integer.parseInt(globalListLimit.getValue()));
				} else {
					request.addParam(GlobalConstant.LISTLIMIT, Integer.parseInt(globalListLimit.getDefaultValue()));
				}
			}
		}
		
		if (!request.containsParam(GlobalConstant.SVCAPIVERSION)){
			request.addParam(GlobalConstant.SVCAPIVERSION, "1.0");
		}

		if (!request.containsParam(GlobalConstant.SVCAPPVERSION)){
			request.addParam(GlobalConstant.SVCAPPVERSION, "1.0");
		}
	
	} // setupDefaults
	
	public void addStatus(String level, String status, String message, RestResponse response) {
		
		response.setStatus(status);
		StatusMessage statusMessage = new StatusMessage(status, message);
		// check for status object
		//if (response.getParam(RestResponse.STATUS) == null){
		//	response.addParam(RestResponse.STATUS, new ResponseStatus());
		//}
		//ResponseStatus responseStatus = (ResponseStatus) response.getParam(RestResponse.STATUS);
		switch (level) {
		case RestResponse.INFO :
			if (response.getInfos() != null) {
				response.getInfos().add(statusMessage);
			} else {
				response.setInfos(new ArrayList<StatusMessage>());
				response.getInfos().add(statusMessage);
			}
			break;
		case RestResponse.WARN :
			if (response.getWarns() != null) {
				response.getWarns().add(statusMessage);
			} else {
				response.setWarns(new ArrayList<StatusMessage>());
				response.getWarns().add(statusMessage);
			}
			break;
		case RestResponse.ERROR :
			if (response.getErrors() != null) {
				response.getErrors().add(statusMessage);
			} else {
				response.setErrors(new ArrayList<StatusMessage>());
				response.getErrors().add(statusMessage);
			}
			break;
		default :
			if (response.getWarns() != null) {
				response.getWarns().add(statusMessage);
			} else {
				response.setWarns(new ArrayList<StatusMessage>());
				response.getWarns().add(statusMessage);
			}
			break;
		}
		
	} // addStatus
	
	public void preProcessParams(RestRequest request, Map<String,String> paramTypes){
		Map<String,Object> params = request.getParams();
		
		for (String key : params.keySet()) {
			if (paramTypes.containsKey(key)){
				if (paramTypes.get(key).equals("Long")){
					Long value = new Long((Integer) params.get(key));
					params.put(key, value);
				}
			}
		}
	} // preProcessParams
	
	@SuppressWarnings("unchecked")
	public void validateParams(RestRequest request, RestResponse response){
		boolean isValid = true;
		//Map<String,Object> params = request.getParams();
		Map<String,Object> inputList = (Map<String, Object>) request.getParam("inputFields");
		List<String> prefForms = (List<String>) request.getParam(PrefCacheUtil.PREFFORMKEYS);
		Map<String,Map<String,List<PrefFormFieldValue>>> prefFields = (Map<String, Map<String, List<PrefFormFieldValue>>>) request.getParam(PrefCacheUtil.PREFFORMFIELDS);
		List<Language> languages = (List<Language>) request.getParam("LANGUAGES");
		
		// loop through each form that was requested
		for (String formKey : prefForms) {
			List<PrefFormFieldValue> formFields = (List<PrefFormFieldValue>) prefFields.get(formKey);
			List<PrefFormFieldValue> subGroups = new ArrayList<PrefFormFieldValue>();
			// process main group
			isValid = processFields(formFields, inputList, languages, subGroups);
			// process subGroups
			isValid = processSubGroups(formFields, inputList, languages, subGroups);
			
		}
		request.addParam(GlobalConstant.VALID, isValid);
	} // validateParams 	
	
	private boolean processFields(List<PrefFormFieldValue> fields, Map<String,Object> inputList, List<Language> languages, List<PrefFormFieldValue> subGroups) {
		boolean isValid = true;
		// loop through each field that is available for this form
		for (PrefFormFieldValue field : fields) {
			String fieldName = field.getName();
			if (field.getSubGroup() != null && !field.getSubGroup().isEmpty()) {
				continue;
			}
			if (inputList.containsKey(fieldName)) {
				try {
					switch (field.getFieldType()) {
						case "TXT":
							// check if required
							String value = (String) inputList.get(fieldName);
							if ( field.getRequired() && (value == null || (value != null && value.isEmpty())) ){
								isValid = false;
							}
							// check against validation
							if (field.getValidation() != null && !"".equals(field.getValidation())) {
								Map<String,Object> paramObj = new Gson().fromJson(field.getValidation(),Map.class);
								if (paramObj.containsKey("regex")) {
									String regex = (String) paramObj.get("regex");
									if (value.matches(regex)){
										String test = "";
									}
								}
							}
							break;
						case "TXTAREA":
							// check if required
							String area = (String) inputList.get(fieldName);
							if ( field.getRequired() && (area == null || (area != null && area.isEmpty())) ){
								isValid = false;
							}
							// check against validation
							if (field.getValidation() != null && !"".equals(field.getValidation())) {
								Map<String,Object> paramObj = new Gson().fromJson(field.getValidation(),Map.class);
								if (paramObj.containsKey("regex")) {
									String regex = (String) paramObj.get("regex");
									if (area.matches(regex)){
										String test = "";
									}
								}
							}
							break;
						case "MTXT":
							// Check if required
							if (field.getRequired()) {
								// check default
								String defaultValue = (String) inputList.get(fieldName.concat("-DEFAULT"));
								if ( field.getRequired() && (defaultValue == null || (defaultValue != null && defaultValue.isEmpty())) ){
									isValid = false;
								}
								// check text
								for (Language language : languages) {
									if (language.isDefaultLang()) {
										String txtValue = (String) inputList.get(fieldName.concat("-TEXT-").concat(language.getCode()));
										if (txtValue == null || (txtValue != null && txtValue.isEmpty())) {
											isValid = false;
										}
									}
								}
							}
							
							break;
						case "BLN":
							if (inputList.get(fieldName) instanceof Boolean) {
								Boolean blnVal =  (Boolean) inputList.get(fieldName);
								if ( field.getRequired() && blnVal == null){
									isValid = false;
								}
							} else {
								String blnVal =  (String) inputList.get(fieldName);
								if ( field.getRequired() && (blnVal == null || (blnVal != null && blnVal.isEmpty())) ){
									isValid = false;
								}
							}
							
							break;
						case "SLT":
							
							break;
						case "MGRP":
							if (subGroups != null) {
								subGroups.add(field);
							}
							break;
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (field.getRequired() && !"MGRP".equals(field.getFieldType())) {
					// missing field
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}
	
	private boolean processSubGroups(List<PrefFormFieldValue> fields, Map<String,Object> inputList, List<Language> languages, List<PrefFormFieldValue> subGroups) {
		boolean isValid = true;
		
		for(PrefFormFieldValue subGroup : subGroups) {
			// loop through each field that is available for this form
			for (PrefFormFieldValue field : fields) {
				if (field.getSubGroup() == null ||  (field.getSubGroup() != null && !field.getSubGroup().equals(subGroup.getSubGroup()))) {
					continue;
				}
				for (Language language : languages) {
					
					String fieldName = field.getName()+"-"+language.getCode();
						
					if (inputList.containsKey(fieldName)) {
						try {
							switch (field.getFieldType()) {
								case "TXT":
									// check if required
									String value = (String) inputList.get(fieldName);
									if ( field.getRequired() && (value == null || (value != null && value.isEmpty())) && language.isDefaultLang()){
										isValid = false;
									}
									// check against validation
									if (field.getValidation() != null && !"".equals(field.getValidation())) {
										Map<String,Object> paramObj = new Gson().fromJson(field.getValidation(),Map.class);
										if (paramObj.containsKey("regex")) {
											String regex = (String) paramObj.get("regex");
											if (value.matches(regex)){
												String test = "";
											}
										}
									}
									break;
								case "BLN":
									if (inputList.get(fieldName) instanceof Boolean) {
										Boolean blnVal =  (Boolean) inputList.get(fieldName);
										if ( field.getRequired() && blnVal == null && language.isDefaultLang()){
											isValid = false;
										}
									} else {
										String blnVal =  (String) inputList.get(fieldName);
										if ( field.getRequired() && (blnVal == null || (blnVal != null && blnVal.isEmpty())) && language.isDefaultLang()){
											isValid = false;
										}
									}
									
									break;
								case "SLT":
									
									break;
							}
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						if (field.getRequired() && !"MGRP".equals(field.getFieldType())) {
							// missing field
							isValid = false;
							break;
						}
					}
				}
			}
		}
		return isValid;
	}
	
	@SuppressWarnings("unchecked")
	public void marshallFields(RestRequest request, RestResponse response) throws Exception{
		
		// Must have item to map to
		if (request.getParam(GlobalConstant.ITEM) == null){
			throw new Exception("Missing Item");
		}
		Object item = request.getParam(GlobalConstant.ITEM);
		
		
		
		
		Boolean isValid = true;
		Map<String,Object> inputList = (Map<String, Object>) request.getParam("inputFields");
		List<String> prefForms = (List<String>) request.getParam(PrefCacheUtil.PREFFORMKEYS);
		Map<String,Map<String,List<PrefFormFieldValue>>> prefFields = (Map<String, Map<String, List<PrefFormFieldValue>>>) request.getParam("prefFormFields");
		List<PrefFormFieldValue> subGroups = new ArrayList<PrefFormFieldValue>();
		List<Language> languages = (List<Language>) request.getParam("LANGUAGES");
		
		// loop through each form that was requested
		for (String formKey : prefForms) {
			List<PrefFormFieldValue> formFields = (List<PrefFormFieldValue>) prefFields.get(formKey);
			// loop through each field that is available for this form
			for (PrefFormFieldValue field : formFields) {
				if ("MGRP".equals(field.getFieldType())) {
					subGroups.add(field);
				}
				String fieldName = field.getName();
				fillField(item, field, fieldName, inputList, languages);
				
			} // for formfields
			// multilingual sub group fields
			for(PrefFormFieldValue subGroup : subGroups) {
				List<Object> subItems = new ArrayList<Object>();
				Map<String,Object> paramObj = new Gson().fromJson(subGroup.getClassModel(),Map.class);
				String groupClassName = (String) paramObj.get("groupClazz");
				String groupName = (String) paramObj.get("groupName");
				for (Language language : languages) {
					Object subItem = Class.forName(groupClassName).getConstructor(String.class).newInstance(language.getCode());
					subItems.add(subItem);
					
					for (PrefFormFieldValue field : formFields) {
						if (field.getSubGroup() == null ||  (field.getSubGroup() != null && !field.getSubGroup().equals(groupName))) {
							continue;
						}
					
						String fieldName = field.getName()+"-"+language.getCode();
						fillField(subItem, field, fieldName, inputList, languages);
					}
				}
				for(Object subItem : subItems) {
					Class<?> instanceClass = item.getClass();
					while(instanceClass != null) {
						try {
							String methodName = (String) paramObj.get("method");
							Method m = instanceClass.getDeclaredMethod(methodName,Object.class);
							m.invoke(item, subItem);
						} catch (NoSuchMethodException e) {
							//e.printStackTrace();
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
						instanceClass = instanceClass.getSuperclass();
					}
				}
			}
			
		} // for prefforms
		
	} // marshallFields
	
	private void fillField(Object item, PrefFormFieldValue field, String fieldName, Map<String,Object>inputList, List<Language> languages) throws Exception {
		
		Class stringParams[] = new Class[1];
		stringParams[0] = String.class;
		
		Class longParams[] = new Class[1];
		longParams[0] = Long.class;
		String existingObjName = item.getClass().getName();
		
		if (inputList.containsKey(fieldName)) {
			Map<String,Object> paramObj = new Gson().fromJson(field.getClassModel(),Map.class);
			String clazzName = (String) paramObj.get("clazz");
			if (!existingObjName.equals(clazzName)) {
				// Object does not exist need to
				return;
				//Class clazz = Class.forName(clazzName);
				//itemMap.put(clazzName, clazz.newInstance());
			}

				String value = null;
				Class<?> instanceClass = item.getClass();
				while(instanceClass != null) {
					try {
						switch (field.getFieldType()) {
						case "TXT":
							value = (String) inputList.get(fieldName);
							if (value != null){
								if (paramObj.containsKey("method")) {
									String methodName = (String) paramObj.get("method");
									if (methodName != null) {
										Method m = instanceClass.getDeclaredMethod(methodName,stringParams);
										m.invoke(item, value);
									}
								} else {
									String paramField = (String) paramObj.get("field");
									if (paramField != null){
										Field f = instanceClass.getDeclaredField(paramField);
										f.setAccessible(true);
										f.set(item, value);
									}
									
								}
							}
							break;
						case "TXTAREA":
							value = (String) inputList.get(fieldName);
							if (value != null){
								if (paramObj.containsKey("method")) {
									String methodName = (String) paramObj.get("method");
									if (methodName != null) {
										Method m = instanceClass.getDeclaredMethod(methodName,stringParams);
										m.invoke(item, value);
									}
								} else {
									String paramField = (String) paramObj.get("field");
									if (paramField != null){
										Field f = instanceClass.getDeclaredField(paramField);
										f.setAccessible(true);
										f.set(item, value);
									}
									
								}
							}
							break;
						case "TXTDOUBLE":
							value = (String) inputList.get(fieldName);
							if (value != null){
								double valueDouble = Double.parseDouble(value);
							
								String paramField = (String) paramObj.get("field");
								if (paramField != null){
									Field f = instanceClass.getDeclaredField(paramField);
									f.setAccessible(true);
									f.set(item, valueDouble);
								}
							}
							break;
						case "TXTFLOAT":
							value = (String) inputList.get(fieldName);
							if (value != null){
								float v = Float.parseFloat(value);
								String paramField = (String) paramObj.get("field");
								if (paramField != null){
									Field f = instanceClass.getDeclaredField(paramField);
									f.setAccessible(true);
									f.set(item, v);
								}
							}
							break;
						case "BLN":
							Boolean boolValue = (Boolean) inputList.get(fieldName);
							if (boolValue != null){
								//Boolean b = Boolean.parseBoolean(value);
								String paramField = (String) paramObj.get("field");
								if (paramField != null){
									Field f = instanceClass.getDeclaredField(paramField);
									f.setAccessible(true);
									f.set(item, boolValue);
								}
							} else {
								// Check for default
								
							}
							break;
						case "SLT":
							if (inputList.get(fieldName) instanceof Integer) {
								value = String.valueOf(inputList.get(fieldName));
							} else {
								value = (String) inputList.get(fieldName);
							}
							if (value != null){
								if (paramObj.containsKey("method")) {
									String methodName = (String) paramObj.get("method");
									if (methodName != null) {
										if ("Long".equalsIgnoreCase((String) paramObj.get("type"))) {
											Long id = Long.parseLong(value);
											Method m = instanceClass.getDeclaredMethod(methodName,longParams);
											m.invoke(item, id);
										} else {
											Method m = instanceClass.getDeclaredMethod(methodName,stringParams);
											m.invoke(item, value);
										}
									}
								} else {
									String paramField = (String) paramObj.get("field");
									if (paramField != null){
										Field f = instanceClass.getDeclaredField(paramField);
										f.setAccessible(true);
										f.set(item, value);
									}
								}
							}
							break;
						case "LTXT":
							Map<String,String> valueMap = (Map<String,String>) inputList.get(fieldName);
							if (valueMap != null){
								String methodName = (String) paramObj.get("method");
								if (methodName != null){
									Class[] paramMap = new Class[1];
									paramMap[0] = Map.class;
									Method m = instanceClass.getMethod(methodName,paramMap);
									m.invoke(item, valueMap);
								}
							}
							break;
						case "MTXT":
							// Marshall Default
							String defaultValue = (String) inputList.get(fieldName.concat("-DEFAULT"));
							if (defaultValue != null){
								String methodName = (String) ((Map<String,String>) paramObj.get("defaultClazz")).get("method");
								if (methodName != null){
									Method m = instanceClass.getMethod(methodName,stringParams);
									m.invoke(item, defaultValue);
								}
							}
							// Marshall Text
							
							for (Language language : languages) {
								String textValue = (String) inputList.get(fieldName.concat("-TEXT-").concat(language.getCode()));
								if (textValue != null){
									String methodName = (String) ((Map<String,String>) paramObj.get("textClazz")).get("method");
									if (methodName != null){
										Map<String,String> valMap = new HashMap<String,String>();
										valMap.put(language.getCode(), textValue);
										Class[] paramMap = new Class[1];
										paramMap[0] = Map.class;
										Method m = instanceClass.getMethod(methodName,paramMap);
										m.invoke(item, valMap);
									}
								}
							}
							break;
						case "MBLN":
							Map<String,String> bvaluesMap = (Map<String,String>) inputList.get(fieldName);
							if (bvaluesMap != null){
								String methodName = (String) paramObj.get("method");
								if (methodName != null){
									String paramField = (String) paramObj.get("field");
									if (paramField != null){
										bvaluesMap.put(GlobalConstant.FIELD, paramField);
									}
									Class[] paramMap = new Class[1];
									paramMap[0] = Map.class;
									Method m = instanceClass.getMethod(methodName,paramMap);
									m.invoke(item, bvaluesMap);
								}
							}
							break;
						case "MDLSNG":
							value = (String) inputList.get(fieldName);
							if (value != null){
								
								if (paramObj.containsKey("method")) {
									String methodName = (String) paramObj.get("method");
									if (methodName != null) {
										if(paramObj.containsKey("type") && "String".equals((String) paramObj.get("type"))) {
											Method m = instanceClass.getDeclaredMethod(methodName,stringParams);
											m.invoke(item, value);
										} else {
											Long id = Long.parseLong(value);
											Method m = instanceClass.getDeclaredMethod(methodName,longParams);
											m.invoke(item, id);
										}
									}
								}
							}
							
							break;	
						case "DATE":
							String dateString = (String) inputList.get(fieldName);
							if (!"".equals(dateString)) {
								Instant instant = Instant.parse(dateString);
								if (instant != null){
									String paramField = (String) paramObj.get("field");
									if (paramField != null){
										Field f = instanceClass.getDeclaredField(paramField);
										f.setAccessible(true);
										f.set(item, instant);
									}
								}
							}
							break;
						case "INT":
							Integer	i = 0;
							if (inputList.get(fieldName) instanceof String ) {
								String myInt = (String) inputList.get(fieldName);
								if (!"".equals(myInt)) {
									i = Integer.parseInt(myInt);
								}
							} else {
								i = (Integer) inputList.get(fieldName);
							}
							if (i != null){
								String paramField = (String) paramObj.get("field");
								if (paramField != null){
									Field f = instanceClass.getDeclaredField(paramField);
									f.setAccessible(true);
									f.set(item, i);
								}
							}
							break;
						}
						
					} catch (NoSuchFieldException e) {
						//e.printStackTrace();
					} catch (NoSuchMethodException e) {
						//e.printStackTrace();
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
					instanceClass = instanceClass.getSuperclass();
				}
			
		} else {
				// need to finish
		}
	}
	public byte[] createThumbNail(byte[] imgBytes) throws IOException{
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
		img = Scalr.resize(img, Scalr.Method.SPEED, 1200, Scalr.OP_ANTIALIAS, Scalr.OP_BRIGHTER);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( img, "jpg", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}
	/*
	//get uploaded filename
	public Map<String,String> getDispositionAttributes(MultivaluedMap<String, String> header) {
		Map<String,String> items = new HashMap<String,String>();
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
	
		for (String item : contentDisposition) {
			if (item.contains("=")) {
				item = item.trim();
				String[] attribute = item.split("=");
				if (attribute.length > 1){
					items.put(attribute[0], attribute[1]);
				}
			}
		}
		return items;
	}
	
	public String getContentType(MultivaluedMap<String, String> header) {
		String[] contentType = header.getFirst("Content-Type").split(";");
		return contentType[0];
	}
	*/
	public byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 2000;
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
	}
	
	public byte[] generateSalt() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}
	
	public String createRandomPass(int len) throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		String letters = "abcdefghjkmnpqrstuvwxyz#ABCDEFGHJKMNPQRSTUVWXYZ23456789+@";
		String password = "";
	    for (int i=0; i<len; i++) {
	    	int index = (int)(random.nextDouble()*letters.length());
	        password += letters.substring(index, index+1);
	    }
	    return password;
	}
	
	public void metricsAPIStart(RestRequest request) throws Exception{
		if (request.containsParam("metrics")){
			Map<String,Long> metrics = (Map<String, Long>) request.getParam("metrics");
			metrics.put("APIStart", System.currentTimeMillis());
		} else {
			Map<String,Long> metrics = new HashMap<String,Long>();
			metrics.put("APIStart", System.currentTimeMillis());
			request.addParam("metrics", metrics);
		}
	}
	
	public void metricsAPIEnd(RestRequest request) throws Exception{
		if (request.containsParam("metrics")){
			Map<String,Long> metrics = (Map<String, Long>) request.getParam("metrics");
			metrics.put("APIEnd", System.currentTimeMillis());
		} else {
			Map<String,Long> metrics = new HashMap<String,Long>();
			metrics.put("APIEnd", System.currentTimeMillis());
			request.addParam("metrics", metrics);
		}
	}
}
