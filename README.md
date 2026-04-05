# mergeodt
A Java program which concatenates the content of several .odt files together.

You can create a bash file which runs this program. I'm working on adding that to the repo.

Format:
mergeodt [-pb] [input files] [output file]

The optional flag -pb (position important) means that a page break will be added between the content of every input file.

Make sure to specify an output file, otherwise the last input file will be overwritten.
