/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

import de.monticore.cd.facade.CDConstructorFacade;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

public class ConstructorDecorator {

  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit compilationUnit) {
    for (ASTCDClass c : compilationUnit.getCDDefinition().getCDClassesList()) {
      c.addCDMember(CDConstructorFacade.getInstance().createDefaultConstructor(PUBLIC.build(), c));

      if (!c.getCDAttributeList().isEmpty()) {
        c.addCDMember(CDConstructorFacade.getInstance().createFullConstructor(PUBLIC.build(), c));
      }
    }
    return compilationUnit;
  }
}
