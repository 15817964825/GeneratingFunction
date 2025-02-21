package com.ayplugins.generatingfunction.generate;

import com.ayplugins.generatingfunction.parser.CommentParser;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;

/****
 @author Ay
 @date  - 16:04
 @version 1.0
 */
public class AyMethod {


    public static void generateMethod(Project project, PsiClass psiClass, CommentParser.ParseResult parseResult, Editor editor) {
        String methodText = buildMethodString(parseResult);
        WriteCommandAction.runWriteCommandAction(project,  () -> {
            try {
                JavaPsiFacade facade = JavaPsiFacade.getInstance(project);
                PsiElementFactory factory = facade.getElementFactory();
                PsiMethod newMethod = factory.createMethodFromText(methodText,  psiClass);
                // 智能定位插入点（类体末尾）
                PsiElement anchor = findDynamicInsertionAnchor(psiClass,editor);
                psiClass.addBefore(newMethod,  anchor);
            } catch (IncorrectOperationException e) {
                Messages.showErrorDialog(
                        "方法生成失败: " + e.getMessage()  + "\nMethod Text:\n" + methodText,
                        "生成错误"
                );
            }
        });
    }

    private static PsiElement findInsertionAnchor(PsiClass psiClass) {
        // 优先在最后一个方法后插入
        PsiMethod[] methods = psiClass.getMethods();
        return (methods.length  > 0) ?
                methods[methods.length - 1].getNextSibling() :
                psiClass.getLBrace();
    }

    /**
     * 智能定位插入锚点（支持选中区域感知）
     * @param psiClass 目标类
     * @param editor 当前编辑器实例
     * @return 最佳插入位置
     */
    public static PsiElement findDynamicInsertionAnchor(PsiClass psiClass, Editor editor) {
        // 优先处理选中区域
        if (editor != null && editor.getSelectionModel().hasSelection())  {
            int offset = editor.getSelectionModel().getSelectionStart();
            PsiElement selectedElement = psiClass.findElementAt(offset);
            // 递归查找有效父节点（兼容多层级选中）
            PsiElement contextElement = PsiTreeUtil.getParentOfType(selectedElement,
                    PsiMethod.class,  PsiField.class,  PsiComment.class);
            if (contextElement != null) {
                // 获取选中元素后续插入点
                return findAnchorAfterElement(contextElement);
            }
        }
        // 无选中时回退默认逻辑
        return getDefaultAnchor(psiClass);
    }


    /**
     * 在指定元素后寻找有效插入点
     */
    private static PsiElement findAnchorAfterElement(PsiElement element) {
        PsiElement nextSibling = element.getNextSibling();
        // 跳过空白/换行等无效元素
        while (nextSibling instanceof PsiWhiteSpace || nextSibling instanceof PsiComment) {
            nextSibling = nextSibling.getNextSibling();
        }
        // 如果后续无有效元素，则插入到类体末尾
        return (nextSibling != null) ? nextSibling : element.getParent().getLastChild();
    }

    /**
     * 默认插入策略（原逻辑增强版）
     */
    private static PsiElement getDefaultAnchor(PsiClass psiClass) {
        PsiMethod[] methods = psiClass.getMethods();
        PsiField[] fields = psiClass.getFields();

        // 优先在最后一个方法后插入
        if (methods.length  > 0) {
            return methods[methods.length - 1].getNextSibling();
        }
        // 其次在最后一个字段后插入
        else if (fields.length  > 0) {
            return fields[fields.length - 1].getNextSibling();
        }
        // 最后在类体开始处插入
        else {
            return psiClass.getLBrace().getNextSibling();
        }
    }


    /**
     * 构建方法字符串
     */
    public static String buildMethodString(CommentParser.ParseResult parseResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(parseResult.returnType).append(" ").append(parseResult.methodName).append("(");
        for (int i = 0; i < parseResult.params.size(); i++) {
            CommentParser.Param param = parseResult.params.get(i);
            sb.append(param.type).append(" ").append(param.name);
            if (i < parseResult.params.size() - 1) sb.append(", ");
        }
        sb.append(")");
        if (!parseResult.exceptions.isEmpty()) {
            sb.append(" throws ").append(String.join(", ", parseResult.exceptions));
        }
        if(!parseResult.returnType.equals("void") ){
            sb.append( " { \n return null;    // TODO: Auto-generated method stub\n}");
        }else{
            sb.append(" {\n    // TODO: Auto-generated method stub\n}");
        }
        return sb.toString();
    }

}
