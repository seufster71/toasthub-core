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

package org.toasthub.core.language;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Language;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

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
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
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
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		@SuppressWarnings("unchecked")
		List<Language> languages = query.getResultList();

		response.addParam(GlobalConstant.ITEMS, languages);
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(*) FROM Language as l JOIN l.title AS t JOIN t.langTexts as lt ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; } else { queryStr += " AND "; }
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
			String queryStr = "SELECT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts WHERE l.id =:id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
			query.setParameter("id", new Long((String) request.getParam(GlobalConstant.ITEMID)));
			Language language = (Language) query.getSingleResult();
			
			response.addParam(GlobalConstant.ITEM, language);
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
		
		response.addParam(GlobalConstant.ITEM, language);
		
	}

	@Override
	public void getAllLanguages(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT l FROM Language AS l JOIN FETCH l.title AS t JOIN FETCH t.langTexts as lt ";
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "l.active =:active ";
			and = true;
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
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

		response.addParam(GlobalConstant.ITEMS, languages);
	}


}
