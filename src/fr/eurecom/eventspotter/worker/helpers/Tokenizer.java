/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package fr.eurecom.eventspotter.worker.helpers;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import fr.eurecom.eventspotter.caslight.Feature;
import fr.eurecom.eventspotter.caslight.FeatureStructure;

/**
 * Tokenizer class derived from UIMA WhitespaceTokenizer.
 *
 * 
 */
public class Tokenizer {

    private static final int CH_SPECIAL = 0;
    private static final int CH_NUMBER = 1;
    private static final int CH_LETTER = 2;
    private static final int CH_WHITESPACE = 4;
    private static final int CH_PUNCTUATION = 5;
    private static final int CH_NEWLINE = 6;
    private static final int UNDEFINED = -1;
    private static final int INVALID_CHAR = 0;
    public static final String TOKEN_ANNOTATION_NAME = "org.apache.uima.TokenAnnotation";
    public static final String SENTENCE_ANNOTATION_NAME = "org.apache.uima.SentenceAnnotation";
    public static final String TOKEN_TYPE_FEATURE_NAME = "tokenType";
    private static final String tokenType = "Token";
    private static final String sentenceType = "Sentence";
    private static List<String> punctuations = Arrays.asList(new String[]{".",
                "!", "?"});
    /**
     * Tokenizes a string. 
     * @param input the input string.
     * @param sentences whether or not to look for sentence boundaries also
     * @return retunrs tokens or tokens+sentences.
     */
    
    public static List<FeatureStructure> tokenize(String input, boolean sentences) {
        List<FeatureStructure> ret = new ArrayList<FeatureStructure>();
        // get text content from the CAS
        char[] textContent = input.toCharArray();

        int tokenStart = UNDEFINED;
        int currentCharPos = 0;
        int sentenceStart = 0;
        int nextCharType = UNDEFINED;
        char nextChar = INVALID_CHAR;


        while (currentCharPos < textContent.length) {
            char currentChar = textContent[currentCharPos];
            int currentCharType = getCharacterType(currentChar);

            // get character class for current and next character
            if ((currentCharPos + 1) < textContent.length) {
                nextChar = textContent[currentCharPos + 1];
                nextCharType = getCharacterType(nextChar);
            } else {
                nextCharType = UNDEFINED;
                nextChar = INVALID_CHAR;
                    }

            // check if current character is a letter or number
            if (currentCharType == CH_LETTER || currentCharType == CH_NUMBER) {

                // check if it is the first letter of a token
                if (tokenStart == UNDEFINED) {
                    // start new token here
                    tokenStart = currentCharPos;
                }
            } // check if current character is a whitespace character
            else if (currentCharType == CH_WHITESPACE) {

                // terminate current token
                if (tokenStart != UNDEFINED) {
                    // end of current word
                    createAnnotation(ret, input, tokenType, tokenStart, currentCharPos);
                    tokenStart = UNDEFINED;
                }
            } // check if current character is a special character
            else if (currentCharType == CH_SPECIAL) {

                // terminate current token
                if (tokenStart != UNDEFINED) {
                    // end of current word
                    createAnnotation(ret, input, tokenType, tokenStart, currentCharPos);
                    tokenStart = UNDEFINED;
                }

                // create token for special character
                createAnnotation(ret, input, tokenType, currentCharPos,
                        currentCharPos + 1);
            } // check if current character is new line character
            else if (currentCharType == CH_NEWLINE) {
                // terminate current token
                if (tokenStart != UNDEFINED) {
                    // end of current word
                    createAnnotation(ret, input, tokenType, tokenStart, currentCharPos);
                    tokenStart = UNDEFINED;
                }
            } // check if current character is new punctuation character
            else if (currentCharType == CH_PUNCTUATION) {

                // terminates the current token
                if (tokenStart != UNDEFINED) {
                    createAnnotation(ret, input, tokenType, tokenStart, currentCharPos);
                    tokenStart = UNDEFINED;
                }

                // check next token type so see if we have a sentence end
                if (((nextCharType == CH_WHITESPACE) || (nextCharType == CH_NEWLINE))
                        && (punctuations.contains(new String(
                        new char[]{currentChar})))) {
                    // terminate sentence
                    if (sentences) {
                        createAnnotation(ret, input, sentenceType, sentenceStart,
                                currentCharPos + 1);
                    }
                    sentenceStart = currentCharPos + 1;
                }
                // create token for punctuation character
                createAnnotation(ret, input, tokenType, currentCharPos,
                        currentCharPos + 1);
            }
            // go to the next token
            currentCharPos++;
        } // end of character loop

        // we are at the end of the text terminate open token annotations
        if (tokenStart != UNDEFINED) {
            // end of current word
            createAnnotation(ret, input, tokenType, tokenStart, currentCharPos);
            tokenStart = UNDEFINED;
        }

        // we are at the end of the text terminate open sentence annotations
        if (sentenceStart != UNDEFINED) {
            // end of current word
            if (sentences) {
                createAnnotation(ret, input, sentenceType, sentenceStart, currentCharPos);
            }
            sentenceStart = UNDEFINED;
        }
        return ret;
    }

