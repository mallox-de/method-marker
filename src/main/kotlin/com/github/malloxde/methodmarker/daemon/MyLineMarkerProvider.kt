package com.github.malloxde.methodmarker.daemon

import com.github.malloxde.methodmarker.settings.AppSettingsState
import com.github.malloxde.methodmarker.support.SimpleIcons
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.util.containers.stream
import org.jetbrains.kotlin.idea.hierarchy.calls.getCallHierarchyElement
import org.jetbrains.kotlin.idea.search.KotlinSearchUsagesSupport.Companion.canBeResolvedWithFrontEnd
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import java.util.stream.Collector


class MyLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {
        if (element !is PsiReferenceExpression || element.parent !is PsiMethodCallExpression) {
            return
        }

        PsiTreeUtil.processElements(element, PsiElementProcessor { psiElement ->
            if (psiElement is PsiJavaCodeReferenceElement) {
                val resolvedPsiElement = psiElement.resolve()
                if (resolvedPsiElement is PsiClass) {
                    val qualifiedName = resolvedPsiElement.qualifiedName

                    if (qualifiedName.equals(AppSettingsState.instance.className)) {
                        val builder: NavigationGutterIconBuilder<PsiElement> =
                            NavigationGutterIconBuilder.create(SimpleIcons.FILE)
                                .setTargets(resolvedPsiElement)
                                .setTooltipText("Navigate to $qualifiedName")
                        result.add(builder.createLineMarkerInfo(psiElement))
                        false
                    } else true
                } else {
                    true
                }
            } else {
                true
            }
        })
    }
}
