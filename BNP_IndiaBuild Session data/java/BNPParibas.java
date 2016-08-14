/*
 * Licensed under Creative Commons Attribution 4.0 @ Prakash B. Pimpale, KBCS, CDAC Mumbai - prakashatcdac.in, pbpimpaleatgmail.com
 */
package bnpparibas;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

/**
 *
 * @author prakash
 */
public class BNPParibas {

    static String modelpath = "/home/prakash/Dropbox/myShare/Courses/BNPTask/openNLPModels/";

    //read email
    String readEmail() throws FileNotFoundException {
        // email text holder
        String email = "";
        //Create Scanner
        String emailPath = "/home/prakash/Dropbox/myShare/Courses/BNPTask/dean-c/inbox/24.";
        Scanner emailScanner = new Scanner(new File(emailPath));

        //read the file line by line - you can us any other efficient way
        while (emailScanner.hasNextLine()) {
            // scan email
            String line = emailScanner.nextLine().trim();
            //Keep appending the lines
            email = email + "\n" + line;
        }

        System.out.println("EMAIL FILE:" + email);
        return email;
    }

    //Sample use of regular expression to extract email from given text string
    void RegExExtractEmail(String email) {
        //System.out.println(email
        //Define pattern
        String fromEmailPattern = "From: (([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}))";
        Pattern p = Pattern.compile(fromEmailPattern);

        //Get a matcher for our email text
        Matcher m = p.matcher(email);

        //Extract sender e-mail address
        if (m.find()) {

            //getting groups
            System.out.println("0th Matching Pattern: " + m.group(0));
            System.out.println("1st email ID: " + m.group(1));
            System.out.println("2nd Person ID/Name: " + m.group(2));
            System.out.println("3rd: Organization: " + m.group(3));

        }

    }

    //SENTENCE SEGMENTOR
    String[] detectSentences(String email) throws FileNotFoundException, IOException {

        //Specify model to be used - it's already trained model
        InputStream modelIn = new FileInputStream(modelpath + "en-sent.bin");
        String sentences[] = null;
        try {
            // load the model
            SentenceModel model = new SentenceModel(modelIn);

            // get A sentence detector 
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

            //get sentences
            sentences = sentenceDetector.sentDetect(email);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //print those sentences
        for (String sent : sentences) {
            System.out.println("SENT:" + sent);
        }

        return sentences;
    }

    //TOKENIZER
    String[] tokenizeText(String sentence) throws FileNotFoundException {

        InputStream modelIn = new FileInputStream(modelpath + "en-token.bin");
        String tokens[] = null;

        try {
            //load Tokenizer model
            TokenizerModel model = new TokenizerModel(modelIn);

            //get a tokenizer
            Tokenizer tokenizer = new TokenizerME(model);

            //get the tokens
            tokens = tokenizer.tokenize(sentence);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // print those tokens
        for (String tok : tokens) {
            System.out.println("TOK:" + tok);
        }

        return tokens;
    }

    //POS TAGGER
    String[] POSTag(String[] tokens) throws FileNotFoundException {

        String pos[] = null;
        InputStream modelIn = new FileInputStream(modelpath + "en-pos-maxent.bin");

        try {
            //load model
            POSModel model = new POSModel(modelIn);

            //get a POS tagger
            POSTaggerME tagger = new POSTaggerME(model);

            //get tags
            pos = tagger.tag(tokens);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // print those POS tags with the words
        for (int i = 0; i < pos.length; i++) {
            System.out.println("WORD:POS: " + tokens[i] + " =" + pos[i]);
        }

        return pos;
    }

    //CHUNKER
    String[] chunkTag(String[] tokens, String[] postags) throws FileNotFoundException {
        InputStream modelIn = new FileInputStream(modelpath + "en-chunker.bin");
        String[] chunkTags = null;

        try {
            //load model
            ChunkerModel model = new ChunkerModel(modelIn);
            ChunkerME chunker = new ChunkerME(model);
            chunkTags = chunker.chunk(tokens, postags);

        } catch (IOException e) {
            // handling failure
            e.printStackTrace();
        }

        // print those Chunk tags with the words
        for (int i = 0; i < chunkTags.length; i++) {
            System.out.println("WORD:Chunk: " + tokens[i] + " =" + chunkTags[i]);
        }
        return chunkTags;
    }

    void extractNames(String[] tokens) throws FileNotFoundException {

        InputStream modelIn = new FileInputStream(modelpath + "en-ner-person.bin");
        Span nameSpans[] = null;
        try {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);

            NameFinderME nameFinder = new NameFinderME(model);

            nameSpans = nameFinder.find(tokens);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Span s : nameSpans) {

            // each span is one entity and tell what are indexes where it starts and ends
            // System.out.println(s);
            int is = s.getStart();
            int ie = s.getEnd();
            String type = s.getType();
            //System.out.println("Start:"+is+" end:"+ie+" type:"+type);

            System.out.println("THE NER of type: \"" + type + "\" is as follows:");
            for (int i = is; i <= ie; i++) {
                System.out.print(tokens[i] + " ");
            }

            System.out.println();

        }

    }

    public static void main(String[] args) throws FileNotFoundException, IOException, BoilerpipeProcessingException {
        // An object for calls
        BNPParibas bnp = new BNPParibas();

        System.out.println("=====READING EMAIL FILE====");
        //read email
        String email = bnp.readEmail();

        System.out.println("=====Extracting Sender====");
        //extract email of sender
        bnp.RegExExtractEmail(email);

        //Email text from that e-mail
        String emailText = "Please plan to attend the local UBS HR Presentation given by Gabrielle Hagele and Chris Lue tomorrow, Thursday, 1/24/02 at 9:00 AM in the Mt. Hood Conference Room.  The presentation will detail UBS Benefits and UBS HR Policies.  Time is also set aside for Question and Answers.  \n"
                + "\n"
                + "Several UBS HR required forms will be distributed during this meeting.  These forms are required to process payroll and enroll in the benefit plans.  If you are unable to attend this meeting, please contact me or one of the UBS HR personnel.   See you then!";

        String alternatEmail = "John F. K. went to USA.";
        
        emailText = alternatEmail;
        
        System.out.println("=====SENTENCE SEGMENTING EMAIL TEXT====");
        // Sentence segment
        String sentences[] = bnp.detectSentences(emailText);

        System.out.println("=====TOKENIZING A SENTENCE====");
        // Tokenizer
        String[] tokens = bnp.tokenizeText(sentences[0]);

        System.out.println("=====POS TAGGING A SENTENCE====");
        // POS tagger
        String[] pos = bnp.POSTag(tokens);

        System.out.println("=====CHUNKING A SENTENCE====");
        //Chunker
        String[] chunks = bnp.chunkTag(tokens, pos);

        //NER
        System.out.println("=====NER for a sentence====");

        bnp.extractNames(tokens);
    }

}
