import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
public class JTabbedPaneDemo extends JPanel {
    public JTabbedPaneDemo(String[] pickProjections) {
		JTabbedPane jtbExample = new JTabbedPane();
		for(int i = 0; i < pickProjections.length; i++)
		{
		    JPanel jplInnerPanel = createInnerPanel(pickProjections[i]);
		    jtbExample.addTab("Round "+(i+1), jplInnerPanel);
		}
		jtbExample.setSelectedIndex(0);
		// Add the tabbed pane to this panel.
		setLayout(new GridLayout(1, 1));
		add(jtbExample);
	}
	
	protected JPanel createInnerPanel(String text) {
		JPanel jplPanel = new JPanel();
		jplPanel.setLayout(new GridLayout(1, 1));
		
		
		
		
		
		JEditorPane jEditorPane = new JEditorPane();
        
        // make it read-only
        jEditorPane.setEditable(false);
        
        // add a HTMLEditorKit to the editor pane
        HTMLEditorKit kit = new HTMLEditorKit();
        jEditorPane.setEditorKit(kit);
        
        // now add it to a scroll pane
        // add some styles to the html
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {background-color: #EEEEFF; color:#000; font-family:times; margin: 4px;}");
        //styleSheet.addRule("h1 {color: blue;}");
        styleSheet.addRule("h2, h1 {color: #0D2536;}");
        styleSheet.addRule("pre {font : 10px monaco; color : black; background-color : #fafafa; }");
        // create a document, set it on the jeditorpane, then add the html
        Document doc = kit.createDefaultDocument();
        jEditorPane.setDocument(doc);
        jEditorPane.setText(text);
        
        JScrollPane scrollPane = new JScrollPane(jEditorPane);
        
        
        
		
		jplPanel.add(scrollPane);
		return jplPanel;
	}
    
}