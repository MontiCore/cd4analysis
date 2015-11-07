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
