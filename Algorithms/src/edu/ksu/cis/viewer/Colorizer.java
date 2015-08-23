/*
 * Colorizer.java     11/13/99
 *
 * Copyright (c) 1998, 1999, Rod Howell, All Rights Reserved.
 *
 */

package edu.ksu.cis.viewer;

import java.awt.Color;

/**
 * An interface to encapsulate a mechanism for associating colors to
 * Objects.
 *
 * @author Rod Howell
 *         (<a href="mailto:howell@cis.ksu.edu">howell@cis.ksu.edu</a>)
 */
public interface Colorizer {

  /**
   * Returns the Color associated with the given Object.
   */
  public Color getColor(Object obj);
}
