package core.logging;

/**
 * Interface to be implemented upon each Log object.
 *
 * create a Log instance through createLog(String category)
 *
 * @author Gother Van Roey
 */
public interface LogProvider {

    /**
     * Creates a Log instance.
     * @return a log instance
     */
    public Log createLog ( String category );

}
