package de.monticore.ow2cw.expander;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;

public interface CDExpander {

  ASTCDCompilationUnit getCD();

  /**
   * add newClass as subclass to superclass
   */
  void addNewSubClass(String name, ASTCDClass superclass);

  /**
   * add newClass as sub-class to astcdInterface
   */
  void addNewSubClass(String name, ASTCDInterface astcdInterface);

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  void addClass2Package(ASTCDClass astcdClass, String packageName);

  /**
   * Default Package is troublesome!
   * todo: fix problem with nested packages
   */
  void addClone(ASTCDType cdType);

  void addDummyClass(ASTCDClass srcClass);

  void addDummyClass(String dummyName);

}
