package it.polimi.ingsw.is25am28.Loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Loader<T> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final T readJSON;

    public Loader(String path, Class<T> className) throws IOException {
        this.readJSON = mapper.readValue(new File(path), className);
    }

    protected T getReadJSON() {
        return this.readJSON;
    }
}
