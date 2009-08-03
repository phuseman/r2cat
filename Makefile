OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))


JAVA := javac 

R2CAT_JARFILE := r2cat.jar
CGCAT_JARFILE := cgcat.jar
TREE_JARFILE := treecat.jar


DATE=`date "+%Y%m%d"`

INSTALLPATH := /vol/bioapps/share/r2cat/

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
	jar cvf $(CGCAT_JARFILE) `find . -iname '*.class'` images/*.png extra/* \
META-INF/services/org.freehep.util.export.ExportFileType 
	jarsigner -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(CGCAT_JARFILE) cgcat




clean:
	find . -iname '*.class' -print0 | xargs -0 rm -rvf
startjar:$(R2CAT_JARFILE)
	java -jar $(R2CAT_JARFILE)

install:$(R2CAT_JARFILE)
	cp -v $(R2CAT_JARFILE) $(INSTALLPATH)/$(DATE)_$(R2CAT_JARFILE)
	chmod 644 $(INSTALLPATH)/$(DATE)_$(R2CAT_JARFILE)
	ln -sfv $(INSTALLPATH)/$(DATE)_$(R2CAT_JARFILE) $(INSTALLPATH)/$(R2CAT_JARFILE)

install_techfak:$(R2CAT_JARFILE)
	scp $(R2CAT_JARFILE) techfak:/vol/bibidev/r2cat/data/r2cat/
