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
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.service.EntityManagerDataSvc;
import org.toasthub.core.general.service.UtilSvc;
import org.toasthub.core.general.utils.Utils;


@Repository("BaseDao")
public class BaseDaoImpl implements BaseDao {
	
	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String itemName = (String) request.getParam(BaseEntity.ITEMNAME);
		String queryStr = "";
		if (request.containsParam(BaseEntity.COLUMNS)){
			String[] columns = (String[]) request.getParam(BaseEntity.COLUMNS);
			String c = Utils.arrayToComma(columns);
			queryStr += "SELECT new "+ itemName + "( id," + c + ")";
		}
		queryStr += " FROM " + itemName;
		boolean and = false;
		if (request.containsParam(BaseEntity.SEARCHCOLUMN) && request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lower(" + request.getParam(BaseEntity.SEARCHCOLUMN) + ") LIKE :searchValue"; 
			and = true;
		}
		if (request.containsParam(BaseEntity.OWNER)) {
			if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
			queryStr += "owner.id =:uid";
			and = true;
		}

		if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
		queryStr += "active =:active";
			
		if (request.containsParam(BaseEntity.ORDERCOLUMN)) {
			String direction = "DESC";
			if (request.containsParam(BaseEntity.ORDERDIR)) {
				direction = (String) request.getParam(BaseEntity.ORDERDIR);
			}
			queryStr += " ORDER BY "+(String) request.getParam(BaseEntity.ORDERCOLUMN)+" "+direction;
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		if (request.containsParam(BaseEntity.SEARCHCOLUMN) && request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			query.setParameter(BaseEntity.SEARCHVALUE, "%"+((String)request.getParam(BaseEntity.SEARCHVALUE)).toLowerCase()+"%");
		}
		if (request.containsParam(BaseEntity.OWNER)) {
			query.setParameter("uid", new Long((String) request.getParam(BaseEntity.OWNER)));
		} 
		if (request.containsParam(BaseEntity.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(BaseEntity.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		if (request.containsParam(BaseEntity.PAGELIMIT) && (Integer) request.getParam(BaseEntity.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(BaseEntity.PAGESTART));
			query.setMaxResults((Integer) request.getParam(BaseEntity.PAGELIMIT));
		}
		response.addParam(BaseEntity.ITEMS, (List<?>) query.getResultList());
	}
	
	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String itemName = (String) request.getParam(BaseEntity.ITEMNAME);
		String queryStr = "SELECT COUNT(*) FROM " + itemName;
		
		boolean and = false;
		if (request.containsParam(BaseEntity.SEARCHCOLUMN) && request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lower(" + request.getParam(BaseEntity.SEARCHCOLUMN) + ") LIKE :searchValue"; 
			and = true;
		}
		if (request.containsParam(BaseEntity.OWNER)) {
			if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
			queryStr += "owner.id = :uid";
			and = true;
		}
	
		if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
		queryStr += "active = :active";
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		if (request.containsParam(BaseEntity.SEARCHCOLUMN) && request.containsParam(BaseEntity.SEARCHVALUE) && !request.getParam(BaseEntity.SEARCHVALUE).equals("")){
			query.setParameter(BaseEntity.SEARCHVALUE, "%"+((String) request.getParam(BaseEntity.SEARCHVALUE)).toLowerCase()+"%");
		}
		if (request.containsParam(BaseEntity.OWNER)) {
			query.setParameter("uid", new Long((String) request.getParam(BaseEntity.OWNER)));
		} 
		if (request.containsParam(BaseEntity.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(BaseEntity.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(BaseEntity.ITEMCOUNT, count);
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		String tableName = (String) request.getParam(BaseEntity.ITEMNAME);
		if (tableName != null){
			String queryStr = "FROM " + tableName + " WHERE id = :id";
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr)
					.setParameter("id",new Long((Integer) request.getParam(BaseEntity.ITEMID)));
			response.addParam(BaseEntity.ITEM, query.getSingleResult());
		} else {
			
		}
	}
	
}
