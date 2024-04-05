package ru.logonik.generators;

import org.bukkit.ChatColor;
import org.junit.Test;
import ru.logonik.generators.langcode.Langs;
import ru.logonik.generators.langcode.LineYmlModel;
import ru.logonik.generators.langcode.SectionModel;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LangCodecs {
    public static Langs ru_RU;
    private static final ArrayList<LineYmlModel> codecs;
    private static SectionModel root;

    static {
        ru_RU = Langs.ru_RU;
        codecs = new ArrayList<>();
    }

    @Test
    public void generate() throws Throwable {
        main(new String[0]);
    }

    public static void main(String[] args) throws Throwable {
        File langCodeFile = new File("src/main/java/ru/logonik/unrealminecraft/models/LangCode.java");
        File languagesResourceFolder = new File("src/main/resources/languages");
        fillLangCode();

        String generate = generateLangCode();
        FileOutputStream LangCodeOutputStream = new FileOutputStream(langCodeFile);
        LangCodeOutputStream.write(generate.getBytes(StandardCharsets.UTF_8));
        LangCodeOutputStream.close();

        generate = generateYml(ru_RU);
        FileOutputStream ymlRUOutputStream = new FileOutputStream((new File(languagesResourceFolder, "messages.yml")).getAbsolutePath());
        ymlRUOutputStream.write(generate.getBytes(StandardCharsets.UTF_8));
        ymlRUOutputStream.close();
    }

    private static void fillLangCode() {
        root = new SectionModel("");
        SectionModel exceptionsSection = new SectionModel("exception");
        root.addInner(exceptionsSection);
        SectionModel commandsInfo = new SectionModel("commands_info");
        root.addInner(commandsInfo);
        SectionModel resultSection = new SectionModel("result");
        root.addInner(resultSection);
        SectionModel utilSection = new SectionModel("util");
        root.addInner(utilSection);

        codecs.add(LineYmlModel.newBuilder()
                .setSection(exceptionsSection)
                .setHeader("UNKNOWN_ERROR")
                .setValue(ru_RU, ChatColor.RED + "Неизвестная ошибка!")
                .build());

        codecs.add(LineYmlModel.newBuilder()
                .setSection(commandsInfo)
                .setHeader("INFO_COMMAND_ARENA")
                .setValue(ru_RU, ChatColor.BLUE + "Команда позволяет манипулировать аренами!")
                .build());

        codecs.add(LineYmlModel.newBuilder()
                .setSection(resultSection)
                .setHeader("SUCCESS")
                .setValue(ru_RU, ChatColor.GREEN + "Успех!")
                .build());
    }

    private static String generateLangCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ru.logonik.unrealminecraft.models;\n\n// <-- Generated code--->\npublic enum LangCode {\n");

        for (int i = 0; i < codecs.size(); ++i) {
            LineYmlModel codec = codecs.get(i);
            if (codec.getSection() == root) {
                builder.append("\t").append(codec.getHeader()).append("(\"").append(codec.getPath()).append("\")");
            } else {
                builder.append("\t").append(codec.getHeader())
                        .append("(\"").append(codec.getSection().getPath()).append(".").append(codec.getPath()).append("\")");
            }

            if (codecs.size() - 1 == i) {
                builder.append(";\n");
            } else {
                builder.append(",\n");
            }
        }

        builder.append("\n    private final String value;\n\n    " +
                "LangCode(String code) {\n        " +
                "value = code;\n    }\n\n    " +
                "public String getValue() {\n        " +
                "return value;\n    " +
                "}\n}");
        return builder.toString();
    }

    private static String generateYml(Langs lang) {
        StringBuilder builder = new StringBuilder();
        if (root.getList().size() != 0) {

            for (LineYmlModel line : root.getList()) {
                builder.append(line.getPath()).append(": '").append(line.getValue(lang)).append("'\n");
            }
        }

        ArrayList<SectionModel> inners = null;

        for (SectionModel section : root.getInners()) {
            String tabs = "";
            byte i = 0;

            while (true) {
                builder.append(tabs).append(section.getPath()).append(":\n");

                for (LineYmlModel line : section.getList()) {
                    builder.append(tabs).append("    ").append(line.getPath()).append(": \"").append(line.getValue(lang)).append("\"\n");
                }

                if (inners != null) {
                    if (inners.size() - 1 == i) {
                        inners = section.getInners();
                        i = 0;
                    }
                } else {
                    inners = section.getInners();
                }

                if (inners.size() == 0) {
                    break;
                }

                tabs = tabs + "    ";
                section = inners.get(i);
            }
        }

        return builder.toString().replace("§", "&");
    }
}
