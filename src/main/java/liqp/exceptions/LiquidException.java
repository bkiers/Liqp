package liqp.exceptions;

import liquid.parser.v4.LiquidParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.List;

public class LiquidException extends RuntimeException {

  public final int line;
  public final int charPositionInLine;

  public LiquidException(RecognitionException e) {

    super(createMessage(e), e);

    this.line = e.getOffendingToken().getLine();
    this.charPositionInLine = e.getOffendingToken().getCharPositionInLine();
  }

  public LiquidException(String message, ParserRuleContext ctx) {

    super(message);

    this.line = ctx.start.getLine();
    this.charPositionInLine = ctx.start.getCharPositionInLine();
  }

  public LiquidException(String message, int line, int charPositionInLine, Throwable cause) {
    super(message, cause);
    this.line = line;
    this.charPositionInLine = charPositionInLine;
  }

  private static String createMessage(RecognitionException e) {

    Token offendingToken = e.getOffendingToken();
    String[] inputLines = e.getInputStream().toString().split("\r?\n|\r");
    String errorLine = inputLines[offendingToken.getLine() - 1];

    StringBuilder message = new StringBuilder(String.format("\nError on line %s, column %s:\n",
            offendingToken.getLine(), offendingToken.getCharPositionInLine()));

    message.append(errorLine).append("\n");

    for (int i = 0; i < offendingToken.getCharPositionInLine(); i++) {
      message.append(" ");
    }

    message.append("^");

    if (e instanceof InputMismatchException) {

      InputMismatchException ime = (InputMismatchException)e;

      return String.format("%s\nmatched '%s' as token <%s>, expecting token <%s>",
          message, offendingToken.getText(), tokenName(offendingToken.getType()), tokenNames(ime.getExpectedTokens()));
    }

    if (e instanceof FailedPredicateException) {

      FailedPredicateException fpe = (FailedPredicateException)e;

      return String.format("%s\nfailed predicate '%s' after position %s",
          message, fpe.getPredicate(), offendingToken.getCharPositionInLine());
    }

    if (e instanceof NoViableAltException || e instanceof LexerNoViableAltException) {

      return String.format("%s\ncould not decide what path to take, at position %s",
          message, offendingToken.getCharPositionInLine());
    }

    return message + "\nAn unknown error occurred!";
  }

  private static String tokenName(int type) {

    return type < 0 ? "<EOF>" : LiquidParser.VOCABULARY.getSymbolicName(type);
  }

  private static String tokenNames(IntervalSet types) {
    List<Integer> typeList = types.toList();
    StringBuilder expectedBuilder = new StringBuilder();

    for (Integer t: typeList) {
      expectedBuilder.append(tokenName(t)).append(" ");
    }

    return expectedBuilder.toString().trim();
  }
}
