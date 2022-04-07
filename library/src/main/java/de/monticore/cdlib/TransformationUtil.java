/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib;

import com.google.common.base.Joiner;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDClass;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by
 *
 * @author KH
 */
public class TransformationUtil {

  public static void calculateAndSetName(ASTCDClass superclass,
                                         List<ASTCDClass> subs) {
    List<String> names = subs.stream().map(ASTCDClass::getName)
            .collect(Collectors.toList());
    String sClassName = Joiner.on("").join(names);
    superclass.setName(sClassName);
    for (ASTCDClass c : subs) {
      c.getSuperclassList().clear();
      c.getSuperclassList().add(
              CD4CodeMill.mCQualifiedTypeBuilder()
                      .setMCQualifiedName(
                              CD4CodeMill
                                      .mCQualifiedNameBuilder()
                                      .addAllParts(
                                              Collections.singletonList(sClassName))
                                      .build()).build()
                               );
    }
  }
  
  


}
