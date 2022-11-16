/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.facade.tf.*;
import java.io.IOException;
import java.util.List;

/**
 * Introduce Facade Pattern
 *
 * <p>Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class FacadePattern implements DesignPattern {

  public FacadePattern() {}

  /**
   * Applies the facade pattern to the given classes {@code facadeClasses}
   *
   * @param facadeClasses - list of classes a facade should be introduced for
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceFacadePattern(List<String> facadeClasses, ASTCDCompilationUnit ast)
      throws IOException {
    // Calculate Facade Name from all Classes
    String facadeClassNames = "";
    for (int i = 0; i < facadeClasses.size(); i++) {
      facadeClassNames += facadeClasses.get(i);
    }
    facadeClassNames += "Facade";
    return introduceFacadePattern(facadeClasses, facadeClassNames, ast);
  }

  /**
   * Applies the facade pattern to the given classes {@code facadeClasses} and applies the facade
   * class name {@code facadeClassName}
   *
   * @param facadeClasses - list of classes a facade should be introduced for
   * @param facadeClassName - name of the facade class
   * @param ast - class diagram to be transformed
   * @return true, if applied successfully
   */
  public boolean introduceFacadePattern(
      List<String> facadeClasses, String facadeClassName, ASTCDCompilationUnit ast)
      throws IOException {

    // Create Facade Class
    if (!transformationUtility.createSimpleClass(facadeClassName, ast)) {
      return false;
    }

    // Create Association from Class to Facade
    if (!transformationUtility.createBiDirAssociations(facadeClassName, facadeClasses, ast)) {
      return false;
    }

    while (changeAssociation(facadeClassName, ast))
      ;
    return true;
  }

  private boolean changeAssociation(String fassadeClassName, ASTCDCompilationUnit ast)
      throws IOException {

    // Change Name of FacadeClasses to FacadeClassName for bi directional
    // associations
    FacadeBiLeft biLeft = new FacadeBiLeft(ast);
    biLeft.set_$nameFacade(fassadeClassName);
    if (biLeft.doPatternMatching()) {
      biLeft.doReplacement();
      return true;
    }

    FacadeBiRight biRight = new FacadeBiRight(ast);
    biRight.set_$nameFacade(fassadeClassName);
    if (biRight.doPatternMatching()) {
      biRight.doReplacement();
      return true;
    }

    // Change Name of FacadeClasses to FacadeClassName for uni directional
    // associations
    FacadeUniLeft uniLeft = new FacadeUniLeft(ast);
    uniLeft.set_$nameFacade(fassadeClassName);
    if (uniLeft.doPatternMatching()) {
      uniLeft.doReplacement();
      return true;
    }

    FacadeUniRight uniRight = new FacadeUniRight(ast);
    uniRight.set_$nameFacade(fassadeClassName);
    if (uniRight.doPatternMatching()) {
      uniRight.doReplacement();
      return true;
    }

    // Change Name of FacadeClasses to FacadeClassName for right to left
    // directional associations
    FacadeRightDirLeft leftToRightLeft = new FacadeRightDirLeft(ast);
    leftToRightLeft.set_$nameFacade(fassadeClassName);
    if (leftToRightLeft.doPatternMatching()) {
      leftToRightLeft.doReplacement();
      return true;
    }

    FacadeRightDirRight leftToRightRight = new FacadeRightDirRight(ast);
    leftToRightRight.set_$nameFacade(fassadeClassName);
    if (leftToRightRight.doPatternMatching()) {
      leftToRightRight.doReplacement();
      return true;
    }

    // Change Name of FacadeClasses to FacadeClassName for left to right
    // directional associations
    FacadeLeftDirLeft rightToLeftLeft = new FacadeLeftDirLeft(ast);
    rightToLeftLeft.set_$nameFacade(fassadeClassName);
    if (rightToLeftLeft.doPatternMatching()) {
      rightToLeftLeft.doReplacement();
      return true;
    }

    FacadeLeftDirRight rightToLeftRight = new FacadeLeftDirRight(ast);
    rightToLeftRight.set_$nameFacade(fassadeClassName);
    if (rightToLeftRight.doPatternMatching()) {
      rightToLeftRight.doReplacement();
      return true;
    }

    return false;
  }
}
