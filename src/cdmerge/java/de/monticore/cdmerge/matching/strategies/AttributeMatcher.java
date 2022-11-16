/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdmerge.matching.strategies;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdmerge.matching.matchresult.ASTMatchGraph;
import java.util.Map;

/** Determines if to attributes match */
public interface AttributeMatcher {

  boolean matchAttribute(ASTCDAttribute attribute1, ASTCDAttribute attribute2);

  Map<String, ASTMatchGraph<ASTCDAttribute, ASTCDClass>> findMatchingAttributes(
      ASTMatchGraph<ASTCDClass, ASTCDDefinition> matchedClasses);
}
