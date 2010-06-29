package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.io.FileReader;
import java.util.Stack;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

/**
 * This class reports what kind of tag was met in the XML-file in order to store the
 * information of the XML-file in the same structure.
 * 
 * @author yherrmann
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
	
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private int popMode(Stack<Integer> s){
		if(!s.empty()){
			return (s.pop()).intValue();
		} else{
			return TagType.PRE;
		}
	}
	
	/**
	 * method gets a fileReader which contains a XML-file and parses the file.
	 * @param doc
	 * @param r
	 * @throws Exception
	 */
	
	  public void parse(DocHandler doc,FileReader r) throws Exception {
		    Stack<Integer> st = new Stack<Integer>();
		    StringBuffer sb = new StringBuffer();
		   // StringBuffer etag = new StringBuffer();
		    int depth = 0;
		    int mode = TagType.PRE;
		    int character = 0;
		    int quotec = '"';
		    String tagName = null;
		    String leftValue = null;
		    String rightValue = null;
		    Hashtable hash = null;
		    doc.startDocument();
		    
		    while((character = r.read()) != -1) {
		      if(mode == TagType.DONE) {
			doc.endDocument();
		        return;
		        
		      } else if(mode == TagType.TEXT) {
		        if(character == '<') {
			  st.push(new Integer(mode));
			  mode = TagType.START_TAG;
			  if(sb.length() > 0) {
			    doc.value(sb.toString());
			    sb.setLength(0);
			  }
		        }else
			  sb.append((char)character);

		      } else if(mode == TagType.CLOSE_TAG) {
		        if(character == '>') {
			  mode = popMode(st);
			  tagName = sb.toString();
			  sb.setLength(0);
			  depth--;
			  if(depth==0)
			    mode = TagType.DONE;
			  doc.endElement(tagName);
			} else {
			  sb.append((char)character);
			}
		      }

 			else if(mode == TagType.COMMENT) {
		        if(character == '>'
			&& sb.toString().endsWith("--")) {
			  sb.setLength(0);
			  mode = popMode(st);
			} else
			  sb.append((char)character);

		      }else if(mode == TagType.PRE) {
			if(character == '<') {
				mode = TagType.TEXT;
			  st.push(new Integer(mode));
			  mode = TagType.START_TAG;
		        }

		      } else if(mode == TagType.DOCTYPE) {
			if(character == '>') {
			  mode = popMode(st);
			  if(mode == TagType.TEXT){
				  mode = TagType.PRE;
			  }
			}

		      } else if(mode == TagType.START_TAG) {
		        mode = popMode(st);
			if(character == '/') {
			  st.push(new Integer(mode));
			  mode = TagType.CLOSE_TAG;
			} else if (character == '?') {
			  mode = TagType.DOCTYPE;
		        } else {
			  st.push(new Integer(mode));
			  mode = TagType.OPEN_TAG;
			  tagName = null;
			  hash = new Hashtable();
			  sb.append((char)character);
		        }
		      }
		      else if(mode == TagType.SINGLE_TAG) {
			if(tagName == null)
			  tagName = sb.toString();
		        if(character != '>')
			doc.startElement(tagName,hash);
			doc.endElement(tagName);
			if(depth==0) {
			  doc.endDocument();
			  return;
			}
			sb.setLength(0);
			hash = new Hashtable();
			tagName = null;
			mode = popMode(st);
		      } else if(mode == TagType.OPEN_TAG) {
			if(character == '>') {
			  if(tagName == null)
		            tagName = sb.toString();
			  sb.setLength(0);
			  depth++;
			  doc.startElement(tagName,hash);
			  tagName = null;
			  hash = new Hashtable();
			  mode = popMode(st);
			} else if(character == '/') {
			  mode = TagType.SINGLE_TAG;
			} else if(character == '-' && sb.toString().equals("!-")) {
			  mode = TagType.COMMENT;
			} else if(character == '[' && sb.toString().equals("![CDATA")) {
			  mode = TagType.CDATA;
			  sb.setLength(0);
			} else if(character == 'E' && sb.toString().equals("!DOCTYP")) {
			  sb.setLength(0);
			  mode = TagType.DOCTYPE;
			} else if(Character.isWhitespace((char)character)) {
			  tagName = sb.toString();
			  sb.setLength(0);
			  mode = TagType.IN_TAG;
			} else {
			  sb.append((char)character);
			}
		      } else if(mode == TagType.QUOTE) {
		        if(character == quotec) {
			  rightValue = sb.toString();
			  sb.setLength(0);
			  hash.put(leftValue,rightValue);
			  mode = TagType.IN_TAG;
			} else if(" \r\n\u0009".indexOf(character)>=0) {
			  sb.append(' ');
			} else {
				  sb.append((char)character);
				}
			      } else if(mode == TagType.ATTRIBUTE_RIGHTVALUE) {
		        if(character == '"' || character == '\'') {
			  quotec = character;
			  mode = TagType.QUOTE;
			}
		      } else if(mode == TagType.ATTRIBUTE_LEFTVALUE) {
		        if(Character.isWhitespace((char)character)) {
			  leftValue = sb.toString();
			  sb.setLength(0);
			  mode = TagType.ATTRIBUTE_EQUAL;
			} else if(character == '=') {
			  leftValue = sb.toString();
			  sb.setLength(0);
			  mode = TagType.ATTRIBUTE_RIGHTVALUE;
			} else {
			  sb.append((char)character);
			}
		      } else if(mode ==TagType.ATTRIBUTE_EQUAL) {
		        if(character == '=') {
			  mode = TagType.ATTRIBUTE_RIGHTVALUE;
			}
		      } else if(mode == TagType.IN_TAG) {
			if(character == '>') {
			  mode = popMode(st);
			  doc.startElement(tagName,hash);
			  depth++;
			  tagName = null;
			  hash = new Hashtable();
			} else if(character == '/') {
			  mode = TagType.SINGLE_TAG;
		        } else {
			  mode = TagType.ATTRIBUTE_LEFTVALUE;
			  sb.append((char)character);
			}
		      }
		    }
		    if(mode == TagType.DONE)
		      doc.endDocument();
		  }
}
