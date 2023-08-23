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
import com.intellij.psi.PsiMethod;

import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UExpression;
import org.jetbrains.uast.ULiteralExpression;
import org.jetbrains.uast.UVariable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnsafeHttpUrlDetector extends Detector implements Detector.UastScanner {
    private static final String ISSUE_ID = "UnsafeHttpUrl";
    private static final String ISSUE_DESCRIPTION = "Unsafe HTTP URL";
    private static final String ISSUE_EXPLANATION = "Avoid using unsafe HTTP URLs in your code.";
    private static final Category ISSUE_CATEGORY = Category.SECURITY;
    private static final int ISSUE_PRIORITY = 9;
    private static final Severity ISSUE_SEVERITY = Severity.WARNING;

    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("\\bhttp://");

    public static final Issue ISSUE = Issue.create(
            ISSUE_ID,
            ISSUE_DESCRIPTION,
            ISSUE_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            new Implementation(UnsafeHttpUrlDetector.class, Scope.JAVA_FILE_SCOPE)
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
                if (node.getValue() instanceof String && containsHttp((String) node.getValue(),context,node)) {
                    context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            ISSUE_DESCRIPTION + node.getValue());
                }
            }
        };
    }

   boolean containsHttp(String url,JavaContext context,ULiteralExpression literalExpression){
       Matcher matcher = HTTP_URL_PATTERN.matcher(url);
       if (matcher.find()) {
           Location location = context.getLocation(literalExpression);
           context.report(ISSUE, literalExpression, location, ISSUE_DESCRIPTION);
           return true;
       }
       return false;
   }
}