package it.polimi.ingsw.is25am28.Loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is a generic utility for loading JSON data into a specified Java object.
 * It reads and parses JSON data from an input stream using the provided type.
 *
 * @param <T> The type of the object that the JSON data will be parsed into.
 */
public class Loader<T> {
    private static final ObjectMapper mapper = new ObjectMapper(); // Used to parse the JSON file
    private final T readJSON;

    // Constructor
    public Loader(InputStream stream, Class<T> classType) throws IOException {
        if (stream == null) {
            throw new FileNotFoundException("Resource stream is null");
        }
        // Read and save the parsed json in the given class type
        this.readJSON = mapper.readValue(stream, classType);
    }

    /**
     * Retrieves the parsed JSON data that has been loaded and mapped to an object of type T.
     *
     * @return The parsed JSON object of type T.
     */
    protected T getReadJSON() {
        return this.readJSON;
    }
}
