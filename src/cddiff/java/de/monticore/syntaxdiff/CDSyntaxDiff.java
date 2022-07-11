package de.monticore.syntaxdiff;

import de.monticore.cdassociation._ast.ASTCDAssocType;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;


import java.util.List;

public interface CDSyntaxDiff {
   List<ClassDiff> getMatchedClassList();
   List<ASTCDClass> getAddedClasses();
   List<ASTCDClass> getDeletedClasses();
   List<AssoDiff> getMatchedAssos();
   List<ASTCDAssociation> getAddedAssos();
   List<ASTCDAssociation> getDeletedAssos();

   List<InterfaceDiff> getMatchedInterfaces();
   List<ASTCDInterface> getAddedInterfaces();
   List<ASTCDInterface> getDeletedInterfaces();

  // List<EnumDiff> getMatchedEnums();
   List<ASTCDEnum> getAddedEnums();
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
