/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.codegen.creators;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.cd.codegen.decorators.data.DecoratorData;
import de.monticore.cd4code.CD4CodeTestBasis;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Tests {@link CopyCreator} */
class CopyCreatorTest extends CD4CodeTestBasis {
  
  @Test
  void testSequentialApplication() {
    // Given
    ASTCDCompilationUnit auctionCUnit = parse("cd/codegen/GenAuction.cd");
    ASTCDCompilationUnit automatonCUnit = parse("cd/codegen/Automaton.cd");
    
    DecoratorData decoratorData = new DecoratorData();
    CopyCreator creatingDecorator = new CopyCreator();
    creatingDecorator.init(decoratorData, Optional.empty());
    
    // When
    creatingDecorator.visit(auctionCUnit);
    ASTCDCompilationUnit decoratedAuction = decoratorData.getAsDecorated(auctionCUnit);
    creatingDecorator.visit(automatonCUnit);
    ASTCDCompilationUnit decoratedAutomaton = decoratorData.getAsDecorated(automatonCUnit);
    
    // Then
    assertAll(() -> assertEquals("GenAuction", decoratedAuction.getCDDefinition().getName()),
        () -> assertEquals("Automaton", decoratedAutomaton.getCDDefinition().getName()));
  }
  
}
