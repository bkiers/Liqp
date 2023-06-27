package liqp.parser.v4;

import static liquid.parser.v4.LiquidParser.And;
import static liquid.parser.v4.LiquidParser.Eq;
import static liquid.parser.v4.LiquidParser.Gt;
import static liquid.parser.v4.LiquidParser.GtEq;
import static liquid.parser.v4.LiquidParser.Lt;
import static liquid.parser.v4.LiquidParser.LtEq;
import static liquid.parser.v4.LiquidParser.NEq;
import static liquid.parser.v4.LiquidParser.Or;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

import liqp.Insertion;
import liqp.Insertions;
import liqp.LValue;
import liqp.exceptions.LiquidException;
import liqp.filters.Filters;
import liqp.nodes.AndNode;
import liqp.nodes.AtomNode;
import liqp.nodes.AttributeNode;
import liqp.nodes.BlockNode;
import liqp.nodes.ContainsNode;
import liqp.nodes.EqNode;
import liqp.nodes.FilterNode;
import liqp.nodes.GtEqNode;
import liqp.nodes.GtNode;
import liqp.nodes.InsertionNode;
import liqp.nodes.KeyValueNode;
import liqp.nodes.LNode;
import liqp.nodes.LookupNode;
import liqp.nodes.LtEqNode;
import liqp.nodes.LtNode;
import liqp.nodes.NEqNode;
import liqp.nodes.OrNode;
import liqp.nodes.OutputNode;
import liquid.parser.v4.LiquidParser.AssignmentContext;
import liquid.parser.v4.LiquidParser.AtomContext;
import liquid.parser.v4.LiquidParser.Atom_othersContext;
import liquid.parser.v4.LiquidParser.AttributeContext;
import liquid.parser.v4.LiquidParser.BlockContext;
import liquid.parser.v4.LiquidParser.Capture_tag_IdContext;
import liquid.parser.v4.LiquidParser.Capture_tag_StrContext;
import liquid.parser.v4.LiquidParser.Case_tagContext;
import liquid.parser.v4.LiquidParser.Comment_tagContext;
import liquid.parser.v4.LiquidParser.Continue_tagContext;
import liquid.parser.v4.LiquidParser.Cycle_tagContext;
import liquid.parser.v4.LiquidParser.Elsif_tagContext;
import liquid.parser.v4.LiquidParser.ExprContext;
import liquid.parser.v4.LiquidParser.Expr_containsContext;
import liquid.parser.v4.LiquidParser.Expr_eqContext;
import liquid.parser.v4.LiquidParser.Expr_logicContext;
import liquid.parser.v4.LiquidParser.Expr_relContext;
import liquid.parser.v4.LiquidParser.Expr_termContext;
import liquid.parser.v4.LiquidParser.FilenameContext;
import liquid.parser.v4.LiquidParser.FilterContext;
import liquid.parser.v4.LiquidParser.For_arrayContext;
import liquid.parser.v4.LiquidParser.For_attributeContext;
import liquid.parser.v4.LiquidParser.For_rangeContext;
import liquid.parser.v4.LiquidParser.If_tagContext;
import liquid.parser.v4.LiquidParser.Include_tagContext;
import liquid.parser.v4.LiquidParser.IndexContext;
import liquid.parser.v4.LiquidParser.Jekyll_include_filenameContext;
import liquid.parser.v4.LiquidParser.Jekyll_include_outputContext;
import liquid.parser.v4.LiquidParser.Jekyll_include_paramsContext;
import liquid.parser.v4.LiquidParser.Lookup_IdContext;
import liquid.parser.v4.LiquidParser.Lookup_StrContext;
import liquid.parser.v4.LiquidParser.Lookup_emptyContext;
import liquid.parser.v4.LiquidParser.Lookup_id_indexesContext;
import liquid.parser.v4.LiquidParser.Other_tagContext;
import liquid.parser.v4.LiquidParser.OutputContext;
import liquid.parser.v4.LiquidParser.Param_exprContext;
import liquid.parser.v4.LiquidParser.Param_expr_exprContext;
import liquid.parser.v4.LiquidParser.Param_expr_key_valueContext;
import liquid.parser.v4.LiquidParser.ParseContext;
import liquid.parser.v4.LiquidParser.Raw_tagContext;
import liquid.parser.v4.LiquidParser.Simple_tagContext;
import liquid.parser.v4.LiquidParser.Table_tagContext;
import liquid.parser.v4.LiquidParser.TermContext;
import liquid.parser.v4.LiquidParser.Term_BlankContext;
import liquid.parser.v4.LiquidParser.Term_DoubleNumContext;
import liquid.parser.v4.LiquidParser.Term_EmptyContext;
import liquid.parser.v4.LiquidParser.Term_FalseContext;
import liquid.parser.v4.LiquidParser.Term_LongNumContext;
import liquid.parser.v4.LiquidParser.Term_NilContext;
import liquid.parser.v4.LiquidParser.Term_StrContext;
import liquid.parser.v4.LiquidParser.Term_TrueContext;
import liquid.parser.v4.LiquidParser.Term_exprContext;
import liquid.parser.v4.LiquidParser.Term_lookupContext;
import liquid.parser.v4.LiquidParser.Unless_tagContext;
import liquid.parser.v4.LiquidParser.When_tagContext;
import liqp.nodes.*;
import liquid.parser.v4.LiquidParserBaseVisitor;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static liquid.parser.v4.LiquidParser.*;

