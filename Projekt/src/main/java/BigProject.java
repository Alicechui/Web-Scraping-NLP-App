import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class BigProject extends JFrame {

    private JTextField urlFileField, searchField;
    private JRadioButton keyword, lemma, posTag;
    private JTextArea mainArea;
    private javax.swing.text.Document output = new PlainDocument();

    public BigProject() {

//        setting size and title
        setSize(1000, 800);
        setTitle("BigProject");

//        window listener to be able to close the file
        addWindowListener(new MyWindowListener());

//        BorderLayout manager as a template for the frame
        getContentPane().setLayout(new BorderLayout());

        setUpNorth();

//        JTextArea where the @output - the sentences - are displayed
        mainArea = new JTextArea(output);
        mainArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
//        update mainArea each time @output is updated
        mainArea.addComponentListener(new ValueReporter());
//        make mainArea to be a JScrollPane and add it to the template
        JScrollPane main = new JScrollPane(mainArea);
        getContentPane().add(main);
    }

    private void setUpNorth() {

//        a panel for the North part
        JPanel northPanel = new JPanel();
//        a separate panel for word-lemma-tag choice
        JPanel searchFor = new JPanel();

//        setting a label for URL
        JLabel urlLabel = new JLabel("Enter URL or a filename:");
        urlLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

//        setting a text field for URL
        urlFileField = new JTextField(0);
        urlFileField.setPreferredSize(new Dimension(100, 20));
        urlFileField.setMaximumSize(new Dimension(100, 20));

//        setting a button for search (does not work for now)
        JButton searchLabel = new JButton("SEARCH");
        searchLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        searchLabel.addActionListener(new SearchButtonListener());

//        setting a text field for search
        searchField = new JTextField(0);
        searchField.setPreferredSize(new Dimension(100, 20));
        searchField.setMaximumSize(new Dimension(100, 20));
        searchField.setToolTipText("Enter a word");

//        setting buttons for a possible choice of actions
//        They are global variables as they are later accessed in the other methods
        keyword = new JRadioButton("keyword");
        keyword.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        lemma  = new JRadioButton("lemma");
        lemma.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        posTag = new JRadioButton("POS tag");
        posTag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

//        a ButtonGroup to unite three previous buttons on one system so that only one of them can be chosen at time
        ButtonGroup group = new ButtonGroup();
        keyword.setSelected(true);

//        adding three buttons to the system
        group.add(keyword);
        group.add(lemma);
        group.add(posTag);
//        setting a title of the system
        JLabel ques = new JLabel("What would you like to search for?");
        ques.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));


//        add the title and the buttons to the panel
        searchFor.add(ques);
        searchFor.add(keyword);
        searchFor.add(lemma);
        searchFor.add(posTag);
//        locate the title and the buttons vertically, one under another. That is why I created searchFor panel
//        as in North panel everything is aligned horizontally by default
        searchFor.setLayout(new BoxLayout(searchFor, BoxLayout.Y_AXIS));


//        nicely add all the text fields and buttons to the panel
        northPanel.setBackground(Color.lightGray);
        northPanel.add(Box.createHorizontalGlue());
        northPanel.add(urlLabel);
        northPanel.add(Box.createHorizontalStrut(8));
        northPanel.add(urlFileField);
//        northPanel.add(Box.createVerticalStrut(20));
        northPanel.add(Box.createHorizontalStrut(30));
        northPanel.add(searchField);
        northPanel.add(Box.createHorizontalStrut(8));
        northPanel.add(searchLabel);
        northPanel.add(Box.createHorizontalStrut(30));
        northPanel.add(searchFor);


//        add the panel to the North part of the BorderLayout
        getContentPane().add(northPanel, BorderLayout.NORTH);

    }

//    TODO GUI: add an option to choose the amount of neighbours for the search word

    //    class for a closing button to work
    private class MyWindowListener extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
        }
    }


//    class for a search button to work
    private class SearchButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
//                get the url and the search word
                String url = urlFileField.getText();
                String token = searchField.getText();
//                TODO: now the method works only with URLs. It should be able to process files as well

//                scrape the @url and save its contents without html tags in a String value (so sorry for that)
                Document doc = Jsoup.connect(url).get();
                String contents = doc.body().text();

//                use CorpusBuilder class from opennlp selftest to process the contents (segmentation, tokenization...)
                CorpusBuilder cb = new CorpusBuilder(contents);

//                do the job if "keyword" is chosen
                if (keyword.isSelected())
                    findKeyword(cb, token);
//                TODO: the same for two other options

            } catch (FileNotFoundException er) {
                System.out.println("Problems with corpus");
            } catch (IOException err) {
                System.out.println("No such URL");
            } catch (BadLocationException errr) {
                System.out.println("Problems with output");
            }
        }
    }

//    component listener for the main area
    private class ValueReporter extends ComponentAdapter {

        public void componentResized(ComponentEvent e) { }
    }


//    helper method to display all the sentences from @cb that contain @keyword
//    TODO: update the method when an option to choose the amount of neighbours is added to GUI
    private void findKeyword(CorpusBuilder cb, String keyword) throws BadLocationException {

        for (List<String> sents : cb.getTokens())
//            every sents may contain more than one appearance of "keyword" so I chose the while loop
            while (sents.contains(keyword)) {
//                update the keyword to prevent the loop from being infinite
                sents.set(sents.indexOf(keyword), keyword.toUpperCase());
//                if there is nothing in the output - which is of Document value - put a nicely joined first sentence
                if (output.getLength() == 0)
                    output.insertString(0, String.join(" ", sents)+"\n", null);
//                else add the sentence to the end of the @output
                else
                    output.insertString(output.getLength(), String.join(" ", sents)+"\n", null);
            }
    }

//    TODO: the similar methods if the user chose "lemma" or "POStag"

//    TODO: save @output to XML file from Moodle.

    //    main method to run the program
    public static void main(String[] args) {

        BigProject bP = new BigProject();
        bP.setVisible(true);
    }
}
