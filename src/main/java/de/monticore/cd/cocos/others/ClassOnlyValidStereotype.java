/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cd.cocos.others;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.cd.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that association names start lower-case.
 *
 */
public class ClassOnlyValidStereotype implements CD4AnalysisASTCDClassCoCo {

  public static final Set<String> validStereoTypes = new HashSet<>(Arrays.asList("Quantity"));
  public static final Set<String> validQuantities = new HashSet<>(Arrays.asList(
  "Any", // to support any quantity must be explicitly provided
  "Acceleration",
  "Angle", 
  "QuantityOfSubstance", 
  "AngularAcceleration", 
  "AngularVelocity", 
  "Area", 
  "CatalyticActivity", 
  "DataQuantity", 
  "DataRate", 
  "Dimensionless", 
  "Duration", 
  "DynamicViscosity", 
  "ElectricCapacitance", 
  "ElectricCharge", 
  "ElectricConductance", 
  "ElectricCurrent", 
  "ElectricInductance", 
  "ElectricPotential", 
  "ElectricResistance", 
  "Energy", 
  "Force", 
  "Frequency", 
  "Illuminance", 
  "KinematicViscosity", 
  "Length", 
  "LuminousFlux", 
  "LuminousIntensity", 
  "MagneticFlux", 
  "MagneticFluxDensity", 
  "Mass", 
  "MassFlowRate", 
  "Money", 
  "Power", 
  "Pressure", 
  "RadiationDoseAbsorbed", 
  "RadiationDoseEffective", 
  "RadioactiveActivity", 
  "SolidAngle", 
  "Temperature", 
  "Torque", 
  "Velocity", 
  "Volume", 
  "VolumetricDensity", 
  "VolumetricFlowRate"));

  @Override
  public void check(ASTCDClass c) {
    if (c.isPresentStereotype()) {
      Set<String> stereotypes = c.getStereotype().getValueList().stream().map(s -> s.getName()).collect(Collectors.toSet());
      if (!validStereoTypes.containsAll(stereotypes)) {
        Log.error(String.format("0xC4A40 The class `%s` contains an invalid stereotype. Allowed stereotypes are %s",
            c.getName(), stereotypes.stream().map(s -> "`" + s + "`").collect(Collectors.joining(", "))), c.get_SourcePositionStart());
      }

      if (stereotypes.contains("Quantity")) {
        Optional<ASTCDStereoValue> val = c.getStereotype().getValueList().stream().filter(s -> s.getName().equals("Quantity")).findAny();
        if (!val.get().isPresentValue()) {
          Log.error("0xC4A41 Quantity stereotype must have a valid quantity value.", c.get_SourcePositionStart());
        }
        else if (!validQuantities.contains(val.get().getValue())) {
          Log.error(String.format("0xC4A42 The quantity value `%s` is invalid. Allowed quantity values are %s",
              val.get().getValue(),
              validQuantities.stream().map(s -> "`" + s + "`").collect(Collectors.joining(", "))),
              c.get_SourcePositionStart());
        }
      }
    }
  }
}