public class NodeVisitor extends LiquidParserBaseVisitor<LNode> {

  private Insertions insertions;
  private Filters filters;
  private final boolean liquidStyleInclude;
  private boolean isRootBlock = true;

  public NodeVisitor(Insertions insertions, Filters filters, boolean liquidStyleInclude) {
    if (insertions == null)
      throw new IllegalArgumentException("tags == null");

    if (filters == null)
      throw new IllegalArgumentException("filters == null");

    this.insertions = insertions;

    this.filters = filters;
    this.liquidStyleInclude = liquidStyleInclude;
  }

  // parse
  //  : block EOF
  //  ;
  @Override
  public BlockNode visitParse(ParseContext ctx) {
    return visitBlock(ctx.block());
  }

  // block
  //  : atom*
  //  ;
  @Override
  public BlockNode visitBlock(BlockContext ctx) {

    BlockNode node = new BlockNode(isRootBlock);
    isRootBlock = false;

    for (AtomContext child : ctx.atom()) {
      node.add(visit(child));
    }

    return node;
  }

  // atom
  // : ...
  // | other      #atom_others
  // ;
  @Override
  public LNode visitAtom_others(Atom_othersContext ctx) {
    return new AtomNode(ctx.getText());
  }

  // custom_tag
  //  : tagStart Id custom_tag_parameters? TagEnd custom_tag_block?
  //  ;
  //
  // custom_tag_parameters
  //  : other_than_tag_end
  //  ;
  //
  // other_than_tag_end
  //  : ~TagEnd+
  //  ;
  @Override
  public LNode visitOther_tag(Other_tagContext ctx) {
    String blockId = ctx.BlockId().getText();

    List<LNode> expressions = new ArrayList<LNode>();

    if (ctx.other_tag_parameters() != null) {
      expressions.add(new AtomNode(ctx.other_tag_parameters().getText()));
    }

    BlockNode node = new BlockNode(isRootBlock);

    for (AtomContext child : ctx.atom()) {
      node.add(visit(child));
    }

    Insertion insertion = insertions.get(blockId);
    if (insertion == null) {
      throw new RuntimeException("The tag/block '" + blockId + "' is not registered.");
    }

    expressions.add(node);

    return new InsertionNode(insertion, expressions.toArray(new LNode[expressions.size()]));
  }

