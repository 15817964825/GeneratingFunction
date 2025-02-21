package com.ayplugins.generatingfunction;

import com.ayplugins.generatingfunction.generate.AyMethod;
import com.ayplugins.generatingfunction.parser.CommentParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
/**
 *
 @author Ay
 @date  - 16:14
 @version 1.0
 */
public class AyGeneratingFunctionAction extends AnAction {




    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Project project = e.getProject();
        if (editor == null || !(psiFile instanceof PsiJavaFile) || project == null) return;
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null) return;
        CommentParser.ParseResult parseResult = CommentParser.parse(selectedText);
        if (parseResult.methodName == null) {
            // 提示错误
            return;
        }
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass == null) return;
        AyMethod.generateMethod(project, psiClass, parseResult,editor);
    }

}
