package org.board.springboot.util;

import javax.servlet.http.Cookie;

public class CookieUtils {

    public static Cookie getCookie(Cookie[] cookies, String name) {
        if (cookies == null || name == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
