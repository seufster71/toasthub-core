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
import org.toasthub.core.preference.model.PrefTextName;
import org.toasthub.core.preference.model.PrefTextValue;

@Repository("PrefTextDao")
@Transactional("TransactionManagerData")
public class PrefTextDaoImpl implements PrefTextDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	public List<PrefTextValue> getTexts(String prefName, String lang) {
		@SuppressWarnings("unchecked")
		List<PrefTextValue> texts = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW PrefTextValue(t.id, t.value, t.lang, t.rendered, t.prefTextName.name) FROM PrefTextValue t WHERE t.lang =:lang AND t.prefTextName.prefName.name =:prefName AND t.prefTextName.archive = false")
			.setParameter("prefName", prefName)
			.setParameter("lang", lang)
			.getResultList();
		return texts;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT prefText FROM PrefTextName AS prefText JOIN FETCH prefText.title AS t JOIN FETCH t.langTexts AS lt JOIN FETCH prefText.values WHERE lt.lang =:lang AND prefText.prefName.id =:prefNameId";
		
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
		List<PrefTextName> texts = query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, texts);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT COUNT(DISTINCT prefText) FROM PrefTextName AS prefText WHERE prefText.prefName.id =:prefNameId";
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
			String queryStr = "SELECT pt FROM PrefTextName AS pt JOIN FETCH pt.title AS t JOIN FETCH t.langTexts as l JOIN FETCH pt.values WHERE pt.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			PrefTextName prefTextName = (PrefTextName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, prefTextName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}

}
