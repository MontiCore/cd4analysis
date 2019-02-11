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
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import de.monticore.symboltable.types.CommonJFieldSymbol;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

@Deprecated //new class at cd4analysis/src/main/java/de/monticore/cd
public class CDFieldSymbol extends CommonJFieldSymbol<CDTypeSymbolReference> {

  public static final CDFieldSymbolKind KIND = new CDFieldSymbolKind();

  private boolean isReadOnly;
  private boolean isDerived;

  private boolean isEnumConstant;

  private boolean isInitialized;

  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  public CDFieldSymbol(String name, CDTypeSymbolReference type) {
    super(name, KIND, type);
  }
  
  public String getExtendedName() {
    return "CD field " + getName();  
  }
  
  public boolean isInitialized() {
    return isInitialized;
  }

  public void setInitialized(boolean isInitialized) {
    this.isInitialized = isInitialized;
  }

  public boolean isReadOnly() {
    return isReadOnly;
  }
  
  public void setReadOnly(boolean isReadOnly) {
    this.isReadOnly = isReadOnly;
  }
  
  public boolean isDerived() {
    return isDerived;
  }
  
  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }
  
  public boolean isEnumConstant() {
    return isEnumConstant;
  }

  public void setEnumConstant(boolean isEnumConstant) {
    this.isEnumConstant = isEnumConstant;
  }

  public List<Stereotype> getStereotypes() {
    return ImmutableList.copyOf(stereotypes);
  }

  public Optional<Stereotype> getStereotype(String name) {
    for (Stereotype stereotype: this.stereotypes) {
      if (stereotype.getName().equals(name)) {
        return Optional.of(stereotype);
      }
    }
    return Optional.empty();
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
    return  CDFieldSymbol.class.getSimpleName() + " " + getName();
  }

}
