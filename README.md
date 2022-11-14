

To run a program in an explicit way add following code after "BatchInputProcessor.run(argv[0]);":
P1.init(argv[0]); //To make a database connection 
P1.MainMenu(); //To run/start the program from MainMenu
//Comment out "BatchInputProcessor.run(argv[0]);", so it does not interrupt
