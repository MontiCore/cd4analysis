/* generated by template cd2java.Class*/

/* (c) https://github.com/MontiCore/monticore */

/* Hookpoint: ClassContent:addComment */

/* generated by template cd2java.Package*/
package de.monticore;


/* generated by template cd2java.Imports*/

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import de.monticore.Car;
import de.monticore.Car;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.SourcePosition;



/* Hookpoint: ClassContent:Imports */

/* generated by template cd2java.Annotations*/
// empty template: no Annotation


/* Hookpoint: ClassContent:Annotations */

 class Person  {

/* Hookpoint: ClassContent:Elements */

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 String fullName

;

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 String birthday

;

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 Set<de.monticore.Car> drives

;


   public boolean updateKilometers4Car(de.monticore.Car car, long newValue){
     if (drives.contains(car)){
       car.setKilometers(newValue);
       long kilometers = car.getKilometers();
       if (kilometers != newValue) {
         System.out.println(kilometers);
         assert false;
       }
       return true;
     } else {
       return false;
     }
   }

   public boolean updateKilometers4Truck(de.monticore.Truck truck, long newValue){
     if (drives.contains(truck)){
       truck.setKilometers(newValue);
       long kilometers = truck.getKilometers();
       if (kilometers != newValue) {
         System.out.println(kilometers);
         assert false;
       }
       return true;
     } else {
       return false;
     }
   }

   public boolean updateProductionYear4Car(de.monticore.Car car, int newValue){
     if (drives.contains(car)){
       car.setProductionYear(newValue);
       int productionYear = car.getProductionYear();
       if (productionYear != newValue) {
         System.out.println(productionYear);
         assert false;
       }
       return true;
     } else {
       return false;
     }
   }

   public boolean updateProductionYear4Truck(de.monticore.Truck truck, int newValue){
     if (drives.contains(car)){
       car.setProductionYear(newValue);
       int productionYear = truck.getProductionYear();
       if (productionYear != newValue) {
         System.out.println(productionYear);
         assert false;
       }
       return true;
     } else {
       return false;
     }
   }

  public int getCarNumber(){
     Car car1 = new de.monticore.Car();
     Car car2 = new de.monticore.Car();
     Car car3 = new de.monticore.Car();

     drives.add(car1);
     drives.add(car2);
     drives.add(car3);

     return drives.size();
  }













}

