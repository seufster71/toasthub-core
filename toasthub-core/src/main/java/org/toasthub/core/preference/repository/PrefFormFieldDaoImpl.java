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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefFormFieldName;
import org.toasthub.core.preference.model.PrefFormFieldValue;

@Repository("PrefFormFieldDao")
@Transactional("TransactionManagerData")
public class PrefFormFieldDaoImpl implements PrefFormFieldDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	public List<PrefFormFieldValue> getFormFields(String prefName, String lang) {
		@SuppressWarnings("unchecked")
		List<PrefFormFieldValue> formFields = entityManagerDataSvc.getInstance()
			.createQuery("SELECT NEW PrefFormFieldValue(f.id, f.value, f.label, f.lang, f.rendered, f.required, f.validation, f.image, f.prefFormFieldName.name, f.prefFormFieldName.fieldType, f.prefFormFieldName.htmlType, f.prefFormFieldName.className, f.prefFormFieldName.group, f.prefFormFieldName.subGroup, f.prefFormFieldName.tabIndex, f.prefFormFieldName.optionalParams, f.prefFormFieldName.classModel, f.prefFormFieldName.sortOrder) FROM PrefFormFieldValue f WHERE f.lang =:lang AND f.prefFormFieldName.prefName.name =:prefName AND f.prefFormFieldName.archive = false ORDER BY f.prefFormFieldName.group ASC, f.prefFormFieldName.sortOrder ASC")
			.setParameter("prefName", prefName)
			.setParameter("lang", lang)
			.getResultList();
		return formFields;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		
		String queryStr = "SELECT DISTINCT f FROM PrefFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts AS lt WHERE lt.lang =:lang AND f.prefName.id =:prefNameId";
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			queryStr += "AND f.active =:active ";
		}
		
		// search
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_TITLE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "lt.text LIKE :titleValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.name LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_GROUP")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.group LIKE :groupValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_FIELDTYPE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.fieldType LIKE :fieldTypeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				queryStr += " AND ( " + lookupStr + " ) ";
			}
			
		}
		// order by
		ArrayList<LinkedHashMap<String,String>> orderCriteria = null;
		StringBuilder orderItems = new StringBuilder();
		if (request.containsParam(GlobalConstant.ORDERCRITERIA) && !request.getParam(GlobalConstant.ORDERCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.ORDERCRITERIA) instanceof Map) {
				orderCriteria = new ArrayList<>();
				orderCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.ORDERCRITERIA));
			} else {
				orderCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.ORDERCRITERIA);
			}
			
			// Loop through all the criteria
			boolean comma = false;
			
			
			for (LinkedHashMap<String,String> item : orderCriteria) {
				if (item.containsKey(GlobalConstant.ORDERCOLUMN) && item.containsKey(GlobalConstant.ORDERDIR)) {
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_FORMFIELD_TABLE_TITLE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("lt.text ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_FORMFIELD_TABLE_CODE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("f.name ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_FORMFIELD_TABLE_GROUP")){
						if (comma) { orderItems.append(","); }
						orderItems.append("f.group ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_FORMFIELD_TABLE_FIELDTYPE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("f.fieldType ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_FORMFIELD_TABLE_STATUS")){
						if (comma) { orderItems.append(","); }
						orderItems.append("f.active ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
				}
			}
		}
		if (!"".equals(orderItems.toString())) {
			queryStr += " ORDER BY ".concat(orderItems.toString());
		} else {
			// default order
			queryStr += " ORDER BY f.sortOrder ASC";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		query.setParameter("prefNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		
		// search criteria
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_TITLE")){
						query.setParameter("titleValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
						query.setParameter("lang",request.getParam(GlobalConstant.LANG));
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_GROUP")){
						query.setParameter("groupValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_FIELDTYPE")){
						query.setParameter("fieldTypeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		
		// paging
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
				
		List<?> formFields =query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, (List<PrefFormFieldName>) formFields);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT f) FROM PrefFormFieldName AS f JOIN f.title AS t JOIN t.langTexts AS lt WHERE lt.lang =:lang AND f.prefName.id =:prefNameId";
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			queryStr += "AND f.active =:active ";
		}
		
		// search criteria
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_TITLE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "lt.text LIKE :titleValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.name LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_GROUP")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.group LIKE :groupValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_FIELDTYPE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.fieldType LIKE :fieldTypeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "f.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				queryStr += " AND ( " + lookupStr + " ) ";
			}
			
		}
			
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		
		query.setParameter("prefNameId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		// search criteria
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_TITLE")){
						query.setParameter("titleValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_GROUP")){
						query.setParameter("groupValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_FIELDTYPE")){
						query.setParameter("fieldTypeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_FORMFIELD_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
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
			String queryStr = "SELECT f FROM PrefFormFieldName AS f JOIN FETCH f.title AS t JOIN FETCH t.langTexts as l JOIN FETCH f.values WHERE f.id =:id";
			
			Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)));
			PrefFormFieldName prefFormFieldName = (PrefFormFieldName) query.getSingleResult();
		
			response.addParam(GlobalConstant.ITEM, prefFormFieldName);
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}

}
