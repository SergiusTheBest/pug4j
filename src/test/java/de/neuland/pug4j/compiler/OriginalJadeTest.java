package de.neuland.pug4j.compiler;

import de.neuland.pug4j.Pug4J;
import de.neuland.pug4j.PugConfiguration;
import de.neuland.pug4j.TestFileHelper;
import de.neuland.pug4j.filter.CDATAFilter;
import de.neuland.pug4j.filter.PlainFilter;
import de.neuland.pug4j.template.FileTemplateLoader;
import de.neuland.pug4j.template.PugTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OriginalJadeTest {
    private static String[] ignoredCases = new String[]{"attrs", "attrs.js", "code.conditionals", "code.iteration", "comments",
            "escape-chars", "filters.coffeescript", "filters.less", "filters.markdown", "filters.stylus", "html", "include-only-text-body",
            "include-only-text", "include-with-text-head", "include-with-text", "mixin.blocks", "mixin.merge", "quotes", "script.whitespace", "scripts", "scripts.non-js",
            "source", "styles", "template", "text-block", "text", "vars", "yield-title", "doctype.default","comments.conditional","html5"};

    private File file;

    public OriginalJadeTest(String file) throws FileNotFoundException, URISyntaxException {
        this.file = new File(TestFileHelper.getOriginalResourcePath(file));
    }

    @Test
    public void shouldCompileJadeToHtml() throws Exception {
        PugConfiguration jade = new PugConfiguration();
        jade.setTemplateLoader(new FileTemplateLoader("", "jade"));
        jade.setMode(Pug4J.Mode.XHTML); // original jade uses xhtml by default
        jade.setFilter("plain", new PlainFilter());
        jade.setFilter("cdata", new CDATAFilter());

        PugTemplate template = jade.getTemplate(file.getPath());
        Writer writer = new StringWriter();
        jade.renderTemplate(template, new HashMap<String, Object>(), writer);
        String html = writer.toString();

        String expected = readFile(file.getPath().replace(".jade", ".html")).trim().replaceAll("\r", "");

        assertEquals(file.getName(), expected, html.trim().replaceAll("\r", ""));
    }

    private String readFile(String fileName) throws IOException {
        return FileUtils.readFileToString(new File(fileName),"UTF-8");
    }

    @Parameterized.Parameters(name="{0}")
    public static Collection<String[]> data() throws FileNotFoundException, URISyntaxException {
        File folder = new File(TestFileHelper.getOriginalResourcePath(""));
        Collection<File> files = FileUtils.listFiles(folder, new String[]{"jade"}, false);

        Collection<String[]> data = new ArrayList<String[]>();
        for (File file : files) {
            if (!ArrayUtils.contains(ignoredCases, file.getName().replace(".jade", ""))) {
                data.add(new String[]{file.getName()});
            }

        }
        return data;
    }
}
