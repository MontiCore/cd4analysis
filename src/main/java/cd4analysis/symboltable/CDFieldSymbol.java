package cd4analysis.symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.AbstractSymbol;

import java.util.ArrayList;
import java.util.List;

public class CDFieldSymbol extends AbstractSymbol {

  public static final CDFieldSymbolKind KIND = new CDFieldSymbolKind();

  private final CDTypeSymbol type;

  private boolean isFinal;
  private boolean isLocal;
  private boolean isReadOnly;
  private boolean isDerived;
  private boolean isStatic;

  private boolean isEnumConstant;

  private boolean isInitialized;
  
  private List<Stereotype> stereotypes = new ArrayList<>();
  
  protected CDFieldSymbol(String name, CDTypeSymbol type) {
    super(name, KIND);
    this.type = type;
  }
  
  public String getExtendedName() {
    return "CD field " + getName();  
  }
  
  public CDTypeSymbol getType() {
    return type;
  }

  public boolean isStatic() {
    return isStatic;
  }
  
  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }
  
  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }
  
  public boolean isFinal() {
    return isFinal;
  }
  
  public boolean isInitialized() {
    return isInitialized;
  }

  public void setInitialized(boolean isInitialized) {
    this.isInitialized = isInitialized;
  }

  public boolean isLocal() {
    return isLocal;
  }
  
  public void setLocal(boolean isLocal) {
    this.isLocal = isLocal;
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
    return  CDFieldSymbol.class.getSimpleName() + " " + getName();
  }

}
