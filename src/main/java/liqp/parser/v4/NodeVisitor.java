package liqp.parser.v4;

import liqp.ParseSettings;
import liqp.exceptions.LiquidException;
import liqp.filters.Filter;
import liqp.nodes.BlockNode;
import liqp.nodes.*;
import liqp.parser.Flavor;
import liqp.tags.Tag;
import liquid.parser.v4.LiquidParser;
import liquid.parser.v4.LiquidParserBaseVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static liquid.parser.v4.LiquidParser.*;

public class NodeVisitor extends LiquidParserBaseVisitor<LNode> {

  private Map<String, Tag> tags;
  private Map<String, Filter> filters;
  private final ParseSettings parseSettings;
  private boolean isRootBlock = true;

  public NodeVisitor(Map<String, Tag> tags, Map<String, Filter> filters, ParseSettings parseSettings) {

    if (tags == null)
      throw new IllegalArgumentException("tags == null");

    if (filters == null)
      throw new IllegalArgumentException("filters == null");

    if (parseSettings == null)
      throw new IllegalArgumentException("parseSettings == null");

    this.tags = tags;
    this.filters = filters;
    this.parseSettings = parseSettings;
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

    List<LNode> expressions = new ArrayList<LNode>();

    if (ctx.other_tag_parameters() != null) {
      expressions.add(new AtomNode(ctx.other_tag_parameters().getText()));
    }

    if (ctx.other_tag_block() != null) {
      expressions.add(visitOther_tag_block(ctx.other_tag_block()));
    }

    return new TagNode(tags.get(ctx.Id().getText()), expressions.toArray(new LNode[expressions.size()]));
  }

  // custom_tag_block
  //  : atom+? tagStart EndId TagEnd
  //  ;
  @Override
  public BlockNode visitOther_tag_block(Other_tag_blockContext ctx) {

    BlockNode node = new BlockNode(isRootBlock);

    for (AtomContext child : ctx.atom()) {
      node.add(visit(child));
    }

    return node;
  }

  // raw_tag
  //  : tagStart RawStart raw_body RawEnd TagEnd
  //  ;
  @Override
  public LNode visitRaw_tag(Raw_tagContext ctx) {
    return new TagNode(tags.get("raw"), new AtomNode(ctx.raw_body().getText()));
  }

  // comment_tag
  //  : tagStart CommentStart TagEnd .*? tagStart CommentEnd TagEnd
  //  ;
  @Override
  public LNode visitComment_tag(Comment_tagContext ctx) {
    return new TagNode(tags.get("comment"), new AtomNode(ctx.getText()));
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

    return new TagNode(tags.get("if"), nodes.toArray(new LNode[nodes.size()]));
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

    return new TagNode(tags.get("unless"), nodes.toArray(new LNode[nodes.size()]));
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

    return new TagNode(tags.get("case"), nodes.toArray(new LNode[nodes.size()]));
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

    return new TagNode(tags.get("cycle"), nodes.toArray(new LNode[nodes.size()]));
  }

  // for_array
  //  : tagStart ForStart Id In lookup attribute* TagEnd
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

    expressions.add(new AtomNode(ctx.Id().getText()));
    expressions.add(visit(ctx.lookup()));

    expressions.add(visitBlock(ctx.for_block().a));
    expressions.add(ctx.for_block().Else() == null ? null : visitBlock(ctx.for_block().b));

    for (AttributeContext attribute : ctx.attribute()) {
      expressions.add(visit(attribute));
    }

