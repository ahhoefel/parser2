package com.github.ahhoefel.ast;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ErrorLog {

  private List<ParseError> errors;

  public ErrorLog() {
    errors = new ArrayList<>();
  }

  public void add(ParseError err) {
    this.errors.add(err);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (ParseError err : errors) {
      builder.append(err.toString()).append("\n");
    }
    return builder.toString();
  }

  public static ErrorLog readErrors(Target target) {
    ErrorLog log = new ErrorLog();
    try {
      List<String> lines = Files.readAllLines(target.getErrorPath());
      for (int i = 0; i < lines.size(); i++) {
        String[] locationParts = lines.get(i).split(":");
        Target t = new Target(target.getSource(), locationParts[0] + ":" + locationParts[1]);
        CodeLocation location = new CodeLocation(t, Integer.parseInt(locationParts[2]) - 1, Integer.parseInt(locationParts[3]) - 1, 0);
        StringBuilder errorMsg = new StringBuilder();
        i++;
        errorMsg.append(lines.get(i));
        i++;
        while (i < lines.size() && !lines.get(i).isEmpty()) {
          errorMsg.append("\n").append(lines.get(i));
          i++;
        }
        ParseError error = new ParseError(location, errorMsg.toString());
        log.add(error);
      }
    } catch (IOException e) {
    }
    return log;
  }

  public boolean equals(Object o) {
    if (!(o instanceof ErrorLog)) {
      return false;
    }
    ErrorLog log = (ErrorLog) o;
    return log.errors.equals(errors);
  }

  public boolean isEmpty() {
    return errors.isEmpty();
  }
}
