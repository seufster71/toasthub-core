package org.toasthub.core.workflow;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface WorkFlowSvc {

	public void item(RestRequest request, RestResponse response);
	public void items(RestRequest request, RestResponse response);
	public void itemCount(RestRequest request, RestResponse response);
	
}
