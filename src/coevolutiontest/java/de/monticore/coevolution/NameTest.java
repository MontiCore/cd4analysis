package de.monticore.coevolution;

import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NameTest {
  @Test
  public void test(){
    Log.init();

    String codeToBeUpdated = "src/coevolutiontest/resources/de.monticore"
        + ".coevolution/handwrittenCode.java";
    String fileToBeRead = "src/coevolutiontest/resources/de.monticore.coevolution/rules.txt";
    String filePath = "target/generated/co-evolution-test";
    String checkFileName = "/updatedNameHandwrittenCode.java";

    Map<String, String> rules;
    ChangeSpecificationFileReader changeSpecificationFileReader = new ChangeSpecificationFileReader(fileToBeRead);

    try {

      rules = changeSpecificationFileReader.fileReader();

      for (Map.Entry<String, String> rule : rules.entrySet()) {
        NameUpdater nameUpdater = new NameUpdater(rule.getKey(), rule.getValue());

        NameImpl<Object> nameImpl = new NameImpl<>(codeToBeUpdated, nameUpdater);

        String classContent = nameImpl.generateUpdates();

        Assert.assertTrue(classContent.contains("num"));

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
