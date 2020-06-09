package com.app.lint_checks.kotlin;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UMethod;

import java.util.ArrayList;
import java.util.List;

//Детектор определяет классы, имплементирующие StateHolder,
//но имеющие пустые тела методов saveState и restoreState
@SuppressWarnings("UnstableApiUsage")
public class NoStateHolderMethodsDetector extends Detector implements Detector.UastScanner {
    private static String STATE_HOLDER = "com.app.mscorebase.appstate.StateHolder";
    private static String SAVE_STATE = "saveState";
    private static String RESTORE_STATE = "restoreState";

    private static String ID = "NoStateHolderMethodsDetector";
    private static String SHORT_DESCRIPTION = "Пустой метод StateHolder-а";
    private static String EXPLANATION = "Эта проверка обнаруживает методы StateHolder-а, " +
            "не содержащие логики сохранения/восстановления состояни.";

    public static Issue ISSUE = Issue.create(ID, SHORT_DESCRIPTION, EXPLANATION, Category.CORRECTNESS,
            6, Severity.WARNING, new Implementation(NoStateHolderMethodsDetector.class, Scope.JAVA_FILE_SCOPE));

    public NoStateHolderMethodsDetector() {}

    @Override
    public List<String> getApplicableMethodNames() {
        List<String> result =  new ArrayList<>();
        result.add(SAVE_STATE);
        result.add(RESTORE_STATE);
        return result;
    }

    @Override
    public void visitMethod(JavaContext context, JavaElementVisitor visitor,
                            @NotNull PsiMethodCallExpression call, PsiMethod method) {
        JavaEvaluator evaluator = context.getEvaluator();
        PsiClass cls = method.getContainingClass();
        if (cls != null) {
            boolean isStateHolder = evaluator.implementsInterface(cls, STATE_HOLDER, false);
            if (isStateHolder && method.getBody() != null) {
                if (method.getBody().isEmpty()) {
                    context.report(ISSUE,
                            context.getLocation(method),
                            SHORT_DESCRIPTION
                    );
                }
            }
        }
    }

    @Override
    public UElementHandler createUastHandler(JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitMethod(@NotNull UMethod node) {
                JavaEvaluator evaluator = context.getEvaluator();
                PsiClass cls = node.getContainingClass();
                if (cls != null) {
                    boolean isStateHolder = evaluator.implementsInterface(cls, STATE_HOLDER, false);
                    if (isStateHolder && node.getBody() != null) {
                        if (node.getBody().isEmpty()) {
                            context.report(ISSUE, context.getLocation(node), SHORT_DESCRIPTION);
                        }
                    }
                }
            }
        };
    }
}