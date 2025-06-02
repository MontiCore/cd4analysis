/* (c) https://github.com/MontiCore/monticore */
package de.monticore.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.common.base.Stopwatch;
import de.monticore.cd._visitor.CDMemberVisitor;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.cdbasis._ast.ASTCDMember;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.LogStub;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

@Disabled("For manual performance checks between revisions")
public class GetCDMemberListBenchmark {
  
  @Test
  public void benchmarkGetCDMemberListSmall() {
    benchmarkGetCDMemberList(3);
  }
  
  @Test
  public void benchmarkGetCDMemberListMedium() {
    benchmarkGetCDMemberList(10);
  }
  
  @Test
  public void benchmarkGetCDMemberListLarge() {
    benchmarkGetCDMemberList(100);
  }
  
  public void benchmarkGetCDMemberList(int size) {
    ASTCDClass res = constructClass(size);
    
    Stopwatch s = Stopwatch.createStarted();
    int i = 0;
    while (s.elapsed(TimeUnit.SECONDS) < 10) {
      List<ASTCDMember> attrs = res.getCDMemberList(CDMemberVisitor.Options.ATTRIBUTES);
      assertEquals(size, attrs.size());
      i++;
    }
    
    long elapsed = s.elapsed(TimeUnit.MILLISECONDS);
    System.out.println("Did " + i + " iterations in " + elapsed + "ms, avg " + ((double) elapsed
        / i));
  }
  
  private static ASTCDClass constructClass(int size) {
    LogStub.init();
    CD4CodeMill.init();
    
    ASTCDClassBuilder builder = CD4CodeMill.cDClassBuilder().setModifier(CDModifier.PUBLIC.build())
        .setName("Foo");
    
    for (int i = 0; i < size; i++) {
      builder.addCDMember(CD4CodeMill.cDAttributeBuilder().setName("attr" + i).setModifier(
          CDModifier.PUBLIC.build()).setMCType(MCTypeFacade.getInstance().createBooleanType())
          .build());
      
      builder.addCDMember(CD4CodeMill.cDMethodBuilder().setName("method" + i).setModifier(
          CDModifier.PUBLIC.build()).setMCReturnType(CD4CodeMill.mCReturnTypeBuilder().setMCType(
              MCTypeFacade.getInstance().createBooleanType()).build()).build());
    }
    
    ASTCDClass res = builder.build();
    assertNotNull(res);
    return res;
  }
  
}