    return new TagNode(tags.get("for"), expressions.toArray(new LNode[expressions.size()]));
  }

  // for_range
  //  : tagStart ForStart Id In OPar expr DotDot expr CPar attribute* TagEnd
  //    block
  //    tagStart ForEnd TagEnd
  //  ;
  @Override
  public LNode visitFor_range(For_rangeContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();
    expressions.add(new AtomNode(false));

    expressions.add(new AtomNode(ctx.Id().getText()));
    expressions.add(visit(ctx.from));
    expressions.add(visit(ctx.to));

    expressions.add(visitBlock(ctx.block()));

    for (AttributeContext attribute : ctx.attribute()) {
      expressions.add(visit(attribute));
    }

    return new TagNode(tags.get("for"), expressions.toArray(new LNode[expressions.size()]));
  }

  // attribute
  //  : Id Col expr
  //  ;
  @Override
  public LNode visitAttribute(AttributeContext ctx) {
    return new AttributeNode(new AtomNode(ctx.Id().getText()), visit(ctx.expr()));
  }

  // table_tag
  //  : tagStart TableStart Id In lookup attribute* TagEnd block tagStart TableEnd TagEnd
  //  ;
  @Override
  public LNode visitTable_tag(Table_tagContext ctx) {

    List<LNode> expressions = new ArrayList<LNode>();

    expressions.add(new AtomNode(ctx.Id().getText()));
    expressions.add(visit(ctx.lookup()));
    expressions.add(visitBlock(ctx.block()));

    for (AttributeContext attribute : ctx.attribute()) {
      expressions.add(visit(attribute));
    }

    return new TagNode(tags.get("tablerow"), expressions.toArray(new LNode[expressions.size()]));
  }

  // capture_tag
  //  : tagStart CaptureStart Id TagEnd block tagStart CaptureEnd TagEnd  #capture_tag_Id
  //  | ...
  //  ;
  @Override
  public LNode visitCapture_tag_Id(Capture_tag_IdContext ctx) {
    return new TagNode(tags.get("capture"), new AtomNode(ctx.Id().getText()), visitBlock(ctx.block()));
  }

  // capture_tag
  //  : ...
  //  | tagStart CaptureStart Str TagEnd block tagStart CaptureEnd TagEnd #capture_tag_Str
  //  ;
  @Override
  public LNode visitCapture_tag_Str(Capture_tag_StrContext ctx) {
    return new TagNode(tags.get("capture"), fromString(ctx.Str()), visitBlock(ctx.block()));
  }

  // include_tag
  //  : tagStart Include file_name_or_output (With Str)? TagEnd
  //  ;
  @Override
  public LNode visitInclude_tag(Include_tagContext ctx) {
    if (ctx.Str() != null)  {
      return new TagNode(tags.get("include"), visit(ctx.file_name_or_output()), fromString(ctx.Str()));
    }
    return new TagNode(tags.get("include"), visit(ctx.file_name_or_output()));
  }

  // file_name_or_output
  //  : Str                          #file_name_or_output_Str
  //  | ...
  //  ;
  @Override
  public LNode visitFile_name_or_output_Str(File_name_or_output_StrContext ctx) {
    return fromString(ctx.Str());
  }

  // file_name_or_output
  //  : ...
  //  | output                       #file_name_or_output_output
  //  | ...
  //  ;
  @Override
  public LNode visitFile_name_or_output_output(File_name_or_output_outputContext ctx) {

    if (this.parseSettings.flavor != Flavor.JEKYLL)
      throw new LiquidException("`{% include ouput %}` can only be used for Flavor.JEKYLL", ctx);

    return visitOutput(ctx.output());
  }

  // file_name_or_output
  //  : ...
  //  | other_than_tag_end_out_start #file_name_or_output_other_than_tag_end_out_start
  //  ;
  //
  // other_than_tag_end_out_start
  //  : ~(TagEnd | OutStart | OutStart2)+
  //  ;
  @Override
  public LNode visitFile_name_or_output_other_than_tag_end_out_start(File_name_or_output_other_than_tag_end_out_startContext ctx) {

    if (this.parseSettings.flavor != Flavor.JEKYLL)
      throw new LiquidException("`{% include other_than_tag_end_out_start %}` can only be used for Flavor.JEKYLL", ctx);

    return new AtomNode(ctx.other_than_tag_end_out_start().getText());
  }

  // output
  //  : outStart expr filter* OutEnd
  //  ;
  @Override
  public OutputNode visitOutput(OutputContext ctx) {

    OutputNode node = new OutputNode(visit(ctx.expr()));

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

    AtomNode idNode = new AtomNode(ctx.Id().getText());
    LNode exprNode = visit(ctx.expr());
    List<LNode> allNodes = new ArrayList<>();

    allNodes.add(idNode);
    allNodes.add(exprNode);

    for (FilterContext filterContext : ctx.filter()) {
      allNodes.add(visit(filterContext));
    }

    return new TagNode(tags.get("assign"), allNodes);
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
    return new AtomNode(new Double(ctx.DoubleNum().getText()));
  }

  // term
  //  : ...
  //  | LongNum        #term_LongNum
  //  | ...
  //  ;
  @Override
  public LNode visitTerm_LongNum(Term_LongNumContext ctx) {
    return new AtomNode(new Long(ctx.LongNum().getText()));
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
        node.add(new LookupNode.Index(visit(index.expr())));
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
