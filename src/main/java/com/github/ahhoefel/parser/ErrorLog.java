package com.github.ahhoefel.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.github.ahhoefel.lang.ast.CodeLocation;
import com.github.ahhoefel.lang.ast.ParseError;
import com.github.ahhoefel.lang.ast.Target;

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

  // Reads the error file for a given target.
  // Error files are the expected errors from testing the given target.
  public static ErrorLog readErrors(Target target) {
    ErrorLog log = new ErrorLog();

    List<String> lines;
    try {
      lines = Files.readAllLines(target.getErrorPath());
    } catch (IOException e) {
      // File may not be present. Resulting log is empty.
      return log;
    }

    for (int i = 0; i < lines.size(); i++) {
      String[] locationParts = lines.get(i).split(":");
      Target t = new Target(target.getSource(), locationParts[0] + ":" + locationParts[1]);
      CodeLocation location;
      if (locationParts.length == 5) {
        location = new CodeLocation(t, Integer.parseInt(locationParts[2]) - 1, Integer.parseInt(locationParts[3]) - 1,
            Integer.parseInt(locationParts[4]));
      } else {
        location = new CodeLocation(t, Integer.parseInt(locationParts[2]) - 1, Integer.parseInt(locationParts[3]) - 1,
            0);
      }
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
