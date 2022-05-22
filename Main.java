import java.io.File;
import java.util.Scanner;

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

public class Main {

    public static void main(String[] args) {
        String fileName = fileChoice();
        Lexer lex = new Lexer(fileName + ".txt");
        Token result = lex.run();
        if (result.value.equals("LEXICAL ERROR") || result.value.equals("File not found.")) {
            return;
        }
        lex.printTokens();
        Parser parser = new Parser(result, fileName);
        parser.parse();

    }

    public static String fileChoice() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter the file name: ");
        String name = reader.nextLine();
        reader.close();
        return name;
    }

    public static void xmlExample() {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element root = doc.createElement("GOD");
            doc.appendChild(root);

            Element rootElement = doc.createElement("CONFIGURATION");
            root.appendChild(rootElement);
            rootElement.appendChild(doc.createTextNode("chrome"));

            Element browser = doc.createElement("BROWSER");
            browser.appendChild(doc.createTextNode("chrome"));
            rootElement.appendChild(browser);

            Element base = doc.createElement("BASE");
            base.appendChild(doc.createTextNode("http:fut"));
            rootElement.appendChild(base);

            Element employee = doc.createElement("EMPLOYEE");
            rootElement.appendChild(employee);
            Element empName = doc.createElement("EMP_NAME");
            empName.appendChild(doc.createTextNode("Anhorn, Irene"));
            employee.appendChild(empName);
            Element actDate = doc.createElement("ACT_DATE");
            actDate.appendChild(doc.createTextNode("20131201"));
            rootElement.appendChild(doc.createTextNode("chrome"));
            employee.appendChild(actDate);
            Element yeet = doc.createElement("CONFIGURATION");
            root.appendChild(yeet);
            Element test = doc.createElement("BROWSER");
            test.appendChild(doc.createTextNode("chrome"));
            rootElement.appendChild(test);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult output = new StreamResult(new File("output.xml"));
            transformer.transform(source, output);
            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

}