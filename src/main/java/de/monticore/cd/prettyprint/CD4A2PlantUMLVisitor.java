/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd.prettyprint;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.cd.cd4analysis._visitor.CD4AnalysisVisitor;

public class CD4A2PlantUMLVisitor implements CD4AnalysisVisitor {

    protected CD4A2PlantUMLVisitor realThis;
    protected IndentPrinter printer;
    protected Boolean showAtt, showAssoc, showRoles, showCard;
    protected int nodesep = -1;
    protected int ranksep = -1;
    protected Boolean ortho = false;
    protected Boolean shortenWords = false;

    public CD4A2PlantUMLVisitor(IndentPrinter printer) {
        this.showAtt = false;
        this.showAssoc = false;
        this.showRoles = false;
        this.showCard = true;
        this.printer=printer;
        realThis = this;
    }

    public CD4A2PlantUMLVisitor(IndentPrinter printer, Boolean showAtt, Boolean showAssoc,
                                Boolean showRoles, Boolean showCard) {
        this.showAtt = showAtt;
        this.showAssoc = showAssoc;
        this.showRoles = showRoles;
        this.showCard = showCard;
        this.printer=printer;
        realThis = this;
    }

    public CD4A2PlantUMLVisitor(IndentPrinter printer, Boolean showAtt, Boolean showAssoc,
                                Boolean showRoles, Boolean showCard, boolean ortho, boolean shortenWords, int nodesep, int ranksep) {
        this.showAtt = showAtt;
        this.showAssoc = showAssoc;
        this.showRoles = showRoles;
        this.showCard = showCard;
        this.printer=printer;
        this.nodesep = nodesep;
        this.ranksep = ranksep;
        this.ortho = ortho;
        this.shortenWords = shortenWords;
        realThis = this;
    }

    public IndentPrinter getPrinter() {
        return this.printer;
    }

    public String print2PlantUML(ASTCDCompilationUnit node) {
        getPrinter().clearBuffer();
        node.accept(getRealThis());
        return getPrinter().getContent();
    }

    @Override
    public CD4A2PlantUMLVisitor getRealThis() {
        return realThis;
    }

    @Override
    public void handle(ASTCDCompilationUnit node) {
        node.getCDDefinition().accept(getRealThis());
    }

    @Override
    public void handle(ASTCDDefinition node) {
        getPrinter().print("@startuml");
        if (ortho)
            getPrinter().print("\nskinparam linetype ortho");
        if (nodesep != -1)
            getPrinter().print("\nskinparam nodesep " + nodesep);
        if (ranksep != -1)
            getPrinter().print("\nskinparam ranksep " + ranksep);
        getPrinter().println();
        node.getCDInterfaceList().forEach(i -> i.accept(getRealThis()));
        node.getCDEnumList().forEach(e -> e.accept(getRealThis()));
        node.getCDClassList().forEach(c -> c.accept(getRealThis()));
        node.getCDAssociationList().forEach(a -> a.accept(getRealThis()));
        getPrinter().print("@enduml");
    }

    @Override
    public void handle(ASTCDEnumConstant node) {
        getPrinter().print(node.getName() + "\n");
    }

    @Override
    public void handle(ASTCDInterface node) {
        getPrinter().print("interface " + node.getName() + "\n");
    }

    @Override
    public void handle(ASTCDEnum node) {
        getPrinter().print("enum " + node.getName() + "\n");

        if (showAtt && !node.getCDEnumConstantList().isEmpty()) {
            getPrinter().print(" {\n");
            getPrinter().indent();
            node.getCDEnumConstantList().forEach(a -> a.accept(getRealThis()));
            getPrinter().unindent();
            getPrinter().print("}\n");
        } else {
            getPrinter().print("\n");
        }
    }

    @Override
    public void handle(ASTCDClass node) {
        getPrinter().print("class " + node.getName());

        if (node.isPresentSuperclass()) {
            getPrinter().print(" extends " + node.printSuperClass());
        }

        if (!node.getInterfaceList().isEmpty()) {
            getPrinter().print(" implements " + node.printInterfaces());
        }

        if (showAtt && node.getCDAttributeList().size() > 0) {
            getPrinter().print(" {\n");
            getPrinter().indent();
            node.getCDAttributeList().forEach(a -> a.accept(getRealThis()));
            getPrinter().unindent();
            getPrinter().print("}\n");
        } else {
            getPrinter().print("\n");
        }
    }

    @Override
    public void handle(ASTCDAttribute node) {
        if(node.isPresentModifier())
            node.getModifier().accept(getRealThis());
        getPrinter().print(node.printType() + " " + node.getName() + "\n");
    }

    @Override
    public void handle(ASTModifier node) {
        if(node.isPrivate())
            getPrinter().print("-");
        else if(node.isProtected())
            getPrinter().print("#");
        else if(node.isPublic())
            getPrinter().print("+");
    }

    @Override
    public void handle(ASTCDAssociation node) {
        getPrinter().print(node.getLeftReferenceName().toString() + " ");

        if((showRoles && node.isPresentLeftRole()) || (showCard && node.isPresentLeftCardinality())) {
            getPrinter().print("\"");
            if(showRoles && node.isPresentLeftRole())
                getPrinter().print("(" + s(node.getLeftRole().getName()) + ") ");
            if(node.isPresentLeftCardinality())
                node.getLeftCardinality().accept(getRealThis());
            getPrinter().print("\" ");
        }

        if(node.isLeftToRight()) {
            getPrinter().print("-->");
        } else if (node.isRightToLeft()) {
            getPrinter().print("<--");
        } else if (node.isBidirectional()){
            getPrinter().print("<-->");
        } else {
            getPrinter().print("--");
        }

        if((showRoles && node.isPresentRightRole()) || (showCard && node.isPresentRightCardinality())) {
            getPrinter().print(" \"");
            if(showRoles && node.isPresentRightRole())
                getPrinter().print("(" + s(node.getRightRole().getName()) + ") ");
            if(node.isPresentRightCardinality())
                node.getRightCardinality().accept(getRealThis());
            getPrinter().print("\"");
        }

        getPrinter().print(" " + node.getRightReferenceName().toString());

        if(showAssoc && node.isPresentName())
            getPrinter().print(" : " + s(node.getName()));

        getPrinter().print("\n");
    }

    @Override
    public void handle(ASTCardinality cardinality) {
        if(showCard) {
            if (cardinality.isMany()) {
                getPrinter().print("*");
            } else if (cardinality.isOne()) {
                getPrinter().print("1");
            } else if (cardinality.isOneToMany()) {
                getPrinter().print("1..*");
            } else if (cardinality.isOptional()) {
                getPrinter().print("0..1");
            }
        }
    }

    private String s(String s) {
        if (!shortenWords)
            return s;

        String uc = "";
        for (int i = 1; i < s.length(); i++) {
            char c = s.charAt(i);
            uc += Character.isUpperCase(c) ? c : "";
        }
        if (!uc.isEmpty())
            return s.charAt(0) + uc;

        if (s.length() < 7)
            return s;

        return s.substring(0, 5) + "~";
    }

}
