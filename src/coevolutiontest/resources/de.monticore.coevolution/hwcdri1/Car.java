/* generated by template cd2java.Class*/

/* (c) https://github.com/MontiCore/monticore */

/* Hookpoint: ClassContent:addComment */

/* generated by template cd2java.Package*/
package de.monticore;


/* generated by template cd2java.Imports*/

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.SourcePosition;



/* Hookpoint: ClassContent:Imports */

/* generated by template cd2java.Annotations*/
// empty template: no Annotation


/* Hookpoint: ClassContent:Annotations */

 class Car  {

/* Hookpoint: ClassContent:Elements */

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 String licensePlate

;

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 int productionYear

;

    /* generated by template cd2java.Attribute*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: AttributeContent:Annotations */
 long kilometers

;

    /* generated by template cd2java.Method*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: MethodContent:Annotations */
 public  boolean equals (Car car)

 {
    /* generated by template cd2java.EmptyBody*/
// empty body

}

    /* generated by template cd2java.Method*/
/* generated by template cd2java.Annotations*/
// empty template: no Annotation

/* Hookpoint: MethodContent:Annotations */
 public  long getMeters (long kmeters)

 {
    /* generated by template cd2java.EmptyBody*/
// empty body
   //hwc
   return kmeters*1000;
}

//hwc

   public void setKilometers(long kilometers) {
     this.kilometers = kilometers;
   }

   public long getKilometers() {
     return kilometers;
   }


   public void setProductionYear(int productionYear){ this.productionYear = productionYear; }


   public int getProductionYear(){ return productionYear;}




}

