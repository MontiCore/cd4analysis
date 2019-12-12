/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.cd4analysis._symboltable;

import java.util.function.Predicate;

import static com.google.common.base.Strings.nullToEmpty;

public class CDAssociationNameAndTargetNamePredicate implements Predicate<CDAssociationSymbol> {

  private final String assocName;
  private final String assocTargetName;

  public CDAssociationNameAndTargetNamePredicate(final String assocName, final String assocTargetName) {
    this.assocName = nullToEmpty(assocName);
    this.assocTargetName = nullToEmpty(assocTargetName);
  }

  @Override
  public boolean test(final CDAssociationSymbol symbol) {
    if (symbol != null) {
      if (!symbol.isPresentAssocName()) {
        return false;
      }
      return symbol.getAssocName().equals(assocName)
          && symbol.getTargetType().getName().equals(assocTargetName);
    }
    return false;
  }
}
