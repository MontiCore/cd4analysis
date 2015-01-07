package cd4analysis.symboltable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.TypeSymbol;
import mc.helper.NameHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class CDTypeSymbol extends TypeSymbol {
  
  public static final CDTypeSymbolKind KIND = new CDTypeSymbolKind();

  private CDTypeSymbol superClass;

  private final List<CDTypeSymbol> interfaces = new ArrayList<>();
  private final List<CDAssociationSymbol> associations = new ArrayList<>();
  private final List<Stereotype> stereotypes = new ArrayList<>();
  
  private boolean isAbstract = false;
  private boolean isFinal = false;
  private boolean isLocal = false;
  private boolean isInterface = false;
  private boolean isEnum = false;

  private AccessModifier accessModifier = BasicAccessModifier.ABSENT;
  

  protected CDTypeSymbol(String name) {
    super(name, KIND);
    spannedScope = new CDTypeScope(Optional.absent());
    spannedScope.setSpanningSymbol(this);
  }

  public Optional<CDTypeSymbol> getSuperClass() {
    return Optional.fromNullable(superClass);
  }

  public void setSuperClass(CDTypeSymbol superClass) {
    this.superClass = superClass;
  }

  public List<CDTypeSymbol> getInterfaces() {
    return ImmutableList.copyOf(interfaces);
  }
  
  public void addInterface(CDTypeSymbol superInterface) {
    this.interfaces.add(requireNonNull(superInterface));
  }
  
  public List<CDTypeSymbol> getSuperTypes() {
    List<CDTypeSymbol> superTypes = new ArrayList<>();
    if (getSuperClass().isPresent()) {
      superTypes.add(getSuperClass().get());
    }
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
    getSpannedScope().define(requireNonNull(field));
  }
  
  public List<CDFieldSymbol> getFields() {
    return getSpannedScope().resolveLocally(CDFieldSymbol.KIND);
  }

  public Optional<CDFieldSymbol> getField(String fieldName) {
    checkArgument(!isNullOrEmpty(fieldName));

    return getSpannedScope().resolveLocally(fieldName, CDFieldSymbol.KIND);
  }
  
  public void addMethod(CDMethodSymbol method) {
    requireNonNull(method);
    checkArgument(!method.isConstructor());

    getSpannedScope().define(method);
  }
  
  public List<CDMethodSymbol> getMethods() {
    final List<CDMethodSymbol> methods = new ArrayList<>();
    final List<CDMethodSymbol> resolvedMethods = getSpannedScope().resolveLocally(CDMethodSymbol.KIND);

    methods.addAll(resolvedMethods.stream().filter(
        method -> !method.isConstructor()).collect(Collectors.toList()));
    return ImmutableList.copyOf(methods);
  }

  public Optional<CDMethodSymbol> getMethod(String methodName) {
    checkArgument(!isNullOrEmpty(methodName));

    Optional<CDMethodSymbol> method = getSpannedScope().resolve(methodName, CDMethodSymbol.KIND);
    if (method.isPresent() && !method.get().isConstructor()) {
      return method;
    }
    return Optional.absent();
  }
  
  public void addConstructor(CDMethodSymbol constructor) {
    requireNonNull(constructor);
    checkArgument(constructor.isConstructor());

    getSpannedScope().define(constructor);
  }
  
  public List<CDMethodSymbol> getConstructors() {
    final List<CDMethodSymbol> constructors = new ArrayList<>();
    final List<CDMethodSymbol> resolvedMethods = getSpannedScope().resolveLocally(CDMethodSymbol.KIND);

    constructors.addAll(resolvedMethods.stream().filter(
        CDMethodSymbol::isConstructor).collect(Collectors.toList()));

    return ImmutableList.copyOf(constructors);
  }
  
  public void addAssociation(CDAssociationSymbol assoc) {
    this.associations.add(assoc);
  }
  
  public List<CDAssociationSymbol> getAssociations() {
    return ImmutableList.copyOf(associations);
  }

  public List<CDFieldSymbol> getEnumConstants() {
    final List<CDFieldSymbol> enums = getFields().stream()
        .filter(CDFieldSymbol::isEnumConstant)
        .collect(Collectors.toList());
    return ImmutableList.copyOf(enums);
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
  
  public String getModelName() {
    return NameHelper.getQualifier(getName());
  }

  public void setPrivate() {
    this.accessModifier = BasicAccessModifier.PRIVATE;
  }

  public void setProtected() {
    this.accessModifier = BasicAccessModifier.PROTECTED;
  }

  public void setPublic() {
    this.accessModifier = BasicAccessModifier.PUBLIC;
  }
}
