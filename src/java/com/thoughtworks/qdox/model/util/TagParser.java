package com.thoughtworks.qdox.model.util;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TagParser {
    
    /**
     * Create a StreamTokenizer suitable for parsing the tag text. 
     */
    static StreamTokenizer makeTokenizer(String tagValue) {
        StreamTokenizer tokenizer = 
            new StreamTokenizer(new StringReader(tagValue));
        tokenizer.resetSyntax();
        tokenizer.wordChars('A','Z');
        tokenizer.wordChars('a','z');
        tokenizer.wordChars('0','9');
        tokenizer.wordChars('-','-');
        tokenizer.wordChars('_','_');
        tokenizer.wordChars('.','.');
        tokenizer.wordChars('<','<');
        tokenizer.wordChars('>','>');
        tokenizer.quoteChar('\'');
        tokenizer.quoteChar('"');
        tokenizer.whitespaceChars(' ',' ');
        tokenizer.whitespaceChars('\t','\t');
        tokenizer.whitespaceChars('\n','\n');
        tokenizer.whitespaceChars('\r','\r');
        tokenizer.eolIsSignificant(false);
        return tokenizer;
    }
    
    /**
     * Extract a Map of named parameters  
     */
    public static Map<String, String> parseNamedParameters(String tagValue) {
        Map<String, String> paramMap = new LinkedHashMap<String, String>();
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        try {
            while (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
                String key = tokenizer.sval;
                if (tokenizer.nextToken() != '=') {
                    break;
                }
                switch (tokenizer.nextToken()) {
                case StreamTokenizer.TT_WORD:
                case '"':
                case '\'':
                    paramMap.put(key, tokenizer.sval);
                default:
                    break;
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return paramMap;
    }

    /**
     * Extract an array of positional parameters  
     */
    public static String[] parseWords(String tagValue) {
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        List<String> wordList = new ArrayList<String>();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (tokenizer.sval == null) {
                    wordList.add(Character.toString((char)tokenizer.ttype));
                } else {
                    wordList.add(tokenizer.sval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error tokenizing tag");
        }
        return wordList.toArray(new String[0]);
    }
    
    /**
     * Extract an array of parameters as name or name=value representation
     * @since 1.11  
     */
    public static List<String> parseParameters(String tagValue) {
        StreamTokenizer tokenizer = makeTokenizer(tagValue);
        List<String> wordList = new LinkedList<String>();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                StringBuffer param = new StringBuffer();
                if (tokenizer.sval != null) {
                    param.append( tokenizer.sval );
                }
                
                //check for parameterValues
                while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                    if(tokenizer.sval == null && ('=' == (char)tokenizer.ttype || ','== (char)tokenizer.ttype)){
                        param.append( Character.toString( (char)tokenizer.ttype ) );
                        
                        //file was parsed correctly, so this works.
                        tokenizer.nextToken();
                        param.append(tokenizer.sval); 
                    }
                    else {
                        tokenizer.pushBack(); break;
                    }
                }
                wordList.add(param.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("error tokenizing tag");
        }
        return wordList;
    }
    
}
