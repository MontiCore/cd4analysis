/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CDFieldSymbol extends CDFieldSymbolTOP {


  private boolean isEnumConstant;

  private final List<Stereotype> stereotypes = new ArrayList<>();

  public CDFieldSymbol(String name, CDTypeSymbolLoader type) {
    super(name);
    setType(type);
  }

  public String getExtendedName() {
    return "CD field " + getName();
  }

  public CDFieldSymbol(String name) {
    super(name);
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
