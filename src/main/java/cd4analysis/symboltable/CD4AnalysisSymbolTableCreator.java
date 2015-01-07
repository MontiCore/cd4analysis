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
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.ScopeManipulationApi;
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

public class CD4AnalysisSymbolTableCreator extends SymbolTableCreator {

  private String packageName;

  public CD4AnalysisSymbolTableCreator(ResolverConfiguration resolverConfig, @Nullable ScopeManipulationApi enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public void visit(ASTCDCompilationUnit compilationUnit) {
    final String cdName = compilationUnit.getCDDefinition().getName();
    Log.info("Building Symboltable for CD: " + cdName,
        CD4AnalysisSymbolTableCreator.class.getSimpleName());

    packageName = dotSeparatedStringFromList(compilationUnit.getPackage());
    // use CD name as part of the package name
    packageName = packageName.isEmpty() ? cdName : packageName + "." + cdName;

    final List<ImportStatement> imports = new ArrayList<>();
    if (compilationUnit.getImportStatements() != null) {
      for (ASTImportStatement imp : compilationUnit.getImportStatements()) {
        imports.add(new ImportStatement(dotSeparatedStringFromList(imp.getImportList()), imp.isStar()));
      }
    }

    CompilationUnitScope scope = new CompilationUnitScope(Optional.absent(), packageName, imports);
    addToStackAndSetEnclosingIfExists(scope);
  }

  public void endVisit(ASTCDCompilationUnit ast) {
    removeCurrentScope();
  }

  public void visit(ASTCDDefinition cdDefinition) {
    // TODO PN needed?
  }

  public void endVisit(ASTCDDefinition cdDefinition) {
    for (ASTCDAssociation astAssociation : cdDefinition.getCDAssociations()) {
      visitAssociationSymbol(astAssociation);
    }
  }

  public void visit(ASTCDClass astClass) {
    CDTypeSymbol cdTypeSymbol = new CDTypeSymbol(packageName + "." + astClass.getName());

    if (astClass.getSuperclasses() != null) {
      CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(astClass.getSuperclasses());
      cdTypeSymbol.setSuperClass(superClassSymbol);
    }

    final ASTModifier astModifier = astClass.getModifier();

    setModifiersOfType(cdTypeSymbol, astModifier);

    addInterfacesToType(cdTypeSymbol, astClass.getInterfaces());

    defineInScope(cdTypeSymbol);
    addScopeToStackAndSetEnclosingIfExists(cdTypeSymbol);
  }

  private void setModifiersOfType(CDTypeSymbol cdTypeSymbol, ASTModifier astModifier) {
    if (astModifier != null) {
      cdTypeSymbol.setAbstract(astModifier.isAbstract());

      if (astModifier.isProtected()) {
        cdTypeSymbol.setProtected();
      }
      else if (astModifier.isPrivate()) {
        cdTypeSymbol.setPrivate();
      } else {
        // public is default
        cdTypeSymbol.setPublic();
      }

      if (astModifier.getStereotype() != null) {
        for (ASTStereoValue stereoValue : astModifier.getStereotype().getValues()) {
          // TODO PN value and name are always the same. Is this ok?
          Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          cdTypeSymbol.addStereotype(stereotype);
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

    CDFieldSymbol field = new CDFieldSymbol(astAttribute.getName(), typeReference);

    if (astAttribute.getModifier() != null) {
      final ASTModifier astModifier = astAttribute.getModifier();

      field.setDerived(astModifier.isDerived());

      if (astModifier.getStereotype() != null) {
        for (ASTStereoValue stereoValue : astModifier.getStereotype().getValues()) {
          // TODO PN value and name are always the same. Is this ok?
          Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          field.addStereotype(stereotype);
        }
      }
    }

    defineInScope(field);
  }

  public void endVisit(ASTCDClass astClass) {
    removeCurrentScope();
  }

  public void visit(ASTCDInterface astInterface) {
    CDTypeSymbol cdTypeSymbol = new CDTypeSymbol(packageName + "." + astInterface.getName());
    cdTypeSymbol.setInterface(true);
    // Interfaces are always abstract
    cdTypeSymbol.setAbstract(true);

    addInterfacesToType(cdTypeSymbol, astInterface.getInterfaces());
    setModifiersOfType(cdTypeSymbol, astInterface.getModifier());

    // Interfaces are always abstract
    cdTypeSymbol.setAbstract(true);


    defineInScope(cdTypeSymbol);
    addScopeToStackAndSetEnclosingIfExists(cdTypeSymbol);
  }

  private void addInterfacesToType(CDTypeSymbol cdTypeSymbol, ASTReferenceTypeList interfaces) {
    if (interfaces != null) {
      for (ASTReferenceType superInterface : interfaces) {
        CDTypeSymbolReference superInterfaceSymbol = createCDTypeSymbolFromReference
            (superInterface);
        cdTypeSymbol.addInterface(superInterfaceSymbol);
      }
    }
  }

  public void endVisit(ASTCDInterface astInterface) {
    removeCurrentScope();
  }

  public void visit(ASTCDEnum astEnum) {
    CDTypeSymbol enumSymbol = new CDTypeSymbol(packageName + "." + astEnum.getName());
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
    setModifiersOfType(enumSymbol, astEnum.getModifier());

    defineInScope(enumSymbol);
    addScopeToStackAndSetEnclosingIfExists(enumSymbol);
  }

  public void endVisit(ASTCDEnum astEnum) {
    removeCurrentScope();
  }


  private void visitAssociationSymbol(ASTCDAssociation cdAssoc) {
    if (cdAssoc.isLeftToRight() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      CDAssociationSymbol assocLeft2RightSymbol = createAssociationSymbol(cdAssoc, cdAssoc
              .getLeftReferenceName(),
          cdAssoc.getRightReferenceName());

      if (assocLeft2RightSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocLeft2RightSymbol.setRelationship(Relationship.COMPOSITE);
        }

        assocLeft2RightSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc
            .getRightCardinality()));
        assocLeft2RightSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc
            .getLeftCardinality()));
        assocLeft2RightSymbol.setRole(cdAssoc.getRightRole());
        if (cdAssoc.getRightModifier() != null) {
          addStereotypes(assocLeft2RightSymbol, cdAssoc.getRightModifier().getStereotype());
        }
        ASTCDQualifier qualifier = cdAssoc.getLeftQualifier();
        if (qualifier != null) {
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

    if (cdAssoc.isRightToLeft() || cdAssoc.isBidirectional() || cdAssoc.isSimple()) {
      CDAssociationSymbol assocRight2LeftSymbol = createAssociationSymbol(cdAssoc, cdAssoc
          .getRightReferenceName(), cdAssoc.getLeftReferenceName());
      // complete association properties
      if (assocRight2LeftSymbol != null) {
        if (cdAssoc.isComposition()) {
          assocRight2LeftSymbol.setRelationship(Relationship.PART);
        }
        assocRight2LeftSymbol.setTargetCardinality(Cardinality.convertCardinality(cdAssoc.getLeftCardinality()));
        assocRight2LeftSymbol.setSourceCardinality(Cardinality.convertCardinality(cdAssoc.getRightCardinality()));
        assocRight2LeftSymbol.setRole(cdAssoc.getLeftRole());
        if (cdAssoc.getLeftModifier() != null) {
          addStereotypes(assocRight2LeftSymbol, cdAssoc.getLeftModifier().getStereotype());
        }
        ASTCDQualifier qualifier = cdAssoc.getRightQualifier();
        if (qualifier != null) {
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

  private CDAssociationSymbol createAssociationSymbol(ASTCDAssociation cdAssoc, ASTQualifiedName
      sourceName, ASTQualifiedName targetName) {
    CDTypeSymbolReference sourceType = new CDTypeSymbolReference(Names.getQualifiedName(
        sourceName.getParts()), currentScope().get());

    CDTypeSymbolReference targetType = new CDTypeSymbolReference(Names.getQualifiedName(
        targetName.getParts()), currentScope().get());

    CDAssociationSymbol associationSymbol = new CDAssociationSymbol(sourceType, targetType);

    associationSymbol.setAssocName(cdAssoc.getName());

    addStereotypes(associationSymbol, cdAssoc.getStereotype());

    if ((sourceName.getParts().size() > 1 && !sourceType.getName().equals(NameHelper.dotSeparatedStringFromList(sourceName.getParts())))) {
      Log.error("0xU0270 Association referenced type " + sourceName + " wasn't declared in the "
          + "class diagram " + packageName + ". Pos: " + cdAssoc.get_SourcePositionStart());
      return null;
    }

    sourceType.addAssociation(associationSymbol);

    defineInScope(associationSymbol);

    return associationSymbol;
  }

  private void addStereotypes(CDAssociationSymbol associationSymbol, ASTStereotype astStereotype) {
    if (astStereotype != null) {
      for (ASTStereoValue val : astStereotype.getValues()) {
        associationSymbol.addStereotype(new Stereotype(val.getName(), val.getName()));
      }
    }
  }

}
