/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.plan.document;

import com.chingo247.structureapi.plan.exception.ElementValueException;
import com.google.common.base.Preconditions;
import fiber.core.impl.xml.located.LocatedElement;
import java.io.File;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class SimpleElement {

    protected final LocatedElement le;
    private final File file;

    SimpleElement(File file, Element element) {
        Preconditions.checkNotNull(file);
        Preconditions.checkArgument(file.exists());
        if (!(element instanceof LocatedElement)) {
            throw new IllegalArgumentException("Element must be instance of " + LocatedElement.class);
        }
        this.le = (LocatedElement) element;
        this.file = file;
    }

    public Element getElement() {
        return le.createCopy();
    }

    public File getFile() {
        return file;
    }

    public String getElementName() {
        return le.getName();
    }

    public String getTextValue() {
        return le.getText();
    }

    public double getDoubleValue() throws ElementValueException {
        String error = "Value of element <" + le.getName() + "> on line " + le.getLine() + " was not a number";
        if (!hasText()) {
            throw new ElementValueException(error + ", but empty");
        }
        try {
            return Double.parseDouble(getTextValue());
        } catch (NumberFormatException nfe) {
            throw new ElementValueException(error);
        }
    }

    public boolean getBooleanValue() throws ElementValueException {
        String value = getTextValue().toLowerCase().trim();
        if (value.isEmpty() || (!value.equals("false") && !value.equals("true"))) {
                throw new ElementValueException("Error in element <" + le.getName() + "> ' of " + file.getAbsolutePath() + "' on line " + le.getLine() + ": "
                        + "value should be either of 'true' or 'false'");
           
        }
        return Boolean.parseBoolean(value);
    }

    public int getIntValue() throws ElementValueException {
        double d = getDoubleValue();
        return (int) Math.round(d);
    }

    public int getLine() {
        return le.getLine();
    }

    public boolean hasNumberValue() {
        return NumberUtils.isNumber(le.getText());
    }

    public boolean hasText() {
        return le.getText() != null && !le.getText().trim().isEmpty();
    }

    public boolean hasElement(String xPath) {
        return le.selectSingleNode(xPath) != null;
    }

    public void checkHasNumber() throws ElementValueException {
        if (!hasNumberValue()) {
            throw new ElementValueException("Value of element <" + le.getName() + "> on line " + le.getLine() + " in '" + file.getAbsolutePath() + "'" + " is not a number");
        }
    }

    public void checkNotNull(String xpath) throws ElementValueException {
        if (!hasElement(xpath)) {
            String element;
            if (xpath.contains("/")) {
                element = xpath.substring(xpath.lastIndexOf("/"));
            } else {
                element = xpath;
            }
            throw new ElementValueException("Expected element <" + element + "> within <" + le.getName() + "> on line " + le.getLine() + " in '" + file.getAbsolutePath() + "'");
        }
    }

    public void checkNotEmpty() throws ElementValueException {
        String text = le.getText().trim();
        if (text.isEmpty()) {
            throw new ElementValueException("Element <" + le.getName() + "> on line " + le.getLine() + "in '" + file.getAbsolutePath() + "'" + " was empty");
        }
    }

    public void checkNullOrNotEmpty(String xpath) throws ElementValueException {
        Node notNull = le.selectSingleNode(xpath);
        if (notNull != null && notNull.getText().trim().isEmpty()) {
            String element = xpath.substring(xpath.lastIndexOf("/"));
            throw new ElementValueException("No value for element <" + element + "> within <" + le.getName() + "> on line " + le.getLine());
        }
    }

    public SimpleElement selectSingleElement(String xpath) {
        Element e = (Element) le.selectSingleNode(xpath);
        if (e != null) {
            return new SimpleElement(file, e);
        }
        return null;
    }
    
    public void createIfNotExist(String elementName, Object value) {
        if(le.selectSingleNode(elementName) == null) {
            LocatedElement element = new LocatedElement(elementName);
            element.setText(String.valueOf(value));
            le.add(element);
        }
    }

}
