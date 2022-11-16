/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDConstructorFacade {

  private static CDConstructorFacade INSTANCE;

  private CDConstructorFacade() {}

  public static CDConstructorFacade getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CDConstructorFacade();
    }
    return INSTANCE;
  }

  /** full constructor adds all attributes of a class into the constructor parameters */
  public ASTCDConstructor createFullConstructor(
      final ASTModifier modifier, final ASTCDClass cdClass) {
    List<ASTCDParameter> parameterList =
        CDParameterFacade.getInstance().createParameters(cdClass.getCDAttributeList());
    return createConstructor(modifier, cdClass.getName(), parameterList);
  }

  /** default constructor creates a constructor without parameters */
  public ASTCDConstructor createDefaultConstructor(
      final ASTModifier modifier, final ASTCDClass cdClass) {
    return createConstructor(modifier, cdClass.getName(), new ArrayList<>());
  }

  /** base method for creation of a constructor via builder */
  public ASTCDConstructor createConstructor(
      final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return CD4CodeBasisMill.cDConstructorBuilder()
        .setModifier(modifier)
        .setName(name)
        .setCDParametersList(
            parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
        .build();
  }

  /** delegation methods for a more comfortable usage */
  public ASTCDConstructor createConstructor(
      final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    return createConstructor(modifier, name, Arrays.asList(parameters));
  }
}
