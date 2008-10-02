OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))


JAVA := javac 

JARFILE := r2cat.jar

.SUFFIXES: .java .class
%.class:%.java
	$(JAVA) $<

all:$(OBJECTS)

jar:all
	jar cvfm $(JARFILE) Manifest.txt `find . -iname '*.class'` images/*.png
clean:
	find . -iname '*.class' -print0 | xargs -0 rm -rvf
startjar: jar
	java -jar $(JARFILE)
