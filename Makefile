all: compile run

compile:
	javac src/*.java

run:
	java -cp src UnicornGUI -config src/config
