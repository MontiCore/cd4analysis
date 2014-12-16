package cd4analysis.symboltable;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.types.Parameter;

import java.util.ArrayList;
import java.util.List;

public class CDMethodSymbol extends ScopeSpanningSymbol {

  public static final CDMethodSymbolKind KIND = new CDMethodSymbolKind();
  
  private CDTypeSymbol returnType;
  private List<Parameter<CDTypeSymbol>> parameters = new ArrayList<>();
  private List<CDTypeSymbol> typeParameters = new ArrayList<>();
  private List<CDTypeSymbol> exceptions = new ArrayList<>();
  private List<Stereotype> stereotypes = new ArrayList<>();

  private CDTypeSymbol definingType;
  private boolean isAbstract = false;
  private boolean isStatic = false;
  private boolean isFinal = false;
  private Visibility visibility = Visibility.DEFAULT;
  private boolean isConstructor = false;
  private boolean isEllipsisParameterMethod = false;
  private boolean hasBody = false;
  
  protected CDMethodSymbol(String name) {
    super(name, KIND);
  }
  
  public String getExtendedName() {
    return "CD method " + getName();  
  }
  
  public CDTypeSymbol getReturnType() {
    return returnType;
  }
  
  public void setReturnType(CDTypeSymbol type) {
    this.returnType = type;
  }
  
  public List<Parameter<CDTypeSymbol>> getParameters() {
    return ImmutableList.copyOf(parameters);
  }
  
  public void setParameters(List<Parameter<CDTypeSymbol>> parameters) {
    this.parameters = parameters;
  }
  
  public void addParameter(Parameter<CDTypeSymbol> paramType) {
    this.parameters.add(paramType);
  }
  
  public List<CDTypeSymbol> getTypeParameters() {
    return ImmutableList.copyOf(typeParameters);
  }
  
  public void setTypeParameters(List<CDTypeSymbol> typeParameter) {
    this.typeParameters = typeParameter;
  }
  
  public void addTypeParameter(CDTypeSymbol typeParameter) {
    this.typeParameters.add(typeParameter);
  }
  
  public List<CDTypeSymbol> getExceptions() {
    return ImmutableList.copyOf(exceptions);
  }
  
  public void setExceptions(List<CDTypeSymbol> exceptions) {
    this.exceptions = exceptions;
  }
  
  public void addException(CDTypeSymbol exception) {
    this.exceptions.add(exception);
  }
  
  public void setDefiningType(CDTypeSymbol definingType) {
    this.definingType = definingType;
  }
  
  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }
  
  public boolean isAbstract() {
    return isAbstract;
  }
  
  public CDTypeSymbol getDefiningType() {
    return definingType;
  }
  
  public void setVisibility(Visibility visibility) {
    this.visibility = visibility;
  }
  
  public Visibility getVisibility() {
    return visibility;
  }
  
  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }
  
  public boolean isStatic() {
    return isStatic;
  }
  
  public void setConstructor(boolean isConstructor) {
    this.isConstructor = isConstructor;
  }
  
  public boolean isConstructor() {
    return isConstructor;
  }
  
  public boolean isFinal() {
    return isFinal;
  }
  
  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
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

  public boolean isEllipsisParameterMethod() {
    return isEllipsisParameterMethod;
  }

  public void setEllipsisParameterMethod(boolean isEllipsisParameterMethod) {
    this.isEllipsisParameterMethod = isEllipsisParameterMethod;
  }

  public void setBody(boolean hasBody) {
    this.hasBody = hasBody;
  }
  
  public boolean hasBody() {
    return this.hasBody;
  }

  @Override
  public String toString() {
    return CDMethodSymbol.class.getSimpleName() + " " + getName() + " of " + getDefiningType();
  }

}
