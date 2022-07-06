package de.monticore.ow2cw.expander;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;

import java.util.Optional;

public class VariableExpander extends BasicExpander{

  public static final String VAR_TAG = "complete";
  /**
   * @param cd Used for checking if adding/etc is allowed
   */
  public VariableExpander(ASTCDCompilationUnit cd) {
    super(cd);
  }

  @Override
  public Optional<ASTCDClass> addDummyClass(String dummyName) {
    if (getCD().getCDDefinition().getModifier().isPresentStereotype() && getCD().getCDDefinition().getModifier().getStereotype().contains(VAR_TAG)) {
      return Optional.empty();
    }
    return super.addDummyClass(dummyName);
  }

  @Override
  public void addAssociation(ASTCDAssociation assoc) {
    ICD4CodeArtifactScope artifactScope =
        CD4CodeMill.scopesGenitorDelegator().createFromAST(getCD());
    if (assoc.getCDAssocDir().isDefinitiveNavigableRight()){
      Optional<CDTypeSymbol> symbol =
          artifactScope.resolveCDTypeDown(assoc.getRightQualifiedName().getQName());
      if (symbol.isPresent() && symbol.get().getAstNode().getModifier().isPresentStereotype() && symbol.get().getAstNode().getModifier().getStereotype().contains(VAR_TAG)){
        return;
      }
    }
    if (assoc.getCDAssocDir().isDefinitiveNavigableLeft()){
      Optional<CDTypeSymbol> symbol =
          artifactScope.resolveCDTypeDown(assoc.getLeftQualifiedName().getQName());
      if (symbol.isPresent() && symbol.get().getAstNode().getModifier().isPresentStereotype() && symbol.get().getAstNode().getModifier().getStereotype().contains(VAR_TAG)){
        return;
      }
    }
    super.addAssociation(assoc);
  }

}
