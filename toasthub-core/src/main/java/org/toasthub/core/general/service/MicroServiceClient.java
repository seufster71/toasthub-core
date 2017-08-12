package org.toasthub.core.general.service;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface MicroServiceClient {

	public void process(RestRequest request, RestResponse response);
}
