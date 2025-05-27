package de.monticore.cdconformance.inc.association;

import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdmatcher.matching.booleanMatchingStrategy.ExternalCandidatesMatchingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class STNamedAssocIncStrategy implements ExternalCandidatesMatchingStrategy<ASTCDAssociation> {

  protected ASTCDCompilationUnit refCD;
  protected String mapping;

  public STNamedAssocIncStrategy(ASTCDCompilationUnit refCD, String mapping) {
    this.refCD = refCD;
    this.mapping = mapping;
  }

  @Override
  public Set<ASTCDAssociation> getMatchedElements(ASTCDAssociation concrete) {
    return refCD.getCDDefinition().getCDAssociationsList().stream()
        .filter(assoc -> isMatched(concrete, assoc))
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isMatched(ASTCDAssociation concrete, ASTCDAssociation ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)
        && ref.isPresentName()) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return ((ICD4CodeScope) refCD.getEnclosingScope())
          .resolveCDAssociationDownMany(refName)
          .contains(ref.getSymbol());
    }
    return false;
  }
}
