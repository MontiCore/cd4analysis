package de.monticore.cdmatcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

public class CD2CDMatcher {

  public static boolean matchAssocsByName(
      ASTCDAssociation srcElem, ASTCDAssociation tgtElem, ASTCDCompilationUnit tgtCD) {
    MatchCDAssocsByName matcher = new MatchCDAssocsByName(tgtCD);
    return matcher.isMatched(srcElem, tgtElem);
  }

  public static boolean matchTypesByName(
      ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit tgtCD) {
    MatchCDTypesByName matcher = new MatchCDTypesByName(tgtCD);
    return matcher.isMatched(srcElem, tgtElem);
  }

  public static boolean matchTypesByStructure(
      ASTCDType srcElem, ASTCDType tgtElem, ASTCDCompilationUnit tgtCD) {
    MatchCDTypeByStructure matcher = new MatchCDTypeByStructure(tgtCD);
    return matcher.isMatched(srcElem, tgtElem);
  }

  public static boolean matchSubToSuperType(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    MatchCDTypesByName matcher = new MatchCDTypesByName(tgtCD);
    MatchCDTypesToSuperTypes superTypeMatcher = new MatchCDTypesToSuperTypes(matcher, srcCD, tgtCD);
    return superTypeMatcher.isMatched(srcElem, tgtElem);
  }

  public static boolean matchAssocBySrcTypeAndTgtRole(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    MatchCDTypesByName typeMatcher = new MatchCDTypesByName(tgtCD);
    MatchCDAssocsBySrcNameAndTgtRole matcher = new MatchCDAssocsBySrcNameAndTgtRole(typeMatcher, srcCD, tgtCD);
    return matcher.isMatched(srcElem, tgtElem);
  }
}
