/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package binarytree.parser;

import java.util.Stack;

/**
 * This is the base class for all parsers - they must provide the implemenation
 * for evaluate(). Also provides some helper functions.
 * @author Sunshine2k
 */
public abstract class ParserBase {

    public abstract double evaluate(String s) throws ParserException;

    public StringBuffer deleteSpaces(StringBuffer sbuf)
    {
        for (int i = 0; i < sbuf.length(); i++)
        {
            if (sbuf.charAt(i) == ' ')
            {
                    sbuf.deleteCharAt(i);
                    i--;
            }
        }
        return sbuf;
    }

    /**
     * Creates the postfix representation as string from the given term.
     * @param s the term string in infix notation
     * @return the term in postfix notation
     * @throws ParserException
     */
    public String createPostfix(String s) throws ParserException
    {
        Stack stack = new Stack<String>();
        StringBuffer resStr = new StringBuffer();

        char c;
        int strpos = 0;
        while (strpos < s.length())
        {
            // get the current character
            c = s.charAt(strpos);
            if (c == ')')
            {
                //resStr.append(stack.pop());
                while (!stack.empty() && !stack.peek().equals('('))
                {
                    resStr.append(stack.pop());
                }
                if (!stack.empty())
                    stack.pop();
            }
            else if (c == '+')
            {
                if (!stack.empty() && (stack.peek().equals('+') || stack.peek().equals('-') ||
                                       stack.peek().equals('*') || stack.peek().equals('/')))
                {
                    resStr.append(stack.pop());
                }
                stack.push(c);
            }
            else if (c == '-')
            {
                if (!stack.empty() && (stack.peek().equals('+') || stack.peek().equals('-') ||
                                       stack.peek().equals('*') || stack.peek().equals('/')))
                {
                    resStr.append(stack.pop());
                }
                stack.push(c);
            }
            else if (c == '*')
            {
                if (!stack.empty() && (stack.peek().equals('*') || stack.peek().equals('/')))
                {
                    resStr.append(stack.pop());
                }
                stack.push(c);
            }
            else if (c == '/')
            {
                if (!stack.empty() && (stack.peek().equals('*') || stack.peek().equals('/')))
                {
                    resStr.append(stack.pop());
                }
                stack.push(c);
            }
            else if (c == '(')
            {
                // just skip open bracket
                stack.push(c);
            }
            else if (c >= '0' && c <= '9')
            {
                // process numericals
                while ( (c >= '0' && c <= '9') || c == '.')
                {
                    resStr.append(c);
                    if (strpos+1 < s.length())
                        c = s.charAt(++strpos);
                    else
                    {
                        // abort while loop if we reach end of string
                        c = 0;
                        strpos = s.length();
                    }
                }

                // inside while loop strpos is incremented one time too often
                strpos--;
            }
            else
            {
                throw new ParserException("Invalid symbol: " + c);
            }
            // make a right step inside the string
            strpos++;
            // insert a space to differ between consecutive numbers
            resStr.append(" ");
        }

        while (!stack.empty())
        {
            resStr.append(stack.pop());
            resStr.append(" ");
        }
        // delete the space character at the end of the string wrongly
        // added in above while-loop
        resStr.deleteCharAt(resStr.length()-1);

        // this has nothing to do with the infix creation but just eliminates
        // the needless spaces inserted during the infix creation.
        for (int i = 0; i < resStr.length() - 1; i++)
        {
            if (resStr.charAt(i) == ' ' && resStr.charAt(i+1) == ' ')
            {
                resStr.deleteCharAt(i+1);
                i--;
            }
        }

        return resStr.toString();
    }
}
