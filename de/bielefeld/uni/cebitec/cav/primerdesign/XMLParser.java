package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

/**
 * This class reports what kind of tag was met in the XML-file in order to store the
 * information of the XML-file in the same structure.
 * 
 *
 */
public class XMLParser {
	class TagType{
		private static final int
				TEXT = 1,
				CDATA= 2,
				OPEN_TAG = 3,
				CLOSE_TAG = 4,
				START_TAG = 5,
				ATTRIBUTE_LEFTVALUE = 6,
				ATTRIBUTE_EQUAL = 9,
				ATTRIBUTE_RIGHTVALUE = 10,
				QUOTE = 7,
				IN_TAG = 8,
				SINGLE_TAG = 12,
				COMMENT = 13,
				DONE = 11,
				DOCTYPE = 14,
				PRE = 15;
	}
	public boolean scanXML(File file) throws IOException{
		boolean isXML = false;
		FileReader fileReader = new FileReader(file);
		 int character = 0;
		 int countClosing = 0;
		 int countOpening = 0;
		while(( character = fileReader.read()) != -1) {
			 if(character=='<'){
				 countClosing++;
			 }
			 if(character=='>'){
				 countOpening++;
			 }
		 }
		if(countOpening == countClosing){
			isXML = true;
			return isXML;
		}else{
			return isXML;
		}
	}
	
/**
 * This method goes through the stack containing integers representing the xml tags.
 * @param stack
 * @return Interger which represents type of xml tag
 */
	private int popMode(Stack<Integer> stack){
		if(!stack.empty()){
			return (stack.pop()).intValue();
		} else{
			return TagType.PRE;
		}
	}
	
	/**
	 * This method gets a document handler and a fileReader which contains a XML-file and saves the xml tags types represented
	 * by different Integers in a stack. Then according to this stack the method of the document handler are
	 * called to parse the xml file.
	 * 
	 * @param documentHandler
	 * @param filereader
	 * @throws Exception
	 */
	
	  public void parse(DocumentHandler documentHandler,File file) throws Exception {
		    Stack<Integer> stack = new Stack<Integer>();
		    StringBuffer stringBuffer = new StringBuffer();
		    FileReader filereader = new FileReader(file);
		    int depth = 0;
		    int mode = TagType.PRE;
		    int character = 0;
		    int quotec = '"';
		    String tagName = null;
		    String leftValue = null;
		    String rightValue = null;
		    Hashtable hash = null;
		    documentHandler.startDocument();
		    
		    while((character = filereader.read()) != -1) {
		      if(mode == TagType.DONE) {
			documentHandler.endDocument();
		        return;
		        
		      } else if(mode == TagType.TEXT) {
		        if(character == '<') {
			  stack.push(new Integer(mode));
			  mode = TagType.START_TAG;
			  if(stringBuffer.length() > 0) {
			    documentHandler.value(stringBuffer.toString());
			    stringBuffer.setLength(0);
			  }
		        }else
			  stringBuffer.append((char)character);

		      } else if(mode == TagType.CLOSE_TAG) {
		        if(character == '>') {
			  mode = popMode(stack);
			  tagName = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  depth--;
			  if(depth==0)
			    mode = TagType.DONE;
			  documentHandler.endElement(tagName);
			} else {
			  stringBuffer.append((char)character);
			}
		      }

 			else if(mode == TagType.COMMENT) {
		        if(character == '>'
			&& stringBuffer.toString().endsWith("--")) {
			  stringBuffer.setLength(0);
			  mode = popMode(stack);
			} else
			  stringBuffer.append((char)character);

		      }else if(mode == TagType.PRE) {
			if(character == '<') {
				mode = TagType.TEXT;
			  stack.push(new Integer(mode));
			  mode = TagType.START_TAG;
		        }

		      } else if(mode == TagType.DOCTYPE) {
			if(character == '>') {
			  mode = popMode(stack);
			  if(mode == TagType.TEXT){
				  mode = TagType.PRE;
			  }
			}

		      } else if(mode == TagType.START_TAG) {
		        mode = popMode(stack);
			if(character == '/') {
			  stack.push(new Integer(mode));
			  mode = TagType.CLOSE_TAG;
			} else if (character == '?') {
			  mode = TagType.DOCTYPE;
		        } else {
			  stack.push(new Integer(mode));
			  mode = TagType.OPEN_TAG;
			  tagName = null;
			  hash = new Hashtable();
			  stringBuffer.append((char)character);
		        }
		      }
		      else if(mode == TagType.SINGLE_TAG) {
			if(tagName == null)
			  tagName = stringBuffer.toString();
		        if(character != '>')
			documentHandler.startElement(tagName,hash);
			documentHandler.endElement(tagName);
			if(depth==0) {
			  documentHandler.endDocument();
			  return;
			}
			stringBuffer.setLength(0);
			hash = new Hashtable();
			tagName = null;
			mode = popMode(stack);
		      } else if(mode == TagType.OPEN_TAG) {
			if(character == '>') {
			  if(tagName == null)
		            tagName = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  depth++;
			  documentHandler.startElement(tagName,hash);
			  tagName = null;
			  hash = new Hashtable();
			  mode = popMode(stack);
			} else if(character == '/') {
			  mode = TagType.SINGLE_TAG;
			} else if(character == '-' && stringBuffer.toString().equals("!-")) {
			  mode = TagType.COMMENT;
			} else if(character == '[' && stringBuffer.toString().equals("![CDATA")) {
			  mode = TagType.CDATA;
			  stringBuffer.setLength(0);
			} else if(character == 'E' && stringBuffer.toString().equals("!DOCTYP")) {
			  stringBuffer.setLength(0);
			  mode = TagType.DOCTYPE;
			} else if(Character.isWhitespace((char)character)) {
			  tagName = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  mode = TagType.IN_TAG;
			} else {
			  stringBuffer.append((char)character);
			}
		      } else if(mode == TagType.QUOTE) {
		        if(character == quotec) {
			  rightValue = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  hash.put(leftValue,rightValue);
			  mode = TagType.IN_TAG;
			} else if(" \r\n\u0009".indexOf(character)>=0) {
			  stringBuffer.append(' ');
			} else {
				  stringBuffer.append((char)character);
				}
			      } else if(mode == TagType.ATTRIBUTE_RIGHTVALUE) {
		        if(character == '"' || character == '\'') {
			  quotec = character;
			  mode = TagType.QUOTE;
			}
		      } else if(mode == TagType.ATTRIBUTE_LEFTVALUE) {
		        if(Character.isWhitespace((char)character)) {
			  leftValue = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  mode = TagType.ATTRIBUTE_EQUAL;
			} else if(character == '=') {
			  leftValue = stringBuffer.toString();
			  stringBuffer.setLength(0);
			  mode = TagType.ATTRIBUTE_RIGHTVALUE;
			} else {
			  stringBuffer.append((char)character);
			}
		      } else if(mode ==TagType.ATTRIBUTE_EQUAL) {
		        if(character == '=') {
			  mode = TagType.ATTRIBUTE_RIGHTVALUE;
			}
		      } else if(mode == TagType.IN_TAG) {
			if(character == '>') {
			  mode = popMode(stack);
			  documentHandler.startElement(tagName,hash);
			  depth++;
			  tagName = null;
			  hash = new Hashtable();
			} else if(character == '/') {
			  mode = TagType.SINGLE_TAG;
		        } else {
			  mode = TagType.ATTRIBUTE_LEFTVALUE;
			  stringBuffer.append((char)character);
			}
		      }
		    }
		    if(mode == TagType.DONE)
		      documentHandler.endDocument();
		  }
}
