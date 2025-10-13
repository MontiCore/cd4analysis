/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;

public class AttrHelper {
  
  // Future work: Instead of enums, use some kind of extendable magic value or a registry?
  
  /**
   * Which multiplicity an attribute follows
   */
  public enum Multiplicity {
    MANDATORY, OPTIONAL, SET,
  }
  
  /**
   * Which kind of type is hidden behind an attribute
   */
  public enum TypeKind {
    BUILD_IN, DOMAIN, ENUM, UNKNOWN;
  }
  
  public static class AttrData {
    
    private final TypeKind typeKind;
    private final Multiplicity multiplicity;
    private final boolean ordered;
    private final SymTypeExpression symTypeExpression;
    
    public AttrData(TypeKind typeKind, Multiplicity multiplicity, boolean ordered,
        SymTypeExpression symTypeExpression) {
      this.typeKind = typeKind;
      this.multiplicity = multiplicity;
      this.ordered = ordered;
      this.symTypeExpression = symTypeExpression;
    }
    
    public TypeKind getTypeKind() { return typeKind; }
    
    public Multiplicity getMultiplicity() { return multiplicity; }
    
    public boolean isOrdered() { return ordered; }
    
    public SymTypeExpression getSymTypeExpression() { return symTypeExpression; }
    
  }
  
  protected SymTypeExpression stringSymTypeExpression;
  
  public AttrHelper() {
    stringSymTypeExpression = SymTypeExpressionFactory.createStringType();
  }
  
  public AttrData getFromSymTypeExpr(SymTypeExpression symTypeExpression) {
    if (MCCollectionSymTypeRelations.isOptional(symTypeExpression)) {
      return createFromGeneric(Multiplicity.OPTIONAL, symTypeExpression, false);
    }
    else if (MCCollectionSymTypeRelations.isSet(symTypeExpression)) {
      return createFromGeneric(Multiplicity.SET, symTypeExpression, false);
    }
    else if (MCCollectionSymTypeRelations.isList(symTypeExpression)) {
      return createFromGeneric(Multiplicity.SET, symTypeExpression, true);
    }
    else if (symTypeExpression.isPrimitive() || SymTypeRelations.isCompatible(symTypeExpression,
        stringSymTypeExpression)) {
      return new AttrData(TypeKind.BUILD_IN, Multiplicity.MANDATORY, false, symTypeExpression);
    }
    else if (symTypeExpression.getTypeInfo() instanceof CDTypeSymbol
        && ((CDTypeSymbol) symTypeExpression.getTypeInfo()).isIsEnum()) {
      return new AttrData(TypeKind.ENUM, Multiplicity.MANDATORY, false, symTypeExpression);
    }
    else if (isWithinDomain(symTypeExpression)) {
      return new AttrData(TypeKind.DOMAIN, Multiplicity.MANDATORY, false, symTypeExpression);
    }
    return new AttrData(TypeKind.UNKNOWN, Multiplicity.MANDATORY, false, symTypeExpression);
  }
  
  public boolean isWithinDomain(SymTypeExpression symTypeExpression) {
    return true;
  }
  
  protected AttrData createFromGeneric(Multiplicity multiplicity,
      SymTypeExpression symTypeExpression, boolean ordered) {
    var inner = MCCollectionSymTypeRelations.getCollectionElementType(symTypeExpression);
    
    if (inner.isPrimitive() || SymTypeRelations.isCompatible(inner, stringSymTypeExpression)) {
      return new AttrData(TypeKind.BUILD_IN, multiplicity, ordered, inner);
    }
    else if (isWithinDomain(inner)) {
      return new AttrData(TypeKind.DOMAIN, multiplicity, ordered, inner);
    }
    return new AttrData(TypeKind.UNKNOWN, multiplicity, ordered, inner);
  }
  
}
