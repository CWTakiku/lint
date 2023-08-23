package com.igrs.lint_rule;

import com.android.tools.lint.detector.api.*;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;

import org.jetbrains.uast.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InsecureRandomDetector extends Detector implements Detector.UastScanner {

    private static final String RANDOM_CLASS_NAME = "java.util.Random";
    private static final Set<String> INSECURE_METHODS = new HashSet<>();

    static {
        INSECURE_METHODS.add("nextInt");
        INSECURE_METHODS.add("nextLong");
        INSECURE_METHODS.add("nextFloat");
        INSECURE_METHODS.add("nextDouble");
    }

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("nextInt", "nextLong", "nextFloat", "nextDouble");
    }

    @Override
    public void visitMethodCall(JavaContext context, UCallExpression call, PsiMethod method) {
        if (isInsecureRandomMethod(call)) {
            context.report(ISSUE, call, context.getLocation(call),
                    "Avoid using insecure random number generator");
        }
    }


    private boolean isInsecureRandomMethod(UCallExpression call) {
        UExpression receiver = call.getReceiver();
        boolean isTargetClassName = false;
        UElement parent = call.getUastParent();
        if (parent instanceof UQualifiedReferenceExpression) {
            UQualifiedReferenceExpression qualifiedExpression = (UQualifiedReferenceExpression) parent;
            UExpression receiver1 = qualifiedExpression.getReceiver();
            PsiType receiverType = receiver1.getExpressionType();
            if (receiverType instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) receiverType;
                PsiClass psiClass = classType.resolve();
                if (psiClass != null) {
                    String className = psiClass.getQualifiedName();
                    if (className != null && className.equals(RANDOM_CLASS_NAME)) {
                        isTargetClassName = true;
                    }
                }
            }
        }
        if (!isTargetClassName) {
            return false;
        }
        if (receiver instanceof UReferenceExpression) {
            String methodName = call.getMethodName();
            return INSECURE_METHODS.contains(methodName);
        }
        return false;
    }

    public static final Issue ISSUE = Issue.create(
            "InsecureRandomUsage",
            "Insecure random number generator usage",
            "Avoid using insecure random number generator methods such as nextInt(), nextLong(), nextFloat(), and nextDouble().",
            Category.SECURITY,
            5,
            Severity.WARNING,
            new Implementation(InsecureRandomDetector.class, Scope.JAVA_FILE_SCOPE)
    );
}