package org.freehep.graphicsio.pdf;

import java.io.IOException;
import java.util.Vector;

/**
 * Implements the Page Tree Node (see Table 3.16).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFPageTree.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PDFPageTree extends PDFPageBase {

    Vector pages = new Vector();

    PDFPageTree(PDF pdf, PDFByteWriter writer, PDFObject object, PDFRef parent)
            throws IOException {
        super(pdf, writer, object, parent);
        entry("Type", pdf.name("Pages"));
    }

    public void addPage(String name) {
        pages.add(pdf.ref(name));
    }

    void close() throws IOException {
        Object[] kids = new Object[pages.size()];
        pages.copyInto(kids);
        entry("Kids", kids);
        entry("Count", kids.length);
        super.close();
    }
}
