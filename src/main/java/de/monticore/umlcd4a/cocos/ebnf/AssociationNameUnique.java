package de.monticore.umlcd4a.cocos.ebnf;

import de.monticore.cocos.CoCoLog;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesException;
import de.monticore.umlcd4a._ast.ASTCDAssociation;
import de.monticore.umlcd4a._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.symboltable.CDAssociationSymbol;

/**
 * Checks that association names are unique in the diagram.
 *
 * @author Robert Heim
 */
public class AssociationNameUnique implements CD4AnalysisASTCDAssociationCoCo {
  
  public static final String ERROR_CODE = "0xC4A26";
  
  public static final String ERROR_MSG_FORMAT = "Association %s is defined multiple times.";
  
  @Override
  public void check(ASTCDAssociation a) {
    if (a.getName().isPresent()) {
      try {
        a.getEnclosingScope().get().resolve(a.getName().get(), CDAssociationSymbol.KIND);
      }
      catch (ResolvedSeveralEntriesException e) {
        // TODO currently there exist two symbols for bidirectional associations, see #1627
        boolean isValid = (a.isBidirectional() || a.isUnspecified()) && e.getSymbols().size() == 2;
        if (!isValid) {
          CoCoLog.error(ERROR_CODE,
              String.format(ERROR_MSG_FORMAT, a.getName().get()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
}
