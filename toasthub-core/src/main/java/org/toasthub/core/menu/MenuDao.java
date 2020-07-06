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

package org.toasthub.core.menu;

import java.util.List;

import org.toasthub.core.general.model.Menu;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;


public interface MenuDao {

	public List<MenuItem> subItems(String menuName,String lang) throws Exception; 
	public Menu item(String menuName, String apiVersion, String appVersion, String lang) throws Exception;
	
	public void item(RestRequest request, RestResponse response) throws Exception;
	public void items(RestRequest request, RestResponse response) throws Exception;
	public void itemCount(RestRequest request, RestResponse response) throws Exception;
	
	public void subItem(RestRequest request, RestResponse response) throws Exception;
	public void subItems(RestRequest request, RestResponse response) throws Exception;
	public void subItemCount(RestRequest request, RestResponse response) throws Exception;
}
