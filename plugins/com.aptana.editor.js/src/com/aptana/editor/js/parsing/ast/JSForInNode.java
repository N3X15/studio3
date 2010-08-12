package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;

public class JSForInNode extends JSNode
{
	private Symbol _leftParenthesis;
	private Symbol _in;
	private Symbol _rightParenthesis;
	
	/**
	 * JSForInNode
	 * 
	 * @param children
	 */
	public JSForInNode(Symbol leftParenthesis, JSNode initializer, Symbol in, JSNode expression, Symbol rightParenthesis, JSNode body)
	{
		super(JSNodeTypes.FOR_IN, initializer, expression, body);
		
		this._leftParenthesis = leftParenthesis;
		this._in = in;
		this._rightParenthesis = rightParenthesis;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getBody
	 * 
	 * @return
	 */
	public IParseNode getBody()
	{
		return this.getChild(2);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		return this.getChild(1);
	}

	/**
	 * getIn
	 * 
	 * @return
	 */
	public Symbol getIn()
	{
		return this._in;
	}
	
	/**
	 * getInitialization
	 * 
	 * @return
	 */
	public IParseNode getInitializer()
	{
		return this.getChild(0);
	}
	
	/**
	 * getLeftParenthesis
	 * 
	 * @return
	 */
	public Symbol getLeftParenthesis()
	{
		return this._leftParenthesis;
	}
	
	/**
	 * getRightParenthesis
	 * 
	 * @return
	 */
	public Symbol getRightParenthesis()
	{
		return this._rightParenthesis;
	}
}
