/*
 * (c) https://github.com/MontiCore/monticore
 */

package de.monticore.cd._symboltable;

import de.monticore.cd4code._visitor.CD4CodeDelegatorVisitor;

public class TypesScopeHelper extends CD4CodeDelegatorVisitor {
  public TypesScopeHelper() {
    setRealThis(this);

    setMCBasicTypesVisitor(new MCBasicTypesScopeHelper());
    setMCCollectionTypesVisitor(new MCCollectionTypesScopeHelper());
    setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesScopeHelper());
    setMCFullGenericTypesVisitor(new MCFullGenericTypesScopeHelper());
  }
}
