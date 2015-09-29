package org.ovirt.engine.api.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ovirt.engine.api.model.NetworkAttachment;

@Produces({ApiMediaType.APPLICATION_XML, ApiMediaType.APPLICATION_JSON})
public interface NetworkAttachmentResource extends UpdatableResource<NetworkAttachment> {

    @DELETE
    Response remove();
}