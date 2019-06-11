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

package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class CDMethOrConstrSymbol extends CDMethOrConstrSymbolTOP {

  private final List<Stereotype> stereotypes = new ArrayList<>();

  public List<CDTypeSymbolReference> getExceptions() {
    return exceptions;
  }

  public void setExceptions(List<CDTypeSymbolReference> exceptions) {
    this.exceptions = exceptions;
  }

  private List<CDTypeSymbolReference> exceptions = new ArrayList<>();

  private CDTypeSymbol definingType;

  public CDMethOrConstrSymbol(String name) {
    super(name);
  }
  
  public String getExtendedName() {
    return "CD method " + getName();  
  }

  public List<Stereotype> getStereotypes() {
    return ImmutableList.copyOf(stereotypes);
  }
  
  public Stereotype getStereotype(String name) {
    for (Stereotype stereotype: this.stereotypes) {
      if (stereotype.getName().equals(name)) {
        return stereotype;
      }
    }
    return null;
  }

  public boolean containsStereotype(String name, String value) {
    for (Stereotype stereotype: this.stereotypes) {
      if (stereotype.compare(name, value)) {
        return true;
      }
    }
    return false;
  }

  public void addStereotype(Stereotype stereotype) {
    this.stereotypes.add(stereotype);
  }

  @Override
  public String toString() {
    return CDMethOrConstrSymbol.class.getSimpleName() + " " + getName() + " of " + getDefiningType();
  }

  public CDTypeSymbol getDefiningType() {
    return definingType;
  }

  public void setDefiningType(final CDTypeSymbol definingType) {
    this.definingType = definingType;
  }

  public java.util.List<CDFieldSymbol> getParameters() {
    return getSpannedScope().getLocalCDFieldSymbols();
  }

}
