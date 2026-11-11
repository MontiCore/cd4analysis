/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.decorators;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

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
    
    @Override
    public String toString() {
      return "AttrData{" + "typeKind=" + typeKind + ", multiplicity=" + multiplicity + ", ordered="
          + ordered + ", symTypeExpression=" + symTypeExpression.printFullName() + '}';
    }
    
  }
  
  protected SymTypeExpression stringSymTypeExpression;
  
  public AttrHelper() {
    stringSymTypeExpression = SymTypeExpressionFactory.createStringType();
  }
  
  public AttrData getFromRole(CDRoleSymbol roleSymbol) {
    // The cardinality of role-symbols is stored on the symbol, not its type!
    AttrData data = getFromSymTypeExpr(roleSymbol.getType());
    if (!roleSymbol.isPresentCardinality()) {
      return data;
    }
    if (roleSymbol.getCardinality().isMult() || roleSymbol.getCardinality().isAtLeastOne()) {
      return new AttrData(data.getTypeKind(), Multiplicity.SET, roleSymbol.isIsOrdered(), data
          .getSymTypeExpression());
    }
    else if (roleSymbol.getCardinality().isOpt()) {
      return new AttrData(data.getTypeKind(), Multiplicity.OPTIONAL, false, data
          .getSymTypeExpression());
    }
    else if (roleSymbol.getCardinality().isOne()) {
      return data;
    }
    else if (roleSymbol.getCardinality().toCardinality().isNoUpperLimit()) {
      return new AttrData(data.getTypeKind(), Multiplicity.SET, roleSymbol.isIsOrdered(), data
          .getSymTypeExpression());
    }
    Log.warn("0xTODO: Unhandled cardinality " + CD4AnalysisMill.prettyPrint(roleSymbol
        .getCardinality().toCardinality(), false), roleSymbol.getSourcePosition());
    return data;
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
