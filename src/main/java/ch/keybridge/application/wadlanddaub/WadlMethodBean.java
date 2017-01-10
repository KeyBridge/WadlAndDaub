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
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;
import net.java.dev.wadl.*;

/**
 *
 * @author Key Bridge LLC
 */
@Named(value = "wadlMethodBean")
@ViewScoped
public class WadlMethodBean implements Serializable {

  private String uri = "http://keybridgewireless.com/gis/rest/application.wadl";

  private Application application;
  /**
   * The top level resource.
   */
  private Resource resource;
  private List<Method> methods;

  private Method method;

  /**
   * Creates a new instance of WadlBean
   */
  public WadlMethodBean() {
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

  public Method getMethod() {
    return method;
  }

  public Method getMethod(String id) {
    if (method == null || !method.getId().equals(id)) {
      for (Method findallMethod : findallMethods()) {
        if (findallMethod.getId().equals(id)) {
          this.method = findallMethod;
        }
      }
    }
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public List<Method> findallMethods() {
    if (methods == null || methods.isEmpty()) {
      /**
       * Initialize and recursively populate the methods array.
       */
      this.methods = new ArrayList();
      for (Resources resourcesTmp : application.getResources()) {
        for (Resource resourceTmp : resourcesTmp.getResource()) {
          this.methods.addAll(findMethodsRecursive(resourceTmp));
        }
      }
      /**
       * Reverse the array to restore the "as found" order.
       */
      Collections.reverse(methods);
    }
    return methods;
  }

  /**
   * Get a list of all methods identified in the {@code Application} instance.
   *
   * @return a non-null ArrayList.
   */
  public List<Method> findMethods(String path) {
    if (methods == null || methods.isEmpty()) {
      this.resource = application.findResource(path);
      /**
       * Initialize and recursively populate the methods array.
       */
      this.methods = new ArrayList();
      methods.addAll(findMethodsRecursive(resource));
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
  private List<Method> findMethodsRecursive(Resource resource) {
    List<Method> tempMethods = new ArrayList<>(resource.getMethods());
    for (Resource res : resource.getResources()) {
      tempMethods.addAll(findMethodsRecursive(res));
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
//      System.out.println(findMethods());
    } catch (MalformedURLException ex) {
      Logger.getLogger(WadlMethodBean.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | JAXBException ex) {
      Logger.getLogger(WadlMethodBean.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Build a map of the representation element names and their supported media
   * types.
   * <p>
   * Note that use of representation elements is confined to HTTP methods that
   * accept an entity body in the request (e.g., PUT or POST). Sibling
   * representation elements represent logically equivalent alternatives, e.g.,
   * a particular resource might support multiple XML grammars for a particular
   * request.
   *
   * @param method the method
   * @return a non-null MultivaluedMap of element names and supported media
   *         types
   */
  public MultivaluedMap<String, Representation> findMethodRequestElements(Method method) {
    MultivaluedMap<String, Representation> elementMediaTypes = new MultivaluedHashMap<>();
    if (HTTPMethods.PUT.equals(method.getName()) || HTTPMethods.POST.equals(method.getName())) {
      for (Representation representation : method.getRequest().getRepresentation()) {
        elementMediaTypes.add(representation.getElement().getLocalPart(), representation);
      }
    }
    return elementMediaTypes;
  }

  /**
   * Build a map of the representation element names and their supported media
   * types.
   *
   * @param response one method response
   * @return a non-null MultivaluedMap of element names and supported media
   *         types
   */
  public MultivaluedMap<String, Representation> findMethodResponseElements(Response response) {
    MultivaluedMap<String, Representation> elementMediaTypes = new MultivaluedHashMap<>();
    for (Representation representation : response.getRepresentation()) {
      elementMediaTypes.add(representation.getElement() != null
                            ? representation.getElement().getLocalPart()
                            : "Response",
                            representation);
    }
    return elementMediaTypes;
  }

  /**
   * Build a list of all parameters relevant to a method. This captures the
   * parent and immediate parameters.
   *
   * @param method the method
   * @return a non-null ArrayList
   */
  public List<Param> findMethodParameters(Method method) {
    List<Param> parameterList = new ArrayList<>(method.getParent().getParam());
    parameterList.addAll(method.getRequest().getParam());
    return parameterList;
  }

  /**
   * Determine if the method is either a PUT or POST type.
   *
   * @param method the method
   * @return TRUE if the method name is either PUT or POST, otherwise FALSE
   */
  public boolean isPutOrPost(Method method) {
    return HTTPMethods.PUT.equals(method.getName()) || HTTPMethods.POST.equals(method.getName());
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
