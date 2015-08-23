/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package binarytree.parser;

import java.util.Stack;

/**
 *
 * @author Administrator
 */
public class ParserPostfixStack extends ParserBase {


    private double evaluatePostfix(String s) throws ParserException
    {
        Stack stack = new Stack();
        int strpos = 0;
        char c;
        double x = 0;
        while (strpos < s.length())
        {
            // get the current character
            c = s.charAt(strpos);
            x = 0;
            if (c == '+')
            {
                double x1 = Double.valueOf(stack.pop().toString());
                double x2 = Double.valueOf(stack.pop().toString());
                x = x2 + x1;
                stack.push(x);
            }
            else if (c == '-')
            {
                double x1 = Double.valueOf(stack.pop().toString());
                double x2 = Double.valueOf(stack.pop().toString());
                x = x2 - x1;
                stack.push(x);
            }
            else if (c == '*')
            {
                double x1 = Double.valueOf(stack.pop().toString());
                double x2 = Double.valueOf(stack.pop().toString());
                x = x2 * x1;
                stack.push(x);
            }
            else if (c == '/')
            {
                double x1 = Double.valueOf(stack.pop().toString());
                double x2 = Double.valueOf(stack.pop().toString());
                x = x2 / x1;
                stack.push(x);
            }
            else if (c >= '0' && c <= '9')
            { 
                // process numericals
                // substring with the number at the beginning of the string
                String sub = s.substring(strpos);
                int i;
                // find end of current number in the string
                for (i = 0; i < sub.length(); i++)
                    if (sub.charAt(i) == ' ')
                        sub = sub.substring(0, i);
                // 'sub' contains now just the number
                try {
                    x = Double.parseDouble(sub);
                } catch (NumberFormatException ex) {
                    throw new ParserException("String to number parsing exception: " + s);
                }
                stack.push(x);
                // go on with next token
                strpos += i-1;
            }

            // ignore other symbols and proceed
            strpos++;
        }

        return x;  // equal to "return stack.pop()";
    }

    @Override
    public double evaluate(String s) throws ParserException {
        StringBuffer sBuf = new StringBuffer(s);
        sBuf = deleteSpaces(sBuf);
        String tmpstr = createPostfix(sBuf.toString());
        //System.out.println(tmpstr);
        return evaluatePostfix(tmpstr);
    }
}