/*
 * Copyright 2002-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JJTree: Do not edit this line. ParserVisitor.java */

package edu.internet2.middleware.grouperClientExt.org.apache.commons.jexl.parser;

public interface ParserVisitor {
    Object visit(SimpleNode node, Object data);

    Object visit(ASTJexlScript node, Object data);

    Object visit(ASTBlock node, Object data);

    Object visit(ASTEmptyFunction node, Object data);

    Object visit(ASTSizeFunction node, Object data);

    Object visit(ASTIdentifier node, Object data);

    Object visit(ASTExpression node, Object data);

    Object visit(ASTAssignment node, Object data);

    Object visit(ASTOrNode node, Object data);

    Object visit(ASTAndNode node, Object data);

    Object visit(ASTBitwiseOrNode node, Object data);

    Object visit(ASTBitwiseXorNode node, Object data);

    Object visit(ASTBitwiseAndNode node, Object data);

    Object visit(ASTEQNode node, Object data);

    Object visit(ASTNENode node, Object data);

    Object visit(ASTLTNode node, Object data);

    Object visit(ASTGTNode node, Object data);

    Object visit(ASTLENode node, Object data);

    Object visit(ASTGENode node, Object data);

    Object visit(ASTAddNode node, Object data);

    Object visit(ASTSubtractNode node, Object data);

    Object visit(ASTMulNode node, Object data);

    Object visit(ASTDivNode node, Object data);

    Object visit(ASTModNode node, Object data);

    Object visit(ASTUnaryMinusNode node, Object data);

    Object visit(ASTBitwiseComplNode node, Object data);

    Object visit(ASTNotNode node, Object data);

    Object visit(ASTNullLiteral node, Object data);

    Object visit(ASTTrueNode node, Object data);

    Object visit(ASTFalseNode node, Object data);

    Object visit(ASTIntegerLiteral node, Object data);

    Object visit(ASTFloatLiteral node, Object data);

    Object visit(ASTStringLiteral node, Object data);

    Object visit(ASTExpressionExpression node, Object data);

    Object visit(ASTStatementExpression node, Object data);

    Object visit(ASTReferenceExpression node, Object data);

    Object visit(ASTIfStatement node, Object data);

    Object visit(ASTWhileStatement node, Object data);

    Object visit(ASTForeachStatement node, Object data);

    Object visit(ASTMethod node, Object data);

    Object visit(ASTArrayAccess node, Object data);

    Object visit(ASTSizeMethod node, Object data);

    Object visit(ASTReference node, Object data);
}