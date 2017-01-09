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

import ch.keybridge.lib.xml.JaxbUtility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import net.java.dev.wadl.Application;
import net.java.dev.wadl.HTTPMethods;
import net.java.dev.wadl.Method;
import net.java.dev.wadl.Resource;

/**
 *
 * @author Key Bridge LLC
 */
@Named(value = "wadlBean")
@ViewScoped
public class WadlBean implements Serializable {

  private String uri = "http://keybridgewireless.com/gis/rest/application.wadl";

  private Application application;
  /**
   * The top level resource.
   */
  private Resource resource;
  private List<Method> methods;

  /**
   * Creates a new instance of WadlBean
   */
  public WadlBean() {
  }

  public Application getApplication() {
    return application;
  }

  public boolean isSetApplication() {
    return application != null;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  /**
   * Get a list of all methods identified in the {@code Application} instance.
   *
   * @return a non-null ArrayList.
   */
  public List<Method> getMethods() {
    if (methods == null) {
      this.methods = new ArrayList();
//      for (Resources resources : application.getResources()) {
//        for (Resource resource : resources.getResource()) {
//          methods.addAll(getMethodsRecursive(resource));
//        }
//      }
    }
    return methods;
  }

  /**
   * Get a list of all methods identified in the {@code Application} instance.
   *
   * @return a non-null ArrayList.
   */
  public List<Method> getMethods(String path) {
    if (methods == null || methods.isEmpty()) {
      this.resource = application.findResource(path);
      /**
       * Initialize and recursively populate the methods array.
       */
      this.methods = new ArrayList();
      methods.addAll(getMethodsRecursive(resource));
      /**
       * Reverse the array to restore the "as found" order.
       */
      Collections.reverse(methods);
    }
    return methods;
  }

  /**
   * Recursively a {@code Resource} instance to identify and extract all
   * methods.
   *
   * @param resource a {@code Resource} instance
   * @return a list of all methods in the Resource tree
   */
  private List<Method> getMethodsRecursive(Resource resource) {
    List<Method> tempMethods = new ArrayList<>(resource.getMethods());
    for (Resource res : resource.getResources()) {
      tempMethods.addAll(getMethodsRecursive(res));
    }
    return tempMethods;
  }

  public void onLoad() {
    try {
      String wadlFile = null;
      URL url = new URL(uri);
      try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
//        System.out.println(inputLine);
          sb.append(inputLine);
        }
        wadlFile = sb.toString();
      }
//      System.out.println(wadlFile);
      /**
       * Unmarshal the application from the WADL file.
       */
      this.application = JaxbUtility.unmarshal(wadlFile, Application.class);
      /**
       * Call PostLoad to set the inter-object parent/child relationships.
       */
      this.application.postLoad();

      /**
       * Test show methods.
       */
//      System.out.println(getMethods());
    } catch (MalformedURLException ex) {
      Logger.getLogger(WadlBean.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | JAXBException ex) {
      Logger.getLogger(WadlBean.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Get the panel class type based upon the method name.
   *
   * @param methodName the method name
   * @return the panel class
   */
  public String buildPanelClass(String methodName) {
    HTTPMethods method = HTTPMethods.valueOf(methodName);
    switch (method) {
      case GET:
        return "panel-info";
      case POST:
        return "panel-success";
      case PUT:
        return "panel-warning";
      case HEAD:
        return "panel-warning";
      case DELETE:
        return "panel-danger";
      default:
        throw new AssertionError(method.name());
    }
  }

  /**
   * Return a media type label class type.
   *
   * @param mediaType the media type
   * @return a corresponding label css class
   */
  public String buildMediaTypeClass(String mediaType) {
    switch (mediaType) {
      case MediaType.APPLICATION_XML:
        return "label-success";
      case MediaType.APPLICATION_JSON:
        return "label-info";
      default:
        return "label-default";
    }

  }

  /**
   * Wrap variables in a URI pattern in an html span and assign the
   * 'text-primary' class. Output from this method should not be escaped.
   *
   * @param uriPattern the URI pattern
   * @return the HTML-coded URI pattern.
   */
  public String buildFormattedURI(String uriPattern) {
    return uriPattern.replaceAll("\\{(\\w+)\\}", "<span class=\"text-primary\">{$1}</span>");
  }

}
