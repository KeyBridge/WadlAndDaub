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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author Key Bridge LLC
 */
@Named(value = "labelsBean")
@RequestScoped
public class LabelsBean {

  private final Properties properties;

  /**
   * Creates a new instance of LabelsBean
   */
  public LabelsBean() throws IOException {
    this.properties = new Properties();

    URL url = getClass().getClassLoader().getResource("META-INF/resources/labelProperties.xml");
//    System.out.println("Labels URL " + url);
    properties.loadFromXML(url.openStream());
//    System.out.println("DEBUG loaded all properties");
  }

  /**
   * Get the label value corresponding to the label key.
   *
   * @param label the label key
   * @return the label value.
   */
  public String getLabelValue(String label) {
    return properties.getProperty(label.trim());
  }

}
