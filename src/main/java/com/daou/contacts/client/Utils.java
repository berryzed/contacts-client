package com.daou.contacts.client;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@UtilityClass
public class Utils {

    public static String encodeURLComponent(String component) {
        try {
            return URLEncoder.encode(component, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("You are dumm enough that you cannot spell UTF-8, are you?");
        }
    }

    public static String queryStringFromPageable(Pageable p) {
        StringBuilder ans = new StringBuilder();
        ans.append("page=");
        ans.append(encodeURLComponent(p.getPageNumber() + ""));

        // Sorting is specified
        for (Sort.Order o : p.getSort()) {
            ans.append("&sort=");
            ans.append(encodeURLComponent(o.getProperty()));
            ans.append(",");
            ans.append(encodeURLComponent(o.getDirection().name()));
        }

        return ans.toString();
    }
}
