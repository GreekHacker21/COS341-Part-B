import java.io.File;

import javax.security.auth.Subject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
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

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult output = new StreamResult(new File(fileName + ".xml"));
            transformer.transform(source, output);
            System.out.println("File saved!");
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    // SPLProgr → ProcDefs main { Algorithm halt ; VarDecl }
    public void SPLProgr() throws SyntaxError {
        Element element = doc.createElement("SPLProgr");
        doc.appendChild(element);

        // ProcDefs check
        if (current.value.equals("proc")) {
            ProcDefs(element);
        }

        // main check
        if (!current.value.equals("main")) {
            throw new SyntaxError("Missing main as beginning keyword on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode("main"));

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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing the keyword halt on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element);
                break;
            case "do":
                Algorithm(element);
                break;
            case "while":
                Algorithm(element);
                break;
            case "call":
                Algorithm(element);
                break;
            case "output":
                Algorithm(element);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element);
                }
        }

        // halt check
        if (!current.value.equals("halt")) {
            throw new SyntaxError("Missing halt after main on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } after halt's ; on line " + current.line + ".");
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(element);
                break;
            case "num":
                VarDecl(element);
                break;
            case "bool":
                VarDecl(element);
                break;
            case "string":
                VarDecl(element);
                break;
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } after halt's ; on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            throw new SyntaxError("No more characters allowed after main definition on line " + current.line + ".");
        }

    }

    // ProcDefs → // nothing (nullable)
    // ProcDefs → PD , ProcDefs
    public void ProcDefs(Element connect) throws SyntaxError {
        Element element = doc.createElement("ProcDefs");
        connect.appendChild(element);

        PD(element);

        if (!current.value.equals(",")) {
            throw new SyntaxError("missing a comma (,) after a proc definition on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("Missing main as beginning keyword on line " + current.line + ".");
        }

        // ProcDefs
        if (current.value.equals("proc")) {
            ProcDefs(connect);
        }

    }

    // PD → proc userDefinedName { ProcDefs Algorithm return ; VarDecl }
    public void PD(Element connect) throws SyntaxError {
        Element element = doc.createElement("PD");
        connect.appendChild(element);
        String userDefinedName = "";

        // proc check
        if (!current.value.equals("proc")) {
            throw new SyntaxError("missing a proc declaration on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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
        addCustomText(element, current.type, current.value);
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

        if (hasNext()) {
            current = current.next;
        } else {
            throw new SyntaxError("missing a return after {  on line " + current.line + ".");
        }

        // ProcDefs check
        if (current.value.equals("proc")) {
            ProcDefs(element);
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element);
                break;
            case "do":
                Algorithm(element);
                break;
            case "while":
                Algorithm(element);
                break;
            case "call":
                Algorithm(element);
                break;
            case "output":
                Algorithm(element);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element);
                }
        }

        // return check
        if (!current.value.equals("return")) {
            throw new SyntaxError("Missing return for " + userDefinedName + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(
                    "Missing } to complete " + userDefinedName + " declaration on line " + current.line + ".");
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(element);
                break;
            case "num":
                VarDecl(element);
                break;
            case "bool":
                VarDecl(element);
                break;
            case "string":
                VarDecl(element);
                break;
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError(
                    "Missing } after return on line for " + userDefinedName + " on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("No main declaration after " + userDefinedName + " on line " + current.line + ".");
        }

    }

    // Algorithm → // nothing (nullable)
    // Algorithm → Instr ; Algorithm
    public void Algorithm(Element connect) throws SyntaxError {
        Element element = doc.createElement("Algorithm");
        connect.appendChild(element);

        Instr(element);

        if (!current.value.equals(";")) {
            throw new SyntaxError("Missing ;  on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            // throw new SyntaxError("Missing a halt or return on line " + current.line +
            // ".");
            return;
        }

        switch (current.value) {
            case "if":
                Algorithm(connect);
                break;
            case "do":
                Algorithm(connect);
                break;
            case "while":
                Algorithm(connect);
                break;
            case "call":
                Algorithm(connect);
                break;
            case "output":
                Algorithm(connect);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(connect);
                }
        }

    }

    // Instr → Assign
    // Instr → Branch
    // Instr → Loop
    // Instr → PCall
    public void Instr(Element connect) throws SyntaxError {
        Element element = doc.createElement("Instr");
        connect.appendChild(element);

        switch (current.value) {
            case "if":
                Branch(element);
                break;
            case "do":
                Loop(element);
                break;
            case "while":
                Loop(element);
                break;
            case "call":
                PCall(element);
                break;
            case "output":
                Assign(element);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Assign(element);
                }
        }

    }

    // Assign → LHS := Expr
    public void Assign(Element connect) throws SyntaxError {
        Element element = doc.createElement("Assign");
        connect.appendChild(element);

        LHS(element);

        // := check
        if (!current.value.equals(":=")) {
            throw new SyntaxError("Missing := for  assignment on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing an expression for assignment on line " + current.line + ".");
        }

        Expr(element);

    }

    // Branch → if (Expr) then { Algorithm } Alternat
    public void Branch(Element connect) throws SyntaxError {
        Element element = doc.createElement("Branch");
        connect.appendChild(element);

        // if check
        if (!current.value.equals("if")) {
            throw new SyntaxError("Missing if on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing an Expr type if ( on line " + current.line + ".");
        }

        Expr(element);

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError("Missing ) after Expr type on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element);
                break;
            case "do":
                Algorithm(element);
                break;
            case "while":
                Algorithm(element);
                break;
            case "call":
                Algorithm(element);
                break;
            case "output":
                Algorithm(element);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element);
                }
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            // throw new SyntaxError("Missing } for then completion on line " + current.line
            // + ".");
            return;
        }

        if (current.value.equals("else")) {
            Alternat(element);
        }

    }

    // Alternat → // nothing (nullable)
    // Alternat → else { Algorithm }
    public void Alternat(Element connect) throws SyntaxError {
        Element element = doc.createElement("Alternat");
        connect.appendChild(element);

        element.appendChild(doc.createTextNode(current.value));
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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing } for else completion on line " + current.line + ".");
        }

        // Algorithm check
        switch (current.value) {
            case "if":
                Algorithm(element);
                break;
            case "do":
                Algorithm(element);
                break;
            case "while":
                Algorithm(element);
                break;
            case "call":
                Algorithm(element);
                break;
            case "output":
                Algorithm(element);
                break;
            default:
                if (current.type.equals("userDefinedName")) {
                    Algorithm(element);
                }
        }

        // } check
        if (!current.value.equals("}")) {
            throw new SyntaxError("Missing } for then completion on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

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
    public void Loop(Element connect) throws SyntaxError {
        Element element = doc.createElement("Loop");
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(current.value));
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

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }

                // Algorithm check
                switch (current.value) {
                    case "if":
                        Algorithm(element);
                        break;
                    case "do":
                        Algorithm(element);
                        break;
                    case "while":
                        Algorithm(element);
                        break;
                    case "call":
                        Algorithm(element);
                        break;
                    case "output":
                        Algorithm(element);
                        break;
                    default:
                        if (current.type.equals("userDefinedName")) {
                            Algorithm(element);
                        }
                }

                // } check
                if (!current.value.equals("}")) {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

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

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                // Expr
                Expr(element);
                // )
                if (!current.value.equals(")")) {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

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

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing ) to complete while on line " + current.line + ".");
                }
                // Expr
                Expr(element);
                // )
                if (!current.value.equals(")")) {
                    throw new SyntaxError("Missing ) to complete until on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

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

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }

                // Algorithm check
                switch (current.value) {
                    case "if":
                        Algorithm(element);
                        break;
                    case "do":
                        Algorithm(element);
                        break;
                    case "while":
                        Algorithm(element);
                        break;
                    case "call":
                        Algorithm(element);
                        break;
                    case "output":
                        Algorithm(element);
                        break;
                    default:
                        if (current.type.equals("userDefinedName")) {
                            Algorithm(element);
                        }
                }

                // } check
                if (!current.value.equals("}")) {
                    throw new SyntaxError("Missing } for do completion on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

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
    public void LHS(Element connect) throws SyntaxError {
        Element element = doc.createElement("LHS");
        connect.appendChild(element);
        if (current.value.equals("output")) {
            element.appendChild(doc.createTextNode(current.value));
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
                Var(element);
                return;
            }
            if (temp.value.equals("[")) {
                Field(element);
            } else {
                Var(element);
            }
        } else {
            throw new SyntaxError("Incorrect LHS type (" + current.value + ") on line: " + current.line);
        }

    }

    // Expr → Const
    // Expr → userDefinedName Field
    // Expr → UnOp
    // Expr → BinOp
    public void Expr(Element connect) throws SyntaxError {
        Element element = doc.createElement("Expr");
        connect.appendChild(element);
        // Const
        if (current.value.equals("true") || current.value.equals("false")) {
            Const(element);
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            Const(element);
            return;
        }

        // UnOp
        switch (current.value) {
            case "input":
                UnOp(element);
                return;
            case "not":
                UnOp(element);
                return;
        }
        // BinOp
        switch (current.value) {
            case "and":
                BinOp(element);
                return;
            case "or":
                BinOp(element);
                return;
            case "eq":
                BinOp(element);
                return;
            case "larger":
                BinOp(element);
                return;
            case "add":
                BinOp(element);
                return;
            case "sub":
                BinOp(element);
                return;
            case "mult":
                BinOp(element);
                return;
        }

        // userDefinedName VarFieldChoice
        if (current.type.equals("userDefinedName")) {
            //addCustomText(element, current.type, current.value);
            Token temp;
            if (hasNext()) {
                temp = current.next;
            } else {
                Var(element);
                return;
            }
            if (temp.value.equals("[")) {
                Field(element);
            } else {
                Var(element);
            }
        } else {
            throw new SyntaxError("Incorrect Expr type (" + current.value + ") on line: " + current.line);
        }

    }

    // PCall → call userDefinedName
    public void PCall(Element connect) throws SyntaxError {
        Element element = doc.createElement("PCall");
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing userDefinedName for call on line " + current.line);
        }

        if (!current.type.equals("userDefinedName")) {
            throw new SyntaxError(current.value + "is not a userDefinedName on line " + current.line);
        }
        addCustomText(element, current.type, current.value);

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // Var → userDefinedName
    public void Var(Element connect) throws SyntaxError {
        Element element = doc.createElement("Var");
        connect.appendChild(element);

        if (!current.type.equals("userDefinedName")) {
            throw new SyntaxError(current.value + " is not of type userDefinedName on line " + current.line + ".");
        }

        addCustomText(element, current.type, current.value);

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // Field → null // new
    // Field → [FieldIndex]
    public void Field(Element connect) throws SyntaxError {
        Element element = doc.createElement("Field");
        connect.appendChild(element);
        // element.appendChild(doc.createTextNode(current.value));
        addCustomText(element, current.type, current.value);

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }

        if (!current.value.equals("[")) {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }

        FieldIndex(element);

        if (!current.value.equals("]")) {
            throw new SyntaxError("Missing [index] format for array on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // FieldIndex → Var
    // FieldIndex → Const
    public void FieldIndex(Element connect) throws SyntaxError {
        //Element element = doc.createElement("FieldIndex");
        //connect.appendChild(element);
        // Const
        if (current.value.equals("true") || current.value.equals("false")) {
            Const(connect);
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            Const(connect);
            return;
        }
        // Var
        if (current.type.equals("userDefinedName")) {
            Var(connect);
            return;
        }

        throw new SyntaxError(current.value + " is not acceptable for an array index on line " + current.line + ".");
    }

    // Const → ShortString
    // Const → Number
    // Const → true
    // Const → false
    public void Const(Element connect) throws SyntaxError {
        Element element = doc.createElement("Const");
        connect.appendChild(element);
        if (current.value.equals("true") || current.value.equals("false")) {
            element.appendChild(doc.createTextNode(current.value));
            if (hasNext()) {
                goToNext();
            }
            return;
        }
        if (current.type.equals("Number") || current.type.equals("ShortString")) {
            element.appendChild(doc.createTextNode(current.value));
            if (hasNext()) {
                goToNext();
            }
            return;
        }
        throw new SyntaxError(current.value + " is not a Const on line " + current.line);
    }

    // UnOp → input(Var)
    // UnOp → not(Expr)
    public void UnOp(Element connect) throws SyntaxError {
        Element element = doc.createElement("UnOp");
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(current.value));
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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }

        if (UnOpType.equals("input")) {
            Var(element);
        }
        if (UnOpType.equals("not")) {
            Expr(element);
        }

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError(UnOpType + " is not a completed unary operation on line " + current.line);
        }
        element.appendChild(doc.createTextNode(current.value));

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
    public void BinOp(Element connect) throws SyntaxError {
        Element element = doc.createElement("BinOp");
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(current.value));
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

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }

        Expr(element);

        // , check
        if (!current.value.equals(",")) {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }

        Expr(element);

        // ) check
        if (!current.value.equals(")")) {
            throw new SyntaxError(BinOpType + " is not a completed binary operation on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

    }

    // VarDecl → // nothing (nullable)
    // VarDecl → Dec ; VarDecl
    public void VarDecl(Element connect) throws SyntaxError {
        Element element = doc.createElement("VarDecl");
        connect.appendChild(element);

        Dec(element);

        // ; check
        if (!current.value.equals(";")) {
            throw new SyntaxError("Missing ; after halt on line " + current.line + ".");
        }
        element.appendChild(doc.createTextNode(current.value));

        if (hasNext()) {
            goToNext();
        } else {
            return;
        }

        // VarDecl check
        switch (current.value) {
            case "arr":
                VarDecl(connect);
                break;
            case "num":
                VarDecl(connect);
                break;
            case "bool":
                VarDecl(connect);
                break;
            case "string":
                VarDecl(connect);
                break;
        }

    }

    // Dec → TYP Var
    // Dec → arr TYP[Const] Var
    public void Dec(Element connect) throws SyntaxError {
        Element element = doc.createElement("Dec");
        connect.appendChild(element);

        switch (current.value) {
            case "arr":
                // VarDecl(element);
                element.appendChild(doc.createTextNode(current.value));
                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                TYP(element);

                // [ check
                if (!current.value.equals("[")) {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }

                Const(element);

                // ] check
                if (!current.value.equals("]")) {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }
                element.appendChild(doc.createTextNode(current.value));

                if (hasNext()) {
                    goToNext();
                } else {
                    throw new SyntaxError("Invalid array declaration on line " + current.line + ".");
                }

                Var(element);

                break;
            case "num":
                TYP(element);
                Var(element);
                break;
            case "bool":
                TYP(element);
                Var(element);
                break;
            case "string":
                TYP(element);
                Var(element);
                break;
        }
    }

    // TYP → num
    // TYP → bool
    // TYP → string
    public void TYP(Element connect) throws SyntaxError {
        Element element = doc.createElement("TYP");
        connect.appendChild(element);
        switch (current.value) {
            case "num":
                element.appendChild(doc.createTextNode(current.value));
                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            case "bool":
                element.appendChild(doc.createTextNode(current.value));
                if (hasNext()) {
                    goToNext();
                } else {
                    return;
                }
                break;
            case "string":
                element.appendChild(doc.createTextNode(current.value));
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

    public void addCustomText(Element connect, String type, String value) {
        Element element = doc.createElement(type);
        connect.appendChild(element);
        element.appendChild(doc.createTextNode(value));
    }

}
