
lab2.jar : IOClass.class Search.class Main.class
	jar -cfm lab2.jar main.mn IOClass.class Search.class Main.class

Main.class : Main.java
	javac Main.java
IOClass.class : IOClass.java
	javac IOClass.java
Search.class : Search.java
	javac Search.java

clean:
	rm -fv lab2.jar *.class
					