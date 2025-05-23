package it.polimi.ingsw.is25am28.Loader;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Loader<T> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final T readJSON;

    public Loader(InputStream stream, Class<T> classType) throws IOException {
        if (stream == null) {
            throw new FileNotFoundException("Resource stream is null");
        }
        this.readJSON = mapper.readValue(stream, classType);
    }

    protected T getReadJSON() {
        return this.readJSON;
    }
}
