/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import de.monticore.cdlib.refactoring.pullup.association.tf.PullUpAssociation2ClassesBi;
import de.monticore.cdlib.refactoring.pullup.association.tf.PullUpAssociation2ClassesLeftToRight;
import de.monticore.cdlib.refactoring.pullup.association.tf.PullUpAssociation2ClassesRightToLeft;
import de.monticore.cdlib.refactoring.pullup.association.tf.PullUpAssociation2ClassesUni;
import de.monticore.cdlib.refactoring.pullup.attribute.tf.PullUpAttributes;
import de.monticore.cdlib.refactoring.pullup.attribute.tf.TurnPrivateAttributeToProtected;
import de.monticore.cdlib.refactoring.pullup.method.tf.PullUpMethods;
import de.monticore.cdlib.refactoring.pullup.method.tf.TurnPrivateMethodToProtected;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

/**
 * Pull up: Pull up methods and/or attributes from subclasses to a common
 * superclass
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */

public class PullUp implements Refactoring {
	public PullUp() {
	}

	/**
	 * Pull up all methods and attributes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean pullUp(ASTCDCompilationUnit ast) {
		boolean success = false;

		if (pullUpAttributes(ast)) {
			success = true;
		}
		if (pullUpMethods(ast)) {
			success = true;
		}

		if (pullUpAssociations(ast)) {
			success = true;
		}
		return success;
	}

	/**
	 * Pull up common attributes of all subclasses to the superclass
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean pullUpAttributes(ASTCDCompilationUnit ast) {

		// Repeat till all attributes, which can be pulled up are found
		if (pullUpOneAttribute(ast)) {
			while (pullUpOneAttribute(ast))
				;
			return true;
		}
		return false;
	}

	private boolean pullUpOneAttribute(ASTCDCompilationUnit ast) {

		/* Pull Up attributes from two classes */
		PullUpAttributes pullUpAttribute = new PullUpAttributes(ast);

		// PullUp all attributes
		if (pullUpAttribute.doPatternMatching()) {
			pullUpAttribute.doReplacement();

//			System.out.println(new CDPrettyPrinterConcreteVisitor(new IndentPrinter()).prettyprint(ast));

			// turns private attributes to protected for accessing the attribute
			// in subclass
			TurnPrivateAttributeToProtected turn = new TurnPrivateAttributeToProtected(ast);
			turn.set_$A1(pullUpAttribute.get_$A1());
			turn.doAll();

			return true;
		}
		return false;
	}

	/**
	 * Pull up common methods of all subclasses to the superclass
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean pullUpMethods(ASTCDCompilationUnit ast) {

		// Repeat till all methods, which can be pulled up are found
		if (pullUpOneMethod(ast)) {
			while (pullUpOneMethod(ast))
				;
			return true;
		}
		return false;
	}

	private boolean pullUpOneMethod(ASTCDCompilationUnit ast) {

		/* Pull Up methods from two classes */
		PullUpMethods pullUpMethod = new PullUpMethods(ast);

		// PullUp all methods
		if (pullUpMethod.doPatternMatching()) {
			pullUpMethod.doReplacement();

			// turns private attributes to protected for accessing the attribute
			// in subclass
			TurnPrivateMethodToProtected turn = new TurnPrivateMethodToProtected(ast);
			turn.set_$M1(pullUpMethod.get_$M1());
			turn.doAll();

			return true;
		}

		return false;
	}

	/**
	 * Pull up common associations of all subclasses to the superclass
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean pullUpAssociations(ASTCDCompilationUnit ast) {
		if (!(pullUpAssociationBi(ast) || pullUpAssociationUni(ast) || pullUpAssociationLeftToRight(ast)
				|| pullUpAssociationRightToLeft(ast))) {
			return false;
		}

		while (pullUpAssociationBi(ast) || pullUpAssociationUni(ast) || pullUpAssociationLeftToRight(ast)
				|| pullUpAssociationRightToLeft(ast)) {
			System.out.println("Pull up Association");
		}
		return true;
	}

	private boolean pullUpAssociationBi(ASTCDCompilationUnit ast) {
		/* Pull Up association from two classes with Bi-Direction */
		PullUpAssociation2ClassesBi pullUpAssociation2 = new PullUpAssociation2ClassesBi(ast);

		if (pullUpAssociation2.doPatternMatching()) {
			pullUpAssociation2.doReplacement();
			return true;
		}

		return false;
	}

	private boolean pullUpAssociationUni(ASTCDCompilationUnit ast) {
		/* Pull Up association from two classes with Uni-Direction */
		PullUpAssociation2ClassesUni pullUpAssociation2 = new PullUpAssociation2ClassesUni(ast);

		if (pullUpAssociation2.doPatternMatching()) {
			pullUpAssociation2.doReplacement();
			return true;
		}
		return false;
	}

	private boolean pullUpAssociationLeftToRight(ASTCDCompilationUnit ast) {
		/*
		 * Pull Up association from two classes with Direction from Left to
		 * Right
		 */
		PullUpAssociation2ClassesLeftToRight pullUpAssociation2 = new PullUpAssociation2ClassesLeftToRight(ast);

		if (pullUpAssociation2.doPatternMatching()) {
			pullUpAssociation2.doReplacement();
			return true;
		}

		return false;
	}

	private boolean pullUpAssociationRightToLeft(ASTCDCompilationUnit ast) {
		/*
		 * Pull Up association from two classes with Direction from Right to
		 * Left
		 */
		PullUpAssociation2ClassesRightToLeft pullUpAssociation2 = new PullUpAssociation2ClassesRightToLeft(ast);

		if (pullUpAssociation2.doPatternMatching()) {
			pullUpAssociation2.doReplacement();
			return true;
		}

		return false;
	}

}
