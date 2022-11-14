# Compile all the *.java files  (e.g. javac *.java)
# Copy p1_create.sql to your container directory: docker cp ./p1_create.sql 92a7b62a44b1:./database/config/db2inst1/.
# Execute p1_create.sql inside the Db2 container with the following command inside the docker: db2 -tvf p1_create.sql
# To compile the program with an file argument: java -cp ":./db2jcc4.jar" ProgramLauncher ./db.properties
# To run a program in an explicit way add a following code after "BatchInputProcessor.run(argv[0]);":

P1.init(argv[0]); //To make a database connection 
P1.MainMenu(); //To run/start the program from MainMenu

# Comment out "BatchInputProcessor.run(argv[0]);", so it does not interrupt
