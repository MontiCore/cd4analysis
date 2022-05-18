/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.types.MCTypeFacade;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CDThrowsFacade {

  /**
   * Class that helps with the creation of ASTThrowsDeclaration
   */

  private static CDThrowsFacade cdThrowsFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDThrowsFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDThrowsFacade getInstance() {
    if (cdThrowsFacade == null) {
      cdThrowsFacade = new CDThrowsFacade();
    }
    return cdThrowsFacade;
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDThrowsDeclaration createThrowsDeclaration(final String exception) {
    return CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(mcTypeFacade.createQualifiedName(exception)).build();
  }

  public ASTCDThrowsDeclaration createThrowsDeclaration(final String ... exception) {
    ASTCDThrowsDeclaration throwsDecl = CD4CodeBasisMill.cDThrowsDeclarationBuilder().build();
    throwsDecl.addAllException(Arrays.stream(exception).map(mcTypeFacade::createQualifiedName).collect(Collectors.toList()));
    return throwsDecl;
  }

}
