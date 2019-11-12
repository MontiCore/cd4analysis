/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDConstructor;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4code._ast.CD4CodeMill;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that helps with the creation of ASTCDConstructors
 */

public class CDConstructorFacade {

  private static CDConstructorFacade cdConstructorFacade;

  private CDConstructorFacade() {
  }

  public static CDConstructorFacade getInstance() {
    if (cdConstructorFacade == null) {
      cdConstructorFacade = new CDConstructorFacade();
    }
    return cdConstructorFacade;
  }

  /**
   * full constructor adds all attributes of a class into the constructor parameters
   */

  public ASTCDConstructor createFullConstructor(final ASTModifier modifier, final ASTCDClass cdClass) {
    List<ASTCDParameter> parameterList = CDParameterFacade.getInstance().createParameters(cdClass.getCDAttributeList());
    return createConstructor(modifier, cdClass.getName(), parameterList);
  }

  /**
   * default constructor creates a constructor without parameters
   */

  public ASTCDConstructor createDefaultConstructor(final ASTModifier modifier, final ASTCDClass cdClass) {
    return createConstructor(modifier, cdClass.getName(), Collections.emptyList());
  }

  public ASTCDConstructor createDefaultConstructor(final ASTModifier modifier, final String name) {
    return createConstructor(modifier, name, Collections.emptyList());
  }

  /**
   * base method for creation of a constructor via builder
   */

  public ASTCDConstructor createConstructor(final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return CD4CodeMill.cDConstructorBuilder()
        .setModifier(modifier)
        .setName(name)
        .setCDParameterList(parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
        .build();
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDConstructor createConstructor(final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    return createConstructor(modifier, name, Arrays.asList(parameters));
  }

  public ASTCDConstructor createFullConstructor(final CDModifier modifier, final ASTCDClass cdClass) {
    return createFullConstructor(modifier.build(), cdClass);
  }

  public ASTCDConstructor createDefaultConstructor(final CDModifier modifier, final ASTCDClass cdClass) {
    return createDefaultConstructor(modifier.build(), cdClass);
  }

  public ASTCDConstructor createConstructor(final CDModifier modifier, final String name) {
    return createConstructor(modifier.build(), name, Collections.emptyList());
  }

  public ASTCDConstructor createConstructor(final CDModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    return createConstructor(modifier.build(), name, parameters);
  }
}
