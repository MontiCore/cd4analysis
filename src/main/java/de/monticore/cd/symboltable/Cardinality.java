/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.cd.symboltable;

import de.monticore.cd.cd4analysis._ast.ASTCardinality;

/**
 * Cardinality of an association end
 */
public class Cardinality {
  
  protected int min;
  
  protected int max;
  
  /**
   * Star-cardinality
   */
  public static final int STAR = -1;
  
  public Cardinality() {
    this.min = 1;
    this.max = 1;
  }
  
  /**
   * @param min cardinality minimum
   * @param max cardinality maximum
   */
  public Cardinality(int min, int max) {
    this.min = min;
    this.max = max;
  }
  
  /**
   * @return maximum (may be STAR)
   */
  public int getMax() {
    return this.max;
  }
  
  /**
   * @return minimum
   */
  public int getMin() {
    return this.min;
  }
  
  public static Cardinality convertCardinality(ASTCardinality aSTCard) {
    if (aSTCard == null) {
      return new Cardinality();
    }
    if (aSTCard.isMany()) {
      return new Cardinality(0, Cardinality.STAR);
    }
    if (aSTCard.isOne()) {
      return new Cardinality(1, 1);
    }
    if (aSTCard.isOptional()) {
      return new Cardinality(0, 1);
    }
    if (aSTCard.isOneToMany()) {
      return new Cardinality(1, Cardinality.STAR);
    }
    return new Cardinality();
  }
  
  public boolean isDefault() {
    return (min == 1 && max == 1);
  }
  
  public boolean isMultiple() {
    return (max > 1 || max == Cardinality.STAR);
  }
  
  public String printMax() {
    if (max == Cardinality.STAR) {
      return "*";
    }
    return String.valueOf(max);
  }
  
  public String printMin() {
    if (min == Cardinality.STAR) {
      return "*";
    }
    return String.valueOf(min);
  }
}
