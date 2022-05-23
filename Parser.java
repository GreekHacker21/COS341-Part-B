import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Parser {

    public Token root;
    public Token current;
    public SyntaxError error;
    public DocumentBuilderFactory docFactory;
    public DocumentBuilder docBuilder;
    public Document doc;
    public String fileName;
    public Node SyntaxTree;

    Parser(Token r, String fN) {
        fileName = fN;
        root = r;
        current = root;
        try {
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }

    }

    public void parse() {
        try {
            SPLProgr();
        } catch (SyntaxError e) {
            System.out.println("SYNTAX ERROR");
            System.out.println(e.getMessage());
            return;
        }

        /*
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult output = new StreamResult(new File(fileName + ".xml"));
            transformer.transform(source, output);
            System.out.println("File saved!");
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }*/
    }

    // SPLProgr → ProcDefs main { Algorithm halt ; VarDecl }
    public void SPLProgr() throws SyntaxError {
        Element element = doc.createElement("SPLProgr");
        doc.appendChild(element);
        SyntaxTree = new Node("SPLProgr");


        // ProcDefs check
        if (current.value.equals("proc")) {
            ProcDefs(element, SyntaxTree);
        }

        // main check
        if (!current.value.equals("main")) {
            throw new SyntaxError("Missing main as beginning keyword on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode("main"));
        SyntaxTree.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing { after main on line " + current.line + ".");
        }

        // { check
        if (!current.value.equals("{")) {
            throw new SyntaxError("Missing { after main on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        SyntaxTree.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing the keyword halt on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element, SyntaxTree);
                break;
            case "do":
                Algorithm(element, SyntaxTree);
                break;
            case "while":
                Algorithm(element, SyntaxTree);
                break;
            case "call":
                Algorithm(element, SyntaxTree);
                break;
            case "output":
                Algorithm(element, SyntaxTree);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element, SyntaxTree);
                }
        }

        // halt check
        if (!current.value.equals("halt")) {
            throw new SyntaxError("Missing halt after main on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        SyntaxTree.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing ; after halt on line " + current.line + ".");
        }

        // ; check
        if (!current.value.equals(";")) {
            throw new SyntaxError("Missing ; after halt on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        SyntaxTree.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } after halt's ; on line " + current.line + ".");
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(element, SyntaxTree);
                break;
            case "num":
                VarDecl(element, SyntaxTree);
                break;
            case "bool":
                VarDecl(element, SyntaxTree);
                break;
            case "string":
                VarDecl(element, SyntaxTree);
                break;
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } after halt's ; on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        SyntaxTree.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            throw new SyntaxError("No more characters allowed after main definition on line " + current.line + ".");
        }

    }

    // ProcDefs → // nothing (nullable)
    // ProcDefs → PD , ProcDefs
    public void ProcDefs(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("ProcDefs");
        connect.appendChild(element);
        Node c = new Node("ProcDefs");
        p.addChild(c);

        PD(element, c);

        if (!current.value.equals(",")) {
            throw new SyntaxError("missing a comma (,) after a proc definition on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("Missing main as beginning keyword on line " + current.line + ".");
        }

        // ProcDefs
        if (current.value.equals("proc")) {
            ProcDefs(connect, p);
        }

    }

    // PD → proc userDefinedName { ProcDefs Algorithm return ; VarDecl }
    public void PD(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("PD");
        connect.appendChild(element);
        Node c = new Node("PD");
        p.addChild(c);
        String userDefinedName = "";

        // proc check
        if (!current.value.equals("proc")) {
            throw new SyntaxError("missing a proc declaration on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("missing userDefinedName for proc on line " + current.line + ".");
        }

        // userDefinedName check
        if (!current.type.equals("userDefinedName")) {
            throw new SyntaxError("missing a userDefinedName on line " + current.line + ".");
        }
        //element.appendChild(doc.createTextNode(current.value));
        userDefinedName = current.value;
        addCustomText(element, current.type, current.value, c);
        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("missing a { after " + current.value + " on line " + current.line + ".");
        }

        // { check
        if (!current.value.equals("{")) {
            throw new SyntaxError("missing a { after " + current.value + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("missing a return after {  on line " + current.line + ".");
        }

        // ProcDefs check
        if (current.value.equals("proc")) {
            ProcDefs(element, c);
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element, c);
                break;
            case "do":
                Algorithm(element, c);
                break;
            case "while":
                Algorithm(element, c);
                break;
            case "call":
                Algorithm(element, c);
                break;
            case "output":
                Algorithm(element, c);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element, c);
                }
        }

        // return check
        if (!current.value.equals("return")) {
            throw new SyntaxError("Missing return for " + userDefinedName + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(
                    "Missing ; after return on line for " + userDefinedName + " on line " + current.line + ".");
        }

        // ; check
        if (!current.value.equals(";")) {
            throw new SyntaxError(
                    "Missing ; after return on line for " + userDefinedName + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(
                    "Missing } to complete " + userDefinedName + " declaration on line " + current.line + ".");
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(element, c);
                break;
            case "num":
                VarDecl(element, c);
                break;
            case "bool":
                VarDecl(element, c);
                break;
            case "string":
                VarDecl(element, c);
                break;
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError(
                    "Missing } after return on line for " + userDefinedName + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("No main declaration after " + userDefinedName + " on line " + current.line + ".");
        }

    }

    // Algorithm → // nothing (nullable)
    // Algorithm → Instr ; Algorithm
    public void Algorithm(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Algorithm");
        connect.appendChild(element);
        Node c = new Node("Algorithm");
        p.addChild(c);

        Instr(element, c);

        if (!current.value.equals(";")) {
            throw new SyntaxError("Missing ;  on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            // throw new SyntaxError("Missing a halt or return on line " + current.line +
            // ".");
            return;
        }

        switch (current.value) {
            case "if":
                Algorithm(connect, p);
                break;
            case "do":
                Algorithm(connect, p);
                break;
            case "while":
                Algorithm(connect, p);
                break;
            case "call":
                Algorithm(connect, p);
                break;
            case "output":
                Algorithm(connect, p);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(connect, p);
                }
        }

    }

    // Instr → Assign
    // Instr → Branch
    // Instr → Loop
    // Instr → PCall
    public void Instr(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Instr");
        connect.appendChild(element);
        Node c = new Node("Instr");
        p.addChild(c);

        switch (current.value) {
            case "if":
                Branch(element, c);
                break;
            case "do":
                Loop(element, c);
                break;
            case "while":
                Loop(element, c);
                break;
            case "call":
                PCall(element, c);
                break;
            case "output":
                Assign(element, c);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Assign(element, c);
                }
        }

    }

    // Assign → LHS := Expr
    public void Assign(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Assign");
        connect.appendChild(element);
        Node c = new Node("Assign");
        p.addChild(c);

        LHS(element, c);

        // := check
        if (!current.value.equals(":=")) {
            throw new SyntaxError("Missing := for  assignment on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing an expression for assignment on line " + current.line + ".");
        }

        Expr(element, c);

    }

    // Branch → if (Expr) then { Algorithm } Alternat
    public void Branch(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Branch");
        connect.appendChild(element);
        Node c = new Node("Branch");
        p.addChild(c);

        // if check
        if (!current.value.equals("if")) {
            throw new SyntaxError("Missing if on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing ( after if on line " + current.line + ".");
        }

        // ( check
        if (!current.value.equals("(")) {
            throw new SyntaxError("Missing ( after if on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing an Expr type if ( on line " + current.line + ".");
        }

        Expr(element, c);

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError("Missing ) after Expr type on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing then after if condition on line " + current.line + ".");
        }

        // then check
        if (!current.value.equals("then")) {
            throw new SyntaxError("Missing then after if condition on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing { after then on line " + current.line + ".");
        }

        // { check
        if (!current.value.equals("{")) {
            throw new SyntaxError("Missing { after then on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element, c);
                break;
            case "do":
                Algorithm(element, c);
                break;
            case "while":
                Algorithm(element, c);
                break;
            case "call":
                Algorithm(element, c);
                break;
            case "output":
                Algorithm(element, c);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element, c);
                }
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            // throw new SyntaxError("Missing } for then completion on line " + current.line
            // + ".");
            return;
        }

        if (current.value.equals("else")) {
            Alternat(element, c);
        }

    }

    // Alternat → // nothing (nullable)
    // Alternat → else { Algorithm }
    public void Alternat(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Alternat");
        connect.appendChild(element);
        Node c = new Node("Alternat");
        p.addChild(c);

        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));
        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing { after else on line  " + current.line + ".");
        }

        // { check
        if (!current.value.equals("{")) {
            throw new SyntaxError("Missing { after else on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } for else completion on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element, c);
                break;
            case "do":
                Algorithm(element, c);
                break;
            case "while":
                Algorithm(element, c);
                break;
            case "call":
                Algorithm(element, c);
                break;
            case "output":
                Algorithm(element, c);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element, c);
                }
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            // throw new SyntaxError("Missing } for then completion on line " + current.line
            // + ".");
            return;
        }

    }

    // Loop → do { Algorithm } until (Expr)
    // Loop → while (Expr) do { Algorithm }
    public void Loop(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Loop");
        connect.appendChild(element);
        Node c = new Node("Loop");
        p.addChild(c);
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));
        switch (current.value) {
            case "do":
                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
                }

                // { check
                if (!current.value.equals("{")) {
                    throw new SyntaxError("Missing { after do on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }

                // Algorithm check
                switch (current.value) {
                    case "if":
                        Algorithm(element, c);
                        break;
                    case "do":
                        Algorithm(element, c);
                        break;
                    case "while":
                        Algorithm(element, c);
                        break;
                    case "call":
                        Algorithm(element, c);
                        break;
                    case "output":
                        Algorithm(element, c);
                        break;
                    default:
                        if (current.type.equals("userDefinedName")) {
                            Algorithm(element, c);
                        }
                }

                // } check
                if (!current.value.equals("}")) {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing until keyword after } on line " + current.line + ".");
                }

                // until
                if (!current.value.equals("until")) {
                    throw new SyntaxError("Missing until keyword after } on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ( after until on line " + current.line + ".");
                }
                // (
                if (!current.value.equals("(")) {
                    throw new SyntaxError("Missing ( after until on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                // Expr
                Expr(element, c);
                // )
                if (!current.value.equals(")")) {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            case "while":
                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ( after while on line " + current.line + ".");
                }

                // (
                if (!current.value.equals("(")) {
                    throw new SyntaxError("Missing ( after while on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ) to complete while on line " + current.line + ".");
                }
                // Expr
                Expr(element, c);
                // )
                if (!current.value.equals(")")) {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing do after } on line " + current.line + ".");
                }
                // do
                if (!current.value.equals("do")) {
                    throw new SyntaxError("Missing do after } on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing { after do on line " + current.line + ".");
                }
                // { check
                if (!current.value.equals("{")) {
                    throw new SyntaxError("Missing { after do on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }

                // Algorithm check
                switch (current.value) {
                    case "if":
                        Algorithm(element, c);
                        break;
                    case "do":
                        Algorithm(element, c);
                        break;
                    case "while":
                        Algorithm(element, c);
                        break;
                    case "call":
                        Algorithm(element, c);
                        break;
                    case "output":
                        Algorithm(element, c);
                        break;
                    default:
                        if (current.type.equals("userDefinedName")) {
                            Algorithm(element, c);
                        }
                }

                // } check
                if (!current.value.equals("}")) {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }

                break;
        }
    }

    // LHS → output
    // LHS → userDefinedName Field
    public void LHS(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("LHS");
        connect.appendChild(element);
        Node c = new Node("LHS");
        p.addChild(c);
        if (current.value.equals("output")) {
            element.appendChild(doc.createTextNode(current.value));
            c.addChild(new Node(current.value, current.type));
            if (hasNext()) {
                goToNext();
            }
            return;
        }
        if (current.type.equals("userDefinedName")) {
            //addCustomText(element, current.type, current.value);
            Token temp;
            if (hasNext()) {
                temp = current.next;
            } else {
                Var(element, c);
                return;
            }
            if (temp.value.equals("[")) {
                Field(element, c);
            } else {
                Var(element, c);
            }
        } else {
            throw new SyntaxError("Incorrect LHS type (" + current.value + ") on line: " + current.line);
        }

    }

    // Expr → Const
    // Expr → userDefinedName Field
    // Expr → UnOp
    // Expr → BinOp
    public void Expr(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Expr");
        connect.appendChild(element);
        Node c = new Node("Expr");
        p.addChild(c);
        // Const
        if (current.value.equals("true") || current.value.equals("false")) {
            Const(element, c);
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            Const(element, c);
            return;
        }

        // UnOp
        switch (current.value) {
            case "input":
                UnOp(element, c);
                return;
            case "not":
                UnOp(element, c);
                return;
        }
        // BinOp
        switch (current.value) {
            case "and":
                BinOp(element, c);
                return;
            case "or":
                BinOp(element, c);
                return;
            case "eq":
                BinOp(element, c);
                return;
            case "larger":
                BinOp(element, c);
                return;
            case "add":
                BinOp(element, c);
                return;
            case "sub":
                BinOp(element, c);
                return;
            case "mult":
                BinOp(element, c);
                return;
        }

        // userDefinedName VarFieldChoice
        if (current.type.equals("userDefinedName")) {
            //addCustomText(element, current.type, current.value);
            Token temp;
            if (hasNext()) {
                temp = current.next;
            } else {
                Var(element, c);
                return;
            }
            if (temp.value.equals("[")) {
                Field(element, c);
            } else {
                Var(element, c);
            }
        } else {
            throw new SyntaxError("Incorrect Expr type (" + current.value + ") on line: " + current.line);
        }

    }

    // PCall → call userDefinedName
    public void PCall(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("PCall");
        connect.appendChild(element);
        Node c = new Node("PCall");
        p.addChild(c);
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing userDefinedName for call on line " + current.line);
        }

        if (!current.type.equals("userDefinedName")) {
            throw new SyntaxError(current.value + "is not a userDefinedName on line " + current.line);
        }
        addCustomText(element, current.type, current.value, c);

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // Var → userDefinedName
    public void Var(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Var");
        connect.appendChild(element);
        Node c = new Node("Var");
        p.addChild(c);

        if (!current.type.equals("userDefinedName")) {
            throw new SyntaxError(current.value + " is not of type userDefinedName on line " + current.line + ".");
        }

        addCustomText(element, current.type, current.value, c);

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // Field → null // new
    // Field → [FieldIndex]
    public void Field(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Field");
        connect.appendChild(element);
        Node c = new Node("Field");
        p.addChild(c);
        // element.appendChild(doc.createTextNode(current.value));
        addCustomText(element, current.type, current.value, c);

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }

        if (!current.value.equals("[")) {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }

        FieldIndex(element, c);

        if (!current.value.equals("]")) {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // FieldIndex → Var
    // FieldIndex → Const
    public void FieldIndex(Element connect, Node p) throws SyntaxError {
        //Element element = doc.createElement("FieldIndex");
        //connect.appendChild(element);
        // Const
        if (current.value.equals("true") || current.value.equals("false")) {
            Const(connect, p);
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            Const(connect, p);
            return;
        }
        // Var
        if (current.type.equals("userDefinedName")) {
            Var(connect, p);
            return;
        }

        throw new SyntaxError(current.value + " is not acceptable for an array index on line " + current.line + ".");
    }

    // Const → ShortString
    // Const → Number
    // Const → true
    // Const → false
    public void Const(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Const");
        connect.appendChild(element);
        Node c = new Node("Const");
        p.addChild(c);
        if (current.value.equals("true") || current.value.equals("false")) {
            element.appendChild(doc.createTextNode(current.value));
            c.addChild(new Node(current.value, current.type));
            if (hasNext()) {
                goToNext();
            }
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            element.appendChild(doc.createTextNode(current.value));
            c.addChild(new Node(current.value, current.type));
            if (hasNext()) {
                goToNext();
            }
            return;
        }
        throw new SyntaxError(current.value + " is not a Const on line " + current.line);
    }

    // UnOp → input(Var)
    // UnOp → not(Expr)
    public void UnOp(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("UnOp");
        connect.appendChild(element);
        Node c = new Node("UnOp");
        p.addChild(c);
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));
        String UnOpType = current.value;
        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }

        // ( check
        if (!current.value.equals("(")) {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }

        if (UnOpType.equals("input")) {
            Var(element, c);
        }
        if (UnOpType.equals("not")) {
            Expr(element, c);
        }

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }
    }

    // BinOp → and(Expr,Expr)
    // BinOp → or(Expr,Expr)
    // BinOp → eq(Expr,Expr)
    // BinOp → larger(Expr,Expr)
    // BinOp → add(Expr,Expr)
    // BinOp → sub(Expr,Expr)
    // BinOp → mult(Expr,Expr)
    public void BinOp(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("BinOp");
        connect.appendChild(element);
        Node c = new Node("BinOp");
        p.addChild(c);
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));
        String BinOpType = current.value;

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line);
        }

        // ( check
        if (!current.value.equals("(")) {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }

        Expr(element, c);

        // , check
        if (!current.value.equals(",")) {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }

        Expr(element, c);

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // VarDecl → // nothing (nullable)
    // VarDecl → Dec ; VarDecl
    public void VarDecl(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("VarDecl");
        connect.appendChild(element);
        Node c = new Node("VarDecl");
        p.addChild(c);

        Dec(element, c);

        // ; check
        if (!current.value.equals(";")) {
            throw new SyntaxError("Missing ; after halt on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));
        c.addChild(new Node(current.value, current.type));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(connect, p);
                break;
            case "num":
                VarDecl(connect, p);
                break;
            case "bool":
                VarDecl(connect, p);
                break;
            case "string":
                VarDecl(connect, p);
                break;
        }

    }

    // Dec → TYP Var
    // Dec → arr TYP[Const] Var
    public void Dec(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("Dec");
        connect.appendChild(element);
        Node c = new Node("Dec");
        p.addChild(c);

        switch (current.value) {
            case "arr":
                // VarDecl(element);
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));
                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                TYP(element, c);

                // [ check
                if (!current.value.equals("[")) {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }

                Const(element, c);

                // ] check
                if (!current.value.equals("]")) {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }

                Var(element, c);

                break;
            case "num":
                TYP(element, c);
                Var(element, c);
                break;
            case "bool":
                TYP(element, c);
                Var(element, c);
                break;
            case "string":
                TYP(element, c);
                Var(element, c);
                break;
        }
    }

    // TYP → num
    // TYP → bool
    // TYP → string
    public void TYP(Element connect, Node p) throws SyntaxError {
        Element element = doc.createElement("TYP");
        connect.appendChild(element);
        Node c = new Node("TYP");
        p.addChild(c);
        switch (current.value) {
            case "num":
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));
                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            case "bool":
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));
                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            case "string":
                element.appendChild(doc.createTextNode(current.value));
                c.addChild(new Node(current.value, current.type));
                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            default:
                throw new SyntaxError(current.value + " is an incorrent TYP on line " + current.line);
        }

    }

    public boolean hasNext() {
        return current.next != null;
    }

    public void goToNext() {
        current = current.next;
    }

    public void addCustomText(Element connect, String type, String value, Node p) {
        Element element = doc.createElement(type);
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(value));
        Node c = new Node(current.type);
        p.addChild(c);
        c.addChild(new Node(current.value, current.type));
    }

    public Node syntaxTree(){
        return SyntaxTree;
    }

}
