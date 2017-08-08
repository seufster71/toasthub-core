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

package org.toasthub.core.general.service;

import java.util.List;
import java.util.Map;

import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface MenuSvc extends ServiceProcessor {

	public void getMenu(RestRequest request, RestResponse response);
	public void getMenus(RestRequest request, RestResponse response);
	public void getMenuCount(RestRequest request, RestResponse response);
	public void getMenuItemCount(RestRequest request, RestResponse response);
	public void getMenuItems(RestRequest request, RestResponse response);
	public List<MenuItem> getMenuItems(String menuName, String lang);
	public Map<Integer,MenuItem> getMenu(String menuName, String apiVersion, String appVersion, String lang);
	
}
