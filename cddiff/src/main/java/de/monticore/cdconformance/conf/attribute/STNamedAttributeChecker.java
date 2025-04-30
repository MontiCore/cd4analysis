package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconcretization.util.NameUtil;
import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

public class STNamedAttributeChecker extends AbstractAttributeChecker {

  public STNamedAttributeChecker(String mapping, MatchingStrategy<ASTCDType> typeMatcher) {
    super(mapping, typeMatcher);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {

    return refType.getCDAttributeList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return refType.getSpannedScope().resolveFieldMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
