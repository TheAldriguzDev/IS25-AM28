package it.polimi.ingsw.is25am28.FileLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class FileLoader {
      private final Map<String,List<Map<String,Object>>> json;

      protected Map<String,List<Map<String,Object>>> getJSONObject(){
            return json;     
      }

      /**
       *  file path must be relative to "~/IS25-AM28"
       * @param fileName
       */
      public FileLoader( String fileName ){
            try {
                  FileReader file = new FileReader(fileName);
                  ObjectMapper mapper = new ObjectMapper();

                  json = mapper.readValue( file, Map.class );
            }
            catch(FileNotFoundException e){
                  throw new Error("file not found with error: " + e.getMessage());
            }
            catch(IOException e){
                  throw new Error("file not found with error: " + e.getMessage());
            }
      }
}