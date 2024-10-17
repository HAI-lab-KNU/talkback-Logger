package com.google.android.libraries.accessibility.utils.log;

public class ViewNodeInfo{
    private static NoodeInfo parentNodeInfo;
    private static String className;
    private static String text;
    private static String contentDescription;
    private static Rect boundInScreen;
    private static List<NodeInfo> childNodes;

    public ViewNodeInfo(NoodeInfo parentNodeInfo,String className,String text,contentDescription,Rect boundInScreen,List<NodeInfo> childNodes) {
        this.parentNodeInfo = parentNodeInfo;
        this.className=className;
        this.text=text;
        this.contentDescription=contentDescription;
        this.boundInScreen=boundInScreen;
        this.childNodes=childNodes;
    }

    public static NoodeInfo getParentNodeInfo() {
        return parentNodeInfo;
    }

    public static String getClassName() {
        return className;
    }

    public static String getText() {
        return text;
    }

    public static String getContentDescription() {
        return contentDescription;
    }

    public static Rect getBoundInScreen() {
        return boundInScreen;
    }

    public static List<NodeInfo> getChildNodes() {
        return childNodes;
    }
}