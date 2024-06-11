package de.neuland.pug4j.template;

import de.neuland.pug4j.Pug4J.Mode;
import de.neuland.pug4j.compiler.Compiler;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.expression.ExpressionHandler;
import de.neuland.pug4j.lexer.token.Doctypes;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.parser.node.Node;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;

public class PugTemplate {

	private boolean prettyPrint = false;
	private Node rootNode;
	private boolean terse = false;
	private boolean xml = false;
	private TemplateLoader templateLoader;
	private ExpressionHandler expressionHandler;
	private String doctypeLine;

	public void process(PugModel model, Writer writer) throws PugCompilerException {
                //
                // Create a new `template` using data from this instance.
                // We need it to workaround the multithreading corruption of `doctypeLine`
                // as it's modified during `compiler.compile()` call and it can happen simultaneously
                // for mutltiple threads rendering the same template.
                //
                // The fix looks somewhat ugly but it fixes the issue and shouldn't impact performance.
                // The proper fix requires bigger changes to the whole template-compiler interaction, 
                // preferable using a contructor for members initialization and making all members final.
                
                PugTemplate template = new PugTemplate();
                template.setTemplateLoader(templateLoader);
                template.setExpressionHandler(expressionHandler);
                template.setPrettyPrint(prettyPrint);
                template.setRootNode(rootNode);
                template.terse = terse;
                template.xml = xml;
                
		Compiler compiler = new Compiler(rootNode);
		compiler.setPrettyPrint(prettyPrint);
		compiler.setTemplate(template);
		compiler.compile(model, writer);
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}

	public boolean isTerse() {
		return terse;
	}

	public boolean isXml() {
		return xml;
	}

	public void setTemplateLoader(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	public TemplateLoader getTemplateLoader() {
		return templateLoader;
	}

	public void setDoctype(String name){
		if (name == null || StringUtils.isBlank(name)) {
			name = "default";
		}
		doctypeLine = Doctypes.get(name);
		if (doctypeLine == null) {
			doctypeLine = "<!DOCTYPE " + name + ">";
		}

		this.terse = "<!doctype html>".equals(this.doctypeLine.toLowerCase());
		this.xml = doctypeLine.startsWith("<?xml");
 	}

	public String getDoctypeLine() {
		return doctypeLine;
	}

	public void setMode(Mode mode) {
		xml = false;
		terse = false;
		switch (mode) {
		case HTML:
			terse = true;
			break;
		case XML:
			xml = true;
			break;
		}
	}

	public void setExpressionHandler(ExpressionHandler expressionHandler) {
		this.expressionHandler = expressionHandler;
	}

	public ExpressionHandler getExpressionHandler() {
		return expressionHandler;
	}
}