  @Override
  public LNode visitSimple_tag(Simple_tagContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();

    if (ctx.other_tag_parameters() != null) {
      expressions.add(new AtomNode(ctx.other_tag_parameters().getText()));
    }

    return new InsertionNode(insertions.get(ctx.SimpleTagId().getText()), expressions.toArray(new LNode[expressions.size()]));
  }

  // raw_tag
  //  : tagStart RawStart raw_body RawEnd TagEnd
  //  ;
  @Override
  public LNode visitRaw_tag(Raw_tagContext ctx) {
    return new InsertionNode(insertions.get("raw"), new AtomNode(ctx.raw_body().getText()));
  }

  // comment_tag
  //  : tagStart CommentStart TagEnd .*? tagStart CommentEnd TagEnd
  //  ;
  @Override
  public LNode visitComment_tag(Comment_tagContext ctx) {
    return new InsertionNode(insertions.get("comment"), new AtomNode(ctx.getText()));
  }

  // if_tag
  //  : tagStart IfStart expr TagEnd block elsif_tag* else_tag? tagStart IfEnd TagEnd
  //  ;
  //
  // elsif_tag
  //  : tagStart Elsif expr TagEnd block
  //  ;
  //
  // else_tag
  //  : tagStart Else TagEnd block
  //  ;
  @Override
  public LNode visitIf_tag(If_tagContext ctx) {

    List<LNode> nodes = new ArrayList<LNode>();

    // if
    nodes.add(visit(ctx.expr()));
    nodes.add(visitBlock(ctx.block()));

    // elsif
    for (Elsif_tagContext elseIf : ctx.elsif_tag()) {
      nodes.add(visit(elseIf.expr()));
      nodes.add(visitBlock(elseIf.block()));
    }

    // else
    if (ctx.else_tag() != null) {
      nodes.add(new AtomNode("TRUE"));
      nodes.add(visitBlock(ctx.else_tag().block()));
    }

    return new InsertionNode(insertions.get("if"), nodes.toArray(new LNode[nodes.size()]));
  }

  // unless_tag
  //  : tagStart UnlessStart expr TagEnd block else_tag? tagStart UnlessEnd TagEnd
  //  ;
  @Override
  public LNode visitUnless_tag(Unless_tagContext ctx) {

    List<LNode> nodes = new ArrayList<LNode>();

    // unless
    nodes.add(visit(ctx.expr()));
    nodes.add(visitBlock(ctx.block()));

    // else
    if (ctx.else_tag() != null) {
      nodes.add(new AtomNode(null));
      nodes.add(visitBlock(ctx.else_tag().block()));
    }

    return new InsertionNode(insertions.get("unless"), nodes.toArray(new LNode[nodes.size()]));
  }

  // case_tag
  //  : tagStart CaseStart expr TagEnd other? when_tag+ else_tag? tagStart CaseEnd TagEnd
  //  ;
  //
  // when_tag
  //  : tagStart When term ((Or | Comma) term)* TagEnd block
  //  ;
  @Override
  public LNode visitCase_tag(Case_tagContext ctx) {

    List<LNode> nodes = new ArrayList<LNode>();
    nodes.add(visit(ctx.expr()));

    // when
    for (When_tagContext child : ctx.when_tag()) {

      for (TermContext grandChild : child.term()) {
        nodes.add(visit(grandChild));
      }

      nodes.add(visitBlock(child.block()));
    }

    // else
    if (ctx.else_tag() != null) {
      nodes.add(nodes.get(0));
      nodes.add(visitBlock(ctx.else_tag().block()));
    }

    return new InsertionNode(insertions.get("case"), nodes.toArray(new LNode[nodes.size()]));
  }

