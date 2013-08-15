The classes of these packages are taken from
http://java.freehep.org/ [1]
to add the functionality to export a drawn object to vector and bitmap graphics.
At that time it seemed that the alternative batik is, although well developed, a bit too blown up for the projects needs.
To make the source code work I had to change a few tiny details of the code. Additionally I decided to strip down the classes to (somewhat of) the minimum needed subset.

Peter

p.s. Make sure to include the META-INF/services directory, as well as the file org/freehep/graphicsio/ps/PSProlog.txt into the appropriate jar file, otherwise the graphics export does not function properly.


[1] On their website they state:
"The goal of the FreeHEP library is to encourage the sharing and reuse of Java code in High Energy Physics. Although some of the code is fairly specific to HEP, other code is more generic and could be used by anyone. To maximize reuse we strive to keep the dependencies between various packages in the FreeHEP library to a minimum, so you can use which ever parts interest you without being forced to use the entire library.

The FreeHEP Java library is an "Open Source" library distributed under the terms of the LGPL."


