import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class FilesTests {

    @Test
    @DisplayName("Check file excel")
    void excelFileTest() throws Exception {
          try (InputStream stream = getClass().getClassLoader().getResourceAsStream("ExampleExcel.xlsx")) {
              assert stream != null;
              XLS parsed = new XLS(stream);
              assertThat(parsed.excel.getNumberOfSheets()).isEqualTo(1);
              assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue()).isEqualTo("FirstRow-FirstColumn");
              assertThat(parsed.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue()).isEqualTo("SecondRow-SecondColumn");
              assertThat(parsed.excel.getSheetAt(0).getRow(0).getCell(2).getStringCellValue()).isEqualTo("FirstRow-ThirdColumn");
          }
    }

    @Test
    @DisplayName("Check file .txt")
    void txtFileTest() throws Exception {
        String result;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("ExampleTXTFile.txt")) {
            assert stream != null;
            result = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(result).hasLineCount(1);
            assertThat(result).contains("Создайте свои файлы и напишите проверки содержимого:");
        }
    }

    @Test
    @DisplayName("Check file .pdf")
    void pdfFileTest() throws Exception {
        PDF parsed = new PDF(getClass().getClassLoader().getResource("ExamplePDFFile.pdf"));
        assertThat(parsed.author).contains("Michael Sorens");
        assertThat(parsed.text).contains("Sprinkled with Selenium usage tips, this is both a general-purpose set of recipes " +
                "for each technology as well as a cross-reference to map from one to another");
    }



    @Test
    @DisplayName("Check file .docx")
    void docxFileTest() throws Exception {
        File docxFile = new File("src/test/resources/ExampleWord.docx");
        try (FileInputStream stream = new FileInputStream(docxFile)) {
            XWPFDocument fileData = new XWPFDocument(stream);
            XWPFWordExtractor extract = new XWPFWordExtractor(fileData);
            //Check data into file
            assertThat(extract.getText()).contains("Word file");
        }
    }

    @Test
    @DisplayName("Check file .zip")
    void zipArchiveTest() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/WorkWithFiles.zip");
        LocalFileHeader localFileHeader;
        String password = "test123";

        //Check how many files in archive
        assertThat(zipFile.getFileHeaders().size()).isEqualTo(138);

        try (InputStream inputStream = new FileInputStream(String.valueOf(zipFile))) {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream, password.toCharArray());
            String fileName;
            ArrayList<String> list = new ArrayList<>();
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());
                fileName = extractedFile.getName();
                list.add(fileName);
            }

            //Check that archive includes .txt file
            assertThat(list).contains("EXampleTXTFile.txt");
        }
    }
}
