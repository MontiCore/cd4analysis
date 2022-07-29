package de.monticore.coevolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UpdatedClassWriter {
  String updatedClass;
  String filePath;
  String checkFileName;

  public UpdatedClassWriter(String newClass, String path, String fileName){
    this.updatedClass = newClass;
    this.filePath = path;
    this.checkFileName = fileName;
  }

  public void createUpdatedClassFile() throws IOException{
    //String filePath = "target/resources/cddifftest/de.monticore.coevolution";
    File dir = new File(filePath);
    if(!dir.exists()){
      dir.mkdirs();
    }
    File checkFile = new File(filePath + checkFileName);
    FileWriter writer = null;

    try{
      if(!checkFile.exists()){
        checkFile.createNewFile();
      }
      writer = new FileWriter(checkFile, false);
      writer.append(updatedClass);
      writer.flush();
    }catch (IOException e){
      e.printStackTrace();
    }finally {
      if(null != writer){ writer.close();}
    }
  }
}
