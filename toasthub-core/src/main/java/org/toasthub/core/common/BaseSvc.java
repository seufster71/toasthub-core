package org.toasthub.core.common;

import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

public interface BaseSvc {

	public void process(final RestRequest request, final RestResponse response);
	public void delete(final RestRequest request, final RestResponse response);
	public void item(final RestRequest request, final RestResponse response);
	public void items(final RestRequest request, final RestResponse response);
	public void save(final RestRequest request, final RestResponse response);
}
