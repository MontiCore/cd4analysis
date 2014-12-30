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
import de.monticore.types._ast.ASTMCImportStatement;
import de.monticore.types._ast.ASTReferenceType;
import de.monticore.types._ast.ASTReferenceTypeList;
import de.monticore.types._ast.ASTSimpleReferenceType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

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
      for (ASTMCImportStatement imp : compilationUnit.getImportStatements()) {
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

  public void visit(ASTCDClass astClass) {
    CDTypeSymbol cdTypeSymbol = new CDTypeSymbol(packageName + "." + astClass.getName());

    if (astClass.getSuperclasses() != null) {
      CDTypeSymbolReference superClassSymbol = createCDTypeSymbolFromReference(astClass.getSuperclasses());
      cdTypeSymbol.setSuperClass(superClassSymbol);
    }

    if (astClass.getModifier() != null) {
      final ASTModifier astModifier = astClass.getModifier();
      cdTypeSymbol.setAbstract(astModifier.isAbstract());

      if (astModifier.getStereotype() != null) {
        for (ASTStereoValue stereoValue : astModifier.getStereotype().getValues()) {
          // TODO PN value and name are always the same. Is this ok?
          Stereotype stereotype = new Stereotype(stereoValue.getName(), stereoValue.getName());
          cdTypeSymbol.addStereotype(stereotype);
        }
      }
    }

    addInterfacesToType(cdTypeSymbol, astClass.getInterfaces());

    defineInScope(cdTypeSymbol);
    addScopeToStackAndSetEnclosingIfExists(cdTypeSymbol);
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

    addInterfacesToType(cdTypeSymbol, astInterface.getInterfaces());

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
        enumSymbol.addField(constantSymbol);
      }
    }

    addInterfacesToType(enumSymbol, astEnum.getInterfaces());

    defineInScope(enumSymbol);
    addScopeToStackAndSetEnclosingIfExists(enumSymbol);
  }

  public void endVisit(ASTCDEnum astEnum) {
    removeCurrentScope();
  }





}
