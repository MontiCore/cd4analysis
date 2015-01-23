/*
 * Copyright (c) 2014 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package cd4analysis.symboltable;

import cd4analysis.symboltable.references.CDTypeSymbolReference;
import com.google.common.base.Optional;
import de.cd4analysis._ast.*;
import de.monticore.symboltable.CompilationUnitScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.types._ast.ASTImportStatement;
import de.monticore.types._ast.ASTQualifiedName;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.helper.NameHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static mc.helper.NameHelper.dotSeparatedStringFromList;

// TODO PN introduce CD4AnalysisSymbolTableCreationVisitor as soon as the CD4ABaseVisitor is generated
public class CD4AnalysisSymbolTableCreator extends SymbolTableCreator {

  private String fullClassDiagramName = "";

  public CD4AnalysisSymbolTableCreator(ResolverConfiguration resolverConfig, @Nullable MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }


  public void visit(ASTCDCompilationUnit compilationUnit) {
    final String cdName = compilationUnit.getCDDefinition().getName();

    Log.info("Building Symboltable for CD: " + cdName,
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

      String packageName = dotSeparatedStringFromList(compilationUnit.getPackage());
      // use CD name as part of the package name
      fullClassDiagramName = packageName.isEmpty() ? cdName : packageName + "." + cdName;

    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getImportStatements() != null) {
      for (ASTImportStatement imp : compilationUnit.getImportStatements()) {
        imports.add(new ImportStatement(dotSeparatedStringFromList(imp.getImportList()), imp.isStar()));
      }
    }

    CompilationUnitScope scope = new CompilationUnitScope(Optional.absent(), fullClassDiagramName, imports);
    putOnStackAndSetEnclosingIfExists(scope);
  }

  public void endVisit(ASTCDCompilationUnit compilationUnit) {
    removeCurrentScope();

    Log.info("Finished build of symboltable for CD: " + compilationUnit.getCDDefinition().getName(),
        CD4AnalysisSymbolTableCreator.class.getSimpleName());
  }

  public void visit(ASTCDDefinition astDefinition) {
    // nothing to do here...
  }

  public void endVisit(ASTCDDefinition cdDefinition) {
    cdDefinition.getCDAssociations().forEach(this::handleAssociation);
  }

  public void visit(ASTCDClass astClass) {
    CDTypeSymbol typeSymbol = new CDTypeSymbol(astClass.getName());

    if (astClass.getSuperclasses().isPresent()) {
      CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(astClass.getSuperclasses().get());
      typeSymbol.setSuperClass(superClassSymbol);
    }

    final ASTModifier astModifier = astClass.getModifier().or(new ASTModifier.Builder().build());

    setModifiersOfType(typeSymbol, astModifier);

    addInterfacesToType(typeSymbol, astClass.getInterfaces());

    defineInScope(typeSymbol);
    putScopeOnStackAndSetEnclosingIfExists(typeSymbol);
  }

  private void setModifiersOfType(CDTypeSymbol typeSymbol, ASTModifier astModifier) {
    if (astModifier != null) {
      typeSymbol.setAbstract(astModifier.isAbstract());
      typeSymbol.setFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        typeSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        typeSymbol.setPrivate();
      } else {
        // public is default
        typeSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        for (ASTStereoValue stereoValue : astModifier.getStereotype().get().getValues()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
          Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          typeSymbol.addStereotype(stereotype);
        }
      }
    }
  }

  private CDTypeSymbolReference createCDTypeSymbolFromReference(ASTReferenceType astReferenceType) {
    // TODO PN replace by type converter
    CDTypeSymbolReference superSymbol = null;
    if (astReferenceType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSuperClass = (ASTSimpleReferenceType) astReferenceType;
      superSymbol = new CDTypeSymbolReference(Names.getQualifiedName(astSuperClass.getName()),
        currentScope().get());
    }

    return superSymbol;
  }

  public void visit(ASTCDAttribute astAttribute) {
    String typeName = "TODO_Type"; // TODO PN use TypePrinter for astAttribute.getType() instead
    CDTypeSymbolReference typeReference = new CDTypeSymbolReference(typeName, currentScope().get());

    CDFieldSymbol fieldSymbol = new CDFieldSymbol(astAttribute.getName(), typeReference);

    if (astAttribute.getModifier().isPresent()) {
      final ASTModifier astModifier = astAttribute.getModifier().get();

      fieldSymbol.setDerived(astModifier.isDerived());
      fieldSymbol.setStatic(astModifier.isStatic());
      fieldSymbol.setFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        fieldSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        fieldSymbol.setPrivate();
      } else {
        // public is default
        fieldSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        for (ASTStereoValue stereoValue : astModifier.getStereotype().get().getValues()) {
          // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
          Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          fieldSymbol.addStereotype(stereotype);
        }
      }
    }

    defineInScope(fieldSymbol);
  }

  public void endVisit(ASTCDClass astClass) {
    removeCurrentScope();
  }

  public void visit(ASTCDInterface astInterface) {
    CDTypeSymbol typeSymbol = new CDTypeSymbol(astInterface.getName());
    typeSymbol.setInterface(true);
    // Interfaces are always abstract
    typeSymbol.setAbstract(true);

    addInterfacesToType(typeSymbol, astInterface.getInterfaces());
    setModifiersOfType(typeSymbol, astInterface.getModifier().or(new ASTModifier.Builder().build()));

    // Interfaces are always abstract
    typeSymbol.setAbstract(true);


    defineInScope(typeSymbol);
    putScopeOnStackAndSetEnclosingIfExists(typeSymbol);
  }

  private void addInterfacesToType(CDTypeSymbol typeSymbol, ASTReferenceTypeList astInterfaces) {
    if (astInterfaces != null) {
      for (ASTReferenceType superInterface : astInterfaces) {
        CDTypeSymbolReference superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
        typeSymbol.addInterface(superInterfaceSymbol);
      }
    }
  }

  public void endVisit(ASTCDInterface astInterface) {
    removeCurrentScope();
  }

  public void visit(ASTCDEnum astEnum) {
    CDTypeSymbol enumSymbol = new CDTypeSymbol(astEnum.getName());
    enumSymbol.setEnum(true);

    if (astEnum.getCDEnumConstants() != null) {
      for (ASTCDEnumConstant astConstant : astEnum.getCDEnumConstants()) {
        CDFieldSymbol constantSymbol = new CDFieldSymbol(astConstant.getName(), enumSymbol);
        constantSymbol.setEnumConstant(true);
        // enum constants are implicitly public static final (Java Langspec 3rd Edition Chapter 8.9 Enums)
        constantSymbol.setStatic(true);
        constantSymbol.setFinal(true);

        enumSymbol.addField(constantSymbol);
      }
    }

    addInterfacesToType(enumSymbol, astEnum.getInterfaces());
    setModifiersOfType(enumSymbol, astEnum.getModifier().or(new ASTModifier.Builder().build()));

    defineInScope(enumSymbol);
    putScopeOnStackAndSetEnclosingIfExists(enumSymbol);
  }

  public void endVisit(ASTCDEnum astEnum) {
    removeCurrentScope();
  }


  public void visit(ASTCDMethod astMethod) {
    CDMethodSymbol methodSymbol = new CDMethodSymbol(astMethod.getName());

    setModifiersOfMethod(methodSymbol, astMethod.getModifier());
    setParametersOfMethod(methodSymbol, astMethod);
    setReturnTypeOfMethod(methodSymbol);
    setExceptionsOfMethod(methodSymbol, astMethod);
    setDefiningTypeOfMethod(methodSymbol);

    defineInScope(methodSymbol);
    putScopeOnStackAndSetEnclosingIfExists(methodSymbol);
  }

  public void endVisit(ASTCDMethod astMethod) {
    removeCurrentScope();
  }

  private void setModifiersOfMethod(CDMethodSymbol methodSymbol, ASTModifier astModifier) {
    if (astModifier != null) {
      methodSymbol.setAbstract(astModifier.isAbstract());
      methodSymbol.setStatic(astModifier.isStatic());
      methodSymbol.setFinal(astModifier.isFinal());

      if (astModifier.isPrivate()) {
        methodSymbol.setPrivate();
      }
      else if(astModifier.isProtected()) {
        methodSymbol.setProtected();
      }
      else {
        methodSymbol.setPublic();
      }

      if (astModifier.getStereotype().isPresent()) {
        addStereotypes(methodSymbol, astModifier.getStereotype().get());
      }
    }
  }

  private void addStereotypes(CDMethodSymbol methodSymbol, ASTStereotype astStereotype) {
    if (astStereotype != null) {
      for (ASTStereoValue val : astStereotype.getValues()) {
        // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
        methodSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

  private void setParametersOfMethod(CDMethodSymbol methodSymbol, ASTCDMethod astMethod) {
    if (astMethod.getCDParameters() != null) {
      CDTypeSymbolReference paramTypeSymbol;

      for (ASTCDParameter astParameter : astMethod.getCDParameters()) {
        final String paramName = astParameter.getName();
        // TODO PN use ASTTypesConverter
        //paramTypeSymbol = ASTTypesConverter.astTypeToTypeEntry(CDTypeEntryCreator.getInstance(), // astParameter.getType());
        paramTypeSymbol = new CDTypeSymbolReference("TODO_TYPE", currentScope().get());

        if (astParameter.isEllipsis()) {
          methodSymbol.setEllipsisParameterMethod(true);
          // ellipsis parameters are (like) arrays
          // TODO: Ist das so?
          // paramTypeSymbol = CDTypeEntryCreator.getInstance().create(paramTypeSymbol, 1);
        }

        final CDFieldSymbol parameterSymbol = new CDFieldSymbol(paramName, paramTypeSymbol);
        parameterSymbol.setParameter(true);
        // Parameters are always private
        parameterSymbol.setPrivate();

        methodSymbol.addParameter(parameterSymbol);
      }
    }
  }

  private void setReturnTypeOfMethod(CDMethodSymbol methodSymbol) {
    // TODO PN use ASTTypesConverter
    CDTypeSymbolReference returnSymbol = new CDTypeSymbolReference("TODO_RETURN_TYPE", currentScope().get());
    /*ASTTypesConverter
    .astReturnTypeToTypeEntry
        (CDTypeEntryCreator.getInstance(), cdMethod.getReturnType());
    if (returnSymbol == null) {
      delegator.addErrorToCurrentResource("Return type couldn't be converted: " + ASTTypesConverter.astReturnTypeToString(cdMethod.getReturnType()));
    }
    else {*/
    methodSymbol.setReturnType(returnSymbol);
    //}
  }

  private void setExceptionsOfMethod(CDMethodSymbol methodSymbol, ASTCDMethod astMethod) {
    if (astMethod.getExceptions() != null) {
      for (ASTQualifiedName exceptionName : astMethod.getExceptions()) {
        CDTypeSymbol exception = new CDTypeSymbolReference(exceptionName.toString(), currentScope().get());
        methodSymbol.addException(exception);
      }
    }
  }

  private void setDefiningTypeOfMethod(CDMethodSymbol methodSymbol) {
    if (currentSymbol().isPresent()) {
      if (currentSymbol().get() instanceof CDTypeSymbol) {
        CDTypeSymbol definingType = (CDTypeSymbol) currentSymbol().get();
        methodSymbol.setDefiningType(definingType);

        if (definingType.isInterface()) {
          methodSymbol.setAbstract(true);
        }
      }
    }
  }

  private void handleAssociation(ASTCDAssociation cdAssoc) {
    handleLeftToRightAssociation(cdAssoc);
    handleRightToLeftAssociation(cdAssoc);
  }

  private void handleRightToLeftAssociation(ASTCDAssociation cdAssoc) {
    if (cdAssoc.isRightToLeft() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      CDAssociationSymbol assocRight2LeftSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getRightReferenceName(), cdAssoc.getLeftReferenceName());
      // complete association properties
      if (assocRight2LeftSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocRight2LeftSymbol.setRelationship(Relationship.PART);
        }
        assocRight2LeftSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orNull()));
        assocRight2LeftSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orNull()));
        assocRight2LeftSymbol.setRole(cdAssoc.getLeftRole().orNull());

        if (cdAssoc.getLeftModifier().isPresent()) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().get().getStereotype().orNull());
        }

        if (cdAssoc.getRightQualifier().isPresent()) {
          ASTCDQualifier qualifier = cdAssoc.getRightQualifier().get();
          if ((qualifier.getName() != null) && (qualifier.getName().equals(""))) {
            assocRight2LeftSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType() != null) {
            // TODO PN get type
            //            assocRight2LeftSymbol.setQualifier(qualifier.printType());
            assocRight2LeftSymbol.setQualifier("TODO_QUALIFIER_TYPE");
          }
        }
        assocRight2LeftSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isSimple());
      }
    }
  }

  private void handleLeftToRightAssociation(ASTCDAssociation cdAssoc) {
    if (cdAssoc.isLeftToRight() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      CDAssociationSymbol assocLeft2RightSymbol = createAssociationSymbol(cdAssoc, cdAssoc.getLeftReferenceName(),
          cdAssoc.getRightReferenceName());

      if (assocLeft2RightSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocLeft2RightSymbol.setRelationship(Relationship.COMPOSITE);
        }
        assocLeft2RightSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality().orNull()));
        assocLeft2RightSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality().orNull()));
        assocLeft2RightSymbol.setRole(cdAssoc.getRightRole().orNull());
        if (cdAssoc.getRightModifier().isPresent()) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().get().getStereotype().orNull());
        }

        if (cdAssoc.getLeftQualifier().isPresent()) {
          ASTCDQualifier qualifier = cdAssoc.getLeftQualifier().get();
          if ((qualifier.getName() != null) && (!qualifier.getName().equals(""))) {
            assocLeft2RightSymbol.setQualifier(qualifier.getName());
          }
          else if (qualifier.getType() != null) {
            // TODO PN get type
            //            assocLeft2RightSymbol.setQualifier(qualifier.printType());
            assocLeft2RightSymbol.setQualifier("TODO_QUALIFIER_TYPE");
          }
        }
        assocLeft2RightSymbol.setBidirectional(cdAssoc.isBidirectional() || cdAssoc.isSimple());
      }
    }
  }

  private CDAssociationSymbol createAssociationSymbol(ASTCDAssociation astAssoc, ASTQualifiedName
      astSourceName, ASTQualifiedName astTargetName) {
    CDTypeSymbolReference sourceType = new CDTypeSymbolReference(Names.getQualifiedName(
        astSourceName.getParts()), currentScope().get());

    CDTypeSymbolReference targetType = new CDTypeSymbolReference(Names.getQualifiedName(
        astTargetName.getParts()), currentScope().get());

    CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);

    associationSymbol.setAssocName(astAssoc.getName().orNull());

    addStereotypes(associationSymbol, astAssoc.getStereotype().orNull());

    if ((astSourceName.getParts().size() > 1 && !sourceType.getName().equals(NameHelper.dotSeparatedStringFromList(astSourceName.getParts())))) {
      Log.error("0xU0270 Association referenced type " + astSourceName + " wasn't declared in the "
          + "class diagram " + fullClassDiagramName + ". Pos: " + astAssoc.get_SourcePositionStart());
      return null;
    }

    defineInScope(associationSymbol);

    return associationSymbol;
  }

  private void addStereotypes(CDAssociationSymbol associationSymbol, ASTStereotype astStereotype) {
    if (astStereotype != null) {
      // TODO PN<-RH values fehlen (Bug muss SO beheben, habe ihm ne Mail geschrieben)
      for (ASTStereoValue val : astStereotype.getValues()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }
}
