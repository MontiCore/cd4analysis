package de.monticore.cddiff.syndiff.interfaces;

import de.monticore.ast.ASTNode;
import de.monticore.cddiff.syndiff.imp.Actions;
import de.monticore.cddiff.syndiff.imp.DiffTypes;
import java.util.Optional;

/** TODO: Write Comments */
public interface ICDNodeDiff<SrcType extends ASTNode, TgtType extends ASTNode> {
  boolean isPresent();

  Optional<DiffTypes> getDiff();

  Optional<Actions> getAction();

  Optional<SrcType> getSrcValue();

  Optional<TgtType> getTgtValue();
}
