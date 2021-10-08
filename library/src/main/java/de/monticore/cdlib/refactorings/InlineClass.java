/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.delete.tf.Delete1to1Association;
import de.se_rwth.commons.logging.Log;

/**
 * Inline class: For two classes with one-to-one association move methods from a
 * class, which should be removed, to another class and delete the first class
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class InlineClass implements Refactoring {
	public InlineClass() {
	}

	/**
	 * Moves methods from a class, which should be removed, to another class and deletes the first class.
	 * Only possible if there is an one-to-one association between those classes.
	 *
	 * @param classToRemove - the class to be removed
	 * @param newClass - the new class
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean inlineClass(String classToRemove, String newClass, ASTCDCompilationUnit ast) {
		// delete 1:1 association between classToRemove and newClass
		Delete1to1Association deleteAssociation = new Delete1to1Association(ast);
		deleteAssociation.set_$newClassName(newClass);
		deleteAssociation.set_$oldClassName(classToRemove);
		if (deleteAssociation.doPatternMatching()) {
			deleteAssociation.doReplacement();
		} else {
		  Log.info("0xF4101: One-to-One-Association between " + newClass + " and " + classToRemove + "is assumed.", InlineClass.class.getName());
			return false;
		}

		// Move all methods and attributes from the removedClass to the newClass
		Move move = new Move();
		move.moveMethodsAndAttributes(classToRemove, newClass, ast);

		// Change referenceName in all associations from removedClass to
		// newClass
		transformationUtility.changeRefNameInAllAssociations(classToRemove, newClass, ast);

		Remove removeClass = new Remove();
		// remove classToRemove
		if (removeClass.removeClass(classToRemove, ast)) {
			return true;
		} else {
		  Log.info("0xF4102: Inline Class: Could not remove Class " + classToRemove, InlineClass.class.getName());
		}
		return false;
	}

}
