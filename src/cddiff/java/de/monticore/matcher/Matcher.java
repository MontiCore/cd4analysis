package de.monticore.matcher;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

public class Matcher {

  public static boolean matchingAssocNameStrategy(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    NameAssocMatcher associationNameMatch = new NameAssocMatcher();
    return associationNameMatch.isMatched(srcElem, tgtElem, tgtCD, srcCD);
  }

  public static boolean matchingNameTypeStrategy(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher();
    return nameTypeMatch.isMatched(srcElem, tgtElem, srcCD, tgtCD);
  }

  public static boolean matchingAssocSubToSuperClass(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    NameTypeMatcher nameTypeMatch = new NameTypeMatcher();
    SuperTypeMatcher associationSubToSuperClass = new SuperTypeMatcher(nameTypeMatch);
    return associationSubToSuperClass.isMatched(srcElem, tgtElem, srcCD, tgtCD);
  }

  public static boolean matchingAssocSrcClassTgtRoleName(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    NameTypeMatcher typeMatcher = new NameTypeMatcher();
    SrcTgtAssocMatcher associationSrcTgtMatch = new SrcTgtAssocMatcher(typeMatcher);
    return associationSrcTgtMatch.isMatched(srcElem, tgtElem, srcCD, tgtCD);
  }
}
