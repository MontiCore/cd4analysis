/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff.ow2cw.expander;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import java.util.Optional;
import java.util.Set;

public interface CDExpander {

  ASTCDCompilationUnit getCD();

  /** add newClass as subclass to superclass */
  void addNewSubClass(String name, ASTCDClass superclass);

  /** add newClass as sub-class to astcdInterface */
  void addNewSubClass(String name, ASTCDInterface astcdInterface);

  /** Default Package is troublesome! todo: fix problem with nested packages */
  void addType2Package(ASTCDType astcdType, String packageName);

  /** Default Package is troublesome! todo: fix problem with nested packages */
  Optional<ASTCDType> addClone(ASTCDType cdType);

  Optional<ASTCDClass> addDummyClass(ASTCDType srcType);

  Optional<ASTCDInterface> addDummyInterface(ASTCDInterface srcInterface);

  Optional<ASTCDClass> addDummyClass(String dummyName);

  Optional<ASTCDInterface> addDummyInterface(String dummyName);

  Optional<ASTCDAssociation> buildDummyAssociation(String left, String roleName, String right);

  void addAssociation(ASTCDAssociation assoc);

  void addAttribute(ASTCDType type, ASTCDAttribute attribute);

  void addEnumConstant(ASTCDEnum targetEnum, ASTCDEnumConstant constant);

  void updateExtends(ASTCDClass targetClass, Set<String> extendsSet);

  void updateImplements(ASTCDClass targetClass, Set<String> implementsSet);

  void updateExtends(ASTCDInterface targetInterface, Set<String> extendsSet);

  void updateUnspecifiedDir2Default();

  void mismatchDir(ASTCDAssociation src, ASTCDAssociation target);

  void mismatchDirInReverse(ASTCDAssociation src, ASTCDAssociation target);

  void matchDir(ASTCDAssociation src, ASTCDAssociation target);

  void matchDirInReverse(ASTCDAssociation src, ASTCDAssociation target);

  void addImportStatement(ASTMCImportStatement imp);
}
