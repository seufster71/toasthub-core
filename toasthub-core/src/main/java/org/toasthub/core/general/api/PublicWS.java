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

package org.toasthub.core.general.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.model.ServiceClass;
import org.toasthub.core.serviceCrawler.MicroServiceClient;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.AppCacheServiceCrawler;

import com.fasterxml.jackson.annotation.JsonView;

@RestController()
@RequestMapping("/api/public")
public class PublicWS {

	@Autowired 
	UtilSvc utilSvc;
	
	@Autowired
	AppCacheServiceCrawler serviceCrawler;
	
	@Autowired
	MicroServiceClient microServiceClient;
	
	@JsonView(View.Public.class)
	@RequestMapping(value = "callService", method = RequestMethod.POST)
	public RestResponse callService(@RequestBody RestRequest request) {

		RestResponse response = new RestResponse();
		// set defaults
		utilSvc.setupDefaults(request);
		// validate request

		
		// call service locator
		ServiceClass serviceClass = serviceCrawler.getServiceClass("PUBLIC",(String) request.getParam(GlobalConstant.SERVICE),
				(String) request.getParam(GlobalConstant.SVCAPIVERSION), (String) request.getParam(GlobalConstant.SVCAPPVERSION));
		// process 
		if (serviceClass != null) {
			if ("LOCAL".equals(serviceClass.getLocation()) && serviceClass.getServiceProcessor() != null) {
				// use local service
				serviceClass.getServiceProcessor().process(request, response);
			} else {
				// use remote service
				request.addParam(GlobalConstant.MICROSERVICENAME, "service-public");
				request.addParam(GlobalConstant.MICROSERVICEPATH, "api/public");
				microServiceClient.process(request, response);
			}
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.EXECUTIONFAILED, "Service is not available", response);
		}
		
		return response;
	}
}
