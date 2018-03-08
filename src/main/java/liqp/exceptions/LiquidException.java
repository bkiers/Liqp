package liqp.exceptions;

import liquid.parser.v4.LiquidParser;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.v4.runtime.ParserRuleContext;

public class LiquidException extends RuntimeException {

  public final int line;
  public final int charPositionInLine;

  public LiquidException(RecognitionException e) {

    super(createMessage(e), e);

    this.line = e.line;
    this.charPositionInLine = e.charPositionInLine;
  }

  public LiquidException(String message, ParserRuleContext ctx) {

    super(message);

    this.line = ctx.start.getLine();;
    this.charPositionInLine = ctx.start.getCharPositionInLine();
  }

  private static String createMessage(RecognitionException e) {

    String[] inputLines = e.input.toString().split("\r?\n|\r");
    String errorLine = inputLines[e.line - 1];

    StringBuilder message = new StringBuilder(String.format("\nError on line %s, column %s:\n", e.line, e.charPositionInLine));

    message.append(errorLine).append("\n");

    for (int i = 0; i < e.charPositionInLine; i++) {
      message.append(" ");
    }

    message.append("^");

    if (e instanceof MismatchedTokenException) {

      MismatchedTokenException mte = (MismatchedTokenException)e;

      return String.format("%s\nmatched '%s' as token <%s>, expecting token <%s>",
          message, e.token.getText(), tokenName(mte.getUnexpectedType()), tokenName(mte.expecting));
    }

    if (e instanceof EarlyExitException) {

      EarlyExitException eee = (EarlyExitException)e;

      return String.format("%s\nmissing character '%s' after position %s",
          message, (char)eee.c, e.charPositionInLine);
    }

    if (e instanceof NoViableAltException) {

      NoViableAltException nvae = (NoViableAltException)e;

      return String.format("%s\ncould not decide what path to take, at position %s, expecting one of: %s",
          message, e.charPositionInLine, nvae.grammarDecisionDescription);
    }

    return message + "\nAn unknown error occurred!";
  }

  private static String tokenName(int type) {

    return type < 0 ? "<EOF>" : LiquidParser.VOCABULARY.getSymbolicName(type);
  }
}
