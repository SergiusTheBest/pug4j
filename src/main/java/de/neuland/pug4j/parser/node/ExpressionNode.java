package de.neuland.pug4j.parser.node;

import org.apache.commons.text.StringEscapeUtils;

import de.neuland.pug4j.compiler.IndentWriter;
import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.exceptions.PugCompilerException;
import de.neuland.pug4j.model.PugModel;
import de.neuland.pug4j.template.PugTemplate;

public class ExpressionNode extends Node {

	private boolean escape;
	private boolean buffer;
	private boolean inline;

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public void setBuffer(boolean buffer) {
		this.buffer = buffer;
	}

	public boolean isInline() {
		return inline;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	@Override
	public void execute(IndentWriter writer, PugModel model, PugTemplate template) throws PugCompilerException {
		try {
			String value = getValue();
			if (hasBlock()) {
				model.put("pug4j__innerblock",block);
				model.put("pug4j__template",template);
				model.put("pug4j__model",model);
				model.put("pug4j__writer",writer);
				value = value+"{pug4j__innerblock.execute(pug4j__writer,pug4j__model,pug4j__template)}";
			}
			Object result = template.getExpressionHandler().evaluateExpression(value, model);
			if (result == null || !buffer) {
				return;
			}
			String string = result.toString();
			if (escape) {
				string = StringEscapeUtils.escapeHtml4(string);
			}
			writer.append(string);
		} catch (ExpressionException e) {
			throw new PugCompilerException(this, template.getTemplateLoader(), e);
		}
	}

	@Override
	public void setValue(String value) {
		super.setValue(value.trim());
	}
}
