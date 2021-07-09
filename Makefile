DESTDIR=dist

.PHONY: compile jar clean

default: jar

jar:
	ant jar

clean:
	rm -fr build dist out

