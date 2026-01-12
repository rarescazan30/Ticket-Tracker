package main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Commands.Command;
import main.Commands.CommandFactory;
import main.Database.Database;
import main.Exceptions.StopExecutionException;
import main.PeriodLogic.InteractionManager;
import main.PeriodLogic.TimeManager;
import main.Users.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * main.App represents the main application logic that processes input commands,
 * generates outputs, and writes them to a file
 */
public class App {
    private App() {
    }

    private static final String INPUT_USERS_FIELD = "input/database/users.json";

    private static final ObjectWriter WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();

    /**
     * Runs the application: reads commands from an input file,
     * processes them, generates results, and writes them to an output file
     *
     * @param inputPath path to the input file containing commands
     * @param outputPath path to the file where results should be written
     */
    public static void run(final String inputPath, final String outputPath) {
        // feel free to change this if needed
        // however keep 'outputs' variable name to be used for writing
        Database.reset();
        TimeManager.reset();
        InteractionManager.reset();
        TimeManager.getInstance().addObserver(InteractionManager.getInstance());
        List<ObjectNode> outputs = new ArrayList<>();

        /*
            TODO 1 :
            Load initial user data and commands. we strongly recommend using jackson library.
            you can use the reading from hw1 as a reference.
            however you can use some of the more advanced features of
            jackson library, available here: https://www.baeldung.com/jackson-annotations
        */

        ObjectMapper mapper = new ObjectMapper();
        try {
            File databaseUsers = new File(INPUT_USERS_FIELD);
            // avoid type erasure
            List<User> users = mapper.readValue(databaseUsers, new TypeReference<List<User>>() {});
            Database.getInstance().setUsers(users);
            JsonNode[] commands = mapper.readValue(new File(inputPath), JsonNode[].class);

            for (JsonNode command : commands) {
                // we use the Command Design Pattern combined with a Factory Design Pattern here for code clarity
                Command delegatedCommand = CommandFactory.create(outputs, command);

                if (delegatedCommand != null) {
                    try {
                        delegatedCommand.execute();
                    } catch (StopExecutionException e) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file");
        }


        // TODO 3: create objectnodes for output, add them to outputs list.

        // DO NOT CHANGE THIS SECTION IN ANY WAY
        try {
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            WRITER.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
        } catch (IOException e) {
            System.out.println("error writing to output file: " + e.getMessage());
        }
    }
}
