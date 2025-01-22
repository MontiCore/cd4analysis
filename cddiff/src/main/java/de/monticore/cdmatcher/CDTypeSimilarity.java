package de.monticore.cdmatcher;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import java.util.ArrayList;
import java.util.List;

public class CDTypeSimilarity {
  public Double computeWeight(ASTCDType srcElem, ASTCDType tgtElem) {
    double weight;
    List<ASTCDAttribute> srcAttr = new ArrayList<>(srcElem.getCDAttributeList());
    List<ASTCDAttribute> tgtAttr = new ArrayList<>(tgtElem.getCDAttributeList());

    List<ASTCDAttribute> tgtAttrDeletedAttr = new ArrayList<>(tgtAttr);
    List<ASTCDAttribute> similarities = new ArrayList<>();

    for (ASTCDAttribute x : srcAttr) {
      for (ASTCDAttribute y : tgtAttr) {
        if (x.getName().equals(y.getName())) {
          tgtAttrDeletedAttr.remove(y);
          similarities.add(x);
        }
      }
    }

    List<ASTCDAttribute> allAttributes = new ArrayList<>(srcAttr);
    allAttributes.addAll(tgtAttrDeletedAttr);

    // Jaccard Index
    if (srcElem.getName().equals(tgtElem.getName())) {
      weight = (double) (similarities.size() + 2) / allAttributes.size();
    } else {
      weight = (double) similarities.size() / allAttributes.size();
    }

    return weight;
  }
}
