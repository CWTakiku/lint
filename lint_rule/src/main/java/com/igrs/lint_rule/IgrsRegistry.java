package com.igrs.lint_rule;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.client.api.Vendor;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public final class IgrsRegistry extends IssueRegistry {


    @Override
    public List<Issue> getIssues() {
        return Arrays.asList(LoggerUsageDetector.ISSUE,PrintDetector.ISSUE,
                ContainsChineseDetector.ISSUE,
                InsecureRandomDetector.ISSUE,
                UnsafeHttpUrlDetector.ISSUE);
    }

    @Nullable
    @Override
    public Vendor getVendor() {
        return new Vendor("takiku","chengwl@igrslab.com");
    }
}