  // cycle_tag
  //  : tagStart Cycle cycle_group expr (Comma expr)* TagEnd
  //  ;
  //
  // cycle_group
  //  : (expr Col)?
  //  ;
  @Override
  public LNode visitCycle_tag(Cycle_tagContext ctx) {

    List<LNode> nodes = new ArrayList<LNode>();

    nodes.add(ctx.cycle_group().expr() == null ? null : visit(ctx.cycle_group().expr()));

    for (ExprContext child : ctx.expr()) {
      nodes.add(visit(child));
    }

    return new InsertionNode(insertions.get("cycle"), nodes.toArray(new LNode[nodes.size()]));
  }

  // for_array
  //  : tagStart ForStart Id In lookup Reversed? for_attribute* TagEnd
  //    for_block
  //    tagStart ForEnd TagEnd
  //  ;
  //
  // for_block
  //  : a=block (tagStart Else TagEnd b=block)?
  //  ;
  @Override
  public LNode visitFor_array(For_arrayContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();
    expressions.add(new AtomNode(true));

    expressions.add(new AtomNode(ctx.id().getText()));
    expressions.add(visit(ctx.lookup()));

    expressions.add(visitBlock(ctx.for_block().a));
    expressions.add(ctx.for_block().Else() == null ? null : visitBlock(ctx.for_block().b));
    expressions.add(new AtomNode(ctx.lookup().getText()));
    expressions.add(new AtomNode(ctx.Reversed() != null));

    for (For_attributeContext attribute : ctx.for_attribute()) {
      expressions.add(visit(attribute));
    }

    return new InsertionNode(insertions.get("for"), expressions.toArray(new LNode[expressions.size()]));
  }

  // for_range
  //  : tagStart ForStart Id In OPar expr DotDot expr CPar Reversed? for_attribute* TagEnd
  //    block
  //    tagStart ForEnd TagEnd
  //  ;
  @Override
  public LNode visitFor_range(For_rangeContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();
    expressions.add(new AtomNode(false));

    expressions.add(new AtomNode(ctx.id().getText()));
    expressions.add(visit(ctx.from));
    expressions.add(visit(ctx.to));

    expressions.add(visitBlock(ctx.block()));
    expressions.add(new AtomNode("(" + ctx.from.getText() + ".." + ctx.to.getText() + ")"));
    expressions.add(new AtomNode(ctx.Reversed() != null));

    for (For_attributeContext attribute : ctx.for_attribute()) {
      expressions.add(visit(attribute));
    }

    return new InsertionNode(insertions.get("for"), expressions.toArray(new LNode[expressions.size()]));
  }

  // attribute
  //  : Id Col expr
  //  ;
  @Override
  public LNode visitAttribute(AttributeContext ctx) {
    if (ctx.Offset() != null) {
        return new AttributeNode(new AtomNode(ctx.Offset().getText()), visit(ctx.expr()));
    } else {
        return new AttributeNode(new AtomNode(ctx.Id().getText()), visit(ctx.expr()));
    }
  }



  @Override
  public LNode visitFor_attribute(For_attributeContext ctx) {
    if (ctx.Id() != null) {
        return new AttributeNode(new AtomNode(ctx.Id().getText()), visit(ctx.expr()));
    }
    // else "offset" attr
    if (ctx.Continue() != null) {
        return new AttributeNode(new AtomNode(ctx.Offset().getText()), new AtomNode(LValue.CONTINUE));
    }
    return new AttributeNode(new AtomNode(ctx.Offset().getText()), visit(ctx.expr()));
    }

    @Override
    public LNode visitContinue_tag(Continue_tagContext ctx) {
        return new AtomNode(LValue.CONTINUE);
    }

    // table_tag
  //  : tagStart TableStart Id In lookup attribute* TagEnd block tagStart TableEnd TagEnd
  //  ;
  @Override
  public LNode visitTable_tag(Table_tagContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();

    expressions.add(new AtomNode(ctx.id().getText()));
    expressions.add(visit(ctx.lookup()));
    expressions.add(visitBlock(ctx.block()));

    for (AttributeContext attribute : ctx.attribute()) {
      expressions.add(visit(attribute));
    }

    return new InsertionNode(insertions.get("tablerow"), expressions.toArray(new LNode[expressions.size()]));
  }

