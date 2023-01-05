package datasource;

import java.awt.Component;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Param {
    private final String name;
    private String value;
    //private Component uiComponent;

    public static Param getParam(List<Param> params, String name) throws IOException {
        return params.stream()
                .filter(p -> Objects.equals(p.getName(), name))
                .findFirst()
                .orElseThrow(() -> new IOException("No param found with name: " + name));
    }
}
