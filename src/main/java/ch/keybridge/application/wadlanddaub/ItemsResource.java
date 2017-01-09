/*
 * Copyright 2017 Key Bridge LLC. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge LLC generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package ch.keybridge.application.wadlanddaub;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Key Bridge LLC
 */
@Path("/Items")
public class ItemsResource {

  @Context
  private UriInfo context;

  /**
   * Creates a new instance of ItemsResource
   */
  public ItemsResource() {
  }

  /**
   * Retrieves representation of an instance of ch.keybridge.application.wadlanddaub.ItemsResource
   * @return an instance of java.lang.String
   */
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public String getXml() {
    //TODO return proper representation object
    throw new UnsupportedOperationException();
  }

  /**
   * POST method for creating an instance of ItemResource
   * @param content representation for the new resource
   * @return an HTTP response with content of the created resource
   */
  @POST
  @Consumes(MediaType.APPLICATION_XML)
  @Produces(MediaType.APPLICATION_XML)
  public Response postXml(String content) {
    //TODO
    return Response.created(context.getAbsolutePath()).build();
  }

  /**
   * Sub-resource locator method for {id}
   */
  @Path("{id}")
  public ItemResource getItemResource(@PathParam("id") String id) {
    return ItemResource.getInstance(id);
  }
}
