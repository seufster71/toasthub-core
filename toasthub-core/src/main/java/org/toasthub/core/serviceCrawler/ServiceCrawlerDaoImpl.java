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

package org.toasthub.core.serviceCrawler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.model.ServiceClass;


@Repository("ServiceCrawlerDao")
@Transactional("TransactionManagerData")
public class ServiceCrawlerDaoImpl implements ServiceCrawlerDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Map<String,ServiceClass>> getServices() {
		String queryStr = "FROM ServiceClass AS sc where active = true";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		// get all services
		List<ServiceClass> services = (List<ServiceClass>) query.getResultList();
		
		Map<String,Map<String,ServiceClass>> results = new ConcurrentHashMap<String,Map<String,ServiceClass>>();
		
		for (ServiceClass serviceClass : services) {
			if (!results.containsKey(serviceClass.getCategory())){
				// if category does not exist create it
				results.put(serviceClass.getCategory(), new ConcurrentHashMap<String,ServiceClass>());
			}
			// add service to category
			results.get(serviceClass.getCategory()).put(serviceClass.getServiceKey(), serviceClass);
		}
		return results;
	}


	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT s FROM ServiceClass AS s ";
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "s.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHCOLUMN) && request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lower(s." + request.getParam(GlobalConstant.SEARCHCOLUMN) + ") LIKE :searchValue"; 
			and = true;
		}
		if (request.containsParam(GlobalConstant.ORDERCOLUMN)) {
			String direction = "DESC";
			if (request.containsParam(GlobalConstant.ORDERDIR)) {
				direction = (String) request.getParam(GlobalConstant.ORDERDIR);
			}
			queryStr += " ORDER BY "+(String) request.getParam(GlobalConstant.ORDERCOLUMN)+" "+direction;
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
		}
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		@SuppressWarnings("unchecked")
		List<ServiceClass> services = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, services);
		
	}


	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(*) FROM ServiceClass AS s ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "s.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "s.serviceName LIKE :searchValue"; 
			and = true;
		}

		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
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
			String queryStr = "SELECT s FROM ServiceClass AS s WHERE s.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((String) request.getParam(GlobalConstant.ITEMID)));
			ServiceClass serviceClass = (ServiceClass) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, serviceClass);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
	}

}
