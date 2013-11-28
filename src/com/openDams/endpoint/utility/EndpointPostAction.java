package com.openDams.endpoint.utility;

import com.openDams.endpoint.managing.EndPointManager.EndpointRecordContainerList;

public abstract class EndpointPostAction {
	EndpointRecordContainerList endpointRecordContainerList;

	public EndpointRecordContainerList getEndpointRecordContainerList() {
		return endpointRecordContainerList;
	}

	public void setEndpointRecordContainerList(
			EndpointRecordContainerList endpointRecordContainerList) {
		this.endpointRecordContainerList = endpointRecordContainerList;
	}
}
