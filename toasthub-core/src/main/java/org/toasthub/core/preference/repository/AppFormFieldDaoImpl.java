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

package org.toasthub.core.preference.repository;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppPageFormFieldName;
import org.toasthub.core.preference.model.AppPageFormFieldValue;

@Repository("AppFormFieldDao")
@Transactional("TransactionManagerData")
public class AppFormFieldDaoImpl implements AppFormFieldDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	public List<AppPageFormFieldValue> getFormFields(String pageName, String lang) {
		@SuppressWarnings("unchecked")
		List<AppPageFormFieldValue> formFields = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW AppPageFormFieldValue(f.id, f.value, f.label, f.lang, f.rendered, f.required, f.order, f.validation, f.image, f.pageFormFieldName.name, f.pageFormFieldName.fieldType, f.pageFormFieldName.htmlType, f.pageFormFieldName.className, f.pageFormFieldName.group, f.pageFormFieldName.subGroup, f.pageFormFieldName.tabIndex, f.pageFormFieldName.optionalParams, f.pageFormFieldName.classModel) FROM AppPageFormFieldValue f WHERE f.lang =:lang AND f.pageFormFieldName.pageName.name =:pageName AND f.pageFormFieldName.archive = false ORDER BY f.pageFormFieldName.group ASC, f.order ASC")
			.setParameter("pageName", pageName)
			.setParameter("lang", lang)
			.getResultList();
		return formFields;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		List<AppPageFormFieldName> formFields = entityManagerDataSvc.getInstance()
				.createQuery("SELECT DISTINCT f FROM AppPageFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts AS l JOIN FETCH f.values WHERE f.pageName.id =:pageNameId")
				.setParameter("pageNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)))
				.getResultList();
			response.addParam("appPageFormField", formFields);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT COUNT(*) FROM AppPageFormFieldName AS f WHERE f.pageName.id =:pageNameId";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery).setParameter("pageNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT f FROM AppPageFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts as l JOIN FETCH f.values WHERE f.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			AppPageFormFieldName appPageFormFieldName = (AppPageFormFieldName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, appPageFormFieldName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

}
