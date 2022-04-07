/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;

import java.util.List;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.extractintermediateclass.additionalclasses.tf.ExtractIntermediateClassAdaptSubclassAttribute;
import de.monticore.cdlib.refactoring.extractintermediateclass.additionalclasses.tf.ExtractIntermediateClassAdaptSubclassMethod;
import de.monticore.cdlib.refactoring.extractintermediateclass.autoname.attribute.tf.*;
import de.monticore.cdlib.refactoring.extractintermediateclass.autoname.method.tf.*;
import de.monticore.cdlib.refactoring.extractintermediateclass.manualname.attribute.tf.*;
import de.monticore.cdlib.refactoring.extractintermediateclass.manualname.method.tf.*;
import de.se_rwth.commons.logging.Log;

/**
 * Extract superclass: Extracts additional superclass for classes with same
 * superclass and same attributes or methods
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class ExtractIntermediateClass implements Refactoring {
	public ExtractIntermediateClass() {
	}

	/**
	 * Extracts all super classes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractAllIntermediateClasses(ASTCDCompilationUnit ast) {
		PullUp pullUp = new PullUp();
		pullUp.pullUp(ast);

		if (extractIntermediateClass(ast)) {
			pullUp.pullUp(ast);
			while (extractIntermediateClass(ast)) {
				pullUp.pullUp(ast);
			}
			return true;
		}
		return false;
	}

	// Extracts additional superclass for classes with same superclass and same
	// attributes or methods
	// to avoid adding useless classes use pullUp before
	private boolean extractIntermediateClass(ASTCDCompilationUnit ast) {

		/* Extract Superclass from six classes (or more) */
		// for attributes
		ExtractIntermediateClass6ClassesAttribute extract6Superclasses = new ExtractIntermediateClass6ClassesAttribute(ast);
		if (extract6Superclasses.doPatternMatching()) {
			extract6Superclasses.doReplacement();

			// Adapt all additional Classes with this attribute
			ExtractIntermediateClassAdaptSubclassAttribute additionalSubclass = new ExtractIntermediateClassAdaptSubclassAttribute(
					ast);
			additionalSubclass.set_$A(extract6Superclasses.get_$A1());
			additionalSubclass.set_$parent(extract6Superclasses.get_$parent());
			additionalSubclass.set_$newParent(extract6Superclasses.get_$newParent());
			while (additionalSubclass.doPatternMatching()) {
				additionalSubclass = new ExtractIntermediateClassAdaptSubclassAttribute(ast);
				additionalSubclass.set_$A(extract6Superclasses.get_$A1());
				additionalSubclass.set_$parent(extract6Superclasses.get_$parent());
				additionalSubclass.set_$newParent(extract6Superclasses.get_$newParent());
			}

			return true;
		}

		// for methods
		ExtractIntermediateClass6ClassesMethod extract6SuperclassesMethod = new ExtractIntermediateClass6ClassesMethod(ast);
		if (extract6SuperclassesMethod.doPatternMatching()) {
			extract6SuperclassesMethod.doReplacement();

			// Adapt all additional classes with this method
			ExtractIntermediateClassAdaptSubclassMethod additionalSubclassMethod = new ExtractIntermediateClassAdaptSubclassMethod(
					ast);
			additionalSubclassMethod.set_$A(extract6SuperclassesMethod.get_$A1());
			additionalSubclassMethod.set_$parent(extract6SuperclassesMethod.get_$parent());
			additionalSubclassMethod.set_$newParent(extract6SuperclassesMethod.get_$newParent());
			while (additionalSubclassMethod.doPatternMatching()) {
				additionalSubclassMethod = new ExtractIntermediateClassAdaptSubclassMethod(ast);
				additionalSubclassMethod.set_$A(extract6SuperclassesMethod.get_$A1());
				additionalSubclassMethod.set_$parent(extract6SuperclassesMethod.get_$parent());
				additionalSubclassMethod.set_$newParent(extract6SuperclassesMethod.get_$newParent());
			}
			return true;
		}

		/* Extract Superclass from five classes */
		// for attributes
		ExtractIntermediateClass5ClassesAttribute extract5Superclasses = new ExtractIntermediateClass5ClassesAttribute(ast);
		if (extract5Superclasses.doPatternMatching()) {
			extract5Superclasses.doReplacement();
			return true;
		}

		// for methods
		ExtractIntermediateClass5ClassesMethod extract5SuperclassesMethods = new ExtractIntermediateClass5ClassesMethod(ast);
		if (extract5SuperclassesMethods.doPatternMatching()) {
			extract5SuperclassesMethods.doReplacement();
			return true;
		}

		/* Extract Superclass from four classes */
		// for attributes
		ExtractIntermediateClass4ClassesAttribute extract4Superclasses = new ExtractIntermediateClass4ClassesAttribute(ast);
		if (extract4Superclasses.doPatternMatching()) {
			extract4Superclasses.doReplacement();
			return true;
		}

		// for methods
		ExtractIntermediateClass4ClassesMethod extract4SuperclassesMethod = new ExtractIntermediateClass4ClassesMethod(ast);
		if (extract4SuperclassesMethod.doPatternMatching()) {
			extract4SuperclassesMethod.doReplacement();
			return true;
		}

		/* Extract Superclass from three classes */
		// for attributes
		ExtractIntermediateClass3ClassesAttribute extract3Superclasses = new ExtractIntermediateClass3ClassesAttribute(ast);
		if (extract3Superclasses.doPatternMatching()) {
			extract3Superclasses.doReplacement();
			return true;
		}
		// for methods
		ExtractIntermediateClass3ClassesMethod extract3SuperclassesMethod = new ExtractIntermediateClass3ClassesMethod(ast);
		if (extract3SuperclassesMethod.doPatternMatching()) {
			extract3SuperclassesMethod.doReplacement();
			return true;
		}

		/* Extract Superclass from two classes */
		// for attributes
		ExtractIntermediateClass2ClassesAttribute extract2Superclasses = new ExtractIntermediateClass2ClassesAttribute(ast);
		if (extract2Superclasses.doPatternMatching()) {
			extract2Superclasses.doReplacement();
			return true;
		}

		// for methods
		ExtractIntermediateClass2ClassesMethod extract2SuperclassesMethod = new ExtractIntermediateClass2ClassesMethod(ast);
		if (extract2SuperclassesMethod.doPatternMatching()) {
			extract2SuperclassesMethod.doReplacement();
			return true;
		}

		return false;

	}

	/**
	 * Extract all (up to 6) listed subclasses {@code subclasses}.
	 *
	 * @param newSuperclassName - new super class
	 * @param subclasses - list of subclasses
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractIntermediateClass(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {

		boolean success = false;

		switch (subclasses.size()) {
		case 2:
			success = extractIntermediateClasses(newSuperclassName, subclasses, ast);
			break;
		case 3:
			success = extractIntermediateClass3(newSuperclassName, subclasses, ast);
			break;
		case 4:
			success = extractIntermediateClass4(newSuperclassName, subclasses, ast);
			break;
		case 5:
			success = extractIntermediateClass5(newSuperclassName, subclasses, ast);
			break;
		case 6:
			success = extractIntermediateClass6(newSuperclassName, subclasses, ast);
			break;
		default:
		  Log.info("0xF4091: ExtractSuperclass is only applicable for up to six subclasses", ExtractIntermediateClass.class.getName());
			return false;
		}

		return success;

	}

	// Create new Superclass for six subclasses with same attribute or method
	private boolean extractIntermediateClass6(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
		/* Pull Up attributes from six classes */
		ExtractIntermediateClass6ClassesManualNameAttribute extractSuperclass = new ExtractIntermediateClass6ClassesManualNameAttribute(
				ast);
		extractSuperclass.set_$newParent(newSuperclassName);
		extractSuperclass.set_$subclass1(subclasses.get(0));
		extractSuperclass.set_$subclass2(subclasses.get(1));
		extractSuperclass.set_$subclass3(subclasses.get(2));
		extractSuperclass.set_$subclass4(subclasses.get(3));
		extractSuperclass.set_$subclass5(subclasses.get(4));
		extractSuperclass.set_$subclass6(subclasses.get(5));

		if (extractSuperclass.doPatternMatching()) {
			extractSuperclass.doReplacement();
			return true;
		} else {

			/* Pull Up methods from six classes */
			ExtractIntermediateClass6ClassesManualNameMethod extractSuperclassMethod = new ExtractIntermediateClass6ClassesManualNameMethod(
					ast);
			extractSuperclassMethod.set_$newParent(newSuperclassName);
			extractSuperclassMethod.set_$subclass1(subclasses.get(0));
			extractSuperclassMethod.set_$subclass2(subclasses.get(1));
			extractSuperclassMethod.set_$subclass3(subclasses.get(2));
			extractSuperclassMethod.set_$subclass4(subclasses.get(3));
			extractSuperclassMethod.set_$subclass5(subclasses.get(4));
			extractSuperclassMethod.set_$subclass6(subclasses.get(5));

			if (extractSuperclassMethod.doPatternMatching()) {
				extractSuperclassMethod.doReplacement();
				return true;
			}
		}
		return false;
	}

	// Create new Superclass for five subclasses with same attribute or method
	private boolean extractIntermediateClass5(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
		/* Pull Up attributes from five classes */
		ExtractIntermediateClass5ClassesManualNameAttribute extractSuperclass = new ExtractIntermediateClass5ClassesManualNameAttribute(
				ast);
		extractSuperclass.set_$newParent(newSuperclassName);
		extractSuperclass.set_$subclass1(subclasses.get(0));
		extractSuperclass.set_$subclass2(subclasses.get(1));
		extractSuperclass.set_$subclass3(subclasses.get(2));
		extractSuperclass.set_$subclass4(subclasses.get(3));
		extractSuperclass.set_$subclass5(subclasses.get(4));

		if (extractSuperclass.doPatternMatching()) {
			extractSuperclass.doReplacement();
			return true;
		} else {
			/* Pull Up methods from five classes */
			ExtractIntermediateClass5ClassesManualNameMethod extractSuperclassMethod = new ExtractIntermediateClass5ClassesManualNameMethod(
					ast);
			extractSuperclassMethod.set_$newParent(newSuperclassName);
			extractSuperclassMethod.set_$subclass1(subclasses.get(0));
			extractSuperclassMethod.set_$subclass2(subclasses.get(1));
			extractSuperclassMethod.set_$subclass3(subclasses.get(2));
			extractSuperclassMethod.set_$subclass4(subclasses.get(3));
			extractSuperclassMethod.set_$subclass5(subclasses.get(4));

			if (extractSuperclassMethod.doPatternMatching()) {
				extractSuperclassMethod.doReplacement();
				return true;
			}
		}
		return false;
	}

	// Create new superclass for four subclasses with same attributes or methods
	private boolean extractIntermediateClass4(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
		/* Pull Up attributes from four classes */
		ExtractIntermediateClass4ClassesManualNameAttribute extractSuperclass = new ExtractIntermediateClass4ClassesManualNameAttribute(
				ast);
		extractSuperclass.set_$newParent(newSuperclassName);
		extractSuperclass.set_$subclass1(subclasses.get(0));
		extractSuperclass.set_$subclass2(subclasses.get(1));
		extractSuperclass.set_$subclass3(subclasses.get(2));
		extractSuperclass.set_$subclass4(subclasses.get(3));
		if (extractSuperclass.doPatternMatching()) {
			extractSuperclass.doReplacement();
			return true;
		} else {
			/* Pull Up methods from four classes */
			ExtractIntermediateClass4ClassesManualNameMethod extractSuperclassMethod = new ExtractIntermediateClass4ClassesManualNameMethod(
					ast);
			extractSuperclassMethod.set_$newParent(newSuperclassName);
			extractSuperclassMethod.set_$subclass1(subclasses.get(0));
			extractSuperclassMethod.set_$subclass2(subclasses.get(1));
			extractSuperclassMethod.set_$subclass3(subclasses.get(2));
			extractSuperclassMethod.set_$subclass4(subclasses.get(3));
			if (extractSuperclassMethod.doPatternMatching()) {
				extractSuperclassMethod.doReplacement();
				return true;
			}
		}
		return false;
	}

	// Create new superclass for three subclasses with same attributes or
	// methods
	private boolean extractIntermediateClass3(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
		/* Pull Up attributes from three classes */
		ExtractIntermediateClass3ClassesManualNameAttribute extract3Superclasses = new ExtractIntermediateClass3ClassesManualNameAttribute(
				ast);
		extract3Superclasses.set_$newParent(newSuperclassName);
		extract3Superclasses.set_$subclass1(subclasses.get(0));
		extract3Superclasses.set_$subclass2(subclasses.get(1));
		extract3Superclasses.set_$subclass3(subclasses.get(2));

		if (extract3Superclasses.doPatternMatching()) {
			extract3Superclasses.doReplacement();
			return true;
		} else {
			/* Pull Up methods from three classes */
			ExtractIntermediateClass3ClassesManualNameMethod extract3SuperclassesMethod = new ExtractIntermediateClass3ClassesManualNameMethod(
					ast);
			extract3SuperclassesMethod.set_$newParent(newSuperclassName);
			extract3SuperclassesMethod.set_$subclass1(subclasses.get(0));
			extract3SuperclassesMethod.set_$subclass2(subclasses.get(1));
			extract3SuperclassesMethod.set_$subclass3(subclasses.get(2));

			if (extract3SuperclassesMethod.doPatternMatching()) {
				extract3SuperclassesMethod.doReplacement();
				return true;
			}
		}
		return false;
	}

	// Create new superclass for two subclasses with same attributes or methods
	private boolean extractIntermediateClasses(String newSuperclassName, List<String> subclasses, ASTCDCompilationUnit ast) {
		/* Pull Up attribtues from two classes */
		ExtractIntermediateClass2ClassesManualNameAttribute extract2Superclasses = new ExtractIntermediateClass2ClassesManualNameAttribute(
				ast);
		extract2Superclasses.set_$newParent(newSuperclassName);
		extract2Superclasses.set_$subclass1(subclasses.get(0));
		extract2Superclasses.set_$subclass2(subclasses.get(1));

		if (extract2Superclasses.doPatternMatching()) {
			extract2Superclasses.doReplacement();
			return true;
		} else {
			/* Pull Up methods from two classes */
			ExtractIntermediateClass2ClassesManualNameMethod extract2SuperclassesMethod = new ExtractIntermediateClass2ClassesManualNameMethod(
					ast);
			extract2SuperclassesMethod.set_$newParent(newSuperclassName);
			extract2SuperclassesMethod.set_$subclass1(subclasses.get(0));
			extract2SuperclassesMethod.set_$subclass2(subclasses.get(1));

			if (extract2SuperclassesMethod.doPatternMatching()) {
				extract2SuperclassesMethod.doReplacement();
				return true;
			}
		}
		return false;
	}

	private boolean extractIntermediateClasses(String newSuperclassName,  ASTCDCompilationUnit ast) {
		/* Pull Up attribtues from two classes */
		ExtractIntermediateClass2ClassesManualNameAttribute extract2Superclasses = new ExtractIntermediateClass2ClassesManualNameAttribute(
				ast);
		extract2Superclasses.set_$newParent(newSuperclassName);

		if (extract2Superclasses.doPatternMatching()) {
			extract2Superclasses.doReplacement();
			return true;
		} else {
			/* Pull Up methods from two classes */
			ExtractIntermediateClass2ClassesManualNameMethod extract2SuperclassesMethod = new ExtractIntermediateClass2ClassesManualNameMethod(
					ast);
			extract2SuperclassesMethod.set_$newParent(newSuperclassName);

			if (extract2SuperclassesMethod.doPatternMatching()) {
				extract2SuperclassesMethod.doReplacement();
				return true;
			}
		}
		return false;
	}
}
