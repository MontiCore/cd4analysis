package de.monticore.cddiff.syndiff.imp;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import edu.mit.csail.sdg.alloy4.Pair;

import java.util.Optional;

public class CDHelper {
  public static Pair<ASTCDClass, ASTCDClass> getConnectedClasses(ASTCDAssociation association, ASTCDCompilationUnit compilationUnit) {
    Optional<CDTypeSymbol> astcdClass =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getLeftQualifiedName().getQName());
    Optional<CDTypeSymbol> astcdClass1 =
        compilationUnit
            .getEnclosingScope()
            .resolveCDTypeDown(association.getRightQualifiedName().getQName());
    return new Pair<ASTCDClass, ASTCDClass>(
        (ASTCDClass) astcdClass.get().getAstNode(), (ASTCDClass) astcdClass1.get().getAstNode());
  }
}
