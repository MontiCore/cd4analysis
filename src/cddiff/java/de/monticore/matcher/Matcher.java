package de.monticore.matcher;

import de.monticore.ast.ASTNode;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;

import java.util.List;

public class Matcher {

  public static boolean matchingAssocNameStrategy(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit tgtCD) {
    NameAssocMatcher associationNameMatch = new NameAssocMatcher(tgtCD);
    return associationNameMatch.isMatched(srcElem, tgtElem);
  }

  public static boolean matchingNameTypeStrategy(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit tgtCD) {
    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    return nameTypeMatch.isMatched(srcElem, tgtElem);
  }

  public static boolean matchingStructureTypeStrategy(
    ASTCDType srcElem,
    ASTCDType tgtElem,
    ASTCDCompilationUnit tgtCD) {
    StructureTypeMatcher structureTypeMatch = new StructureTypeMatcher(tgtCD);
    return structureTypeMatch.isMatched(srcElem, tgtElem);
  }

  public static boolean matchingAssocSubToSuperClass(
      ASTCDType srcElem,
      ASTCDType tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    NameTypeMatcher nameTypeMatch = new NameTypeMatcher(tgtCD);
    SuperTypeMatcher associationSubToSuperClass = new SuperTypeMatcher(nameTypeMatch, srcCD, tgtCD);
    return associationSubToSuperClass.isMatched(srcElem, tgtElem);
  }

  public static boolean matchingAssocSrcClassTgtRoleName(
      ASTCDAssociation srcElem,
      ASTCDAssociation tgtElem,
      ASTCDCompilationUnit srcCD,
      ASTCDCompilationUnit tgtCD) {

    NameTypeMatcher typeMatcher = new NameTypeMatcher(tgtCD);
    SrcTgtAssocMatcher associationSrcTgtMatch = new SrcTgtAssocMatcher(typeMatcher, srcCD, tgtCD);
    return associationSrcTgtMatch.isMatched(srcElem, tgtElem);
  }
}
