package com.app.lint_checks;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.app.lint_checks.kotlin.NoStateHolderMethodsDetector.ISSUE;

public class Registry extends IssueRegistry {

    public Registry() {
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    @Override
    public List<Issue> getIssues() {
        List<Issue> result = new ArrayList<>();
        result.add(ISSUE);
        return result;
    }
}