  // capture_tag
  //  : tagStart CaptureStart Id TagEnd block tagStart CaptureEnd TagEnd  #capture_tag_Id
  //  | ...
  //  ;
  @Override
  public LNode visitCapture_tag_Id(Capture_tag_IdContext ctx) {
    return new InsertionNode(insertions.get("capture"), new AtomNode(ctx.id().getText()), visitBlock(ctx.block()));
  }

  // capture_tag
  //  : ...
  //  | tagStart CaptureStart Str TagEnd block tagStart CaptureEnd TagEnd #capture_tag_Str
  //  ;
  @Override
  public LNode visitCapture_tag_Str(Capture_tag_StrContext ctx) {
    return new InsertionNode(insertions.get("capture"), fromString(ctx.Str()), visitBlock(ctx.block()));
  }

  // include_tag
  // : {isLiquid()}? tagStart liquid=Include expr (With Str)? TagEnd
  // | {isJekyll()}? tagStart jekyll=Include file_name_or_output TagEnd
  // ;
  @Override
  public LNode visitInclude_tag(Include_tagContext ctx) {
    if (ctx.jekyll != null) {
      Stream<? extends LNode> stream = Stream.concat( //
          Stream.of(visit(ctx.file_name_or_output())), //
          ctx.jekyll_include_params().stream().map(this::visitJekyll_include_params) //
      );

      return new InsertionNode(insertions.get("include"), stream.toArray((n) -> new LNode[n]));
    } else if (ctx.liquid != null) {
      if (ctx.Str() != null) {
        return new InsertionNode(insertions.get("include"), visit(ctx.expr()), new AtomNode(strip(ctx.Str().getText())));
      } else {
        return new InsertionNode(insertions.get("include"), visit(ctx.expr()));
      }
    }
    throw new LiquidException("Unknown syntax of `Include` tag", ctx);
  }

  @Override
  public LNode visitJekyll_include_params(Jekyll_include_paramsContext ctx) {
    return new KeyValueNode(ctx.id().getText(), visit(ctx.expr()));
  }

  // file_name_or_output
  //  : ...
  //  | output                       #jekyll_include_output
  //  | ...
  //  ;
  @Override
  public LNode visitJekyll_include_output(Jekyll_include_outputContext ctx) {
    if (this.liquidStyleInclude)
      throw new LiquidException("`{% include ouput %}` can only be used for Flavor.JEKYLL", ctx);

    return visitOutput(ctx.output());
  }

  // file_name_or_output
  //  : ...
  //  | filename                       #jekyll_include_filename
  //  | ...
  //  ;
  @Override
  public LNode visitJekyll_include_filename(Jekyll_include_filenameContext ctx) {
    if (this.liquidStyleInclude) {
      throw new LiquidException("`{% include other_than_tag_end_out_start %}` can only be used for Flavor.JEKYLL", ctx);
    }
    // valid filename in jekyll doesn't allow whitespaces
    // as far as whitespaces are in hidden channel, we are reading
    // the whole interval between first and last token
    Interval interval = Interval.of(ctx.filename().start.getStartIndex(), ctx.filename().stop.getStopIndex());
    String filename = ctx.filename().start.getInputStream().getText(interval);

    if (filename.matches(".*\\s.*")) {
      throw new LiquidException("in `{% include filename %}` the `filename` is {" + filename + "}, but it cannot have spaces for Flavor.JEKYLL", ctx);
    }
    return new AtomNode(filename);
  }

  @Override
  public LNode visitFilename(FilenameContext ctx) {
    return super.visitFilename(ctx);
  }

