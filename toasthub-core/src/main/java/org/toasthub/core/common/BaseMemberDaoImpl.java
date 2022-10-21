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

package org.toasthub.core.common;

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.utils.Utils;


@Repository("BaseMemberDao")
public class BaseMemberDaoImpl implements BaseMemberDao {
	
	@Autowired
	protected EntityManagerMemberSvc entityManagerSvc;
	@Autowired
	protected UtilSvc utilSvc;

	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String itemName = (String) request.getParam(GlobalConstant.ITEMNAME);
		String queryStr = "";
		if (request.containsParam(GlobalConstant.COLUMNS)){
			String[] columns = (String[]) request.getParam(GlobalConstant.COLUMNS);
			String c = Utils.arrayToComma(columns);
			queryStr += "SELECT new "+ itemName + "( id," + c + ")";
		}
		queryStr += " FROM " + itemName;
		boolean and = false;
		if (request.containsParam(GlobalConstant.SEARCHCOLUMN) && request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lower(" + request.getParam(GlobalConstant.SEARCHCOLUMN) + ") LIKE :searchValue"; 
			and = true;
		}
		if (request.containsParam(GlobalConstant.OWNER)) {
			if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
			queryStr += "owner.id =:uid";
			and = true;
		}

		if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
		queryStr += "active =:active";
			
		if (request.containsParam(GlobalConstant.ORDERCOLUMN)) {
			String direction = "DESC";
			if (request.containsParam(GlobalConstant.ORDERDIR)) {
				direction = (String) request.getParam(GlobalConstant.ORDERDIR);
			}
			queryStr += " ORDER BY "+(String) request.getParam(GlobalConstant.ORDERCOLUMN)+" "+direction;
		}
		Query query = entityManagerSvc.getInstance().createQuery(queryStr);
		if (request.containsParam(GlobalConstant.SEARCHCOLUMN) && request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter(GlobalConstant.SEARCHVALUE, "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
		}
		if (request.containsParam(GlobalConstant.OWNER)) {
			query.setParameter("uid", request.getParamLong(GlobalConstant.OWNER));
		} 
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		response.addParam(GlobalConstant.ITEMS, (List<?>) query.getResultList());
	}
	
	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String itemName = (String) request.getParam(GlobalConstant.ITEMNAME);
		String queryStr = "SELECT COUNT(*) FROM " + itemName;
		
		boolean and = false;
		if (request.containsParam(GlobalConstant.SEARCHCOLUMN) && request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			if (!and) { queryStr += " WHERE "; }
			queryStr += "lower(" + request.getParam(GlobalConstant.SEARCHCOLUMN) + ") LIKE :searchValue"; 
			and = true;
		}
		if (request.containsParam(GlobalConstant.OWNER)) {
			if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
			queryStr += "owner.id = :uid";
			and = true;
		}
	
		if (and) { queryStr += " AND "; } else { queryStr += " WHERE "; }
		queryStr += "active = :active";
		
		Query query = entityManagerSvc.getInstance().createQuery(queryStr);
		if (request.containsParam(GlobalConstant.SEARCHCOLUMN) && request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter(GlobalConstant.SEARCHVALUE, "%"+((String) request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
		}
		if (request.containsParam(GlobalConstant.OWNER)) {
			query.setParameter("uid", request.getParamLong(GlobalConstant.OWNER));
		} 
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(GlobalConstant.ITEMCOUNT, count);
	}
	
	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		String tableName = (String) request.getParam(GlobalConstant.ITEMNAME);
		if (tableName != null){
			String queryStr = "FROM " + tableName + " WHERE id = :id";
			Query query = entityManagerSvc.getInstance().createQuery(queryStr)
					.setParameter("id",request.getParamLong(GlobalConstant.ITEMID));
			response.addParam(GlobalConstant.ITEM, query.getSingleResult());
		} else {
			
		}
	}

	@Override
	public void delete(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(RestRequest request, RestResponse response) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
