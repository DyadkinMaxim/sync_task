package datasource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatasourceManager {
    public final List<Datasource> datasources = new ArrayList<>();

    public void add(Datasource datasource) {
        if(getByName(datasource.getName()) !=null){
            throw new IllegalArgumentException("Datasource already exists: "+ datasource.getName());
        }
        datasources.add(datasource);
    }

    public List<String> getNames() {
        return datasources.stream()
                .map(Datasource::getName)
                .collect(Collectors.toList());
    }

    public Datasource getByName(String name) {
        return datasources.stream()
                .filter(o -> o.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
