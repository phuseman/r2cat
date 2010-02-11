OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))


JAVA := javac 

R2CAT_JARFILE := r2cat.jar
CGCAT_JARFILE := cg-cat.jar
TREE_JARFILE := treecat.jar


DATE=`date "+%Y%m%d"`

INSTALLPATHCEBITEC := /vol/bioapps/share/r2cat/
INSTALLPATHBIBISERV := /vol/bibidev/cg-cat/data/cg-cat/

.SUFFIXES: .java .class
%.class:%.java
	$(JAVA) $<

all:$(OBJECTS)

jar:$(R2CAT_JARFILE)

$(R2CAT_JARFILE):all
	jar cvfm $(R2CAT_JARFILE) Manifest_r2cat.txt `find . -iname '*.class'` images/*.png extra/* \
META-INF/services/org.freehep.util.export.ExportFileType 
	jarsigner -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(R2CAT_JARFILE) cgcat

$(TREE_JARFILE):all
	jar cvfm $(TREE_JARFILE) Manifest_treecat.txt `find . -iname '*.class'` images/*.png extra/* \
META-INF/services/org.freehep.util.export.ExportFileType 
	jarsigner -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(TREE_JARFILE) cgcat

$(CGCAT_JARFILE):all
	-rm -vf extra/*~
	jar cvf $(CGCAT_JARFILE) `find . -iname '*.class'` images/*.png extra/* \
META-INF/services/org.freehep.util.export.ExportFileType 
	jarsigner -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(CGCAT_JARFILE) cgcat

zipsources:
	zip -9 sources.zip Manifest_*.txt `find . -iname '*.java'` images/*.png extra/* \
	META-INF/services/org.freehep.util.export.ExportFileType LICENSE.txt ReadmeLicenses.txt



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
