/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package binarytree.parser;

/**
 *
 * @author Administrator
 */
public class ParserRecursive extends ParserBase {

    /**
     * Returns the last position of c outside of all brackets in the string s.
     * @param s the string in which is searched for
     * @param c the character to find
     * @return the index of the first occurrence of the character
     */
    private int find(String s, char c)
    {
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--)
        {
            if (s.charAt(i) == '(') count++;
            if (s.charAt(i) == ')') count--;
            if (s.charAt(i) == c && count == 0) return i;
        }
        return -1;
    }
        
    @Override
    public double evaluate(String s) throws ParserException
    {
        StringBuffer sBuf = new StringBuffer(s);
        sBuf = deleteSpaces(sBuf);
        return evaluateIntern(sBuf.toString());
    }

    private double evaluateIntern(String s) throws ParserException
    {
        int index;
        
        // this should not happen...
        if (s.isEmpty())
            throw new ParserException("Empty string");

        // if we a minus/plus sign before a the first number, insert a zero
        // to make it a valid expression with two operands
        if (s.charAt(0) == '-' || s.charAt(0) == '+')
            s = '0' + s;

        if ((index = find(s, '-')) >= 0)
        {
            return (evaluateIntern(s.substring(0, index)) -
                    evaluateIntern(s.substring(index+1, s.length())));
        }
        else if ((index = find(s, '+')) >= 0)
        {
            return (evaluateIntern(s.substring(0, index)) +
                    evaluateIntern(s.substring(index+1, s.length())));
        }
        else if ((index = find(s, '*')) >= 0)
        {
            return (evaluateIntern(s.substring(0, index)) *
                    evaluateIntern(s.substring(index+1, s.length())));
        }
        else if ((index = find(s, '/')) >= 0)
        {
            return (evaluateIntern(s.substring(0, index)) /
                    evaluateIntern(s.substring(index+1, s.length())));
        }

        /* if we reach this point, then our expression does not contain
           any operands anymore. But maybe the string begins with a bracket,
           then we have to eliminate those brackets until the string consists
           just of numericals.
         */

        // remove brackets at the beginning and end
        if (s.charAt(0) == '(')
        {
            if (s.charAt(s.length()-1) == ')')
                return (evaluate(s.substring(1, s.length()-1)));
            else
                throw new ParserException("Invalid brackets: " + s);
        }

        // now finally convert the string (that hopefully consists of number)
        // to a double type.
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            throw new ParserException("String to number parsing exception: " + s);
        }

    }

}
