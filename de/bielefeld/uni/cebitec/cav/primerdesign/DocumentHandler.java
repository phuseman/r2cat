package de.bielefeld.uni.cebitec.cav.primerdesign;

import java.util.Hashtable;

/**
 * Interface to handle the tags while going through a xml-file.
 * 
 * @author yherrman
 *
 */

public interface DocumentHandler {
	
	/**
	 * Method is called when a element of XML starts
	 *
	 * @param tag represents the xml-tag
	 * @param attrs includes the expression infront of '=' as a key and 
	 * the following expression as attributes
	 * @throws Exception
	 */
	
  public void startElement(String tag,Hashtable attrs) throws Exception;
  
  /**
   * Method is called when a element of XML ends
   * @param tag
   * @throws Exception
   */
  
  public void endElement(String tag) throws Exception;
  
  /**
   * Method is called when XML-file starts.
   * @throws Exception
   */
  
  public void startDocument() throws Exception;
  
  /**
   * Method is called when XML-file ends.
   * @throws Exception
   */
  
  public void endDocument() throws Exception;
  
  /**
   * Method is called if there is a value between the XML-tags
   * @param str
   * @throws Exception
   */
  
  public void value(String str) throws Exception;
}
