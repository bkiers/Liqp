/*
 * Copyright (c) 2010 by Bart Kiers
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Project      : Liqp; a Liquid Template grammar/parser
 * Developed by : Bart Kiers, bart@big-o.nl
 */
tree grammar LiquidWalker;

options {
  tokenVocab=Liquid;
  ASTLabelType=CommonTree;
}

@header {
  package liqp.parser;

  import liqp.nodes.*;
  import liqp.tags.*;
}

walk returns [LNode node]
 : block {$node = $block.node;}
 ;

block returns [BlockNode node]
@init{$node = new BlockNode();}
 : ^(BLOCK (atom {$node.add($atom.node);})*)
 ;

atom returns [LNode node]
 : tag        {$node = $tag.node;}
 | output     {$node = $output.node;}
 | assignment {$node = $assignment.node;}
 | PLAIN      {$node = new PlainNode($PLAIN.text);}
 ;

tag returns [LNode node]
 : RAW              {if(true) throw new RuntimeException("tag.RAW");}
 | COMMENT          {if(true) throw new RuntimeException("tag.COMMENT");}
 | if_tag           {if(true) throw new RuntimeException("tag.if_tag");}
 | unless_tag       {if(true) throw new RuntimeException("tag.unless_tag");}
 | case_tag         {if(true) throw new RuntimeException("tag.case_tag");}
 | cycle_tag        {if(true) throw new RuntimeException("tag.cycle_tag");}
 | for_tag          {if(true) throw new RuntimeException("tag.for_tag");}
 | table_tag        {if(true) throw new RuntimeException("tag.table_tag");}
 | capture_tag      {if(true) throw new RuntimeException("tag.capture_tag");}
 | include_tag      {if(true) throw new RuntimeException("tag.include_tag");}
 | custom_tag       {$node = $custom_tag.node;}
 | custom_tag_block {$node = $custom_tag_block.node;}
 ;

if_tag returns [LNode node]
 : ^(IF expr block ^(ELSE block?)) {if(true) throw new RuntimeException("if_tag");}
 ;

unless_tag returns [LNode node]
 : ^(UNLESS expr block ^(ELSE block?)) {if(true) throw new RuntimeException("unless_tag");}
 ;

case_tag returns [LNode node]
 : ^(CASE expr when_tag+ ^(ELSE block?)) {if(true) throw new RuntimeException("case_tag");}
 ;

when_tag returns [LNode node]
 : ^(WHEN expr block) {if(true) throw new RuntimeException("when_tag");}
 ;

cycle_tag returns [LNode node]
 : ^(CYCLE ^(GROUP expr?) expr+) {if(true) throw new RuntimeException("cycle_tag");}
 ;

for_tag returns [LNode node]
 : for_array {if(true) throw new RuntimeException("for_tag.for_array");}
 | for_range {if(true) throw new RuntimeException("for_tag.for_range");}
 ;

// attributes must be 'limit' or 'offset'!
for_array returns [LNode node]
 : ^(FOR_ARRAY Id lookup ^(ATTRIBUTES attribute*) block) {if(true) throw new RuntimeException("for_array");}
 ;

attribute returns [LNode node]
 : ^(Id expr) {if(true) throw new RuntimeException("attribute");}
 ;

for_range returns [LNode node]
 : ^(FOR_RANGE Id expr expr block) {if(true) throw new RuntimeException("for_range");}
 ;

// attributes must be 'limit' or 'cols'!
table_tag returns [LNode node]
 : ^(TABLE Id Id ^(ATTRIBUTES attribute*) block) {if(true) throw new RuntimeException("table");}
 ;

capture_tag returns [LNode node]
 : ^(CAPTURE Id block) {if(true) throw new RuntimeException("capture");}
 ;

include_tag returns [LNode node]
 : ^(INCLUDE Str ^(WITH Str?)) {if(true) throw new RuntimeException("include_tag");}
 ;

custom_tag returns [LNode node]
@init{List<LNode> expressions = new ArrayList<LNode>();}
 : ^(CUSTOM_TAG Id (expr {expressions.add($expr.node);})*)
    {$node = new TagNode($Id.text, expressions.toArray(new LNode[expressions.size()]));}
 ;

custom_tag_block returns [LNode node]
@init{List<LNode> expressions = new ArrayList<LNode>();}
 : ^(CUSTOM_TAG_BLOCK Id (expr {expressions.add($expr.node);})* block {expressions.add($block.node);})
    {$node = new TagNode($Id.text, expressions.toArray(new LNode[expressions.size()]));}
 ;

output returns [OutputNode node]
 : ^(OUTPUT expr {$node = new OutputNode($expr.node);} ^(FILTERS (filter {$node.addFilter($filter.node);})*))
 ;

filter returns [FilterNode node]
 : ^(FILTER Id {$node = new FilterNode($Id.text);} ^(PARAMS params[$node]?))
 ;

params[FilterNode node]
 : (expr {$node.add($expr.node);})+
 ;

assignment returns [LNode node]
 : ^(ASSIGNMENT Id expr) {$node = new TagNode("assign", new IdNode($Id.text), $expr.node);}
 ;

expr returns [LNode node]
 : ^(Or expr expr)   {if(true) throw new RuntimeException("expr.Or");}
 | ^(And expr expr)  {if(true) throw new RuntimeException("expr.And");}
 | ^(Eq expr expr)   {if(true) throw new RuntimeException("expr.Eq");}
 | ^(NEq expr expr)  {if(true) throw new RuntimeException("expr.NEq");}
 | ^(LtEq expr expr) {if(true) throw new RuntimeException("expr.LtEq");}
 | ^(Lt expr expr)   {if(true) throw new RuntimeException("expr.Lt");}
 | ^(GtEq expr expr) {if(true) throw new RuntimeException("expr.GtEq");}
 | ^(Gt expr expr)   {if(true) throw new RuntimeException("expr.Gt");}
 | Num               {$node = new AtomNode(new Double($Num.text));}
 | Str               {$node = new AtomNode($Str.text);}
 | True              {if(true) throw new RuntimeException("expr.True");}
 | False             {if(true) throw new RuntimeException("expr.False");}
 | Nil               {if(true) throw new RuntimeException("expr.Nil");}
 | NO_SPACE          {$node = new AtomNode($NO_SPACE.text);}
 | lookup            {$node = $lookup.node;}
 ;

lookup returns [LookupNode node]
@init{$node = new LookupNode();}
 : ^(LOOKUP (Id {$node.add($Id.text);})+)
 ;
