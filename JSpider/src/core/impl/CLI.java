package core.impl;


import java.io.File;

import main.Constants;

/**
 * $Id: CLI.java,v 1.1 2003/03/24 16:58:35 vanrogu Exp $
 */
public class CLI {

    public static void printSignature ( ) {
        System.err.println(Constants.VERSIONSTRING);
        System.err.println("Build: " + Constants.BUILDTIMESTAMP);
        System.err.println("Started from " + new File("."));
    }

}
