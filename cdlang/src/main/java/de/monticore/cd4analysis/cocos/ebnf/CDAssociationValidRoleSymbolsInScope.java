/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd4analysis.cocos.ebnf;

import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This CoCo checks for the usage of identical role names across multiple
 * associations for a given reference type. The presence of duplicate role
 * names can lead to ambiguity when accessing the referenced elements,
 * and results in incomplete storage of CDRoleSymbols within the
 * scope and symbol table.
 * <p>
 * This CoCo only considers the roles of an isolated reference type, as this
 * serves as the fundamental basis. More in-depth CoCos use the symbol table
 * and are faulty if it is faulty.
 * <p>
 * Example:
 * <pre>
 * class Person {
 * }
 * association knows [1] Person -> Person [*];
 * </pre>
 * Since no explicit role names were provided, the name of the reference type (in lowercase)
 * is used as the role name. Since this is identical in both cases, only one of the two
 * CDRoleSymbols is stored in the scope of the Person type.
 */
public class CDAssociationValidRoleSymbolsInScope implements CDBasisASTCDDefinitionCoCo {
  
  @Override
  public void check(ASTCDDefinition definition) {
    // maps class to all contained role names
    Map<String, List<String>> knownRoleNames = new HashMap<>();
    for (ASTCDAssociation assoc : definition.getCDAssociationsList()) {
      // left side
      CDRoleSymbol rightRoleSymbol = assoc.getRight().getSymbol();
      String leftTypeFQN = assoc.getLeftQualifiedName().getQName();
      knownRoleNames.putIfAbsent(leftTypeFQN, new ArrayList<>());
      
      if (knownRoleNames.get(leftTypeFQN).contains(rightRoleSymbol.getName())) {
        Log.error("0xCDCE4: Duplicate role (" + rightRoleSymbol.getName() + ") in reference Type "
                + assoc.getLeft().getMCQualifiedType().printType(),
            rightRoleSymbol.getSourcePosition());
      }
      else {
        knownRoleNames.get(leftTypeFQN).add(rightRoleSymbol.getName());
      }
      
      // right side
      CDRoleSymbol leftRoleSymbol = assoc.getLeft().getSymbol();
      String rightTypeFQN = assoc.getRightQualifiedName().getQName();
      knownRoleNames.putIfAbsent(rightTypeFQN, new ArrayList<>());
      
      if (knownRoleNames.get(rightTypeFQN).contains(leftRoleSymbol.getName())) {
        Log.error("0xCDCE4: Duplicate role (" + leftRoleSymbol.getName() + ") in reference Type "
                + assoc.getRight().getMCQualifiedType().printType(),
            leftRoleSymbol.getSourcePosition());
      }
      else {
        knownRoleNames.get(rightTypeFQN).add(leftRoleSymbol.getName());
      }
    }
  }
}
