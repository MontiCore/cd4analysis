package cd4analysis.symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.types.CommonJMethodSymbol;

import java.util.ArrayList;
import java.util.List;

public class CDMethodSymbol extends CommonJMethodSymbol<CDTypeSymbol, CDAttributeSymbol> {

  public static final CDMethodSymbolKind KIND = new CDMethodSymbolKind();
  

  private List<Stereotype> stereotypes = new ArrayList<>();



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

}
