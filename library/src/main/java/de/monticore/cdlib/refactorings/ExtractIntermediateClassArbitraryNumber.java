/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.refactorings;


import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.refactoring.extractintermediateclass.autoname.attribute.tf.ExtractIntermediateClassAttribute;
import de.monticore.cdlib.refactoring.extractintermediateclass.autoname.method.tf.ExtractIntermediateClassMethod;
import de.monticore.cdlib.refactoring.extractintermediateclass.manualname.attribute.tf.ExtractIntermediateClassManualNameAttribute;
import de.monticore.cdlib.refactoring.extractintermediateclass.manualname.method.tf.ExtractIntermediateClassManualNameMethod;

/**
 * Extract superclass: Extracts additional superclass for classes with same
 * superclass and same attributes or methods
 *
 * Created by
 *
 * @author Philipp Nolte
 * @montitoolbox
 */
public class ExtractIntermediateClassArbitraryNumber implements Refactoring {
	public ExtractIntermediateClassArbitraryNumber() {
	}

	/**
	 * Extracts all super classes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractAllIntermediateClassesAttribute(ASTCDCompilationUnit ast) {
		PullUp pullUp = new PullUp();
		pullUp.pullUp(ast);

		if (extractIntermediateClassAttribute(ast)) {
			pullUp.pullUp(ast);
			while (extractIntermediateClassAttribute(ast)) {
				pullUp.pullUp(ast);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts all super classes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractAllIntermediateClassesMethod(ASTCDCompilationUnit ast) {
		PullUp pullUp = new PullUp();
		pullUp.pullUp(ast);

		if (extractIntermediateClassMethod(ast)) {
			pullUp.pullUp(ast);
			while (extractIntermediateClassMethod(ast)) {
				pullUp.pullUp(ast);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts all super classes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractAllIntermediateClassesAttribute(ASTCDCompilationUnit ast, String className) {
		PullUp pullUp = new PullUp();
		pullUp.pullUp(ast);

		if (extractIntermediateClassManualNameAttribute(ast, className)) {
			pullUp.pullUp(ast);
			while (extractIntermediateClassManualNameAttribute(ast, className)) {
				pullUp.pullUp(ast);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts all super classes
	 *
	 * @param ast - the class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean extractAllIntermediateClassesMethod(ASTCDCompilationUnit ast, String className) {
		PullUp pullUp = new PullUp();
		pullUp.pullUp(ast);

		if (extractIntermediateClassManualNameMethod(ast, className)) {
			pullUp.pullUp(ast);
			while (extractIntermediateClassManualNameMethod(ast, className)) {
				pullUp.pullUp(ast);
			}
			return true;
		}
		return false;
	}

	/**
	 * Extracts additional superclass for classes with same superclass and same
	 * attributes to avoid adding useless classes use pullUp before
	 * 
	 * @param ast The ast to apply the transformation on
	 * @return <code>true</code> if the transformation was successful
	 */
	private boolean extractIntermediateClassAttribute(ASTCDCompilationUnit ast) {
		ExtractIntermediateClassAttribute extract = new ExtractIntermediateClassAttribute(ast);
		if(extract.doPatternMatching()){
			extract.doReplacement();
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts additional superclass for classes with same superclass and same
	 * methods to avoid adding useless classes use pullUp before
	 * 
	 * @param ast The ast to apply the transformation on
	 * @return <code>true</code> if the transformation was successful
	 */
	private boolean extractIntermediateClassMethod(ASTCDCompilationUnit ast) {
		ExtractIntermediateClassMethod extract = new ExtractIntermediateClassMethod(ast);
		if(extract.doPatternMatching()){
			extract.doReplacement();
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts additional superclass for classes with same superclass and same
	 * attributes to avoid adding useless classes use pullUp before
	 * 
	 * @param ast The ast to apply the transformation on
	 * @param name The new name of the newly created class
	 * @return <code>true</code> if the transformation was successful
	 */
	private boolean extractIntermediateClassManualNameAttribute(ASTCDCompilationUnit ast, String name) {
		ExtractIntermediateClassManualNameAttribute extract = new ExtractIntermediateClassManualNameAttribute(ast);
		extract.set_$newParent(name);
		if(extract.doPatternMatching()){
			extract.doReplacement();
			return true;
		}
		return false;
	}
	
	/**
	 * Extracts additional superclass for classes with same superclass and same
	 * methods to avoid adding useless classes use pullUp before
	 * 
	 * @param ast The ast to apply the transformation on
	 * @param name The new name of the newly created class
	 * @return <code>true</code> if the transformation was successful
	 */
	private boolean extractIntermediateClassManualNameMethod(ASTCDCompilationUnit ast, String name) {
		ExtractIntermediateClassManualNameMethod extract = new ExtractIntermediateClassManualNameMethod(ast);
		extract.set_$newParent(name);
		if(extract.doPatternMatching()){
			extract.doReplacement();
			return true;
		}
		return false;
	}

	
}
