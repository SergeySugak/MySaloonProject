package com.app.lint_checks.kotlin

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import org.jetbrains.uast.UMethod

//Детектор определяет классы, имплементирующие StateHolder,
//но имеющие пустые тела методов saveState и restoreState
@Suppress("UnstableApiUsage")
class NoStateHolderMethodsDetector: Detector(), SourceCodeScanner {


    override fun getApplicableMethodNames() = listOf(SAVE_STATE, RESTORE_STATE)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object: UElementHandler() {
            override fun visitMethod(node: UMethod) {
                val evaluator = context.evaluator
                val cls = node.containingClass

                cls?.let {
                    val isStateHolder = evaluator.implementsInterface(it, STATE_HOLDER, false)
                    if (isStateHolder) {
                        if (node.body?.isEmpty == true) {
                            context.report(ISSUE,
                                context.getLocation(node),
                                SHORT_DESCRIPTION
                            )
                        }
                    }
                }

            }
        }
    }

    override fun visitMethod(context: JavaContext, visitor: JavaElementVisitor?,
                             call: PsiMethodCallExpression, method: PsiMethod) {
        println(method.containingClass.toString())
        val evaluator = context.evaluator
        val cls = method.containingClass
        cls?.let {
            val isStateHolder = evaluator.implementsInterface(it, STATE_HOLDER, false)
            if (isStateHolder) {
                if (method.body?.isEmpty == true) {
                    context.report(ISSUE,
                        context.getLocation(method),
                        SHORT_DESCRIPTION
                    )
                }
            }
        }
    }

    companion object {
        private const val STATE_HOLDER = "com.app.mscorebase.appstate.StateHolder"
        private const val SAVE_STATE = "saveState"
        private const val RESTORE_STATE = "restoreState"

        private const val ID = "NoStateHolderMethodsDetector"
        private const val SHORT_DESCRIPTION = "Пустой метод StateHolder-а"
        private const val EXPLANATION = """Эта проверка обнаруживает методы StateHolder-а, 
                не содержащие логики сохранения/восстановления состояни."""

        @JvmField
        val ISSUE: Issue = Issue.create(
            id = ID,
            briefDescription = SHORT_DESCRIPTION,
            explanation = EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                NoStateHolderMethodsDetector::class.java,
                Scope.JAVA_FILE_SCOPE)
        )
    }
}