/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import java.util.List;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.logging.Log;

/**
 * Extract interface: Create common interface for given classes
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */

public class ExtractInterface implements Refactoring {
	public ExtractInterface() {
	}

	/**
	 * Creates an interface and all subclasses {@code subclasses} will implement this interface
	 *
	 * @param interfaceName - name of the interface
	 * @param subclasses - list of all subclasses
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractInterface(String interfaceName, List<String> subclasses, ASTCDCompilationUnit ast) {
		if (transformationUtility.createInterface(interfaceName, ast)) {
			for (int i = 0; i < subclasses.size(); i++) {
				if (!transformationUtility.addInheritanceToInterface(subclasses.get(i), interfaceName, ast)) {
				  Log.info("0xF4081: Could not add Interface " + interfaceName + " to " + subclasses.get(i), ExtractInterface.class.getName());
					return false;
				}
			}
			return true;
		} else {
		  Log.info("0xF4082: extractInterface: Could not create Interface " + interfaceName, ExtractInterface.class.getName());
		}
		return false;
	}

}
