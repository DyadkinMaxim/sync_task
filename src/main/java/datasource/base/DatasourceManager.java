package datasource.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


/**
 * Abstract factory for operating datasource types
 */
@Slf4j
public class DatasourceManager implements Cloneable {
    public final List<Datasource> datasources = new ArrayList<>();

    public void add(Datasource datasource) {
        if(getByName(datasource.getName()) !=null){
            throw new IllegalArgumentException("Datasource already exists: "+ datasource.getName());
        }
        datasources.add(datasource);
        log.debug(datasource.getName() + " added");
    }

    public List<String> getNames() {
        return datasources.stream()
                .map(Datasource::getName)
                .collect(Collectors.toList());
    }

    public Datasource getByName(String name) {
        var foundDS =  datasources.stream()
                .filter(o -> o.getName().equals(name))
                .findFirst()
                .orElse(null);
        return foundDS != null ? foundDS.copy() : null;
    }
}
