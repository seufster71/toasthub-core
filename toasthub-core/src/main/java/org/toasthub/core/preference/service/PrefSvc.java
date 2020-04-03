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

package org.toasthub.core.preference.service;

import java.util.List;
import java.util.Map;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefFormFieldValue;
import org.toasthub.core.preference.model.PrefLabelValue;
import org.toasthub.core.preference.model.PrefName;
import org.toasthub.core.preference.model.PrefOptionValue;
import org.toasthub.core.preference.model.PrefTextValue;

public interface PrefSvc {

	public PrefName getPrefName(Long id);
	public PrefName getPrefName(String name);
	public List<PrefFormFieldValue> getFormFields(String prefName, String lang);
	public Map<String, PrefFormFieldValue> getFormFieldsMap(String prefName,String lang);
	public List<PrefLabelValue> getLabels(String prefName, String lang);
	public List<PrefOptionValue> getOptions(String prefName, String lang);
	public Map<String, PrefOptionValue> getOptionsMap(String prefName, String lang);
	public List<PrefTextValue> getTexts(String prefName, String lang);
	public Map<String, PrefTextValue> getTextsMap(String prefName, String lang);
	
	public void itemCount(RestRequest request, RestResponse response);
	public void items(RestRequest request, RestResponse response);
	public void item(RestRequest request, RestResponse response);
}
