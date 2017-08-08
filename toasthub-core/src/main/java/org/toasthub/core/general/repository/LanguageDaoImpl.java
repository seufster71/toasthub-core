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

package org.toasthub.core.general.repository;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Language;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.service.EntityManagerDataSvc;
import org.toasthub.core.general.service.UtilSvc;

@Repository("LanguageDao")
@Transactional("TransactionManagerData")
public class LanguageDaoImpl implements LanguageDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts as lt ";
		
		boolean and = false;
		if (request.containsParam(BaseEntity.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
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
		List<Language> languages = query.getResultList();

		response.addParam(BaseEntity.ITEMS, languages);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(*) FROM Language as l JOIN l.title AS t JOIN t.langTexts as lt ";
		boolean and = false;
		if (request.containsParam(BaseEntity.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		if (request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
			queryStr += "lt.lang =:lang AND lt.text LIKE :searchValue"; 
			and = true;
		}

		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
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
			String queryStr = "SELECT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts WHERE l.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((String) request.getParam(BaseEntity.ITEMID)));
			Language language = (Language) query.getSingleResult();
			
			response.addParam(BaseEntity.ITEM, language);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

	@Override
	public void getDefault(RestRequest request, RestResponse response) throws Exception {

		String queryStr = "SELECT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts WHERE l.defaultLang =:defaultLang";
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);

		query.setParameter("defaultLang", true);
		Language language = (Language) query.getSingleResult();
		
		response.addParam(BaseEntity.ITEM, language);
		
	}

	@Override
	public void getAllLanguages(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts as lt ";
		
		boolean and = false;
		if (request.containsParam(BaseEntity.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(BaseEntity.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(BaseEntity.ACTIVE));
		} 
		
		
		@SuppressWarnings("unchecked")
		List<Language> languages = query.getResultList();
		
		// clean out things that are not needed at this time
		for(Language language : languages){
			language.setArchive(null);
			language.setCreated(null);
			language.setLocked(null);
			language.setLockOwnerRefId(null);
			language.setModified(null);
			language.setLockTime(null);
		}

		response.addParam(BaseEntity.ITEMS, languages);
	}


}
