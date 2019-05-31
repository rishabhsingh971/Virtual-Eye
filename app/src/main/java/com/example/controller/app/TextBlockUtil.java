package com.example.controller.app;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextBlockUtil {

    // The difference limit so that two block center are on the same line
    private static final float SAME_LINE_THRESHOLD = 15.0f;
    private static final int MAGIC_NUM_SPACE = 16;
    private static final int MAGIC_NUM_ENDL = 32;
    private static final String TAG = TextBlockUtil.class.getSimpleName();

    private static class CLine {
        String text;
        Rect bBox;
        CLine(String text, int left, int top, int right, int bottom) {
            this.text = text;
            this.bBox = new Rect(left,top, right,bottom);
        }
        CLine(Line line) {
            this.text = line.getValue();
            this.bBox = line.getBoundingBox();
        }
        Rect getBoundingBox() {
            return bBox;
        }

        public void setTop(int top) {
            this.bBox.top = top;
        }

        public void setBottom(int bottom) {
            this.bBox.bottom = bottom;
        }

        public void setLeft(int left) {
            this.bBox.left = left;
        }

        public void setRight(int right) {
            this.bBox.right = right;
        }
        public void setValue(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text+" "+bBox.toShortString();
        }

        public int getLeft() {
            return bBox.left;
        }

        public int getBottom() {
            return bBox.bottom;
        }

        public int getTop() {
            return bBox.top;
        }
    }

    public static List<String> getSortedLines(SparseArray<TextBlock> textBlocks) {
        List<TextBlock> sortedTextBlocks = getSortedTextBlockList(textBlocks, false);
        // List of all the lines in text blocks
        List<Line> textLines = getLineList(sortedTextBlocks);
        // takes care of merging of lines in case of multi column text
        boolean[] merged = new boolean[textLines.size()];

        List<String> sortedLines = new ArrayList<>();
        for (int i = 0; i < textLines.size(); ++i) {
            if (merged[i])
                continue;
            // line at ith position in list
            String lineI = textLines.get(i).getValue();
            // current line
            StringBuilder currentLine = new StringBuilder(lineI);
            int topi = textLines.get(i).getBoundingBox().top;
            int lefti = textLines.get(i).getBoundingBox().left;
            // check and merge lines in same line with ith line
            for (int j = i + 1; j < textLines.size(); j++) {
                int topj = textLines.get(j).getBoundingBox().top;
                int leftj = textLines.get(j).getBoundingBox().left;
                if (inSameLine(topi, topj)) {
                    String lineJ = textLines.get(j).getValue();
                    String a = lineI, b = lineJ;
                    int diff = leftj - textLines.get(i).getBoundingBox().right;
                    if (lefti > leftj) {
                        a = lineJ;
                        b = lineI;
                        diff = lefti - textLines.get(j).getBoundingBox().right;
                        lefti = leftj;
                    }
                    // Merging ith and jth line
                    currentLine = new StringBuilder(a).append(" ");
                    for (int k = 0; k < diff / 16; ++k) {
                        currentLine.append(" ");
                    }
                    currentLine.append(b);
                    merged[j] = true;
                }
            }
            sortedLines.add(currentLine.toString());
        }
        return sortedLines;
    }

    private static List<Line> getLineList(List<TextBlock> textBlocks) {
        Log.e(TAG, LogUtil.prependCallLocation("getLineList: start"));
        List<Line> textLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (TextBlock textBlock : textBlocks) {
            int k = 1;
            for (Text line : textBlock.getComponents()) {
                textLines.add((Line) line);
                float a = ((Line) line).getAngle();
                int l = line.getBoundingBox().left;
                int r = line.getBoundingBox().right;
                int t = line.getBoundingBox().top;
                sb.append(i).append(".").append(k)
//                        .append(" - ").append(line.getBoundingBox().toString())
//                        .append(" Angle - ").append(((Line) line).getAngle())
//                        .append(" new top - ").append(t-(Math.sin(a)*(r-l))).append(" ")
//                        .append(Arrays.toString(line.getCornerPoints())).append(" ")
                        .append(line.getValue()).append("\n");
                k += 1;
            }
            i += 1;
        }
        Log.e(TAG, LogUtil.prependCallLocation("getLineList: " + sb));
        return textLines;
    }

    private static List<TextBlock> getTextBlockList(SparseArray<TextBlock> textBlockSparseArray) {
        List<TextBlock> textBlockList = new ArrayList<>();
        for (int i = 0; i < textBlockSparseArray.size(); i++) {
            textBlockList.add(textBlockSparseArray.valueAt(i));
        }
        return textBlockList;
    }

    private static List<TextBlock> getSortedTextBlockList(SparseArray<TextBlock> textBlockSparseArray, boolean multiColumn) {
        //Moving from SparseArray to List
        List<TextBlock> textBlockList = getTextBlockList(textBlockSparseArray);
        // Sorting List
        Collections.sort(textBlockList, (o1, o2) -> {
            int t1 = o1.getBoundingBox().top;
            int l1 = o1.getBoundingBox().left;
            int t2 = o2.getBoundingBox().top;
            int l2 = o2.getBoundingBox().left;
            if (!multiColumn) {
                return compareCoord(t1, t2, l1, l2);
            } else {
                return compareCoord(l1, l2, t1, t2);
            }
        });
        return textBlockList;
    }

    private static int compareCoord(int x1, int x2, int y1, int y2) {
        if (inSameLine(x1, x2)) {
            return y1 - y2;
        }
        return x1 - x2;
    }

    private static boolean inSameLine(int c1, int c2) {
        return (c1 - c2 >= -1 * SAME_LINE_THRESHOLD && c1 - c2 <= SAME_LINE_THRESHOLD);
    }

    public static String getString(SparseArray<TextBlock> textBlockSparseArray, boolean multiColumn) {
        StringBuilder text = new StringBuilder();
        if (textBlockSparseArray == null || textBlockSparseArray.size() == 0) {
            return text.toString();
        }
        for (TextBlock textBlock : sortTB(textBlockSparseArray)) {
            for (Text textLine : textBlock.getComponents()) {
                String line = textLine.getValue();
                text.append(line).append("\n");
            }
        }
        Log.e(TAG, LogUtil.prependCallLocation("getString: sortTB "+text.toString() ));
        text = new StringBuilder();
//        fun(textBlockSparseArray);
        if (multiColumn) {
            List<TextBlock> sortedTextBlocks = getSortedTextBlockList(textBlockSparseArray, true);
//            TextBlock prevTextBlock = null;
//            boolean newColumn = false;
            for (TextBlock textBlock : sortedTextBlocks) {
                for (Text textLine : textBlock.getComponents()) {
                    String line = textLine.getValue();
                    text.append(line).append("\n");
                }
            }
            Log.e(TAG, LogUtil.prependCallLocation("getString: multi T "+text.toString()));
        } else {
//            for (TextBlock textBlock : sortTB(textBlockSparseArray)) {
//                for (Text textLine : textBlock.getComponents()) {
//                    String line = textLine.getValue();
//                    text.append(line).append("\n");
//                }
//            }
            Log.e(TAG, LogUtil.prependCallLocation("getString: multi F " + getSingleColumnText(textBlockSparseArray)));
            return getSingleColumnText(textBlockSparseArray);
        }
        return text.toString();
    }

    private static String getSingleColumnText(SparseArray<TextBlock> textBlockSparseArray) {
        List<TextBlock> sortedTextBlocks = getSortedTextBlockList(textBlockSparseArray, false);
        // List of all the lines in text blocks
        List<Line> textLines = getLineList(sortedTextBlocks);
        // takes care of merging of lines in case of multi column text
        boolean[] merged = new boolean[textLines.size()];
        int leftest_pt = -1;
        List<CLine> sortedLines = new ArrayList<>();
        for (int i = 0; i < textLines.size(); ++i) {
            if (merged[i])
                continue;
            // line at ith position in list
            String lineI = textLines.get(i).getValue();
            int topi = textLines.get(i).getBoundingBox().top;
            int lfti = textLines.get(i).getBoundingBox().left;
            int btmi = textLines.get(i).getBoundingBox().bottom;
            int rgti = textLines.get(i).getBoundingBox().right;

            if(leftest_pt == -1  || lfti < leftest_pt ) {
                leftest_pt = lfti;
            }
            CLine cLine = new CLine(textLines.get(i));
            // check and merge lines in same line with ith line
            for (int j = i + 1; j < textLines.size(); j++) {
                int topj = textLines.get(j).getBoundingBox().top;
                int lftj = textLines.get(j).getBoundingBox().left;
                int rgtj = textLines.get(j).getBoundingBox().right;
                int btmj = textLines.get(j).getBoundingBox().bottom;
                if (inSameLine(topi, topj)) {
                    String lineJ = textLines.get(j).getValue();
                    String a = lineI, b = lineJ;
                    int diff = lftj - rgti;
                    rgti = rgtj;
                    if (lfti > lftj) {
                        a = lineJ;
                        b = lineI;
                        diff = lfti - rgtj;
                        lfti = lftj;
                    }
                    // Merging ith and jth line
                    StringBuilder currentLine = new StringBuilder(a).append(" ");
                    for (int k = MAGIC_NUM_SPACE; k < diff ; k+=MAGIC_NUM_SPACE) {
                        currentLine.append(" ");
                    }
                    currentLine.append(b);
                    lineI = currentLine.toString();
                    merged[j] = true;
//                    cLine.setTop((topi + topj) / 2);
//                    cLine.setBottom((btmi + btmj) / 2);
                    cLine.setLeft(lfti);
                    cLine.setRight(rgti);
                    cLine.setValue(lineI);
                }
            }
            sortedLines.add(cLine);
        }

        StringBuilder text = new StringBuilder();
        for(int i=0;i<sortedLines.size();++i) {
            Log.e(TAG, LogUtil.prependCallLocation("getSortedLines: "+sortedLines.get(i)));
            CLine currLine = sortedLines.get(i);
            // current left - left boundary(0)
            int leftdiff = currLine.getLeft() - leftest_pt;
            for(int j = 0; j<leftdiff; j+= MAGIC_NUM_ENDL) {
                text.append(" ");
            }
            if(i == sortedLines.size()-1) {
                text.append(currLine.text).append("\n");
                currLine.setValue(text.toString());
                sortedLines.set(i, currLine);
                break;
            }
            CLine nextLine = sortedLines.get(i+1);
            // next top - current bottom
            int ntcbDiff = nextLine.getTop() - currLine.getBottom();

            if( ntcbDiff > MAGIC_NUM_ENDL) {
                text.append(currLine.text);
                for(int j = 0; j<ntcbDiff;j += MAGIC_NUM_ENDL) {
                    text.append("\n");
                }
            } else if(inSameLine(currLine.getLeft(), nextLine.getLeft())) {
                if (currLine.text.endsWith(".") || currLine.text.endsWith("?") || currLine.text.endsWith("!") ||
                        Character.isUpperCase(nextLine.text.codePointAt(0))) {
                    text.append(currLine.text).append("\n");
                } else if (currLine.text.endsWith("-")) {
                    text.append(currLine.text.substring(0, currLine.text.length() - 1));
                } else {
                    text.append(currLine.text).append("\n");
                }
            } else {
                text.append(currLine.text).append("\n");
            }
            currLine.setValue(text.toString());
            sortedLines.set(i, currLine);
        }
        return text.toString();
    }


    /*public static void fun(SparseArray<TextBlock> textBlockSparseArray) {
        Log.e(TAG, LogUtil.prependCallLocation("fun: start\n"));
        printBlockLine(getTextBlockList(textBlockSparseArray));
        Log.e(TAG, LogUtil.prependCallLocation("fun: algo 1\n"));
        printBlockLine(getTextBlockList(sortTB(textBlockSparseArray)));
        List<TextBlock> textBlockList1 = getSortedTextBlockList(textBlockSparseArray, false);
        List<TextBlock> textBlockList2 = getSortedTextBlockList(textBlockSparseArray, true);
        Log.e(TAG, LogUtil.prependCallLocation("fun: ***********Multicloumn False\n"));
        printBlockLine(textBlockList1);
        printLine(getSortedLines(textBlockSparseArray));
        Log.e(TAG, LogUtil.prependCallLocation("fun: ***********Multicloumn True\n"));
        printBlockLine(textBlockList2);
    }

    private static void printLine(List<String> lines) {
        Log.e(TAG, LogUtil.prependCallLocation("printLine: start\n"));
        int i = 1;
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            text.append(i).append(" - ").append(line).append("\n");
            i += 1;
        }
        Log.e(TAG, LogUtil.prependCallLocation(text.toString()));
    }

    private static void printBlockLine(List<TextBlock> textBlockList) {
        Log.e(TAG, LogUtil.prependCallLocation("printBlockLine: start\n"));
        StringBuilder blocks = new StringBuilder();
        StringBuilder lines = new StringBuilder();
        int i = 1;
        int k = 1;
        for (TextBlock textBlock : textBlockList) {
            blocks.append("\t").append(i).append(" - ")
//                    .append(textBlock.getBoundingBox().toString())
                    .append(textBlock.getValue()).append("\n\n");
            for (Text line : textBlock.getComponents()) {
                //extract scanned text lines her
                lines.append("\t").append(i).append(".").append(k).append(" - ")
//                        .append(line.getBoundingBox().toString())
                        .append(line.getValue()).append("\n\n");
                k += 1;
            }
            i += 1;
        }

        Log.e(TAG, LogUtil.prependCallLocation("\tprintBlockLine: blocks " + blocks.toString()));
        Log.e(TAG, LogUtil.prependCallLocation("\tprintBlockLine: lines " + lines.toString()));
    }*/


    private static List<TextBlock> sortTB(SparseArray<TextBlock> items) {
        if (items == null) {
            return null;
        }

        int size = items.size();
        if (size == 0) {
            return null;
        }

        //Moving from SparseArray to List, to use Lambda expression
        List<TextBlock> sortedTBList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            sortedTBList.add(items.valueAt(i));
        }
        Collections.sort(sortedTBList, (o1, o2) -> {
            RectF rect1 = new RectF(o1.getComponents().get(0).getBoundingBox());
            RectF rect2 = new RectF(o2.getComponents().get(0).getBoundingBox());

            //Test if textBlock are on the same line
            if (rect2.centerY() < rect1.centerY() + SAME_LINE_THRESHOLD
                    && rect2.centerY() > rect1.centerY() - SAME_LINE_THRESHOLD) {
                //sort on the same line (X value)
                return Float.compare(rect1.left, rect2.left);
            }
            //else sort them by their Y value
            return Float.compare(rect1.centerY(), rect2.centerY());
        });


        return sortedTBList;
    }
}
