package com.app.lint_checks

import com.android.tools.lint.client.api.IssueRegistry
import com.app.lint_checks.kotlin.NoStateHolderMethodsDetector.Companion.ISSUE

class Registry: IssueRegistry() {
    override val issues = listOf(ISSUE)

}