  // output
  // : {evaluateInOutputTag}? outStart evaluate=expr filter* OutEnd
  // | outStart term filter* OutEnd
  // ;
  @Override
  public LNode visitOutput(OutputContext ctx) {
    OutputNode node;
    if (ctx.evaluate != null) {
      node = new OutputNode(visit(ctx.expr()), null, null);
    } else {
      String unparsed = null;
      Integer unparsedStart = null;
      if (ctx.unparsed != null) {
        unparsed = ctx.unparsed.getText();
        unparsedStart = ctx.unparsed.getStart().getStartIndex();
      }
      node = new OutputNode(visit(ctx.term()), unparsed, unparsedStart);
    }

    for (FilterContext child : ctx.filter()) {
      node.addFilter(visitFilter(child));
    }

    return node;
  }

  // filter
  //  : Pipe Id params?
  //  ;
  //
  // params
  //  : Col param_expr (Comma param_expr)*
  //  ;
  @Override
  public FilterNode visitFilter(FilterContext ctx) {

    FilterNode node = new FilterNode(ctx, filters.get(ctx.Id().getText()));

    if (ctx.params() != null) {
      for (Param_exprContext child : ctx.params().param_expr()) {
        node.add(visit(child));
      }
    }

    return node;
  }

  // param_expr
  //  : id2 Col expr #param_expr_key_value
  //  | ...
  //  ;
  @Override
  public LNode visitParam_expr_key_value(Param_expr_key_valueContext ctx) {
    return new KeyValueNode(ctx.id2().getText(), visit(ctx.expr()));
  }

  // param_expr
  //  : ...
  //  | expr         #param_expr_expr
  //  ;
  @Override
  public LNode visitParam_expr_expr(Param_expr_exprContext ctx) {
    return visit(ctx.expr());
  }

  // assignment
  //  : tagStart Assign Id EqSign expr filter* TagEnd
  //  ;
  @Override
  public LNode visitAssignment(AssignmentContext ctx) {

    AtomNode idNode = new AtomNode(ctx.id().getText());
    LNode exprNode = visit(ctx.expr());
    List<LNode> allNodes = new ArrayList<>();

    allNodes.add(idNode);
    allNodes.add(exprNode);

    for (FilterContext filterContext : ctx.filter()) {
      allNodes.add(visit(filterContext));
    }

    return new InsertionNode(insertions.get("assign"), allNodes);
  }

  // expr
  //  : expr op=(LtEq | Lt | GtEq | Gt) expr #expr_rel
  //  | ...
  //  ;
  @Override
  public LNode visitExpr_rel(Expr_relContext ctx) {
    switch (ctx.op.getType()) {
      case LtEq:
        return new LtEqNode(visit(ctx.lhs), visit(ctx.rhs));
      case Lt:
        return new LtNode(visit(ctx.lhs), visit(ctx.rhs));
      case GtEq:
        return new GtEqNode(visit(ctx.lhs), visit(ctx.rhs));
      case Gt:
        return new GtNode(visit(ctx.lhs), visit(ctx.rhs));
      default:
        throw new RuntimeException("unknown operator: " + ctx.op.getText());
    }
  }

  // expr
  //  : ...
  //  | expr Contains expr                   #expr_contains
  //  | ...
  //  ;
  @Override
  public LNode visitExpr_contains(Expr_containsContext ctx) {
    return new ContainsNode(visit(ctx.lhs), visit(ctx.rhs));
  }

  // expr
  //  : ...
  //  | expr op=(Eq | NEq) expr              #expr_eq
  //  | ...
  //  ;
  @Override
  public LNode visitExpr_eq(Expr_eqContext ctx) {
    switch (ctx.op.getType()) {
      case Eq:
        return new EqNode(visit(ctx.lhs), visit(ctx.rhs));
      case NEq:
        return new NEqNode(visit(ctx.lhs), visit(ctx.rhs));
      default:
        throw new RuntimeException("unknown operator: " + ctx.op.getText());
    }
  }

