OBJECTS := $(patsubst %.java,%.class,$(shell find . -iname '*.java'))


JAVA := javac 

JARFILE := cav.jar

.SUFFIXES: .java .class
%.class:%.java
	$(JAVA) $<

all:$(OBJECTS)

jar:all
	jar cfm $(JARFILE) Manifest.txt `find . -iname '*.class'` images/
clean:
	find . -iname '*.class' -print0 | xargs -0 rm -rvf
startjar: jar
	java -jar $(JARFILE)
