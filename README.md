# scratch_game

# build jar file
mvn package

# run 
java -cp target/lib/\*:target/ScratchGame-1.0.jar  org.sg.Main --config resources/config.json --betting-amount 500