  // expr
  //  : ...
  //  | <assoc=right> expr op=(And | Or) expr #expr_logic
  //  | ...
  //  ;
  @Override
  public LNode visitExpr_logic(Expr_logicContext ctx) {
    switch (ctx.op.getType()) {
      case And:
        return new AndNode(visit(ctx.lhs), visit(ctx.rhs));
      case Or:
        return new OrNode(visit(ctx.lhs), visit(ctx.rhs));
      default:
        throw new RuntimeException("unknown operator: " + ctx.op.getText());
    }
  }

  // expr
  //  : ...
  //  | term                                 #expr_term
  //  ;
  @Override
  public LNode visitExpr_term(Expr_termContext ctx) {
    return visit(ctx.term());
  }

  // term
  //  : DoubleNum      #term_DoubleNum
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_DoubleNum(Term_DoubleNumContext ctx) {
    return new AtomNode(Double.valueOf(ctx.DoubleNum().getText()));
  }

  // term
  //  : ...
  //  | LongNum        #term_LongNum
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_LongNum(Term_LongNumContext ctx) {
    return new AtomNode(Long.valueOf(ctx.LongNum().getText()));
  }

  // term
  //  : ...
  //  | Str            #term_Str
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_Str(Term_StrContext ctx) {
    return fromString(ctx.Str());
  }

  // term
  //  : ...
  //  | True           #term_True
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_True(Term_TrueContext ctx) {
    return new AtomNode(true);
  }

  // term
  //  : ...
  //  | False          #term_False
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_False(Term_FalseContext ctx) {
    return new AtomNode(false);
  }

  // term
  //  : ...
  //  | Nil            #term_Nil
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_Nil(Term_NilContext ctx) {
    return new AtomNode(null);
  }

  // term
  //  : ...
  //  | lookup         #term_lookup
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_lookup(Term_lookupContext ctx) {
    return visit(ctx.lookup());
  }

  // term
  //  : ...
  //  | Empty          #term_Empty
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_Empty(Term_EmptyContext ctx) {
    return AtomNode.EMPTY;
  }

  // term
  //  : ...
  //  | Blank          #term_Blank
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_Blank(Term_BlankContext ctx) {
    return AtomNode.BLANK;
  }

  // term
  //  : ...
  //  | OPar expr CPar #term_expr
  //  ;
  @Override
  public LNode visitTerm_expr(Term_exprContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public LNode visitLookup_empty(Lookup_emptyContext ctx) {
    return AtomNode.EMPTY;
  }

    // lookup
  //  : id index* QMark?   #lookup_id_indexes
  //  | ...
  //  ;
  //
  // index
  //  : Dot id2
  //  | OBr expr CBr
  //  ;
  @Override
  public LookupNode visitLookup_id_indexes(Lookup_id_indexesContext ctx) {

    LookupNode node = new LookupNode(ctx.id().getText());

    for (IndexContext index : ctx.index()) {

      if (index.Dot() != null) {
        node.add(new LookupNode.Hash(index.id2().getText()));
      }
      else {
        node.add(new LookupNode.Index(visit(index.expr()), index.expr().getText()));
      }
    }

    return node;
  }

  // lookup
  //  : ...
  //  | OBr Str CBr QMark? #lookup_Str
  //  | ...
  //  ;
  @Override
  public LookupNode visitLookup_Str(Lookup_StrContext ctx) {
    return new LookupNode(strip(ctx.Str().getText()));
  }

  // lookup
  //  : ...
  //  | OBr Id CBr QMark?  #lookup_Id
  //  ;
  @Override
  public LookupNode visitLookup_Id(Lookup_IdContext ctx) {
    return new LookupNode("@" + ctx.Id().getText());
  }

  private static AtomNode fromString(TerminalNode str) {
    return new AtomNode(strip(str.getText()));
  }

  private static String strip(String str) {
    return str.substring(1, str.length() - 1);
  }
}
