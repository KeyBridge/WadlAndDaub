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

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Key Bridge LLC
 */
public class ItemResource {

  private String id;

  /**
   * Creates a new instance of ItemResource
   */
  private ItemResource(String id) {
    this.id = id;
  }

  /**
   * Get instance of the ItemResource
   */
  public static ItemResource getInstance(String id) {
    // The user may use some kind of persistence mechanism
    // to store and restore instances of ItemResource class.
    return new ItemResource(id);
  }

  /**
   * Retrieves representation of an instance of ch.keybridge.application.wadlanddaub.ItemResource
   * @return an instance of java.lang.String
   */
  @GET
  @Produces(MediaType.APPLICATION_XML)
  public String getXml() {
    //TODO return proper representation object
    throw new UnsupportedOperationException();
  }

  /**
   * PUT method for updating or creating an instance of ItemResource
   * @param content representation for the resource
   */
  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public void putXml(String content) {
  }

  /**
   * DELETE method for resource ItemResource
   */
  @DELETE
  public void delete() {
  }
}
