package tool.impl;

import tool.Parameters;
import tool.Tool;

/**
 * $Id: BaseToolImpl.java,v 1.3 2003/04/01 19:44:42 vanrogu Exp $
 */
abstract class BaseToolImpl implements Tool {

    protected Parameters parameters;

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public String getUsage() {
        return "";
    }

    public int getParameterCount ( ) {
        return 0;
    }

    public void initialize() {
    }

    public void shutdown() {
    }

}
