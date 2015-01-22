package cd4analysis.symboltable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.types.CommonJAttributeSymbol;

import java.util.ArrayList;
import java.util.List;

public class CDFieldSymbol extends CommonJAttributeSymbol<CDTypeSymbol> {

  public static final CDFieldSymbolKind KIND = new CDFieldSymbolKind();

  private boolean isReadOnly;
  private boolean isDerived;

  private boolean isEnumConstant;

  private boolean isInitialized;

  private List<Stereotype> stereotypes = new ArrayList<>();
  
  protected CDFieldSymbol(String name, CDTypeSymbol type) {
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
    return Optional.absent();
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
