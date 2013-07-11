This folder contains scripts that need to be run in order to apply changes to 
an existing installation.

The changes should also be included in the main scripts so a fresh installation
DO NOT need to apply files in this folder.

Scripts should content:

1. Alter table sentences to include/delete columns
2. If the New column is not null, it should be created with a default value and
a script to assign the correct values to existing rows should be placed here.  