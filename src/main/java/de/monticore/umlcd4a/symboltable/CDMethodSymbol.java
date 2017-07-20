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

package de.monticore.umlcd4a.symboltable;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.types.CommonJMethodSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

public class CDMethodSymbol extends CommonJMethodSymbol<CDTypeSymbol, CDTypeSymbolReference, CDFieldSymbol> {

  public static final CDMethodSymbolKind KIND = new CDMethodSymbolKind();

  private final List<Stereotype> stereotypes = new ArrayList<>();

  private CDTypeSymbol definingType;

  protected CDMethodSymbol(String name) {
    super(name, KIND);
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
    return CDMethodSymbol.class.getSimpleName() + " " + getName() + " of " + getDefiningType();
  }

  public CDTypeSymbol getDefiningType() {
    return definingType;
  }

  public void setDefiningType(final CDTypeSymbol definingType) {
    this.definingType = definingType;
  }
}
