package core.storage.memory;

import core.storage.Storage;
import core.storage.spi.CookieDAOSPI;
import core.storage.spi.StorageSPI;
import core.model.SiteInternal;
import api.model.Cookie;
import api.model.Site;

import java.util.*;
import java.sql.*;

/**
 * $Id: CookieDAOImpl.java,v 1.2 2003/04/11 16:37:06 vanrogu Exp $
 */
class CookieDAOImpl implements CookieDAOSPI {

    protected StorageSPI storage;
    protected Map cookies;

    public CookieDAOImpl ( StorageSPI storage ) {
        this.storage = storage;
        this.cookies = new HashMap ( );
    }

    public Cookie[] find(int id) {
        Cookie[] cookies = (Cookie[]) this.cookies.get(new Integer(id));
        if ( cookies == null ) {
            cookies = new Cookie[0];
        }
        return cookies;
    }

    public void save(int id, Cookie[] cookies) {
        this.cookies.put(new Integer(id), cookies);
    }

}
