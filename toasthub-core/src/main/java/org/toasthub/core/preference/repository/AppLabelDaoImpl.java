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
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.service.EntityManagerDataSvc;
import org.toasthub.core.general.service.UtilSvc;
import org.toasthub.core.preference.model.AppPageLabelName;
import org.toasthub.core.preference.model.AppPageLabelValue;

@Repository("AppLabelDao")
@Transactional("TransactionManagerData")
public class AppLabelDaoImpl implements AppLabelDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	public List<AppPageLabelValue> getLabels(String pageName, String lang) {
		@SuppressWarnings("unchecked")
		List<AppPageLabelValue> labels = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW AppPageLabelValue(l.id, l.value, l.lang, l.rendered, l.order, l.pageLabelName.name, l.pageLabelName.className, l.pageLabelName.tabIndex,l.pageLabelName.optionalParams) FROM AppPageLabelValue l WHERE l.lang =:lang AND l.pageLabelName.pageName.name =:pageName AND l.pageLabelName.archive = false ORDER BY l.order ASC")
			.setParameter("pageName", pageName)
			.setParameter("lang", lang)
			.getResultList();
		return labels;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		List<AppPageLabelName> labels = entityManagerDataSvc.getInstance()
				.createQuery("SELECT DISTINCT l FROM AppPageLabelName AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts AS lt JOIN FETCH l.values WHERE l.pageName.id =:pageNameId")
				.setParameter("pageNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)))
				.getResultList();
			response.addParam("appPageLabel", labels);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT COUNT(*) FROM AppPageLabelName AS l WHERE l.pageName.id =:pageNameId";
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery).setParameter("pageNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT l FROM AppPageLabelName AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts AS lt JOIN FETCH l.values WHERE l.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			AppPageLabelName appPageLabelName = (AppPageLabelName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, appPageLabelName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}

}
