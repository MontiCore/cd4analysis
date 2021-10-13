/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.switchinheritancedelegation.tf.ReplaceDelegationByInheritance;
import de.monticore.cdlib.refactoring.switchinheritancedelegation.tf.ReplaceInheritanceByDelegation;

/**
 * Replace an association by an inheritance between the classes and the other
 * way around
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @author KE
 * @montitoolbox
 */

public class SwitchInheritanceDelegation implements Refactoring {
	public SwitchInheritanceDelegation() {
	}

	/**
	 * Replace an inheritance between two classes by an association
	 *
	 * @param superclassName - name of the super class
	 * @param subclassName - name of the sub class
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean replaceInheritanceByDelegation(String superclassName, String subclassName,
			ASTCDCompilationUnit ast) {

		ReplaceInheritanceByDelegation replace = new ReplaceInheritanceByDelegation(ast);
		replace.set_$subclassName(subclassName);
		replace.set_$superclass(superclassName);
		if (replace.doPatternMatching()) {
			replace.doReplacement();
			return true;
		}
		return false;
	}

	/**
	 * Replace an association by an inheritance between the classes
	 *
	 * @param superclassName - name of the super class
	 * @param subclassName - name of the sub class
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean replaceDelegationByInheritance(String superclassName, String subclassName,
			ASTCDCompilationUnit ast) {

		ReplaceDelegationByInheritance replace = new ReplaceDelegationByInheritance(ast);
		replace.set_$subclassName(subclassName);
		replace.set_$superclassName(superclassName);
		if (replace.doPatternMatching()) {
			replace.doReplacement();
			return true;
		}

		return false;
	}

}
