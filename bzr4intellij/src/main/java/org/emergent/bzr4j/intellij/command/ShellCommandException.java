package org.emergent.bzr4j.intellij.command;

public class ShellCommandException extends Exception {

  ShellCommandException() {
  }

  ShellCommandException(String message) {
    super(message);
  }

  ShellCommandException(String message, Throwable cause) {
    super(message, cause);
  }

  ShellCommandException(Throwable cause) {
    super(cause);
  }
}
