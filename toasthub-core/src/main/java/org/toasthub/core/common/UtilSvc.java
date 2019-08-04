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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.ResponseStatus;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.model.StatusMessage;
import org.toasthub.core.preference.model.AppCachePageUtil;
import org.toasthub.core.preference.model.AppPageFormFieldValue;
import org.toasthub.core.preference.model.AppPageOptionValue;

import com.google.gson.Gson;

@Service("UtilSvc")
public class UtilSvc {
	
	@Autowired 
	AppCachePageUtil appCachePageUtil;
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
		
		if (request.getParam(GlobalConstant.PAGESTART) == null){
			request.addParam(GlobalConstant.PAGESTART, 0);
		}
		
		AppPageOptionValue globalPageLimit = appCachePageUtil.getAppOption("GLOBAL_PAGE", "GLOBAL_PAGE_PAGELIMIT",(String)request.getParam(GlobalConstant.LANG));
		AppPageOptionValue globalPageLimitMax = appCachePageUtil.getAppOption("GLOBAL_PAGE", "GLOBAL_PAGE_PAGELIMIT_MAX",(String)request.getParam(GlobalConstant.LANG));
		if (request.getParam(GlobalConstant.PAGELIMIT) == null){
			if (globalPageLimit != null) {
				if (!"".equals(globalPageLimit.getValue())) {
					request.addParam(GlobalConstant.PAGELIMIT, Integer.parseInt(globalPageLimit.getValue()));
				} else {
					request.addParam(GlobalConstant.PAGELIMIT, Integer.parseInt(globalPageLimit.getDefaultValue()));
				}
			} else {
				request.addParam(GlobalConstant.PAGELIMIT, 20);
			}
		} else {
			Integer max = 200;
			if (globalPageLimitMax != null) {
				if (!"".equals(globalPageLimitMax.getValue())) {
					max = Integer.parseInt(globalPageLimitMax.getValue());
				} else {
					max = Integer.parseInt(globalPageLimitMax.getDefaultValue());
				}
			}
			if ((Integer) request.getParam(GlobalConstant.PAGELIMIT) > max ) {
				if (!"".equals(globalPageLimit.getValue())) {
					request.addParam(GlobalConstant.PAGELIMIT, Integer.parseInt(globalPageLimit.getValue()));
				} else {
					request.addParam(GlobalConstant.PAGELIMIT, Integer.parseInt(globalPageLimit.getDefaultValue()));
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
	
	@SuppressWarnings("unchecked")
	public void addStatus(String level, String status, String message, RestResponse response) {
		
		response.setStatus(status);
		StatusMessage statusMessage = new StatusMessage(status, message);
		// check for status object
		if (response.getParam(RestResponse.STATUS) == null){
			response.addParam(RestResponse.STATUS, new ResponseStatus());
		}
		ResponseStatus responseStatus = (ResponseStatus) response.getParam(RestResponse.STATUS);
		switch (level) {
		case RestResponse.INFO :
			if (responseStatus.getInfo() != null) {
				responseStatus.getInfo().add(statusMessage);
			} else {
				responseStatus.setInfo(new ArrayList<StatusMessage>());
				responseStatus.getInfo().add(statusMessage);
			}
			break;
		case RestResponse.WARN :
			if (responseStatus.getWarn() != null) {
				responseStatus.getWarn().add(statusMessage);
			} else {
				responseStatus.setWarn(new ArrayList<StatusMessage>());
				responseStatus.getWarn().add(statusMessage);
			}
			break;
		case RestResponse.ERROR :
			if (responseStatus.getError() != null) {
				responseStatus.getError().add(statusMessage);
			} else {
				responseStatus.setError(new ArrayList<StatusMessage>());
				responseStatus.getError().add(statusMessage);
			}
			break;
		default :
			if (responseStatus.getWarn() != null) {
				responseStatus.getWarn().add(statusMessage);
			} else {
				responseStatus.setWarn(new ArrayList<StatusMessage>());
				responseStatus.getWarn().add(statusMessage);
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
		Boolean isValid = true;
		//Map<String,Object> params = request.getParams();
		Map<String,Object> inputList = (Map<String, Object>) request.getParam("inputFields");
		List<String> appForms = (List<String>) request.getParam("appForms");
		Map<String,Map<String,List<AppPageFormFieldValue>>> appFields = (Map<String, Map<String, List<AppPageFormFieldValue>>>) request.getParam("appPageFormFields");
		// loop through each form that was requested
		for (String formKey : appForms) {
			List<AppPageFormFieldValue> formFields = (List<AppPageFormFieldValue>) appFields.get(formKey);
			// loop through each field that is available for this form
			for (AppPageFormFieldValue field : formFields) {
				if (inputList.containsKey(field.getName())) {
					try {
						switch (field.getFieldType()) {
							case "TXT":
								// check if required
								String value = (String) inputList.get(field.getName());
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
							case "MTXT":
								
								
								break;
							case "BLN":
								
								break;
							case "MBLN":
								
								break;
							case "MDLSNG":
								
								break;
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					if (field.getRequired() && !"GRP".equals(field.getFieldType())) {
						// missing field
						isValid = false;
						break;
					}
				}
			}
		}
		request.addParam(GlobalConstant.VALID, isValid);
	} // validateParams 	
	
	@SuppressWarnings("unchecked")
	public void marshallFields(RestRequest request, RestResponse response) throws Exception{
		
		// Must have item to map to
		if (request.getParam(GlobalConstant.ITEM) == null){
			throw new Exception("Missing Item");
		}
		Object item = request.getParam(GlobalConstant.ITEM);
		String existingObjName = item.getClass().getName();
		
		Class stringParams[] = new Class[1];
		stringParams[0] = String.class;
		
		Class longParams[] = new Class[1];
		longParams[0] = Long.class;
		
		Boolean isValid = true;
		Map<String,Object> inputList = (Map<String, Object>) request.getParam("inputFields");
		List<String> appForms = (List<String>) request.getParam("appForms");
		Map<String,Map<String,List<AppPageFormFieldValue>>> appFields = (Map<String, Map<String, List<AppPageFormFieldValue>>>) request.getParam("appPageFormFields");
	
		
		// loop through each form that was requested
		for (String formKey : appForms) {
			List<AppPageFormFieldValue> formFields = (List<AppPageFormFieldValue>) appFields.get(formKey);
			// loop through each field that is available for this form
			for (AppPageFormFieldValue field : formFields) {
				
				if (inputList.containsKey(field.getName())) {
					Map<String,Object> paramObj = new Gson().fromJson(field.getClassModel(),Map.class);
					String clazzName = (String) paramObj.get("clazz");
					if (!existingObjName.equals(clazzName)) {
						// Object does not exist need to
						continue;
						//Class clazz = Class.forName(clazzName);
						//itemMap.put(clazzName, clazz.newInstance());
					}

						String value = null;
						Class<?> instanceClass = item.getClass();
						while(instanceClass != null) {
							try {
								switch (field.getFieldType()) {
								case "TXT":
									value = (String) inputList.get(field.getName());
									if (value != null){
										if (paramObj.containsKey("method")) {
											String methodName = (String) paramObj.get("method");
											if (methodName != null) {
												Method m = instanceClass.getDeclaredMethod(methodName,stringParams);
												m.invoke(item, value);
											}
										} else {
											String fieldName = (String) paramObj.get("field");
											if (fieldName != null){
												Field f = instanceClass.getDeclaredField(fieldName);
												f.setAccessible(true);
												f.set(item, value);
											}
											
										}
									}
									break;
								case "TXTDOUBLE":
									value = (String) inputList.get(field.getName());
									if (value != null){
										double valueDouble = Double.parseDouble(value);
									
										String fieldName = (String) paramObj.get("field");
										if (fieldName != null){
											Field f = instanceClass.getDeclaredField(fieldName);
											f.setAccessible(true);
											f.set(item, valueDouble);
										}
									}
									break;
								case "TXTFLOAT":
									value = (String) inputList.get(field.getName());
									if (value != null){
										float v = Float.parseFloat(value);
										String fieldName = (String) paramObj.get("field");
										if (fieldName != null){
											Field f = instanceClass.getDeclaredField(fieldName);
											f.setAccessible(true);
											f.set(item, v);
										}
									}
									break;
								case "BLN":
									value = (String) inputList.get(field.getName());
									if (value != null){
										Boolean b = Boolean.parseBoolean(value);
										String fieldName = (String) paramObj.get("field");
										if (fieldName != null){
											Field f = instanceClass.getDeclaredField(fieldName);
											f.setAccessible(true);
											f.set(item, b);
										}
									}
									break;
								case "SLT":
									value = (String) inputList.get(field.getName());
									if (value != null){
										String fieldName = (String) paramObj.get("field");
										if (fieldName != null){
											Field f = instanceClass.getDeclaredField(fieldName);
											f.setAccessible(true);
											f.set(item, value);
										}
									}
									break;
								case "LTXT":
									Map<String,String> valueMap = (Map<String,String>) inputList.get(field.getName());
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
									Map<String,String> valuesMap = (Map<String,String>) inputList.get(field.getName());
									if (valuesMap != null){
										String methodName = (String) paramObj.get("method");
										if (methodName != null){
											String fieldName = (String) paramObj.get("field");
											if (fieldName != null){
												valuesMap.put(GlobalConstant.FIELD, fieldName);
											}
											Class[] paramMap = new Class[1];
											paramMap[0] = Map.class;
											Method m = instanceClass.getMethod(methodName,paramMap);
											m.invoke(item, valuesMap);
										}
									}
									break;
								case "MBLN":
									Map<String,String> bvaluesMap = (Map<String,String>) inputList.get(field.getName());
									if (bvaluesMap != null){
										String methodName = (String) paramObj.get("method");
										if (methodName != null){
											String fieldName = (String) paramObj.get("field");
											if (fieldName != null){
												bvaluesMap.put(GlobalConstant.FIELD, fieldName);
											}
											Class[] paramMap = new Class[1];
											paramMap[0] = Map.class;
											Method m = instanceClass.getMethod(methodName,paramMap);
											m.invoke(item, bvaluesMap);
										}
									}
									break;
								case "MDLSNG":
									value = (String) inputList.get(field.getName());
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
			} // for formfields
		} // for appforms
		
	} // marshallFields
	
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
