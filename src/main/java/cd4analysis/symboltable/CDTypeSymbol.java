package cd4analysis.symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.TypeSymbol;
import mc.helper.NameHelper;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class CDTypeSymbol extends TypeSymbol {
  
  public static final CDTypeSymbolKind KIND = new CDTypeSymbolKind();
  
  private final List<CDFieldSymbol> fields = new ArrayList<>();
  
  private final List<CDMethodSymbol> methods = new ArrayList<>();
  private final List<CDMethodSymbol> constructors = new ArrayList<>();
  private final List<CDTypeSymbol> superClasses = new ArrayList<>();
  private final List<CDTypeSymbol> interfaces = new ArrayList<>();
  private final List<CDAssociationSymbol> associations = new ArrayList<>();
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  private boolean isAbstract = false;
  private boolean isFinal = false;
  private boolean isLocal = false;
  private boolean isInterface = false;
  private boolean isEnum = false;
  

  protected CDTypeSymbol(String name) {
    super(name, KIND);
  }
  

  public List<CDTypeSymbol> getSuperClasses() {
    return ImmutableList.copyOf(superClasses);
  }
  
  public void addSuperClass(CDTypeSymbol superClass) {
    this.superClasses.add(requireNonNull(superClass));
  }
  
  public List<CDTypeSymbol> getInterfaces() {
    return ImmutableList.copyOf(interfaces);
  }
  
  public void addInterface(CDTypeSymbol superInterface) {
    this.interfaces.add(requireNonNull(superInterface));
  }
  
  public List<CDTypeSymbol> getSuperTypes() {
    List<CDTypeSymbol> superTypes = new ArrayList<>();
    superTypes.addAll(getSuperClasses());
    superTypes.addAll(getInterfaces());
    return superTypes;
  }

  @Override
  public List<? extends CDTypeSymbol> getFormalTypeParameters() {
    return (List<CDTypeSymbol>) super.getFormalTypeParameters();
  }
  

  public String getExtendedName() {
    return "CD type " + getName();  
  }
  
  public void addField(CDFieldSymbol field) {
    fields.add(field);
  }
  
  public List<CDFieldSymbol> getFields() {
    return ImmutableList.copyOf(fields);
  }
  
  public void addMethod(CDMethodSymbol method) {
    this.methods.add(method);
  }
  
  public List<CDMethodSymbol> getMethods() {
    return ImmutableList.copyOf(methods);
  }
  
  public void addConstructor(CDMethodSymbol constructor) {
    this.constructors.add(constructor);
  }
  
  public List<CDMethodSymbol> getConstructors() {
    return ImmutableList.copyOf(constructors);
  }
  
  public void addAssociation(CDAssociationSymbol assoc) {
    this.associations.add(assoc);
  }
  
  public List<CDAssociationSymbol> getAssociations() {
    return ImmutableList.copyOf(associations);
  }
  
  public List<String> getEnumConstants() {
    final List<String> enums = new ArrayList<>();
    for (CDFieldSymbol field: fields) {
      if (field.isEnumConstant()) {
        enums.add(field.getName());
      }
    }
    return enums;
  }
  
  public List<? extends Symbol> getChildren() {
    final List<Symbol> children = new ArrayList<>();
    children.addAll(getFields());
    children.addAll(getMethods());
    children.addAll(getConstructors());
    children.addAll(getAssociations());
    return children;
  }
  
  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }
  
  public boolean isAbstract() {
    return isAbstract;
  }
  
  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }
  
  public boolean isFinal() {
    return isFinal;
  }
  
  public void setLocal(boolean isLocal) {
    this.isLocal = isLocal;
  }
  
  public boolean isLocal() {
    return isLocal;
  }
  
  public void setInterface(boolean isInterface) {
    this.isInterface = isInterface;
  }
  
  public boolean isInterface() {
    return isInterface;
  }
  
  public void setEnum(boolean isEnum) {
    this.isEnum = isEnum;
  }
  
  public boolean isEnum() {
    return isEnum;
  }
  
  public boolean isClass() {
    return !isInterface() && !isEnum();
  }
  
  public List<Stereotype> getStereotypes() {
    return stereotypes;
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
  
  public String getModelName() {
    return NameHelper.getQualifier(getName());
  }
}
