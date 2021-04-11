package com.kasisoft.mgnl.fmx.internal;

import java.util.*;

import java.io.*;

import java.lang.ref.*;

import freemarker.core.*;
import freemarker.template.*;

/**
 * @author daniel.kasmeroglu@kasisoft.net
 */
public class CleanDirective implements TemplateDirectiveModel {
  
  private List<SoftReference<StringWriter>> writer = new LinkedList<>();
  
  private boolean isTrim(Map params) throws TemplateException {
    TemplateBooleanModel model  = (TemplateBooleanModel) params.get("trim");
    boolean              result = false;
    if (model != null) {
      result = model.getAsBoolean();
    }
    return result;
  }
  
  @SuppressWarnings("resource")
  @Override
  public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
    StringWriter writer = allocate();
    try {
      writer = new StringWriter();
      body.render(writer);
      String content = writer.toString().replaceAll("([\n]\\s*[\n])", "\n");
      if (isTrim(params)) {
        content = content.trim();
      }
      env.getOut().write(content);
    } finally {
      if (writer != null) {
        release(writer);
      }
    }
  }
  
  private void release(StringWriter strWriter) {
    strWriter.getBuffer().setLength(0);
    synchronized (writer) {
      writer.add(new SoftReference<StringWriter>(strWriter));
    }
  }

  private StringWriter allocate() {
    synchronized (writer) {
      StringWriter result = null;
      while ((!writer.isEmpty()) && (result == null)) {
        result = writer.remove(0).get();
      }
      if (result == null) {
        result = new StringWriter();
      }
      return result;
    }
  }
  
} /* ENDCLASS */
