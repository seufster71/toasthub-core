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
import org.toasthub.core.preference.model.AppPageName;

@Repository("AppPageDao")
@Transactional("TransactionManagerData")
public class AppPageDaoImpl implements AppPageDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	public AppPageName getPageName(Long id) {
		AppPageName pagename = (AppPageName) entityManagerDataSvc.getInstance()
			.createQuery("FROM AppPageName where id = :id")
			.setParameter("id",id)
			.getSingleResult();
		return pagename;
	}

	public AppPageName getPageName(String name) {
		AppPageName pagename = (AppPageName) entityManagerDataSvc.getInstance()
			.createQuery("FROM AppPageName where name = :name")
			.setParameter("name",name)
			.getSingleResult();
		return pagename;
	}

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT p FROM AppPageName AS p JOIN FETCH p.title AS t JOIN FETCH t.langTexts AS l WHERE p.category =:category AND p.active =:active";
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			queryStr += " AND l.lang =:lang AND l.text LIKE :searchValue"; 
		}
		
		queryStr += " ORDER BY p.name ASC";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("category", (String) request.getParam("category"));
	
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		
		if (request.containsParam(GlobalConstant.PAGELIMIT) && (Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		List<?> results = query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, (List<AppPageName>) results);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String queryStr = "SELECT COUNT(*) FROM AppPageName AS p JOIN p.title AS t JOIN t.langTexts AS l WHERE p.active = true AND category =:category AND p.active =:active";
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			queryStr += " AND l.lang =:lang AND l.text LIKE :searchValue"; 
		}
		query = entityManagerDataSvc.getInstance().createQuery(queryStr).setParameter("category", (String) request.getParam("category"));
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
	
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String HQLQuery = "SELECT p FROM AppPageName AS p JOIN FETCH p.title AS t JOIN FETCH t.langTexts WHERE p.id = :id ";
			
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)) );
			response.addParam(GlobalConstant.ITEM, query.getSingleResult());
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
		
		
	}

}