    /**
     * returns the character type of the given character. Possible character
     * classes are: CH_LETTER for all letters CH_NUMBER for all numbers
     * CH_WHITESPACE for all whitespace characters CH_PUNCTUATUATION for all
     * punctuation characters CH_NEWLINE for all new line characters CH_SPECIAL
     * for all other characters that are not in any of the groups above
     *
     * @param character aCharacter
     *
     * @return returns the character type of the given character
     */
    private static int getCharacterType(char character) {

        switch (Character.getType(character)) {

            // letter characters
            case Character.UPPERCASE_LETTER:
            case Character.LOWERCASE_LETTER:
            case Character.TITLECASE_LETTER:
            case Character.MODIFIER_LETTER:
            case Character.OTHER_LETTER:
            case Character.NON_SPACING_MARK:
            case Character.ENCLOSING_MARK:
            case Character.COMBINING_SPACING_MARK:
            case Character.PRIVATE_USE:
            case Character.SURROGATE:
            case Character.MODIFIER_SYMBOL:
                return CH_LETTER;

            // number characters
            case Character.DECIMAL_DIGIT_NUMBER:
            case Character.LETTER_NUMBER:
            case Character.OTHER_NUMBER:
                return CH_NUMBER;

            // whitespace characters
            case Character.SPACE_SEPARATOR:
                // case Character.CONNECTOR_PUNCTUATION:
                return CH_WHITESPACE;

            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
                return CH_PUNCTUATION;

            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
                return CH_NEWLINE;

            case Character.CONTROL:
                if (character == '\n' || character == '\r') {
                    return CH_NEWLINE;
                } else {
                    // tab is in the char category CONTROL
                    if (Character.isWhitespace(character)) {
                        return CH_WHITESPACE;
                    }
                    return CH_SPECIAL;
                }

            default:
                // the isWhitespace test is slightly more expensive than the above switch,
                // so it is placed here to avoid performance impact.
                // Also, calling code has explicit tests for CH_NEWLINE, and this test should not swallow those
                if (Character.isWhitespace(character)) {
                    return CH_WHITESPACE;
                }
                return CH_SPECIAL;
        }
    }
    

    private static void createAnnotation(List<FeatureStructure> ret, String sofa, String annotationType, int startPos, int endPos) {
        FeatureStructure fs = new FeatureStructure(UUID.randomUUID().toString(), annotationType);
        fs.addFeature(new Feature<Integer>("begin", startPos));
        fs.addFeature(new Feature<Integer>("end", endPos));
        fs.setCoveredText(fs.getSofaChunk(sofa));
        ret.add(fs);

    }
    public static List<FeatureStructure> sentensize(String input) 
    {    	
    //	int event_beg= fs.getFeature("begin").getValueAsInteger();
    	//int event_end= fs.getFeature("end").getValueAsInteger();
    	List<FeatureStructure> ret = new ArrayList<FeatureStructure>();
    	int i=0;
    	BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = input;
        //String surround=new String();
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
            end != BreakIterator.DONE;
            start = end, end = iterator.next()) {
        	
         // System.out.println(source.substring(start,end));
          createSentenceAnnotation(ret, source.substring(start,end), sentenceType,start,end,i+1);
        	
        	i++;
        }
    	return ret;
    }
    
    private static void createSentenceAnnotation(List<FeatureStructure> ret, String sofa, String annotationType, int startPos, int endPos, int number) {
        FeatureStructure fs = new FeatureStructure(UUID.randomUUID().toString(), annotationType);
        fs.addFeature(new Feature<Integer>("begin", startPos));
        fs.addFeature(new Feature<Integer>("end", endPos));
        fs.addFeature(new Feature<Integer>("number", number));
        fs.setCoveredText(sofa);
        ret.add(fs);

    }
    
}