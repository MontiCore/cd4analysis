/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import java.util.List;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.factory.tf.AddDoCreateToFactory;
import de.monticore.cdlib.designpatterns.factory.tf.AddFactoryClass;
import de.se_rwth.commons.logging.Log;

/**
 * Introduce Factory Pattern
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */

public class FactoryPattern implements DesignPattern {

	public FactoryPattern() {
	}

	/**
	 * IntroduceFactoryPattern - Introduces a Factory for the class with the given name {@code className}.
	 *
	 * @param model input model to apply the transformation to
	 * @param className name of the class a factory should be introduced for
	 * @param subclasses A list of names of subclasses that should also be considered by the introduced factory.
	 * @return true if and only if the transformation was applied successfully
	 */
	public boolean introduceFactoryPattern(List<String> subclasses, String className, ASTCDCompilationUnit model) {

		// Create factory class
		AddFactoryClass factoryClass = new AddFactoryClass(model);
		factoryClass.set_$abstractProduct(className);
		if (factoryClass.doPatternMatching()) {
			factoryClass.doReplacement();
		} else {
		  Log.info("0xF4031: Could not create Factory", FactoryPattern.class.getName());
			return false;
		}

		// add method toCreate for all subclasses to factory
		for (int i = 0; i < subclasses.size(); i++) {
			AddDoCreateToFactory addDoCreate = new AddDoCreateToFactory(model);
			addDoCreate.set_$factory(factoryClass.get_$factory());
			addDoCreate.set_$concreteProduct(subclasses.get(i));
			if (addDoCreate.doPatternMatching()) {
				addDoCreate.doReplacement();
			} else {
				Log.info("0xF4032: Could not create Method doCreate for class" + subclasses.get(i), FactoryPattern.class.getName());
				return false;
			}
		}
		return true;
	}

}
