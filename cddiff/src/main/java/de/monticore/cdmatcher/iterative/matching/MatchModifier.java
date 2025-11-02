package de.monticore.cdmatcher.iterative.matching;

import de.monticore.cdmatcher.MatchingStrategy;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.math.DoubleMath.mean;

public class MatchModifier implements MatchingStrategy<ASTModifier> {

  @Override
  public double getScore(ASTModifier srcElem, ASTModifier tgtElem) {
    List<Double> modifierScores = new LinkedList<>();
    // exclusivity should be checked by the parser
    boolean srcHasAccessModifier = srcElem.isPublic() || srcElem.isPrivate() || srcElem.isProtected();
    boolean tgtHasAccessModifier = tgtElem.isPublic() || tgtElem.isPrivate() || tgtElem.isProtected();

    if(srcHasAccessModifier || tgtHasAccessModifier) {
      // consider no access modifier the same as public as class diagrams almost always contain only one package
      if((!srcHasAccessModifier || srcElem.isPublic()) && (!tgtHasAccessModifier || tgtElem.isPublic())) {
        modifierScores.add(1.0);
      } else if(srcElem.isPrivate() && tgtElem.isPrivate()) {
        modifierScores.add(1.0);
      } else if(srcElem.isProtected() && tgtElem.isProtected()) {
        modifierScores.add(1.0);
      } else if((!srcHasAccessModifier || srcElem.isPublic()) && tgtElem.isProtected()) {
        modifierScores.add(0.6);
      } else if((!tgtHasAccessModifier || tgtElem.isPublic()) && srcElem.isProtected()) {
        modifierScores.add(0.6);
      } else {
        modifierScores.add(0.0);
      }
    }

    if(srcElem.isStatic() || tgtElem.isStatic()) {
      if(srcElem.isStatic() && tgtElem.isStatic()) {
        modifierScores.add(1.0);
      } else {
        modifierScores.add(0.0);
      }
    }

    if(srcElem.isFinal() || tgtElem.isFinal()) {
      if (srcElem.isFinal() && tgtElem.isFinal()) {
        modifierScores.add(1.0);
      } else {
        modifierScores.add(0.0);
      }
    }

    if(srcElem.isAbstract() || tgtElem.isAbstract()) {
      if (srcElem.isAbstract() && tgtElem.isAbstract()) {
        modifierScores.add(1.0);
      } else {
        modifierScores.add(0.0);
      }
    }

    if(srcElem.isDerived() || tgtElem.isDerived()) {
      if (srcElem.isDerived() && tgtElem.isDerived()) {
        modifierScores.add(1.0);
      } else {
        modifierScores.add(0.0);
      }
    }

    if(modifierScores.isEmpty()) {
      return 1.0;
    }

    return mean(modifierScores);
  }

  public static boolean hasModifier(ASTModifier modifier) {
    return modifier.isAbstract() || modifier.isDerived() || modifier.isFinal() || modifier.isPrivate() || modifier.isProtected() || modifier.isPublic() || modifier.isStatic();
  }
}
