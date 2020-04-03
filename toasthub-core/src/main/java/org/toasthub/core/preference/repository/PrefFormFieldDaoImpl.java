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
import org.toasthub.core.preference.model.PrefFormFieldName;
import org.toasthub.core.preference.model.PrefFormFieldValue;

@Repository("PrefFormFieldDao")
@Transactional("TransactionManagerData")
public class PrefFormFieldDaoImpl implements PrefFormFieldDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	public List<PrefFormFieldValue> getFormFields(String prefName, String lang) {
		@SuppressWarnings("unchecked")
		List<PrefFormFieldValue> formFields = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW PrefFormFieldValue(f.id, f.value, f.label, f.lang, f.rendered, f.required, f.order, f.validation, f.image, f.prefFormFieldName.name, f.prefFormFieldName.fieldType, f.prefFormFieldName.htmlType, f.prefFormFieldName.className, f.prefFormFieldName.group, f.prefFormFieldName.subGroup, f.prefFormFieldName.tabIndex, f.prefFormFieldName.optionalParams, f.prefFormFieldName.classModel) FROM PrefFormFieldValue f WHERE f.lang =:lang AND f.prefFormFieldName.prefName.name =:prefName AND f.prefFormFieldName.archive = false ORDER BY f.prefFormFieldName.group ASC, f.order ASC")
			.setParameter("prefName", prefName)
			.setParameter("lang", lang)
			.getResultList();
		return formFields;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT f FROM PrefFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts AS lt JOIN FETCH f.values WHERE lt.lang =:lang AND f.prefName.id =:prefNameId";
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			queryStr += "AND p.active =:active ";
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		query.setParameter("prefNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		
		@SuppressWarnings("unchecked")
		List<PrefFormFieldName> formFields =query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, formFields);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT COUNT(DISTINCT f) FROM PrefFormFieldName AS f WHERE f.prefName.id =:prefNameId";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery).setParameter("prefNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		
		response.addParam(GlobalConstant.ITEMCOUNT, count);
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT f FROM PrefFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts as l JOIN FETCH f.values WHERE f.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			PrefFormFieldName prefFormFieldName = (PrefFormFieldName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, prefFormFieldName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

}
