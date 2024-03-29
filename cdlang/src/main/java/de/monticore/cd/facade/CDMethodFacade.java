/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.facade;

import com.google.common.collect.Lists;
import de.monticore.cd.facade.exception.CDFactoryErrorCode;
import de.monticore.cd.facade.exception.CDFactoryException;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDMethodBuilder;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cd4codebasis._ast.ASTCDThrowsDeclaration;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Class that helps with the creation of ASTCDMethods */
public class CDMethodFacade {

  private static CDMethodFacade cdMethodFacade;

  private final MCTypeFacade mcTypeFacade;

  private CDMethodFacade() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
  }

  public static CDMethodFacade getInstance() {
    if (cdMethodFacade == null) {
      cdMethodFacade = new CDMethodFacade();
    }
    return cdMethodFacade;
  }

  /** base method for creation of a methods via builder */
  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final List<ASTCDParameter> parameters) {
    return createMethodInternal(modifier, returnType, name, true, parameters, Lists.newArrayList());
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final List<ASTCDParameter> parameters) {
    return createMethodInternal(
        modifier, returnType, name, false, parameters, Lists.newArrayList());
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final List<ASTCDParameter> parameters,
      final List<ASTMCQualifiedName> exceptions) {
    return createMethodInternal(modifier, returnType, name, true, parameters, Lists.newArrayList());
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final List<ASTCDParameter> parameters,
      final List<ASTMCQualifiedName> exceptions) {
    return createMethodInternal(
        modifier, returnType, name, false, parameters, Lists.newArrayList());
  }

  public ASTCDMethod createMethodInternal(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      boolean isDefault,
      final List<ASTCDParameter> parameters,
      final List<ASTMCQualifiedName> exceptions) {
    ASTCDMethodBuilder builder =
        CD4CodeBasisMill.cDMethodBuilder()
            .setModifier(modifier)
            .setMCReturnType(returnType)
            .setName(name)
            .setCDParametersList(
                parameters.stream().map(ASTCDParameter::deepClone).collect(Collectors.toList()))
            .setIsDefault(isDefault);
    if (!exceptions.isEmpty()) {
      ASTCDThrowsDeclaration throwsDecl =
          CD4CodeMill.cDThrowsDeclarationBuilder().addAllException(exceptions).build();
      builder.setCDThrowsDeclaration(throwsDecl);
    }
    return builder.build();
  }

  /** delegation methods for a more comfortable usage */
  public ASTCDMethod createMethod(
      final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    ASTMCReturnType returnType =
        MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(mcTypeFacade.createVoidType()).build();
    return createMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier, final String name, final ASTCDParameter... parameters) {
    ASTMCReturnType returnType =
        MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(mcTypeFacade.createVoidType()).build();
    return createDefaultMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier, final String name, final List<ASTCDParameter> parameters) {
    ASTMCReturnType returnType =
        MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(mcTypeFacade.createVoidType()).build();
    return createMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final ASTMCType returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final ASTMCReturnType returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createDefaultMethod(modifier, returnType, name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final String returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createMethod(
        modifier, mcTypeFacade.createQualifiedType(returnType), name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final String returnType,
      final String name,
      final String exception,
      final ASTCDParameter... parameters) {
    ASTMCReturnType ast =
        MCBasicTypesMill.mCReturnTypeBuilder()
            .setMCType(mcTypeFacade.createQualifiedType(returnType))
            .build();
    return createMethodInternal(
        modifier,
        ast,
        name,
        false,
        Arrays.asList(parameters),
        Lists.newArrayList(mcTypeFacade.createQualifiedType(exception).getMCQualifiedName()));
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final String returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createDefaultMethod(
        modifier, mcTypeFacade.createQualifiedType(returnType), name, Arrays.asList(parameters));
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final Class<?> returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createMethod(modifier, returnType.getCanonicalName(), name, parameters);
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final Class<?> returnType,
      final String name,
      final ASTCDParameter... parameters) {
    return createDefaultMethod(modifier, returnType.getCanonicalName(), name, parameters);
  }

  public ASTCDMethod createMethod(
      final ASTModifier modifier,
      final ASTMCType astmcType,
      final String name,
      final List<ASTCDParameter> parameters) {
    ASTMCReturnType returnType =
        MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astmcType).build();
    return createMethod(modifier, returnType, name, parameters);
  }

  public ASTCDMethod createDefaultMethod(
      final ASTModifier modifier,
      final ASTMCType astmcType,
      final String name,
      final List<ASTCDParameter> parameters) {
    ASTMCReturnType returnType =
        MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astmcType).build();
    return createDefaultMethod(modifier, returnType, name, parameters);
  }

  /**
   * creates a method by a string definition with the help of the parser only use this method if no
   * of the other methods fit your context !
   */
  public ASTCDMethod createMethodByDefinition(final String signature) {
    Optional<ASTCDMethod> method;
    try {
      method = CD4CodeMill.parser().parse_StringCDMethod(signature);
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature, e);
    }

    if (!method.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_METHOD, signature);
    }

    return method.get();
  }
}
