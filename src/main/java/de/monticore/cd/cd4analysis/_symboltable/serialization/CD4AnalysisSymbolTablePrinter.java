/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cd4analysis._symboltable.serialization;

import de.monticore.cd.cd4analysis._symboltable.*;

import java.util.List;
import java.util.Optional;

/**
 * This class prints all non-primitive attributes of CD4A symbol classes. FOr each symbol loader,
 * only the name of the symbol is stored.
 */
public class CD4AnalysisSymbolTablePrinter extends CD4AnalysisSymbolTablePrinterTOP  {

  public static final String  CD_INTERFACES =  "cdInterfaces";
  public static final String  SUPER_CLASS =  "superClass";
  public static final String  TYPE =  "type";
  public static final String  RETURN_TYPE =  "returnType";
  public static final String  EXCEPTIONS =  "exceptions";


  public  CD4AnalysisSymbolTablePrinterTOP getRealThis ()  {
    return this;
  }

  @Override protected void serializeCDTypeCdInterfaces(List<CDTypeSymbolLoader> cdInterfaces) {
    printer.beginArray(CD_INTERFACES);
    for(CDTypeSymbolLoader loader : cdInterfaces){
      printer.value(loader.getName());
    }
    printer.endArray();
  }

  @Override protected void serializeCDTypeSuperClass(Optional<CDTypeSymbolLoader> superClass) {
    if(superClass.isPresent()){
      printer.member(SUPER_CLASS, superClass.get().getName());
    }
  }

  @Override protected void serializeCDFieldType(CDTypeSymbolLoader type) {
    printer.member(TYPE, type.getName());
  }

  @Override protected void serializeCDMethOrConstrReturnType(CDTypeSymbolLoader returnType) {
    printer.member(RETURN_TYPE, returnType.getName());
  }

  @Override protected void serializeCDMethOrConstrExceptions(List<CDTypeSymbolLoader> exceptions) {
    printer.beginArray(EXCEPTIONS);
    for(CDTypeSymbolLoader loader : exceptions){
      printer.value(loader.getName());
    }
    printer.endArray();
  }
}
