package com.digitald4.common.tld;
import java.util.ArrayList;
import java.util.Collection;

import com.digitald4.common.dao.DataAccessObject;

/**
 * This is a simple tag example to show how content is added to the
 * output stream when a tag is encountered in a JSP page. 
 */
public class InputTag extends DD4Tag {
	public enum Type {
		TEXT("<input type=\"text\" name=\"%name\" id=\"%name\" value=\"%value\" class=\"full-width\" />\n"),
		ACK_TEXT("<input type=\"text\" name=\"%name\" id=\"%name\" value=\"%value\" class=\"full-width\" />\n"),
		COMBO("<select name=\"%name\" id=\"%name\" value=\"%value\" class=\"full-width\" />\n","\t<option value=\"%op_value\">%op_text</option>\n","</select>\n"),
		CHECK("<input type=\"checkbox\" name=\"%name\" id=\"%name\" value=\"%value\" class=\"switch\" />\n"),
		DATE("<input type=\"text\" name=\"%name\" id=\"%name\" value=\"%value\" class=\"datepicker\" />\n"
				+"<img src=\"images/icons/fugue/calendar-month.png\" width=\"16\" height=\"16\" />\n"),
		RADIO("<p><span class=\"label\">%label</span>\n", "", 
				"\t<input type=\"radio\" name=\"%name\" id=\"%name-%op_value\" value=\"%op_value\"> <label for=\"%name-%op_value\">%op_text</label>\n",
				"</p>\n"),
		MULTI_CHECK("<p><span class=\"label\">%label</span>\n", "", 
				"\t<input type=\"checkbox\" name=\"%name\" id=\"%name-%op_value\" value=\"%op_value\"> <label for=\"%name-%op_value\">%op_text</label>\n",
				"</p>\n"),
		TEXTAREA("<textarea name=\"%name\" id=\"%name\" rows=10 class=\"full-width\">%value</textarea>\n");
		
		private final String label;
		private final String start;
		private final String option;
		private final String end;
		
		Type(String start) {
			this(start,null,"");
		}
		
		Type(String start, String option, String end) {
			this("<label for=\"%name\">%label</label>\n", start, option, end);
		}
		
		Type(String label, String start, String option, String end) {
			this.label = label;
			this.start = start;
			this.option = option;
			this.end = end;
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getStart() {
			return start;
		}
		
		public String getOption() {
			return option;
		}
		
		public String getEnd() {
			return end;
		}
	};
	
	private String prop;
	private Collection<? extends DataAccessObject> options = new ArrayList<DataAccessObject>();
	private String label;
	private DataAccessObject object;
	private Type type;
	
	/**
	 * Getter/Setter for the attribute name as defined in the tld file 
	 * for this tag
	 */
	public void setProp(String prop){
		this.prop=prop;
		options = new ArrayList<DataAccessObject>();
	}
	
	public String getProp() {
		return prop;
	}
	
	public String getName(){
		return getObject().getClass().getSimpleName() + "." + getProp();
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setObject(DataAccessObject object) {
		this.object = object;
	}
	
	public DataAccessObject getObject() {
		return object;
	}
	
	public void setOptions(Collection<? extends DataAccessObject> options){
		this.options = options;
	}
	
	public Collection<? extends DataAccessObject> getOptions(){
		return options;
	}
	
	public Object getValue() {
		Object value = getObject().getPropertyValue(getProp());
		if (value == null) {
			return "";
		}
		return value;
	}
	
	public String getStart() {
		return getType().getStart().replaceAll("%name", getName()).replace("%value", ""+getValue());
	}
	
	public String getEnd() {
		return getType().getEnd();
	}
	
	public String getOutput() {
		String out = getType().getLabel().replaceAll("%name", getName()).replaceAll("%label", getLabel());
		out += getStart();
		if (getType().getOption() != null) {
			if (getType() == Type.COMBO) {
				out += getType().getOption().replaceAll("%name", getName()).replaceAll("%op_value", "0").replaceAll("%op_text", "[SELECT "+getLabel()+"]");
			}
			for (DataAccessObject option : getOptions()) {
				out += getType().getOption().replaceAll("%name", getName()).replaceAll("%op_value", ""+option.getId()).replaceAll("%op_text", ""+option);
			}
		}
		out += getEnd();
		return out;
	}
}
