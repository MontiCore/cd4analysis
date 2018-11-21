/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.umlcd4a.cocos.others;

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAssociation;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDAssociationCoCo;
import de.monticore.umlcd4a.cd4analysis._cocos.CD4AnalysisASTCDClassCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that association names start lower-case.
 *
 * @author Michael von Wenckstern
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
        if (!val.get().getValueOpt().isPresent()) {
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
