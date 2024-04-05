package ru.logonik.generators.langcode;

import java.util.HashMap;

public class LineYmlModel {
    private String header;
    private String path;
    private final HashMap<Langs, String> value = new HashMap<>();
    private SectionModel section;

    public String getHeader() {
        return this.header;
    }

    public String getPath() {
        return this.path;
    }

    public SectionModel getSection() {
        return this.section;
    }

    public String getValue(Langs key) {
        return this.value.getOrDefault(key, "# TODO");
    }

    public static Builder newBuilder() {
        return new LineYmlModel().new Builder();
    }

    public class Builder {
        private Builder() {
        }

        public Builder setHeader(String header) {
            LineYmlModel.this.header = header;
            return this;
        }

        public Builder setPath(String path) {
            LineYmlModel.this.path = path;
            return this;
        }

        public Builder setValue(Langs code, String value) {
            LineYmlModel.this.value.put(code, value);
            return this;
        }

        public Builder setSection(SectionModel section) {
            section.addLine(LineYmlModel.this);
            LineYmlModel.this.section = section;
            return this;
        }

        public LineYmlModel build() {
            if (header == null || header.equals("")) {
                throw new IllegalArgumentException();
            }
            if (path == null || path.equals("")) {
                path = header.toLowerCase();
            }
            return LineYmlModel.this;
        }
    }
}
