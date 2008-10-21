OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))


JAVA := javac 

JARFILE := r2cat.jar

DATE=`date "+%Y%m%d"`

INSTALLPATH := /vol/bioapps/share/r2cat/

.SUFFIXES: .java .class
%.class:%.java
	$(JAVA) $<

all:$(OBJECTS)

jar:$(JARFILE)

$(JARFILE):all
	jar cvfm $(JARFILE) Manifest.txt `find . -iname '*.class'` images/*.png extra/* 
	jarsigner -keystore /homes/phuseman/.gnupg/jarsigner_keystore_r2cat  -storepass phooM1AhInei5Sho $(JARFILE) r2cat
clean:
	find . -iname '*.class' -print0 | xargs -0 rm -rvf
startjar:$(JARFILE)
	java -jar $(JARFILE)

install:$(JARFILE)
	cp -v $(JARFILE) $(INSTALLPATH)/$(DATE)_$(JARFILE)
	chmod 644 $(INSTALLPATH)/$(DATE)_$(JARFILE)
	ln -sfv $(INSTALLPATH)/$(DATE)_$(JARFILE) $(INSTALLPATH)/$(JARFILE)
