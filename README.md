# COS341-Part-A
Semester Project

# Textfile Testing
The textfiles you wish to test must be placed in the same directory as the u19141859.jar

# Running the .jar file
To use the program, you need to run the command:
java -jar u19141859.jar

# Program Usage
Once you have run the command mentioned above, there will be a prompt for the textfile name.
You must type only the name of the textfile, not the extension.
## Example: 
If your file is "test.txt".
Then in the prompt, "Enter the file name: "
Only the input of "test" is required.

# Post file name Entering
Once the file name has been inputted, the program will in order of these points, check if it is:
- Lexically Correct
- Syntax Correct

If it not Lexically Correct then a message will displayed in the terminal and a description of the error.
If it is Lexically Correct but is an invalid Syntax then a message will be displayed in the terminal along with a description of the error.
If both of these tests pass then the program will create an xml file with the same name as the inital textfile name.

## Example:
If the textfile name was "test.txt" then the outputted file will be "test.xml".

# Part B Update:
This program will no longer produce textfiles.

## New Functionality
The program will output semantic errors if the semantic rules have been violated.
The program will print a syntax tree with the all the nodes (with their nodeID and scopeID).
A hierachy of the scopeIDs will also be displayed.
Lastly the Procedure and Variable symbol table will be displayed if there are no procedure or variable rule violations.