package datasource.base;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents single parameter of datasource configuration(like filePath, host, port etc.)
 */
@RequiredArgsConstructor
@Getter
@Setter
public class Param {
    private final String name;
    private final JComponent uiComponent;
    private final String labelText;
    private String value;

    @Nullable
    public static Param getParam(List<Param> params, String name) {
        return params.stream()
                .filter(p -> Objects.equals(p.getName(), name))
                .findFirst()
                .orElseThrow(null);
    }
}
