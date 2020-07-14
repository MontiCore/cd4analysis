/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CDParameterFacade {

  /**
   * Class that helps with the creation of ASTCDParameter
   */

  private static CDParameterFacade cdParameterFacade;

  private CDParameterFacade() {
  }

  public static CDParameterFacade getInstance() {
    if (cdParameterFacade == null) {
      cdParameterFacade = new CDParameterFacade();
    }
    return cdParameterFacade;
  }

  /**
   * base method for creation of a parameter via builder
   */

  public ASTCDParameter createParameter(final ASTMCType type, final String name) {
    return CD4CodeBasisMill.cDParameterBuilder()
        .setMCType(type)
        .setName(name)
        .build();
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public List<ASTCDParameter> createParameters(final List<ASTCDAttribute> attributes) {
    return attributes.stream()
        .map(this::createParameter)
        .collect(Collectors.toList());
  }

  public ASTCDParameter createParameter(final ASTMCType type) {
    return createParameter(type, StringTransformations.uncapitalize(type.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()))));
  }

  public ASTCDParameter createParameter(final Class<?> type, final String name) {
    return createParameter(MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDParameter createParameter(final Class<?> type) {
    return createParameter(MCTypeFacade.getInstance().createQualifiedType(type), StringTransformations.uncapitalize(type.getSimpleName()));
  }

  public ASTCDParameter createParameter(final ASTCDAttribute ast) {
    return createParameter(ast.getMCType().deepClone(), ast.getName());
  }

  public List<ASTCDParameter> createParameters(final ASTCDAttribute... attributes) {
    return createParameters(Arrays.asList(attributes));
  }
}
