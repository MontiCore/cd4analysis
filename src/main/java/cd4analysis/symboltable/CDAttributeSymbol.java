package cd4analysis.symboltable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.AbstractSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;

import java.util.ArrayList;
import java.util.List;

public class CDAttributeSymbol extends AbstractSymbol {

  public static final CDAttributeSymbolKind KIND = new CDAttributeSymbolKind();

  private final CDTypeSymbol type;

  private boolean isFinal;
  private boolean isReadOnly;
  private boolean isDerived;
  private boolean isStatic;

  private boolean isEnumConstant;

  private boolean isInitialized;
  
  private List<Stereotype> stereotypes = new ArrayList<>();
  
  protected CDAttributeSymbol(String name, CDTypeSymbol type) {
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

  public void setPrivate() {
    setAccessModifier(BasicAccessModifier.PRIVATE);
  }

  public void setProtected() {
    setAccessModifier(BasicAccessModifier.PROTECTED);
  }

  public void setPublic() {
    setAccessModifier(BasicAccessModifier.PUBLIC);
  }

  public boolean isPrivate() {
    return getAccessModifier() == BasicAccessModifier.PRIVATE;
  }

  public boolean isProtected() {
    return getAccessModifier() == BasicAccessModifier.PROTECTED;
  }

  public boolean isPublic() {
    return getAccessModifier() == BasicAccessModifier.PUBLIC;
  }


  @Override
  public String toString() {
    return  CDAttributeSymbol.class.getSimpleName() + " " + getName();
  }

}
