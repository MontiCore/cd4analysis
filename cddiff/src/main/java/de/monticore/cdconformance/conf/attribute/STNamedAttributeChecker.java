package de.monticore.cdconformance.conf.attribute;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdconformance.inc.type.TypeIncarnationHelper;

import java.util.List;
import java.util.stream.Collectors;

public class STNamedAttributeChecker extends AbstractAttributeChecker {

  public STNamedAttributeChecker(String mapping, TypeIncarnationHelper typeHelper) {
    super(mapping, typeHelper);
  }

  @Override
  public List<ASTCDAttribute> getMatchedElements(ASTCDAttribute concrete) {
    return referenceType.getCDAttributeList().stream()
        .filter(ref -> isMatched(concrete, ref))
        .collect(Collectors.toList());
  }

  @Override
  public boolean isMatched(ASTCDAttribute concrete, ASTCDAttribute ref) {
    if (concrete.getModifier().isPresentStereotype()
        && concrete.getModifier().getStereotype().contains(mapping)) {
      String refName = concrete.getModifier().getStereotype().getValue(mapping);
      return referenceType.getSpannedScope().resolveFieldMany(refName).contains(ref.getSymbol());
    }
    return false;
  }
}
