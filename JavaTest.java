import javax.swing.*;
import java.awt.*;
import javax.swing.text.html.*;
import javax.swing.text.*;

public class JavaTest
{
    public static void main(String[] args)
    {
        // create a JEditorPane
        JEditorPane jEditorPane = new JEditorPane();
        
        // make it read-only
        jEditorPane.setEditable(false);
        
        // add a HTMLEditorKit to the editor pane
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        
        // now add it to a scroll pane
        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
        styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2 {color: #ff0000;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        // create a document, set it on the jeditorpane, then add the html
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText("<html><h1>cow</h1></html>");
        
        
        JFrame frame = new JFrame("TabbedPane Source Demo");
		frame.getContentPane().add(jEditorPane,
				BorderLayout.CENTER);
		frame.setSize(400, 125);
		frame.setVisible(true);
    }
}
