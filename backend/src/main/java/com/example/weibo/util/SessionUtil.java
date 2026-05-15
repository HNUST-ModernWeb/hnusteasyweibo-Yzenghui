package com.example.weibo.util;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static final String USER_ID_KEY = "userId";

    public static Integer getUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object userId = session.getAttribute(USER_ID_KEY);
        return userId != null ? (Integer) userId : null;
    }

    public static void setUserId(HttpSession session, Integer userId) {
        if (session != null) {
            session.setAttribute(USER_ID_KEY, userId);
        }
    }
}