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

package org.toasthub.core.system.repository;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.service.EntityManagerMainSvc;
import org.toasthub.core.general.service.UtilSvc;
import org.toasthub.core.system.model.ClientDomain;

@Repository("ClientDomainDao")
@Transactional("TransactionManagerMain")
public class ClientDomainDaoImpl implements ClientDomainDao {
	
	@Autowired 
	protected EntityManagerMainSvc entityManagerMainSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT c FROM ClientDomain AS c JOIN FETCH c.title AS t JOIN FETCH t.langTexts as lt ";
		
		boolean and = false;
		if (request.containsParam(BaseEntity.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "c.active =:active ";
			and = true;
		}
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}
		
		Query query = entityManagerMainSvc.getEntityMgrMain().createQuery(queryStr);
		
		if (request.containsParam(BaseEntity.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(BaseEntity.ACTIVE));
		} 
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(BaseEntity.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(BaseEntity.LANG));
		}
		
		if (request.containsParam(BaseEntity.PAGELIMIT) && (Integer) request.getParam(BaseEntity.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(BaseEntity.PAGESTART));
			query.setMaxResults((Integer) request.getParam(BaseEntity.PAGELIMIT));
		}
		@SuppressWarnings("unchecked")
		List<ClientDomain> clientDomains = query.getResultList();

		response.addParam(BaseEntity.ITEMS, clientDomains);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT c) FROM ClientDomain as c JOIN c.title AS t JOIN t.langTexts as lt ";
		boolean and = false;
		if (request.containsParam(BaseEntity.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "c.active =:active ";
			and = true;
		}
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}

		Query query = entityManagerMainSvc.getEntityMgrMain().createQuery(queryStr);
		
		if (request.containsParam(BaseEntity.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(BaseEntity.ACTIVE));
		} 
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(BaseEntity.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(BaseEntity.LANG));
		}
		
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(BaseEntity.ITEMCOUNT, count);
		
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(BaseEntity.ITEMID) && !"".equals(request.getParam(BaseEntity.ITEMID))) {
			String queryStr = "SELECT c FROM ClientDomain AS c JOIN FETCH c.title AS t JOIN FETCH t.langTexts WHERE c.id =:id";
			Query query = entityManagerMainSvc.getEntityMgrMain().createQuery(queryStr);
		
			query.setParameter("id", new Long((String) request.getParam(BaseEntity.ITEMID)));
			ClientDomain clientDomain = (ClientDomain) query.getSingleResult();
			
			response.addParam(BaseEntity.ITEM, clientDomain);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	public ClientDomain getClientDomain(String url) throws Exception {
		
		ClientDomain cdomain = (ClientDomain) entityManagerMainSvc.getEntityMgrMain().createQuery("FROM ClientDomain WHERE URLDomain = :url").setParameter("url",url).getSingleResult();
		return cdomain;
	}
	
	public List<ClientDomain> loadCache() throws Exception {
		List<ClientDomain> clientDomains = entityManagerMainSvc.getEntityMgrMain().createQuery("FROM ClientDomain").getResultList();
		return clientDomains;
	}
}
