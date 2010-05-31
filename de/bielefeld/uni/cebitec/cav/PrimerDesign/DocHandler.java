package de.bielefeld.uni.cebitec.cav.PrimerDesign;

import java.util.*;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public interface DocHandler {
	/**
	 * method is called when a element of XML starts
	 *
	 * @param tag
	 * @param attrs 
	 * @throws Exception
	 */
	
  public void startElement(String tag,Hashtable attrs) throws Exception;
  
  /**
   * method is called when a element of XML ends
   * @param tag
   * @throws Exception
   */
  
  public void endElement(String tag) throws Exception;
  
  /**
   * method is called when XML-file starts.
   * @throws Exception
   */
  
  public void startDocument() throws Exception;
  
  /**
   * method is called when XML-file ends.
   * @throws Exception
   */
  
  public void endDocument() throws Exception;
  
  /**
   * method is called if there is a value between the XML-tags
   * @param str
   * @throws Exception
   */
  
  public void score(String str) throws Exception;
}
