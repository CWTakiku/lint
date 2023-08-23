package com.igrs.lint_rule;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.google.common.collect.ImmutableList;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.PsiVariable;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.ULiteralExpression;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerAddressLintDetector extends Detector implements Detector.UastScanner {

    private static final String SERVER_ADDRESS_PATTERN = "https?://[^\\s/$.?#].[^\\s]*";
    public static final Issue ISSUE = Issue.create(
            "HardcodedServerAddress",
            "Hardcoded Server Address",
            "Avoid hardcoding server addresses in code.",
            Category.CORRECTNESS,
            9,
            Severity.WARNING,
            new Implementation(ServerAddressLintDetector.class, Scope.JAVA_FILE_SCOPE)
    );

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return ImmutableList.of(ULiteralExpression.class);
    }


    @Override
    public UElementHandler createUastHandler(JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitLiteralExpression(ULiteralExpression node) {
                try {
                    if (node.getValue() instanceof String && isIPInChina(context,(String) node.getValue())) {
                        context.report(
                                ISSUE,
                                node,
                                context.getLocation(node),
                                "unsafe url in china" + node.getValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

//    private boolean isInChina(String url){
//        if (isHttpsOrIpAddress(url)){
//           // if ()
//        }
//    }
    public boolean isIPInChina(JavaContext context,String ipAddress) throws UnknownHostException, IOException {
        System.out.println(" isIPInChina "+ipAddress);
         if (isHttpsOrIpAddress(ipAddress)){
             System.out.println("isHttpsOrIpAddress "+ipAddress);
             InputStream inputStream =   getClass().getClassLoader().getResourceAsStream("Country.mmdb");
             if (inputStream!=null){
                 System.out.println(" input");

                 try {
                     DatabaseReader reader =   new DatabaseReader.Builder(inputStream).build();
                     System.out.println(" input1 ");
                     InetAddress ip = InetAddress.getByName(ipAddress);
                     System.out.println(" input2 ");
                     CountryResponse response = null;
                     response = reader.country(ip);
                     System.out.println(" input3 ");
                     Country country = response.getCountry();
                     System.out.println(" input4 ");
                     String name =    country.getName();
                     System.out.println("name "+name);
                 } catch (Exception e) {
                     System.out.println(e.getMessage());
                     e.printStackTrace();
                   //  throw new RuntimeException(e);
                 }

             }else {
                 System.out.println(" input null");
             }

         }
         return false;
    }
    public static boolean isHttpsOrIpAddress(String string) {
        // 匹配 HTTPS 地址
        String httpsPattern = "^https://[\\w.-]+(:\\d+)?(/.*)?$";
        if (Pattern.matches(httpsPattern, string)) {
            return true;
        }

        // 匹配 IP 地址
        String ipPattern = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
        if (Pattern.matches(ipPattern, string)) {
            return true;
        }

        return false;
    }

}