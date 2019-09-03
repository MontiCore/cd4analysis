/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class CDMethOrConstrSymbol extends CDMethOrConstrSymbolTOP {

  private final List<Stereotype> stereotypes = new ArrayList<>();

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
