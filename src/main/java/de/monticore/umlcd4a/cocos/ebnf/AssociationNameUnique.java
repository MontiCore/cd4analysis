package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that association names are unique in the diagram.
 *
 * @author Robert Heim
 */
public class AssociationNameUnique implements CD4AnalysisASTCDAssociationCoCo {
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.getName().isPresent()) {
      try {
        a.getEnclosingScope().get().resolve(a.getName().get(), CDAssociationSymbol.KIND);
      }
      catch (ResolvedSeveralEntriesException e) {
        // TODO currently there exist two symbols for bidirectional
        // associations, see #1627
        boolean isValid = (a.isBidirectional() || a.isUnspecified()) && e.getSymbols().size() == 2;
        if (!isValid) {
          Log.error(
              String.format("0xC4A26 Association %s is defined multiple times.", a.getName().get()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
}
