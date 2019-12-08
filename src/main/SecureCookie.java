package main;

import javax.servlet.http.Cookie;

public class SecureCookie extends Cookie {
    public SecureCookie (String name, String value, int lifeTime, boolean secure) {
        super(name, value);
        this.setMaxAge(lifeTime);
        this.setHttpOnly(secure);
//        this.setSecure(secure);
    }
}
