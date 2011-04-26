OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))

COMMON := `find de/bielefeld/uni/cebitec/common/ -iname '*.class'`
CONTIGADJACENCYGRAPH := `find de/bielefeld/uni/cebitec/contigadjacencygraph/ -iname '*.class'`
PRIMERDESIGN := `find de/bielefeld/uni/cebitec/primerdesign/ -iname '*.class'`
QGRAM := `find de/bielefeld/uni/cebitec/qgram/ -iname '*.class'`
R2CAT := `find de/bielefeld/uni/cebitec/r2cat/ -iname '*.class' -o -iname '*.png' -o -iname '*.html'`
TREECAT := `find de/bielefeld/uni/cebitec/treecat/ -iname '*.class'`
FREEHEP := `find org/freehep/ -iname '*.class'` META-INF/services/org.freehep.util.export.ExportFileType org/freehep/graphicsio/ps/PSProlog.txt
FREEHEP_SOURCES := `find org/freehep/ -iname '*.java'`

JAVAC := javac 

R2CAT_JARFILE := r2cat.jar
CGCAT_JARFILE := cg-cat.jar
TREE_JARFILE := treecat.jar
FREEHEP_JARFILE := freehepGraphicsExport.jar


DATE=`date "+%Y%m%d"`

INSTALLPATHCEBITEC := /vol/bioapps/share/r2cat/
INSTALLPATHBIBISERV := /vol/bibidev/cg-cat/data/cg-cat/

.SUFFIXES: .java .class
%.class:%.java
	$(JAVAC) $<

all:$(OBJECTS)

jar:$(R2CAT_JARFILE)

$(R2CAT_JARFILE):all
	jar cvfm $(R2CAT_JARFILE) Manifest_r2cat.txt $(R2CAT) $(COMMON) $(QGRAM) $(PRIMERDESIGN) $(FREEHEP)
# 	jarsigner -tsa https://timestamp.geotrust.com/tsa -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(R2CAT_JARFILE) cgcat
# I changed the storepass after migrating to sourceforge :)

$(TREE_JARFILE):all
	jar cvfm $(TREE_JARFILE) Manifest_treecat.txt $(TREECAT) $(COMMON) $(QGRAM) $(CONTIGADJACENCYGRAPH)
# 	jarsigner -tsa https://timestamp.geotrust.com/tsa -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(TREE_JARFILE) cgcat
# I changed the storepass after migrating to sourceforge :)

$(CGCAT_JARFILE):all
	jar cvf $(CGCAT_JARFILE) $(COMMON) $(CONTIGADJACENCYGRAPH) $(PRIMERDESIGN) $(QGRAM) $(R2CAT) $(TREECAT) $(FREEHEP)
	jarsigner -tsa https://timestamp.geotrust.com/tsa -keystore /homes/phuseman/.gnupg/jarsigner_keystore_cg-cat  $(CGCAT_JARFILE) cgcat
# I changed the storepass after migrating to sourceforge :)

$(FREEHEP_JARFILE):all
	jar cvf $(FREEHEP_JARFILE) $(FREEHEP) $(FREEHEP_SOURCES)

clean:
	find . -iname '*.class' -print0 | xargs -0 rm -rvf

startjar:$(R2CAT_JARFILE)
	java -jar $(R2CAT_JARFILE)

install_cebitec:$(R2CAT_JARFILE)
	cp -v $(R2CAT_JARFILE) $(INSTALLPATHCEBITEC)/$(DATE)_$(R2CAT_JARFILE)
	chmod 644 $(INSTALLPATHCEBITEC)/$(DATE)_$(R2CAT_JARFILE)
	ln -sfv $(INSTALLPATHCEBITEC)/$(DATE)_$(R2CAT_JARFILE) $(INSTALLPATHCEBITEC)/$(R2CAT_JARFILE)

install_bibiserv:$(CGCAT_JARFILE)
	cp -v $(CGCAT_JARFILE) $(INSTALLPATHBIBISERV)/$(DATE)_$(CGCAT_JARFILE)
	chmod 644 $(INSTALLPATHBIBISERV)/$(DATE)_$(CGCAT_JARFILE)
	ln -sfv $(INSTALLPATHBIBISERV)/$(DATE)_$(CGCAT_JARFILE) $(INSTALLPATHBIBISERV)/$(CGCAT_JARFILE)
