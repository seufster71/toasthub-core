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
import org.toasthub.core.preference.model.PrefOptionName;
import org.toasthub.core.preference.model.PrefOptionValue;

@Repository("PrefOptionDao")
@Transactional("TransactionManagerData")
public class PrefOptionDaoImpl implements PrefOptionDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	public List<PrefOptionValue> getOptions(String prefName, String lang) {
		@SuppressWarnings("unchecked")
		List<PrefOptionValue> options = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW PrefOptionValue(o.id, o.value, o.lang, o.rendered, o.validation, o.prefOptionName.name, o.prefOptionName.valueType, o.prefOptionName.defaultValue, o.prefOptionName.useDefault) FROM PrefOptionValue o WHERE o.lang =:lang AND o.prefOptionName.prefName.name =:prefName AND o.prefOptionName.archive = false")
			.setParameter("prefName", prefName)
			.setParameter("lang", lang)
			.getResultList();
		return options;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT prefOption FROM PrefOptionName AS prefOption JOIN FETCH prefOption.title AS t JOIN FETCH t.langTexts AS lt JOIN FETCH prefOption.values WHERE lt.lang =:lang AND prefOption.prefName.id =:prefNameId";
		
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
		List<PrefOptionName> options = query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, options);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT COUNT(DISTINCT prefOption) FROM PrefOptionName AS prefOption WHERE prefOption.prefName.id =:prefNameId";
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
			String queryStr = "SELECT po FROM PrefOptionName AS po JOIN FETCH po.title AS t JOIN FETCH t.langTexts as l JOIN FETCH po.values WHERE po.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			PrefOptionName prefOptionName = (PrefOptionName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, prefOptionName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

}
