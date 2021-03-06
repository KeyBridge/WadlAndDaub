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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
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
@Named(value = "wadlBean")
@RequestScoped
public class WadlBean implements Serializable {

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
  public WadlBean() {
    onLoad();
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

  public Method findMethod(String id) {
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

  public boolean isSetMethod() {
    return this.method != null;
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
    System.out.println("DEBUG WadlBean onLoad");
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
      Logger.getLogger(WadlBean.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException | JAXBException ex) {
      Logger.getLogger(WadlBean.class.getName()).log(Level.SEVERE, null, ex);
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

  public Map<String, String> getLabels() {
    Map<String, String> labels = new HashMap<>();
    labels.put("country", " The country of interest, encoded as a two-character ISO 3166-1 alpha-2 Country Code. ISO 3166-1 is part of the ISO 3166 standard published by the International Organization for Standardization (ISO), and defines codes for the names of countries, dependent territories, and special areas of geographical interest. The official name of the standard is Codes for the representation of names of countries and their subdivisions – Part 1: Country codes. ");
    return labels;
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
   * Get the label class type based upon the method name.
   *
   * @param methodName the method name
   * @return the label class
   */
  public String buildCSSType(String methodName) {
    switch (methodName) {
      case "GET":
      case MediaType.APPLICATION_JSON:
        return "info";
      case "POST":
      case MediaType.APPLICATION_XML:
        return "success";
      case "PUT":
        return "warning";
      case "HEAD":
        return "warning";
      case "DELETE":
        return "danger";
      default:
        return "default";
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

  /**
   * Insert paragraph markers into the description text. This method replaces
   * all large whitespace blocks with a paragraph closing and opening HTML tag.
   * <p>
   * This method should only be used inside a HTML paragraph!
   *
   * @param description the description text
   * @return the description text with paragraph tags inserted
   */
  public String formatDescription(String description) {
    return description.replaceAll("\\s{10,}", "</p>\n<p>");
  }

}
