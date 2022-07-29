package de.monticore.coevolution;

import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class TypeTest {
  @Test
  public void test(){
    Log.init();

    String codeToBeUpdated = "src/coevolutiontest/resources/de.monticore.coevolution/handwrittenCode.java";
    String fileToBeRead = "src/coevolutiontest/resources/de.monticore.coevolution/typeChangeRules";
    String filePath = "target/generated/co-evolution-test";
    String checkFileName = "/updatedTypeHandwrittenCode.java";

    Map<String, String> rules;
    ChangeSpecificationFileReader changeSpecificationFileReader = new ChangeSpecificationFileReader(fileToBeRead);

    try {

      rules = changeSpecificationFileReader.fileReader();

      for (Map.Entry<String, String> rule : rules.entrySet()) {

        TypeUpdater typeUpdater = new TypeUpdater(rule.getKey().split(" ")[1], rule.getValue().split(" ")[0]);

        TypeImpl typeImpl = new TypeImpl(codeToBeUpdated, typeUpdater);

        String classContent = typeImpl.generateUpdates();

        Assert.assertTrue(classContent.contains("Integer.parseInt(number)"));

        UpdatedClassWriter updatedClassWriter = new UpdatedClassWriter(classContent.toString(), filePath, checkFileName);

        try{
          updatedClassWriter.createUpdatedClassFile();
        }catch (IOException e){
          e.printStackTrace();
        }
      }
    } catch (Exception e){
      e.printStackTrace();
      Assert.fail();
    }
  }



}
