package de.monticore.cddiff.syntaxdiff;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;


import java.util.List;

public interface CDSyntaxDiff {

  List<CDTypeDiff<ASTCDClass,ASTCDClass>> getMatchedClassList();
  List<CDTypeDiff<ASTCDInterface,ASTCDInterface>> getMatchedInterfaces();
  List<CDTypeDiff<ASTCDEnum,ASTCDEnum>> getMatchedEnumList();
  List<CDAssociationDiff> getMatchedAssos();

  List<ASTCDClass> getAddedClasses();
  List<ASTCDAssociation> getAddedAssos();
  List<ASTCDInterface> getAddedInterfaces();
  List<ASTCDEnum> getAddedEnums();

  List<ASTCDClass> getDeletedClasses();
  List<ASTCDAssociation> getDeletedAssos();
  List<ASTCDInterface> getDeletedInterfaces();
  List<ASTCDEnum> getDeletedEnums();

  // Resolve methods for returning interpretations = what happend to the element?

  // SyntaxDiff.Interpretation resolve(ASTCDAttribute attribute, ASTCDClass astcdClass);

  // SyntaxDiff.Interpretation resolve(ASTCDClass astcdClass, ASTCDCompilationUnit compilationUnit);
  // SyntaxDiff.Interpretation resolve(ASTCDInterface astcdInterface, ASTCDCompilationUnit compilationUnit);
  // SyntaxDiff.Interpretation resolve(ASTCDEnum astcdEnum, ASTCDCompilationUnit compilationUnit);

  // SyntaxDiff.Interpretation resolve(ASTCDAssocType astcdAssocType, ASTCDAssociation astcdAssociation);

  // Various print functions, e.g. print full coloured first CD
   void print();
   void printCD1();
   void printCD2();
}
