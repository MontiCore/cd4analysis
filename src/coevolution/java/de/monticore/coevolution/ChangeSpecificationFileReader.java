package de.monticore.coevolution;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ChangeSpecificationFileReader {
  private final String fileToBeRead;

  public ChangeSpecificationFileReader(String file){
    this.fileToBeRead = file;
  }

  public Map<String,String> fileReader() {
    Map<String,String> map = new HashMap<>();
    File file  = new File(fileToBeRead);

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String tempString;
      while( (tempString = reader.readLine()) !=null){
        String[] strArray = tempString.split(":");
        map.put(strArray[0], strArray[1]);
      }
      reader.close();
    }catch (IOException e){
      e.printStackTrace();
    }
    return map;
  }
}
