/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdlib.designPattern;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdlib.designpatterns.visitor.tf.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Introduce Visitor Pattern
 *
 * Created by
 *
 * @author hoelldobler, KE
 * @montitoolbox
 */
public class VisitorPattern implements DesignPattern {

	public VisitorPattern() {
	}

	// Generate name for Visitors
	/**
	 * Applies the visitor pattern to a given class {@code node}
	 *
	 * @param node - name of the class a visitor should be introduced for
	 * @param replacedMethods - list of replaced methods
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean introduceVisitorPattern(String node, List<String> replacedMethods, ASTCDCompilationUnit ast)
			throws IOException {
		List<String> visitors = new ArrayList<String>();
		for (int i = 0; i < replacedMethods.size(); i++) {
			visitors.add(de.se_rwth.commons.StringTransformations.capitalize(replacedMethods.get(i).concat("Visitor")));
		}
		return introduceVisitorPattern(node, replacedMethods, visitors, ast);
	}

	// Main method

	/**
	 * Applies the visitor pattern to a given class {@code node}.
	 *
	 * @param node - name of the class a visitor should be introduced for
	 * @param replacedMethods - list of replaced methods
	 * @param visitors - list of visitors
	 * @param ast - class diagram to be transformed
	 * @return true, if applied successfully
	 */
	public boolean introduceVisitorPattern(String node, List<String> replacedMethods, List<String> visitors,
			ASTCDCompilationUnit ast) throws IOException {

		if (checkNode(node, replacedMethods, ast)) {
			// Create NodeVisitors
			if (createVisitors(node, visitors, ast)) {
				// Adapt Nodes
				if (adaptNodes(node, replacedMethods, ast)) {
					// Adapt Visitors and add Methods
					if (adaptVisitor(node, visitors, ast)) {
						return true;
					}
				}
			}
		}
		return false;

	}

	// Creates the Visitor Classes
	private boolean createVisitors(String node, List<String> visitors, ASTCDCompilationUnit ast) {
		String visitorSuperclass = node + "Visitor";

		// Create Superclass Visitor
		if (!transformationUtility.createSimpleClass(visitorSuperclass, ast)) {
			return false;
		}

		// Create Subclasses of Visitor and the inheritance to the Visitor
		for (int i = 0; i < visitors.size(); i++) {
			if (!transformationUtility.createSimpleClass(visitors.get(i), ast)) {
				return false;
			}
			if (!transformationUtility.createInheritanceToClass(visitors.get(i), visitorSuperclass, ast)) {
				return true;
			}
		}
		return true;
	}

	// Creates the Visitor Classes
	private boolean checkNode(String node, List<String> replacedMethods, ASTCDCompilationUnit ast) {
		for (int i = 0; i < replacedMethods.size(); i++) {
			if (transformationUtility.getMethod(replacedMethods.get(i), node, ast) == null) {
				return false;
			}
		}
		return true;
	}

	// Adaptes the Visitor classes to the Visitor Pattern
	private boolean adaptVisitor(String node, List<String> visitors, ASTCDCompilationUnit ast) {
		// Adds Methods to Children of Nodes
		NodeVisitorsAddMethods visitor = new NodeVisitorsAddMethods(ast);
		visitor.set_$nodeName(node);
		visitor.set_$NodeVisitor(node + "Visitor");
		while (visitor.doPatternMatching()) {
			visitor.doReplacement();
			visitor = new NodeVisitorsAddMethods(ast);
			visitor.set_$nodeName(node);
			visitor.set_$NodeVisitor(node + "Visitor");
		}

		// Adds Methods to Node Class
		NodeVisitorParentAddMethods visitorParent = new NodeVisitorParentAddMethods(ast);
		visitorParent.set_$NodeVisitor(node + "Visitor");
		visitorParent.set_$nodeName(node);
		while (visitorParent.doPatternMatching()) {
			visitorParent.doReplacement();
			visitorParent = new NodeVisitorParentAddMethods(ast);
			visitorParent.set_$NodeVisitor(node + "Visitor");
			visitorParent.set_$nodeName(node);
		}
		return true;

	}

	// Adaptes the Node Classes to the Visitor Pattern
	private boolean adaptNodes(String node, List<String> replacedMethods, ASTCDCompilationUnit ast) {
		// Delete replacedMethods
		for (int i = 0; i < replacedMethods.size(); i++) {
			// Delete Methods in Subnodes of Node

			SubNodesDeleteMethods visitor = new SubNodesDeleteMethods(ast);
			visitor.set_$name(replacedMethods.get(i));
			visitor.set_$nodeName(node);
			while (visitor.doPatternMatching()) {
				visitor.doReplacement();
				visitor = new SubNodesDeleteMethods(ast);
				visitor.set_$nodeName(node);
			}
		}

		for (int i = 0; i < replacedMethods.size(); i++) {
			// Delete Methods in Node
			ParentNodeDeleteMethods visitorParent = new ParentNodeDeleteMethods(ast);
			visitorParent.set_$node(node);
			visitorParent.set_$name(replacedMethods.get(i));
			visitorParent.set_$node(node);
			while (visitorParent.doPatternMatching()) {
				visitorParent.doReplacement();
				visitorParent = new ParentNodeDeleteMethods(ast);
				visitorParent.set_$node(node);
			}
		}

		// add method +accept(NodeVisitor) in Subnodes
		String nodeVisitor = node + "Visitor";
		SubNodesAddMethod subNodeAdd = new SubNodesAddMethod(ast);
		subNodeAdd.set_$nodeName(node);
		subNodeAdd.set_$parameterName(de.se_rwth.commons.StringTransformations.uncapitalize(nodeVisitor));
		subNodeAdd.set_$parameterType(de.se_rwth.commons.StringTransformations.capitalize(nodeVisitor));

		while (subNodeAdd.doPatternMatching()) {
			subNodeAdd.doReplacement();
			subNodeAdd = new SubNodesAddMethod(ast);
			subNodeAdd.set_$parameterName(de.se_rwth.commons.StringTransformations.uncapitalize(nodeVisitor));
			subNodeAdd.set_$parameterType(de.se_rwth.commons.StringTransformations.capitalize(nodeVisitor));
			subNodeAdd.set_$nodeName(node);
		}

		// add method +accept(NodeVisitor) in parent node
		ParentNodeAddMethod parentNodeAdd = new ParentNodeAddMethod(ast);
		parentNodeAdd.set_$node(node);
		parentNodeAdd.set_$parameterName(de.se_rwth.commons.StringTransformations.uncapitalize(nodeVisitor));
		parentNodeAdd.set_$parameterType(de.se_rwth.commons.StringTransformations.capitalize(nodeVisitor));

		if (parentNodeAdd.doPatternMatching()) {
			parentNodeAdd.doReplacement();
		}

		return true;
	}

}
