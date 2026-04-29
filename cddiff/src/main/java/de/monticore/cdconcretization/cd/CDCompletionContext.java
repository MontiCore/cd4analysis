/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdconcretization.cd;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.cdconformance.CDConformanceContext;
import de.monticore.symboltable.IScope;
import java.util.Set;

/**
 * Provides useful information for the completion process. This includes access to the top level
 * concrete and reference CD, the mapping name and the matching strategies for the incarnation
 * mapping.
 */
public interface CDCompletionContext extends CDConformanceContext {
  
  boolean isForEachNameAdaptationEnabled();
  
  boolean isImplicitNameAdaptationEnabled();
  
  /**
   * Returns all incarnations of the given reference type in the scope of <b>this context</b>.
   *
   * @param referenceType the reference type for which incarnations are searched
   * @return all incarnations of the given reference type in this context
   * @see de.monticore.cdconformance.inc.CDIncarnationMapping#getIncarnations(IScope, ASTCDType)
   */
  Set<ASTCDType> getTypeIncarnations(ASTCDType referenceType);
  
  /**
   * Returns all incarnations of the given reference attribute in the scope of <b>this context</b>.
   *
   * @param referenceAttribute the reference attribute for which incarnations are searched
   * @return all incarnations of the given reference attribute in this context
   * @see de.monticore.cdconformance.inc.CDIncarnationMapping#getIncarnations(IScope,
   * ASTCDAttribute)
   */
  Set<ASTCDAttribute> getAttributeIncarnations(ASTCDAttribute referenceAttribute);
  
  /**
   * Returns all incarnations of the given reference method in the scope of <b>this context</b>.
   *
   * @param referenceMethod the reference method for which incarnations are searched
   * @return all incarnations of the given reference method in this context
   */
  Set<ASTCDMethod> getMethodIncarnations(ASTCDMethod referenceMethod);
  
}
