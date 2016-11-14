package analysis.linecount;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LineCountTest {

    private LineCount lineCount;
    private List<String> lines;

    @Before
    public void setUp() {
        lineCount = new LineCount();
        lines = new ArrayList<>();
    }

    @Test
    public void emptyFileHasZeroCount() {
        File file = new File("testresources/emptyFile.java");
        int count = lineCount.countLines(file);
        assertEquals(0, count);
    }

    @Test
    public void bareClassHasCount() {
        File file = new File("testresources/ClassDeclaration.java");
        int count = lineCount.countLines(file);
        assertEquals(2, count);
    }

    @Test
    public void oneLineClassDeclarationHasOneCount() {
        lines.add("public class MyClass {}");
        int count = lineCount.count(lines.stream());
        assertEquals(1, count);
    }

    @Test
    public void twoLineClassDeclarationHasTwoCount() {
        lines.add("public class MyClass {");
        lines.add("}");
        int count = lineCount.count(lines.stream());
        assertEquals(2, count);
    }

    @Test
    public void emptyLineIsNotCounted() {
        lines.add("public class MyClass {");
        lines.add("");
        lines.add("}");
        int count = lineCount.count(lines.stream());
        assertEquals(2, count);
    }

    @Test
    public void lineWithOnlySpacesIsNotCounted() {
        lines.add("        ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void singleLineCommentIsNotCounted() {
        lines.add("  //        ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void multiLineCommentOnSingleLineIsNotCounted() {
        lines.add("  /* Comment */        ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void multiLineCommentOnMultipleLinesAreNotCounted() {
        lines.add("  /*          ");
        lines.add("  blah          ");
        lines.add("  */          ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void blockCommentAfterCodeIsCounted() {
        lines.add("  Something /*   */        ");
        int count = lineCount.count(lines.stream());
        assertEquals(1, count);
    }

    @Test
    public void blockCommentBeforeCodeIsCounted() {
        lines.add("   /*   */   Something     ");
        int count = lineCount.count(lines.stream());
        assertEquals(1, count);
    }

    @Test
    public void multiBlockCommentIsNotCounted() {
        lines.add("   /*   */   /* Comment  */    ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void codeWithMultiBlockCommentIsCounted() {
        lines.add("   /*   */  Something /* Comment  */    ");
        int count = lineCount.count(lines.stream());
        assertEquals(1, count);
    }

    @Test
    public void commentedOutEndBlock() {
        lines.add("  /*          ");
        lines.add("  comment          ");
        lines.add("  // */          ");
        lines.add("  comment          ");
        lines.add("   */          ");
        int count = lineCount.count(lines.stream());
        assertEquals(0, count);
    }

    @Test
    public void stringWithComment() {
        lines.add("  \"   /*   \"       ");
        lines.add("  comment          ");
        int count = lineCount.count(lines.stream());
        assertEquals(2, count);
    }

}
