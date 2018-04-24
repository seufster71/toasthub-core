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

package org.toasthub.core.category;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.BaseDaoImpl;
import org.toasthub.core.general.model.Category;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;


@Repository("CategoryDao")
@Transactional("TransactionManagerData")
public class CategoryDaoImpl extends BaseDaoImpl implements CategoryDao {

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT c FROM Category AS c JOIN FETCH c.title AS t JOIN FETCH t.langTexts as lt ";
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "c.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		if (request.containsParam(GlobalConstant.PAGELIMIT) && (Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		@SuppressWarnings("unchecked")
		List<Category> categories = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, categories);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT c) FROM Category as c JOIN c.title AS t JOIN t.langTexts as lt ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "c.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}

		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(GlobalConstant.ITEMCOUNT, count);
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String queryStr = "SELECT c FROM Category AS c JOIN FETCH c.title AS t JOIN FETCH t.langTexts WHERE r.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			Category category = (Category) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, category);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}


	//@Authorize
	public void save(RestRequest request, RestResponse response) throws Exception {
		Category category = (Category) request.getParam("category");
		EntityManager multi = entityManagerDataSvc.getInstance();

		multi.merge(category);
	}
	
}
