/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cddiff;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code.trafo.CD4CodeAfterParseTrafo;
import de.monticore.cd4code.trafo.CD4CodeDirectCompositionTrafo;
import de.monticore.cdassociation.CDAssociationMill;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import java.util.HashSet;
import java.util.Set;

public class CDFullNameTrafo {

  public void transform(ASTCDCompilationUnit cd) {
    //new CD4CodeAfterParseTrafo().transform(cd);
    new CD4CodeDirectCompositionTrafo().transform(cd);
    cd.getCDDefinition()
        .getCDAssociationsList()
        .forEach(
            assoc -> {
              assoc
                  .getLeft()
                  .setCDRole(
                      CDAssociationMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(assoc.getLeft()))
                          .build());
              assoc
                  .getRight()
                  .setCDRole(
                      CDAssociationMill.cDRoleBuilder()
                          .setName(CDDiffUtil.inferRole(assoc.getRight()))
                          .build());
            });
    ICD4CodeArtifactScope artifactScope = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    artifactScope.addImports(new ImportStatement("java.lang", true));
    CD4CodeSymbolTableCompleter c = new CD4CodeSymbolTableCompleter(cd);
    cd.accept(c.getTraverser());

    cd.getCDDefinition()
        .getCDAssociationsList()
        .forEach(assoc -> qualifyAssocReferenceTypes(assoc, artifactScope));
    cd.getCDDefinition()
        .getCDClassesList()
        .forEach(
            cdClass -> {
              qualifyInheritance(cdClass);
              cdClass
                  .getCDAttributeList()
                  .forEach(attribute -> qualifyAttributeType(attribute, artifactScope));
            });
    cd.getCDDefinition()
        .getCDInterfacesList()
        .forEach(
            cdInterface -> {
              qualifyInheritance(cdInterface);
              cdInterface
                  .getCDAttributeList()
                  .forEach(attribute -> qualifyAttributeType(attribute, artifactScope));
            });

    CD4CodeMill.globalScope().removeSubScope(artifactScope);
  }

  protected void qualifyInheritance(ASTCDClass cdClass) {
    if (cdClass.isPresentCDExtendUsage()) {
      Set<String> extendsList = new HashSet<>();
      for (ASTMCObjectType type : cdClass.getCDExtendUsage().getSuperclassList()) {
        assert type.getDefiningSymbol().isPresent();
        type.getDefiningSymbol().ifPresent(symbol -> extendsList.add(symbol.getFullName()));
      }
      cdClass.setCDExtendUsage(
          CDExtendUsageFacade.getInstance()
              .createCDExtendUsage(extendsList.toArray(new String[0])));
    }
    if (cdClass.isPresentCDInterfaceUsage()) {
      Set<String> implementsList = new HashSet<>();
      for (ASTMCObjectType type : cdClass.getCDInterfaceUsage().getInterfaceList()) {
        assert type.getDefiningSymbol().isPresent();
        type.getDefiningSymbol().ifPresent(symbol -> implementsList.add(symbol.getFullName()));
      }
      cdClass.setCDInterfaceUsage(
          CDInterfaceUsageFacade.getInstance()
              .createCDInterfaceUsage(implementsList.toArray(new String[0])));
    }
  }

  protected void qualifyInheritance(ASTCDInterface cdInterface) {
    if (cdInterface.isPresentCDExtendUsage()) {
      Set<String> extendsList = new HashSet<>();
      for (ASTMCObjectType type : cdInterface.getCDExtendUsage().getSuperclassList()) {
        assert type.getDefiningSymbol().isPresent();
        type.getDefiningSymbol().ifPresent(symbol -> extendsList.add(symbol.getFullName()));
      }
      cdInterface.setCDExtendUsage(
          CDExtendUsageFacade.getInstance()
              .createCDExtendUsage(extendsList.toArray(new String[0])));
    }
  }

  protected void qualifyAssocReferenceTypes(
      ASTCDAssociation association, ICD4CodeArtifactScope artifactScope) {
    ICDBasisScope currentScope = association.getEnclosingScope();
    qualifyAssocSideRefType(association.getLeft(), currentScope, artifactScope);
    qualifyAssocSideRefType(association.getRight(), currentScope, artifactScope);
  }

  protected void qualifyAssocSideRefType(
      ASTCDAssocSide side, ICDBasisScope currentScope, ICD4CodeArtifactScope artifactScope) {

    side.getMCQualifiedType()
        .getDefiningSymbol()
        .ifPresent(
            symbol ->
                side.setMCQualifiedType(
                    CD4CodeMill.mCQualifiedTypeBuilder()
                        .setMCQualifiedName(
                            MCQualifiedNameFacade.createQualifiedName(symbol.getFullName()))
                        .build()));
    /*
    Optional<CDTypeSymbol> optSymbol;
    while (currentScope != artifactScope){
      optSymbol = currentScope.resolveCDTypeDown(side.getMCQualifiedType().getMCQualifiedName()
      .getQName());
      if (optSymbol.isPresent()){
        break;
      }
      currentScope = currentScope.getEnclosingScope();
    }
    optSymbol = currentScope.resolveCDTypeDown(side.getMCQualifiedType().getMCQualifiedName()
    .getQName());
    optSymbol.ifPresent(cdTypeSymbol -> side.setMCQualifiedType(CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(cdTypeSymbol.getFullName()))
        .build()));

     */
  }

  protected void qualifyAttributeType(
      ASTCDAttribute attribute, ICD4CodeArtifactScope artifactScope) {

    attribute
        .getMCType()
        .getDefiningSymbol()
        .ifPresent(
            symbol ->
                attribute.setMCType(
                    CD4CodeMill.mCQualifiedTypeBuilder()
                        .setMCQualifiedName(
                            MCQualifiedNameFacade.createQualifiedName(symbol.getFullName()))
                        .build()));

    /*
    ICDBasisScope currentScope = attribute.getEnclosingScope();

    Optional<CDTypeSymbol> optSymbol;

    while (currentScope != artifactScope){
      optSymbol = currentScope.resolveCDTypeDown(attribute.printType());
      if (optSymbol.isPresent()){
        break;
      }
      currentScope = currentScope.getEnclosingScope();
    }
    optSymbol = currentScope.resolveCDTypeDown(attribute.printType());
    optSymbol.ifPresent(cdTypeSymbol -> attribute.setMCType(CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCQualifiedNameFacade.createQualifiedName(cdTypeSymbol.getFullName()))
        .build()));

     */
  }
}
