/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import de.monticore.cd.facade.exception.CDFactoryErrorCode;
import de.monticore.cd.facade.exception.CDFactoryException;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class that helps with the creation of ASTCDMethods
 */

public class CDMethodFacade {

  private static CDMethodFacade cdMethodFacade;

  private final MCTypeFacade cdTypeFacade;

  private final CD4CodeParser parser;

  private CDMethodFacade() {
    this.cdTypeFacade = MCTypeFacade.getInstance();
    this.parser = new CD4CodeParser();
  }

  public static CDMethodFacade getInstance() {
    if (cdMethodFacade == null) {
      cdMethodFacade = new CDMethodFacade();
    }
    return cdMethodFacade;
  }

  /**
   * creates a method by a string definition with the help of the parser
   * only use this method if no of the other methods fit your context !
   */

  public ASTCDMethod createMethodByDefinition(final String signature) {
    Optional<ASTCDMethod> method;
    try {
      method = parser.parseCDMethod(new StringReader(signature));
    }
    catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature, e);
    }

    if (!method.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature);
    }

    return method.get();
  }

  /**
   * base method for creation of a methods via builder
   */

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name, final List<ASTCDParameter> parameters) {
    return CD4CodeBasisMill.cDMethodBuilder()
        .setModifier(modifier)
        .setMCReturnType(returnType)
        .setName(name)
        .setCDParametersList(parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
        .build();
  }

  /**
   * delegation methods for a more comfortable usage
   */

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(cdTypeFacade.createVoidType()).build();
    return createMethod(modifier, returnType, name);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(cdTypeFacade.createVoidType()).build();
    return createMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(cdTypeFacade.createVoidType()).build();
    return createMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCType returnType, final String name) {
    return createMethod(modifier, returnType, name, Collections.emptyList());
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name) {
    return createMethod(modifier, returnType, name, Collections.emptyList());
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCReturnType returnType, final String name, final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(final ASTModifier modifier, final ASTMCType astmcType, final String name, final List<ASTCDParameter> parameters) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astmcType).build();
    return createMethod(modifier, returnType, name, parameters);
  }
}
